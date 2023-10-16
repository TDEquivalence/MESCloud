package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.protocol.MesMqttSettings;
import com.alcegory.mescloud.repository.CountingEquipmentRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.dto.ProductionOrderSummaryDto;
import com.alcegory.mescloud.model.entity.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.ProductionOrderSummaryEntity;
import com.alcegory.mescloud.service.ProductionOrderService;
import com.alcegory.mescloud.utility.DateUtil;
import com.alcegory.mescloud.utility.LockUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private final CountingEquipmentRepository countingEquipmentRepository;
    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;
    private final LockUtil lockHandler;

    private final Object processLock = new Object();

    @Override
    public Optional<ProductionOrderDto> findByCode(String code) {
        Optional<ProductionOrderEntity> persistedProductionOrderOpt = repository.findByCode(code);
        if (persistedProductionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductionOrderDto productionOrder = converter.toDto(persistedProductionOrderOpt.get());
        return Optional.of(productionOrder);
    }

    @Override
    public boolean hasActiveProductionOrder(long countingEquipmentId) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(countingEquipmentId);
        return productionOrderEntityOpt.isPresent();
    }

    private ProductionOrderEntity findActiveProductionOrder(long countingEquipmentId) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(countingEquipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            log.info(() -> String.format("No Production Order active for equipment code [%s]:",
                    countingEquipmentId));
            return null;
        }
        return productionOrderEntityOpt.get();
    }

    @Override
    public Optional<ProductionOrderDto> complete(long equipmentId) {
        log.info(() -> String.format("Complete process Production Order started for equipmentId [%s]:", equipmentId));

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentRepository.findById(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        String equipmentCode = countingEquipmentOpt.get().getCode();
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActive(equipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No active Production Order found for Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        boolean isCompletePerformed;
        synchronized (processLock) {

            isCompletePerformed = performFirstVerification(equipmentCode, productionOrderEntityOpt.get(), countingEquipmentOpt.get());

            if (!isCompletePerformed) {
                isCompletePerformed = performSecondVerification(equipmentCode, productionOrderEntityOpt.get(), countingEquipmentOpt.get());
            }

            try {
                log.info(() -> String.format("Wait for execute unlock for equipment with code [%s]", equipmentCode));
                lockHandler.waitForExecute(equipmentCode);
            } catch (InterruptedException e) {
                log.severe("Thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        if (!isProductionOrderCompletedSuccessfully(countingEquipmentOpt.get()) && !isCompletePerformed) {
            Optional<ProductionOrderDto> completionResult =
                    Optional.ofNullable(getPersistedProductionOrder(productionOrderEntityOpt.get().getCode()));
            if (completionResult.isPresent()) {
                return completionResult;
            }
        }

        if (lockHandler.hasLock(equipmentCode)) {
            lockHandler.releaseAllLocks(equipmentCode);
        }

        return Optional.ofNullable(getPersistedProductionOrder(productionOrderEntityOpt.get().getCode()));
    }


    private boolean performFirstVerification(String equipmentCode, ProductionOrderEntity productionOrder,
                                             CountingEquipmentEntity countingEquipment) {
        if (!lockHandler.hasLock(equipmentCode)) {
            lockHandler.lock(equipmentCode);
            log.info(() -> String.format("FIRST verification: get lock for equipment with code [%s]", equipmentCode));
            log.info(() -> String.format("FIRST verification: complete production order with code [%s]", productionOrder.getCode()));
            publishOrderCompletion(countingEquipment, productionOrder);
            return true;
        }
        return false;
    }

    private boolean performSecondVerification(String equipmentCode, ProductionOrderEntity productionOrder,
                                              CountingEquipmentEntity countingEquipment) {
        if (!isCompleted(productionOrder.getCode())) {
            log.info(() -> String.format("SECOND verification: get lock for equipment with code [%s]", equipmentCode));
            log.info(() -> String.format("SECOND verification: complete production order with code [%s]", productionOrder.getCode()));
            ProductionOrderEntity activeProductionOrder = findActiveProductionOrder(countingEquipment.getId());
            if (activeProductionOrder != null) {
                lockHandler.unlockAndLock(equipmentCode);
                publishOrderCompletion(countingEquipment, activeProductionOrder);
            }
            return true;
        }
        return false;
    }

    private boolean isProductionOrderCompletedSuccessfully(CountingEquipmentEntity equipment) {
        ProductionOrderEntity productionOrder = findActiveProductionOrder(equipment.getId());
        if (productionOrder != null && !isCompleted(productionOrder.getCode()) ||
                hasActiveProductionOrder(equipment.getId())) {
            log.info(() -> String.format("Production order was not completed as expected for equipment code [%s]",
                    equipment.getCode()));
            publishOrderCompletion(equipment, productionOrder);
            log.info(() -> String.format("Publish to PLC to complete Production Order for equipment code [%s]",
                    equipment.getCode()));
            lockHandler.unlockAndLock(equipment.getCode());
            log.info(() -> String.format("THIRD verification: Get lock for equipment with code [%s]", equipment.getCode()));
            try {
                log.info(() -> String.format("Wait for execute unlock for equipment with code [%s]", equipment.getCode()));
                lockHandler.waitForExecute(equipment.getCode());
            } catch (InterruptedException e) {
                log.severe("Thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            return true;
        }
        return false;
    }

    public void publishOrderCompletion(CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder) {
        try {
            publishOrderCompletionToPLC(countingEquipment, productionOrder);
        } catch (MesMqttException e) {
            log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment [%s]",
                    countingEquipment.getCode()));
        }
    }

    private void publishOrderCompletionToPLC(CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder)
            throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
        productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_DTO_NAME);
        productionOrderMqttDto.setEquipmentEnabled(false);
        productionOrderMqttDto.setProductionOrderCode(productionOrder.getCode());
        productionOrderMqttDto.setTargetAmount(0);
        productionOrderMqttDto.setEquipmentCode(countingEquipment.getCode());
        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
    }


    private ProductionOrderDto getPersistedProductionOrder(String code) {
        Optional<ProductionOrderEntity> productionOrderOpt = repository.findByCode(code);
        if (productionOrderOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to get persisted Production Order with code [%s]", code));
            return null;
        }
        ProductionOrderEntity productionOrderPersisted = productionOrderOpt.get();
        ProductionOrderDto productionOrderDto = converter.toDto(productionOrderPersisted);
        log.warning(() -> String.format("COMPLETE: Returning complete persisted Production Order with code [%s]",
                productionOrderDto.getCode()));
        return productionOrderDto;
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

        try {
            //TODO: remove to MesProtocolProcess level
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
        String codePrefix = OBO_SECTION_PREFIX + CODE_PREFIX + DateUtil.getCurrentYearLastTwoDigits();

        return productionOrderOpt.isEmpty() ?
                codePrefix + String.format(FIVE_DIGIT_NUMBER_FORMAT, FIRST_CODE_VALUE) :
                codePrefix + generateFormattedCodeValue(productionOrderOpt.get(), codePrefix);
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

    private void publishToPlc(ProductionOrderEntity productionOrderEntity) throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = converter.toMqttDto(productionOrderEntity, true);
        mqttClient.publish(mqttSettings.getProtCountPlcTopic(), productionOrderMqttDto);
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
    public void delete(ProductionOrderEntity productionOrder) {
        repository.delete(productionOrder);
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
        List<ProductionOrderEntity> productionOrders = repository.findByEquipmentAndPeriod(equipmentId, startDate, endDate);
        return converter.toDto(productionOrders);
    }

    @Override
    public boolean isCompleted(String productionOrderCode) {
        if (productionOrderCode == null) {
            return false;
        }
        return repository.isCompleted(productionOrderCode);
    }

    @Override
    public Long calculateScheduledTimeInSeconds(Long equipmentId, Date startDate, Date endDate) {

        List<ProductionOrderEntity> productionOrders = repository.findByEquipmentAndPeriod(equipmentId, startDate, endDate);
        Duration totalActiveTime = Duration.ZERO;
        for (ProductionOrderEntity productionOrder : productionOrders) {

            Duration productionOrderActiveTime = calculateScheduledTime(productionOrder, startDate, endDate);
            totalActiveTime = totalActiveTime.plus(productionOrderActiveTime);
        }

        return totalActiveTime.getSeconds();
    }

    private Duration calculateScheduledTime(ProductionOrderEntity productionOrder, Date startDate, Date endDate) {
        Date createdAt = productionOrder.getCreatedAt();
        Date completedAt = productionOrder.getCompletedAt();

        startDate = (startDate.before(createdAt)) ? createdAt : startDate;
        endDate = (endDate.before(createdAt)) ? createdAt : endDate;
        endDate = (completedAt != null && completedAt.before(endDate)) ? completedAt : endDate;

        long durationInMillisenconds = Math.max(0, endDate.getTime() - startDate.getTime());

        return Duration.ofMillis(durationInMillisenconds);
    }
}
