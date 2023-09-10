package com.tde.mescloud.repository;

import com.tde.mescloud.model.dto.filter.Searchable;
import com.tde.mescloud.model.dto.filter.Sortable;
import com.tde.mescloud.model.dto.filter.WinnowProperty;
import jakarta.persistence.criteria.*;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class AbstractWinnowRepositoryImpl {

    public static final String JAKARTA_FETCHGRAPH = "jakarta.persistence.fetchgraph";
    public static final String SQL_WILDCARD = "%";


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
        return root.get(property);
    }
}