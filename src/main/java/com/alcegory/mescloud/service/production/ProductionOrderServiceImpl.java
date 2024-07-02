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
import org.hibernate.Hibernate;
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

    private static final String SYS_CODE_PREFIX = "SYS";
    private static final String CODE_PREFIX = "PO";
    private static final String FIVE_DIGIT_NUMBER_FORMAT = "%05d";
    private static final int FIRST_CODE_VALUE = 1;

    private final ProductionOrderRepository repository;
    private final ProductionOrderConverter converter;
    private final CounterRecordRepository counterRecordRepository;

    @Override
    public String generateCode(String sectionPrefix, boolean isSystemGenerated) {
        Optional<String> lastCodeOpt;
        String codePrefix;

        if (isSystemGenerated) {
            lastCodeOpt = repository.findTopCodeBySysPrefixSectionPrefixOrderByIdDesc(SYS_CODE_PREFIX,
                    sectionPrefix);
            codePrefix = SYS_CODE_PREFIX + sectionPrefix;
        } else {
            lastCodeOpt = repository.findTopCodeBySectionPrefixAndCodePrefixOrderByIdDesc(sectionPrefix, CODE_PREFIX);
            codePrefix = sectionPrefix + CODE_PREFIX;
        }

        int yearLastTwoDigits = DateUtil.getCurrentYearLastTwoDigits();
        String codeWithYear = codePrefix + yearLastTwoDigits;

        return lastCodeOpt.isEmpty() || hasYearChanged(lastCodeOpt.get(), yearLastTwoDigits, codePrefix) ?
                codeWithYear + String.format(FIVE_DIGIT_NUMBER_FORMAT, FIRST_CODE_VALUE) :
                codeWithYear + generateFormattedCodeValue(lastCodeOpt.get(), codeWithYear);
    }

    private String generateFormattedCodeValue(String productionOrderCode, String codePrefix) {
        if (productionOrderCode == null || productionOrderCode.isEmpty()) {
            String message = "Unable to generate new code: last stored Production Order code is null or empty";
            log.info(message);
            throw new IllegalStateException(message);
        }

        String lastCodeValueAsString = productionOrderCode.substring(codePrefix.length());
        int lastCodeValue = Integer.parseInt(lastCodeValueAsString);

        return String.format(FIVE_DIGIT_NUMBER_FORMAT, ++lastCodeValue);
    }

    private boolean hasYearChanged(String productionOrderCode, int yearDigits, String codePrefix) {
        String numericCode = productionOrderCode.substring(codePrefix.length());
        return !numericCode.startsWith(String.valueOf(yearDigits));
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ProductionOrderDto> getCompletedWithoutComposedFiltered(long sectionId) {
        List<ProductionOrderEntity> persistedProductionOrders = repository.findCompleted(sectionId, true, null, null, null);
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
    public List<ProductionOrderEntity> findByComposedProductionOrderId(Long composedProductionOrderId) {
        return repository.findByComposedProductionOrderId(composedProductionOrderId);
    }

    @Override
    public Long findComposedProductionOrderIdByCode(String code) {
        return repository.findComposedProductionOrderIdByCode(code);
    }

    @Override
    public boolean isCompleted(String productionOrderCode) {
        return repository.isCompleted(productionOrderCode);
    }

    @Override
    @Transactional
    public void deleteByCode(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrder = repository.findByCode(productionOrderCode);
        if (productionOrder.isEmpty()) {
            return;
        }

        repository.delete(productionOrder.get());
    }

    @Override
    @Transactional
    public void delete(ProductionOrderEntity productionOrder) {
        repository.delete(productionOrder);
    }

    @Override
    @Transactional(readOnly = true)
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

    @Transactional
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
    @Transactional(readOnly = true)
    public ProductionOrderEntity getProductionOrderByCode(String code) {
        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findByCode(code);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found for an Equipment with code [%s]", code));
            return null;
        }

        return productionOrderEntityOpt.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductionOrderEntity> findActiveByEquipmentId(long equipmentId) {
        if (equipmentId <= 0) {
            throw new IllegalArgumentException("Equipment ID must be positive");
        }

        Optional<ProductionOrderEntity> productionOrderEntityOpt = repository.findActiveByEquipmentId(equipmentId);

        productionOrderEntityOpt.ifPresent(productionOrder ->
                Hibernate.initialize(productionOrder.getProductionInstructions()));

        return productionOrderEntityOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEquipmentActiveProductionOrder(Long equipmentId) {
        if (equipmentId == null || equipmentId <= 0) {
            throw new IllegalArgumentException("Invalid equipment ID: " + equipmentId);
        }
        return repository.hasEquipmentActiveProductionOrder(equipmentId);
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public Optional<ProductionOrderEntity> findLastByEquipmentId(long equipmentId) {
        return repository.findLastByEquipmentId(equipmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionOrderEntity> findCompleted(long sectionId, boolean withoutComposed, Filter filter, Timestamp startDate,
                                                     Timestamp endDate) {
        return repository.findCompleted(sectionId, withoutComposed, filter, startDate, endDate);
    }

    @Override
    public Optional<ProductionOrderEntity> findActiveByEquipmentCode(String equipmentCode) {
        return repository.findActiveByEquipmentCode(equipmentCode);
    }
}

