package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.winnow.Searchable;
import com.tde.mescloud.model.dto.winnow.Sortable;
import com.tde.mescloud.model.dto.winnow.WinnowProperty;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractWinnowRepository {

    public static final String JAKARTA_FETCHGRAPH = "jakarta.persistence.fetchgraph";
    public static final String SQL_WILDCARD = "%";

    @Autowired
    protected EntityManager entityManager;
    protected Map<String, Function<Root<?>, Path<?>>> pathByJointProperty = new HashMap<>();

    @PostConstruct
    protected <T> void populatePathByJointProperty() {
    }

    protected <T extends WinnowProperty> void addPredicates(Searchable<T> filter,
                                                            List<Predicate> predicates,
                                                            CriteriaBuilder criteriaBuilder,
                                                            Root<?> counterRecordRoot) {

        for (T searchProperty : filter.getSearch().getKeys()) {
            Predicate predicate;

            switch (searchProperty.getDataTypeOperation()) {

                case INTEGER_GREATER_OR_EQUAL -> {
                    int computedValue = Integer.parseInt(filter.getSearch().getValue(searchProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(searchProperty.getEntityProperty()), computedValue);
                }

                case DATE_GREATER_OR_EQUAL -> {
                    ZonedDateTime dateStart = ZonedDateTime.parse(filter.getSearch().getValue(searchProperty));
                    predicate = criteriaBuilder.greaterThanOrEqualTo(counterRecordRoot.get(searchProperty.getEntityProperty()), dateStart);
                }

                case DATE_LESS_OR_EQUAL -> {
                    ZonedDateTime dateEnd = ZonedDateTime.parse(filter.getSearch().getValue(searchProperty));
                    predicate = criteriaBuilder.lessThanOrEqualTo(counterRecordRoot.get(searchProperty.getEntityProperty()), dateEnd);
                }

                default -> {
                    Path<?> path = getPath(counterRecordRoot, searchProperty.getEntityProperty());
                    String value = filter.getSearch().getValue(searchProperty).toUpperCase();
                    predicate = createLikePredicate(path, value, criteriaBuilder);
                }
            }

            predicates.add(predicate);
        }
    }

    protected Predicate createLikePredicate(Path<?> path, String value, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.upper(path.as(String.class)), value.toUpperCase());
    }

    protected <T extends WinnowProperty> void addSortOrders(Sortable<T> filter,
                                                            List<Order> orders,
                                                            CriteriaBuilder criteriaBuilder,
                                                            Root<?> counterRecordRoot) {

        for (T sortProperty : filter.getSort().getKeys()) {

            Order order = filter.getSort().isDescendingSort(sortProperty) ?
                    criteriaBuilder.desc(getPath(counterRecordRoot, sortProperty.getEntityProperty())) :
                    criteriaBuilder.asc(getPath(counterRecordRoot, sortProperty.getEntityProperty()));
            orders.add(order);
        }
    }

    protected <T> Path<?> getPath(Root<T> root, String property) {

        if (pathByJointProperty.containsKey(property)) {
            return pathByJointProperty.get(property).apply(root);
        }

        return root.get(property);
    }
}