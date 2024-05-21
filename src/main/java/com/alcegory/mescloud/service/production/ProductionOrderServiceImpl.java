package com.alcegory.mescloud.service.production;

import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.entity.production.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.production.ProductionOrderRepository;
import com.alcegory.mescloud.repository.record.CounterRecordRepository;
import com.alcegory.mescloud.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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

    private final CounterRecordRepository counterRecordRepository;

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

    @Override
    public Optional<ProductionOrderDto> findDtoByCode(String code) {
        Optional<ProductionOrderEntity> persistedProductionOrderOpt = repository.findByCode(code);
        if (persistedProductionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductionOrderDto productionOrder = converter.toDto(persistedProductionOrderOpt.get());
        return Optional.of(productionOrder);
    }

    @Override
    public Optional<ProductionOrderEntity> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public boolean hasActiveProductionOrderByEquipmentId(long equipmentId) {
        return repository.hasEquipmentActiveProductionOrder(equipmentId);
    }

    @Override
    public boolean hasActiveProductionOrderByEquipmentCode(String equipmentCode) {
        return repository.hasEquipmentActiveProductionOrder(equipmentCode);
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
    @Transactional
    public List<ProductionOrderDto> getCompletedWithoutComposedFiltered() {
        List<ProductionOrderEntity> persistedProductionOrders = repository.findCompleted(true, null, null, null);
        return converter.toDto(persistedProductionOrders);
    }

    @Override
    public void setProductionOrderApproval(Long composedOrderId, boolean isApproved) {
        try {
            List<ProductionOrderEntity> productionOrders = findByComposedProductionOrderId(composedOrderId);

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
    public List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedOrderId) {
        return repository.findByComposedProductionOrderId(composedOrderId);
    }

    @Override
    public Long findComposedProductionOrderIdByCode(String code) {
        return repository.findComposedProductionOrderIdByCode(code);
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

    @Override
    public List<ProductionOrderEntity> findByEquipmentAndPeriod(Long equipmentId, String productionOrderCode,
                                                                Timestamp startDate, Timestamp endDate) {
        return repository.findByEquipmentAndPeriod(equipmentId, productionOrderCode, startDate, endDate);
    }

    @Override
    @Transactional
    public List<ProductionOrderDto> getProductionOrderByComposedId(Long composedId) {
        if (composedId == null) {
            throw new IllegalArgumentException("Composed ID cannot be null");
        }
        List<ProductionOrderEntity> productionOrder = repository.findProductionOrderSummaryByComposedId(composedId);
        return converter.toDto(productionOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductionOrderDto> getProductionOrderById(Long id) {
        Optional<ProductionOrderEntity> productionOrderOpt = repository.findById(id);
        if (productionOrderOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductionOrderEntity productionOrderEntity = productionOrderOpt.get();
        ProductionOrderDto productionOrderDto = converter.toDto(productionOrderEntity);
        return Optional.of(productionOrderDto);
    }

    public void completeProductionOrder(ProductionOrderEntity productionOrder) {
        String productionOrderCode = productionOrder.getCode();
        log.info(() -> String.format("Setting and saving production order [%s] as completed.", productionOrderCode));

        productionOrder.setCompletedAt(new Date());
        productionOrder.setCompleted(true);

        Long validAmount = counterRecordRepository.sumValidCounterIncrementByProductionOrderId(productionOrder.getId());
        productionOrder.setValidAmount(validAmount);

        repository.save(productionOrder);
    }

    @Override
    public ProductionOrderEntity getProductionOrderByCode(String code) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findByCode(code);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found for an Equipment with code [%s]", code));
            return null;
        }

        return productionOrderEntityOpt.get();
    }

    @Override
    public Optional<ProductionOrderEntity> findActiveByEquipmentId(long equipmentId) {
        if (equipmentId <= 0) {
            throw new IllegalArgumentException("Equipment ID must be positive");
        }
        return repository.findActiveByEquipmentId(equipmentId);
    }

    @Override
    public boolean hasEquipmentActiveProductionOrder(Long equipmentId) {
        if (equipmentId == null || equipmentId <= 0) {
            throw new IllegalArgumentException("Invalid equipment ID: " + equipmentId);
        }
        return repository.hasEquipmentActiveProductionOrder(equipmentId);
    }

    @Override
    public ProductionOrderEntity save(ProductionOrderEntity productionOrder) {
        if (productionOrder == null) {
            throw new IllegalArgumentException("Production order cannot be null");
        }

        List<ProductionInstructionEntity> productionInstruction = productionOrder.getProductionInstructions();
        for (ProductionInstructionEntity pi : productionInstruction) {
            pi.setProductionOrder(productionOrder);
        }

        return repository.save(productionOrder);
    }

    @Override
    public Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId) {
        return repository.findLastByEquipmentId(equipmentId);
    }

    @Override
    public List<ProductionOrderEntity> findCompleted(boolean withoutComposed, Filter filter, Timestamp startDate,
                                                     Timestamp endDate) {
        return repository.findCompleted(withoutComposed, filter, startDate, endDate);
    }
}
