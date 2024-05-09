package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.CounterRecordConverter;
import com.alcegory.mescloud.model.dto.*;
import com.alcegory.mescloud.model.entity.*;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.CounterRecordRepository;
import com.alcegory.mescloud.repository.ProductionOrderRepository;
import com.alcegory.mescloud.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class CounterRecordServiceImpl implements CounterRecordService {

    private static final int INITIAL_VALUE = 0;
    private static final int ROLLOVER_OFFSET = 1;
    private static final int ROLLOVER_MAX_VALUE = 65535;

    private final CounterRecordConverter converter;
    private final CounterRecordRepository repository;
    private final EquipmentOutputService equipmentOutputService;
    private final ProductionOrderService productionOrderService;
    private final ProductionOrderRepository productionOrderRepository;
    private final CountingEquipmentService countingEquipmentService;
    private final CompanyService companyService;


    @Override
    public List<CounterRecordSummaryEntity> getEquipmentOutputProductionPerDay(FilterDto filter) {
        return repository.findLastPerProductionOrderAndEquipmentOutputPerDay(filter);
    }

    @Override
    public List<CounterRecordDto> filterConclusionRecordsKpi(FilterDto filter) {
        List<CounterRecordConclusionEntity> counterRecordConclusionEntities = repository.findLastPerProductionOrder(filter);
        return converter.conclusionViewToDto(counterRecordConclusionEntities);
    }

    @Override
    public PaginatedCounterRecordsDto filterConclusionRecordsPaginated(Filter filter) {
        int requestedRecords = filter.getTake();
        filter.setTake(filter.getTake() + 1);

        List<CounterRecordConclusionEntity> counterRecordConclusionEntities = repository.findLastPerProductionOrder(filter);
        boolean hasNextPage = counterRecordConclusionEntities.size() > requestedRecords;

        if (hasNextPage) {
            counterRecordConclusionEntities.remove(counterRecordConclusionEntities.size() - 1);
        }

        List<CounterRecordDto> counterRecords = converter.conclusionViewToDto(counterRecordConclusionEntities);

        PaginatedCounterRecordsDto paginatedCounterRecords = new PaginatedCounterRecordsDto();
        paginatedCounterRecords.setHasNextPage(hasNextPage);
        paginatedCounterRecords.setCounterRecords(counterRecords);

        return paginatedCounterRecords;
    }

    @Override
    public PaginatedCounterRecordsDto getFilteredAndPaginated(Filter filterDto) {
        int requestedRecords = filterDto.getTake();
        filterDto.setTake(filterDto.getTake() + 1);

        List<CounterRecordEntity> counterRecordEntities = repository.getFilteredAndPaginated(filterDto);
        boolean hasNextPage = counterRecordEntities.size() > requestedRecords;

        if (hasNextPage) {
            counterRecordEntities.remove(counterRecordEntities.size() - 1);
        }

        List<CounterRecordDto> counterRecords = converter.toDto(counterRecordEntities);

        PaginatedCounterRecordsDto paginatedCounterRecords = new PaginatedCounterRecordsDto();
        paginatedCounterRecords.setHasNextPage(hasNextPage);
        paginatedCounterRecords.setCounterRecords(counterRecords);

        return paginatedCounterRecords;
    }

    private boolean isValid(PlcMqttDto equipmentCounts) {

        Optional<CountingEquipmentDto> countingEquipmentOpt =
                countingEquipmentService.findByCode(equipmentCounts.getEquipmentCode());

        return countingEquipmentOpt.isPresent() &&
                equipmentCounts.getCounters().length == countingEquipmentOpt.get().getOutputs().size();
    }

    @Override
    public void processCounterRecord(PlcMqttDto equipmentCountsMqttDto) {

        if (!isValid(equipmentCountsMqttDto)) {
            log.warning(() -> String.format("Received counts are invalid either because no Counting Equipment was found " +
                            "with the code [%s] or because received equipment outputs number [%s] does not match " +
                            "the Counting Equipment outputs number", equipmentCountsMqttDto.getEquipmentCode(),
                    equipmentCountsMqttDto.getCounters().length));
            return;
        }

        List<CounterRecordEntity> counterRecords = new ArrayList<>(equipmentCountsMqttDto.getCounters().length);
        for (CounterMqttDto counterMqttDto : equipmentCountsMqttDto.getCounters()) {
            CounterRecordEntity counterRecord = extractCounterRecordEntity(counterMqttDto, equipmentCountsMqttDto);
            counterRecords.add(counterRecord);
        }

        saveAll(counterRecords);
    }

    private CounterRecordEntity extractCounterRecordEntity(CounterMqttDto counterDto, PlcMqttDto equipmentCountsDto) {

        CounterRecordEntity counterRecord = new CounterRecordEntity();
        counterRecord.setRegisteredAt(new Date());
        counterRecord.setRealValue(counterDto.getValue());
        counterRecord.setActiveTime(equipmentCountsDto.getActiveTime());

        setEquipmentOutput(counterRecord, counterDto.getOutputCode());
        setProductionOrder(counterRecord, equipmentCountsDto.getProductionOrderCode());
        setComputedValue(counterRecord);

        return counterRecord;
    }

    private void setEquipmentOutput(CounterRecordEntity counterRecord, String equipmentOutputCode) {

        Optional<EquipmentOutputDto> equipmentOutputOpt = equipmentOutputService.findByCode(equipmentOutputCode);
        if (equipmentOutputOpt.isEmpty()) {
            log.warning(() -> String.format("No Equipment Output found with the code [%s]", equipmentOutputCode));
            return;
        }

        EquipmentOutputDto equipmentOutput = equipmentOutputOpt.get();
        EquipmentOutputEntity equipmentOutputEntity = new EquipmentOutputEntity();
        equipmentOutputEntity.setId(equipmentOutput.getId());
        counterRecord.setEquipmentOutput(equipmentOutputEntity);
        counterRecord.setEquipmentOutputAlias(equipmentOutput.getAlias().getAlias());
        counterRecord.setIsValidForProduction(equipmentOutput.isValidForProduction());
    }

    private void setProductionOrder(CounterRecordEntity counterRecord, String productionOrderCode) {
        Optional<ProductionOrderDto> productionOrderOpt = productionOrderService.findDtoByCode(productionOrderCode);
        if (productionOrderOpt.isEmpty()) {
            log.warning(() -> String.format("No Production Order found with the code [%s]", productionOrderCode));
            return;
        }

        ProductionOrderEntity productionOrderEntity = new ProductionOrderEntity();
        productionOrderEntity.setId(productionOrderOpt.get().getId());
        counterRecord.setProductionOrder(productionOrderEntity);
    }

    private void setComputedValue(CounterRecordEntity receivedCount) {
        Optional<CounterRecordEntity> lastPersistedCountOpt = findLastPersistedCount(receivedCount);

        if (lastPersistedCountOpt.isPresent()) {
            setComputedValue(receivedCount, lastPersistedCountOpt.get());
        } else {
            handleMissingLastPersistedCount(receivedCount);
        }
    }

    private void setComputedValue(CounterRecordEntity receivedCount, CounterRecordEntity lastPersistedCount) {
        calculateCountComputedValue(receivedCount, lastPersistedCount);
        calculateActiveComputedValue(receivedCount, lastPersistedCount);
    }

    private void calculateCountComputedValue(CounterRecordEntity receivedCount, CounterRecordEntity lastPersistedCount) {
        int computedValue = calculate(lastPersistedCount.getRealValue(), receivedCount.getRealValue(),
                lastPersistedCount.getComputedValue());
        receivedCount.setComputedValue(computedValue);
        int increment = calculateIncrement(lastPersistedCount, receivedCount);
        receivedCount.setIncrement(increment);
    }

    private void calculateActiveComputedValue(CounterRecordEntity receivedCount, CounterRecordEntity lastPersistedCount) {
        int updatedComputedActiveTime = calculate(lastPersistedCount.getActiveTime(), receivedCount.getActiveTime(),
                lastPersistedCount.getComputedActiveTime());
        receivedCount.setComputedActiveTime(updatedComputedActiveTime);
        int incrementActiveTime = calculateIncrementActiveTime(lastPersistedCount, receivedCount);
        receivedCount.setIncrementActiveTime(incrementActiveTime);
    }

    private void handleMissingLastPersistedCount(CounterRecordEntity receivedCount) {
        receivedCount.setComputedValue(INITIAL_VALUE);
        receivedCount.setComputedActiveTime(INITIAL_VALUE);
        receivedCount.setIncrement(INITIAL_VALUE);
        receivedCount.setIncrementActiveTime(INITIAL_VALUE);
    }

    private int calculate(int lastPersistedCount, int receivedCount, int computedPersisted) {

        if (isRollover(lastPersistedCount, receivedCount)) {
            return calculateRollover(lastPersistedCount, receivedCount, computedPersisted);
        }

        return increment(lastPersistedCount, receivedCount, computedPersisted);
    }

    private boolean isRollover(int lastPersistedCount, int receivedCount) {
        return receivedCount < lastPersistedCount;
    }

    private int calculateRollover(int lastPersistedCount, int receivedCount, int computedPersisted) {
        int totalIncrement = rolloverCalculateIncrement(lastPersistedCount, receivedCount);
        return computedPersisted + totalIncrement;
    }

    private int rolloverCalculateIncrement(int lastPersistedCount, int receivedCount) {
        int incrementBeforeOverflow = ROLLOVER_MAX_VALUE - lastPersistedCount;
        return incrementBeforeOverflow + ROLLOVER_OFFSET + receivedCount;
    }

    private int increment(int lastPersistedCount, int receivedCount, int computedPersisted) {
        int increment = computeValueIncrement(lastPersistedCount, receivedCount);
        return computedPersisted + increment;
    }

    private int computeValueIncrement(int lastPersistedCount, int receivedCount) {
        return receivedCount - lastPersistedCount;
    }

    private int calculateIncrement(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        if (lastPersistedCount.getComputedValue() > receivedCount.getComputedValue()) {
            return 0;
        }

        return computeValueIncrement(lastPersistedCount.getComputedValue(), receivedCount.getComputedValue());
    }

    private int calculateIncrementActiveTime(CounterRecordEntity lastPersistedCount, CounterRecordEntity receivedCount) {
        if (lastPersistedCount.getComputedActiveTime() > receivedCount.getComputedActiveTime()) {
            return 0;
        }

        return computeValueIncrement(lastPersistedCount.getComputedActiveTime(), receivedCount.getComputedActiveTime());
    }

    private Optional<CounterRecordEntity> findLastPersistedCount(CounterRecordEntity counterRecord) {
        if (counterRecord.getProductionOrder() == null || counterRecord.getEquipmentOutput() == null) {
            return Optional.empty();
        }

        return repository.findLastByProductionOrderId(counterRecord.getProductionOrder().getId(),
                counterRecord.getEquipmentOutput().getId());
    }

    public boolean areValidInitialCounts(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderOpt.isPresent() &&
                repository.findLastByProductionOrderId(productionOrderOpt.get().getId()).isEmpty();
    }

    public boolean areValidContinuationCounts(String productionOrderCode) {
        Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findByCode(productionOrderCode);
        return productionOrderOpt.isPresent() &&
                repository.findLastByProductionOrderId(productionOrderOpt.get().getId()).isPresent();
    }

    @Override
    public Integer sumValidCounterIncrement(Long countingEquipmentId, FilterDto filter) {
        return repository.sumValidCounterIncrement(countingEquipmentId, filter);
    }

    @Override
    public Integer sumCounterIncrement(Long countingEquipmentId, FilterDto filter) {
        return repository.sumCounterIncrement(countingEquipmentId, filter);
    }

    @Override
    public Integer sumIncrementActiveTimeByProductionOrderId(Long productionOrderId, Long equipmentOutputId, Timestamp startDate,
                                                             Timestamp endDate) {

        if (productionOrderId == null) {
            throw new IllegalArgumentException("Production order cannot be null");
        }

        Integer activeTime = repository.sumIncrementActiveTimeByProductionOrderId(productionOrderId, equipmentOutputId,
                startDate, endDate);
        return Optional.ofNullable(activeTime).orElse(0);
    }

    @Override
    public Timestamp getLastRegisteredAtByProductionOrderId(Long productionOrderId) {
        return repository.findLatestRegisteredAtByProductionOrderId(productionOrderId);
    }

    private void saveAll(List<CounterRecordEntity> counterRecords) {
        repository.saveAll(counterRecords);
    }

    @Override
    public void validateProductionOrder(String equipmentCode, String productionOrderCode) {
        if ((productionOrderCode == null || productionOrderCode.isEmpty()) &&
                productionOrderService.hasActiveProductionOrderByEquipmentCode(equipmentCode)) {

            log.info("Production Order is empty or null, but Equipment has Active PO");
            Optional<ProductionOrderEntity> productionOrderOpt = productionOrderRepository.findActiveByEquipmentCode(equipmentCode);

            log.info("Production Order was find in Equipment by findActiveByEquipmentCode method");
            productionOrderOpt.ifPresent(productionOrder -> {
                if (!repository.hasIncrementByProductionOrderCode(productionOrder.getCode())) {
                    Long productionOrderId = productionOrder.getId();
                    boolean hasCounterRecords = repository.existsByProductionOrderId(productionOrderId);

                    if (hasCounterRecords) {
                        repository.deleteByProductionOrderId(productionOrder.getId());
                    }

                    productionOrderRepository.delete(productionOrder);
                }
            });
        }
    }
}