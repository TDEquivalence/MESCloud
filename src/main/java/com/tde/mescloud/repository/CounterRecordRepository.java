package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.CounterRecordFilterDto;
import com.tde.mescloud.model.dto.CounterRecordSearchDto;
import com.tde.mescloud.model.dto.CounterRecordSortDto;
import com.tde.mescloud.model.entity.CounterRecordEntity;
import com.tde.mescloud.model.entity.CountingEquipmentEntity;
import com.tde.mescloud.model.entity.EquipmentOutputEntity;
import com.tde.mescloud.model.entity.ProductionOrderEntity;
import com.tde.mescloud.utility.SpringContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CounterRecordRepository extends CrudRepository<CounterRecordEntity, Long> {

    List<CounterRecordEntity> findByProductionOrderId(Long productionOrderId);

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId AND cr.equipment_output_id = :equipmentOutputId) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLast(Long productionOrderId, Long equipmentOutputId);

    @Query(value = "SELECT * FROM counter_record cr WHERE (cr.production_order_id = :productionOrderId) LIMIT 1", nativeQuery = true)
    Optional<CounterRecordEntity> findLast(Long productionOrderId);

    //TODO: Consider removing to either a different interface || repositoryImpl
    default List<CounterRecordEntity> findByCriteria(CounterRecordFilterDto filterDto) {
        EntityManager entityManager = SpringContext.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CounterRecordEntity> counterRecordCriteriaQuery = criteriaBuilder.createQuery(CounterRecordEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        Root<CounterRecordEntity> counterRecordRoot = counterRecordCriteriaQuery.from(CounterRecordEntity.class);

        for (CounterRecordSearchDto search : filterDto.getSearch()) {
            Predicate predicate;
            switch (search.getId()) {
                case "computedValue" -> {
                    predicate = criteriaBuilder.equal(counterRecordRoot.get("computedValue"),
                            search.getValue());
                }
                case "dateStart" -> {
                    ZonedDateTime date = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get("registeredAt"), date);
                }
                case "dateEnd" -> {
                    ZonedDateTime date = ZonedDateTime.parse(search.getValue());
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get("registeredAt"), date);
                }
                default -> {
                    Path path = getPath(counterRecordRoot, search.getId());
                    predicate = criteriaBuilder.like(criteriaBuilder.upper(path),
                            "%" + search.getValue().toUpperCase() + "%");
                }
            }
            predicates.add(predicate);
        }

        for (CounterRecordSortDto sort : filterDto.getSort()) {
            Order order = sort.isDesc() ?
                    criteriaBuilder.desc(getPath(counterRecordRoot, sort.getId())) :
                    criteriaBuilder.asc(getPath(counterRecordRoot, sort.getId()));
            orders.add(order);
        }

        Order defaultOrder = criteriaBuilder.desc(getPath(counterRecordRoot, "registeredAt"));
        orders.add(defaultOrder);

        counterRecordCriteriaQuery
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(orders);

        TypedQuery<CounterRecordEntity> query = entityManager
                .createQuery(counterRecordCriteriaQuery)
                .setFirstResult(filterDto.getSkip())
                .setMaxResults(filterDto.getTake());
        return query.getResultList();
    }

    //TODO: Refactor to proper code, consider using a map
    private Path<? super CounterRecordEntity> getPath(Root<CounterRecordEntity> counterRecordRoot, String property) {
        switch (property) {
            case "equipmentAlias" -> {
                Join<CounterRecordEntity, EquipmentOutputEntity> equipementOutputJoin =
                        counterRecordRoot.join("equipmentOutput");
                Join<EquipmentOutputEntity, CountingEquipmentEntity> countingEquipmentJoin =
                        equipementOutputJoin.join("countingEquipment");
                return countingEquipmentJoin.get("alias");
            }
            case "productionOrderCode" -> {
                Join<CounterRecordEntity, ProductionOrderEntity> productionOrderJoin =
                        counterRecordRoot.join("productionOrder");
                return productionOrderJoin.get("code");
            }
            default -> {
                return counterRecordRoot.get(property);
            }
        }
    }
}