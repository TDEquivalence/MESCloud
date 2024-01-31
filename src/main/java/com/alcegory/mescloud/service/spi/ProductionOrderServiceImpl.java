package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.protocol.MesMqttSettings;
import com.alcegory.mescloud.repository.CountingEquipmentRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.CountingEquipmentService;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private static final String OBO_SECTION_PREFIX = "OBO";
    private static final String CODE_PREFIX = "PO";
    private static final String FIVE_DIGIT_NUMBER_FORMAT = "%05d";
    private static final int FIRST_CODE_VALUE = 1;

    private final ProductionOrderRepository repository;
    private final ProductionOrderConverter converter;
    private final GenericConverter<ProductionOrderSummaryEntity, ProductionOrderSummaryDto> summaryConverter;
    private final GenericConverter<CountingEquipmentEntity, CountingEquipmentDto> equipmentConverter;
    private final CountingEquipmentService countingEquipmentService;
    private final CountingEquipmentRepository countingEquipmentRepository;
    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;

    @Override
    public Optional<ProductionOrderDto> complete(long equipmentId) {
        log.info(() -> String.format("Complete process Production Order started for equipmentId [%s]:", equipmentId));

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentRepository.findById(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        CountingEquipmentDto countingEquipmentDto = setOperationStatus(countingEquipmentOpt.get(),
                CountingEquipmentEntity.OperationStatus.PENDING);
        log.info(() -> String.format("Change status to PENDING for Equipment with code [%s]", countingEquipmentDto.getCode()));

        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(equipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No active Production Order found for Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        publishProductionOrderCompletion(countingEquipmentOpt.get(), productionOrderEntityOpt.get());
        log.info(() -> String.format("Production Order Conclusion already publish for equipmentId [%s]:", equipmentId));

        return setCompleteDate(productionOrderEntityOpt.get());
    }

    public void publishProductionOrderCompletion(CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder) {
        try {
            publishProductionOrderCompletionToPLC(countingEquipment, productionOrder);
        } catch (MesMqttException e) {
            log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment [%s]",
                    countingEquipment.getCode()));
        }
    }

    private void publishProductionOrderCompletionToPLC(CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder)
            throws MesMqttException {

        ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
        productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_DTO_NAME);
        productionOrderMqttDto.setEquipmentEnabled(false);
        productionOrderMqttDto.setProductionOrderCode(productionOrder.getCode());
        productionOrderMqttDto.setTargetAmount(0);
        productionOrderMqttDto.setEquipmentCode(countingEquipment.getCode());
        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
    }

    @Override
    public void completeByCode(String productionOrderCode) {

        Optional<ProductionOrderEntity> productionOrderOpt = findByCode(productionOrderCode);
        if (productionOrderOpt.isEmpty()) {
            return;
        }

        ProductionOrderEntity productionOrder = productionOrderOpt.get();
        productionOrder.setCompleted(true);
        setCompleteDate(productionOrder);
    }

    private Optional<ProductionOrderDto> setCompleteDate(ProductionOrderEntity productionOrderEntity) {

        productionOrderEntity.setCompletedAt(new Date());
        repository.save(productionOrderEntity);
        ProductionOrderDto productionOrderDto = converter.toDto(productionOrderEntity);

        return Optional.of(productionOrderDto);
    }

    @Override
    public Optional<ProductionOrderDto> create(ProductionOrderDto productionOrder) {

        Optional<CountingEquipmentEntity> countingEquipmentEntityOpt =
                countingEquipmentRepository.findById(productionOrder.getEquipmentId());
        if (countingEquipmentEntityOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to create Production Order - no Equipment found with id [%s]",
                    productionOrder.getEquipmentId()));
            return Optional.empty();
        }

        ProductionOrderEntity productionOrderEntity = converter.toEntity(productionOrder);
        productionOrderEntity.setCreatedAt(new Date());
        productionOrderEntity.setCompleted(false);
        productionOrderEntity.setCode(generateCode());

        CountingEquipmentEntity countingEquipmentEntity = countingEquipmentEntityOpt.get();
        countingEquipmentEntity.setId(productionOrder.getEquipmentId());
        productionOrderEntity.setEquipment(countingEquipmentEntity);
        productionOrderEntity.setIms(countingEquipmentEntity.getIms());

        ProductionOrderEntity persistedProductionOrder = repository.save(productionOrderEntity);

        log.info(() -> String.format("Change status to IN PROGRESS for Equipment with code [%s]", countingEquipmentEntity.getCode()));
        setOperationStatus(countingEquipmentEntity, CountingEquipmentEntity.OperationStatus.IN_PROGRESS);

        try {
            publishToPlc(persistedProductionOrder);
        } catch (MesMqttException e) {
            log.severe("Unable to publish Production Order over MQTT.");
            return Optional.empty();
        }

        ProductionOrderDto persistedProductionOrderDto = converter.toDto(persistedProductionOrder);
        return Optional.of(persistedProductionOrderDto);
    }

    @Override
    public String generateCode() {

        Optional<ProductionOrderEntity> productionOrderOpt = repository.findTopByOrderByIdDesc();
        int yearLastTwoDigits = DateUtil.getCurrentYearLastTwoDigits();
        String codePrefix = OBO_SECTION_PREFIX + CODE_PREFIX;
        String codeWithYear = codePrefix + yearLastTwoDigits;

        return productionOrderOpt.isEmpty() ||
                hasYearChanged(productionOrderOpt.get().getCode(), yearLastTwoDigits, codePrefix) ?
                codeWithYear + String.format(FIVE_DIGIT_NUMBER_FORMAT, FIRST_CODE_VALUE) :
                codeWithYear + generateFormattedCodeValue(productionOrderOpt.get(), codeWithYear);
    }

    private String generateFormattedCodeValue(ProductionOrderEntity productionOrder, String codePrefix) {

        if (productionOrder.getCode() == null || productionOrder.getCode().isEmpty()) {
            String message = "Unable to generate new code: last stored Production Order code is null or empty";
            log.warning(message);
            throw new IllegalStateException(message);
        }

        String lastCodeValueAsString = productionOrder.getCode().substring(codePrefix.length());
        int lastCodeValue = Integer.parseInt(lastCodeValueAsString);

        return String.format(FIVE_DIGIT_NUMBER_FORMAT, ++lastCodeValue);
    }

    private boolean hasYearChanged(String productionOrderCode, int yearDigits, String codePrefix) {
        String numericCode = productionOrderCode.substring(codePrefix.length());
        return !numericCode.startsWith(String.valueOf(yearDigits));
    }

    private void publishToPlc(ProductionOrderEntity productionOrderEntity) throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = converter.toMqttDto(productionOrderEntity, true);
        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
    }

    @Override
    public Optional<ProductionOrderDto> findDtoByCode(String code) {
        Optional<ProductionOrderEntity> persistedProductionOrderOpt = repository.findByCode(code);
        if (persistedProductionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductionOrderDto productionOrder = converter.toDto(persistedProductionOrderOpt.get());
        return Optional.of(productionOrder);
    }

    private Optional<ProductionOrderEntity> findByCode(String code) {
        Optional<ProductionOrderEntity> persistedProductionOrderOpt = repository.findByCode(code);
        if (persistedProductionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        return persistedProductionOrderOpt;
    }

    @Override
    public boolean hasActiveProductionOrder(long equipmentId) {
        return repository.existsByEquipmentIdAndIsCompletedFalse(equipmentId);
    }

    @Override
    public ProductionOrderEntity saveAndUpdate(ProductionOrderEntity productionOrder) {
        return repository.save(productionOrder);
    }

    @Override
    public List<ProductionOrderEntity> saveAndUpdateAll(List<ProductionOrderEntity> productionOrder) {
        return repository.saveAll(productionOrder);
    }

    @Override
    public Optional<ProductionOrderEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<ProductionOrderDto> findDtoById(Long id) {
        Optional<ProductionOrderEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        ProductionOrderDto dto = converter.toDto(entity.get());
        return Optional.of(dto);
    }

    public List<Long> findExistingIds(List<Long> ids) {
        List<ProductionOrderEntity> existingEntities = repository.findByIdIn(ids);

        return existingEntities.stream()
                .map(ProductionOrderEntity::getId)
                .toList();
    }

    @Override
    public List<ProductionOrderSummaryDto> getCompletedWithoutComposed() {
        List<ProductionOrderSummaryEntity> persistedProductionOrders = repository.findCompletedWithoutComposed();
        return summaryConverter.toDto(persistedProductionOrders, ProductionOrderSummaryDto.class);
    }

    @Override
    public void setProductionOrderApproval(Long composedOrderId, boolean isApproved) {
        try {
            List<ProductionOrderEntity> productionOrders = repository.findByComposedProductionOrderId(composedOrderId);

            if (productionOrders == null || productionOrders.isEmpty()) {
                log.warning("No production orders found for composed order ID: " + composedOrderId);
                return;
            }

            for (ProductionOrderEntity productionOrder : productionOrders) {
                productionOrder.setIsApproved(isApproved);
            }

            saveAndUpdateAll(productionOrders);
        } catch (Exception e) {
            log.warning("Error in setProductionOrderApproval: " + e.getMessage());
        }
    }

    @Override
    public List<ProductionOrderDto> findByEquipmentAndPeriod(Long equipmentId, Date startDate, Date endDate) {
        List<ProductionOrderEntity> productionOrders = repository.findByEquipmentAndPeriod(equipmentId, null,
                startDate, endDate);
        return converter.toDto(productionOrders);
    }

    @Override
    public boolean isCompleted(String productionOrderCode) {
        return repository.isCompleted(productionOrderCode);
    }

    @Override
    public Long calculateScheduledTimeInSeconds(Timestamp startDate, Timestamp endDate) {
        Duration productionScheduleTime = calculateScheduledTime(startDate, endDate);

        if (isInclusiveEnd(endDate)) {
            productionScheduleTime = productionScheduleTime.plusSeconds(1);
        }

        return productionScheduleTime.getSeconds();
    }

    private static boolean isInclusiveEnd(Timestamp endDate) {
        Instant endInstant = endDate.toInstant();
        return endInstant.getNano() > 0 || endInstant.getEpochSecond() % 60 > 0;
    }

    private Duration calculateScheduledTime(Timestamp startDate, Timestamp endDate) {
        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        Duration duration = Duration.between(startInstant, endInstant);
        return duration.isNegative() ? Duration.ZERO : duration;
    }

    @Override
    public Timestamp getAdjustedStartDate(ProductionOrderEntity productionOrder, Timestamp startDate) {
        Instant createdAt = productionOrder.getCreatedAt().toInstant();
        Instant adjustedStartDate = createdAt.isAfter(startDate.toInstant()) ? createdAt : startDate.toInstant();
        return Timestamp.from(adjustedStartDate);
    }

    @Override
    public Timestamp getAdjustedEndDate(ProductionOrderEntity productionOrder, Timestamp endDate) {
        Date completedAtDate = productionOrder.getCompletedAt();
        Instant completedAtInstant = (completedAtDate != null) ? completedAtDate.toInstant() : null;

        if (completedAtInstant == null) {
            completedAtInstant = endDate.toInstant();
        }

        Instant nowTime = Instant.now();
        if (completedAtInstant.isAfter(nowTime)) {
            completedAtInstant = nowTime;
        }

        return Timestamp.from(completedAtInstant);
    }

    @Override
    public void deleteByCode(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrder = repository.findByCode(productionOrderCode);
        if (productionOrder.isEmpty()) {
            return;
        }

        repository.delete(productionOrder.get());
    }

    @Override
    public void delete(ProductionOrderEntity productionOrder) {
        repository.delete(productionOrder);
    }

    private CountingEquipmentDto setOperationStatus(CountingEquipmentEntity countingEquipment,
                                                    CountingEquipmentEntity.OperationStatus status) {
        countingEquipmentService.setOperationStatus(countingEquipment, status);
        return equipmentConverter.toDto(countingEquipment, CountingEquipmentDto.class);
    }

    @Override
    public List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, String productionOrderCode,
                                                                Timestamp startDate, Timestamp endDate) {
        return repository.findByEquipmentAndPeriod(equipmentId, productionOrderCode, startDate, endDate);
    }

}
