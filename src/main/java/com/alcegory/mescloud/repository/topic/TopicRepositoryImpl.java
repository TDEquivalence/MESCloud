package com.alcegory.mescloud.repository.topic;

import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class TopicRepositoryImpl {

    private static final String PLC_TOPIC = "plcTopic";
    private static final String BACKEND_TOPIC = "backendTopic";

    private final EntityManager entityManager;

    public List<TopicSummaryEntity> findAllSummary() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TopicSummaryEntity> criteriaQuery = criteriaBuilder.createQuery(TopicSummaryEntity.class);
        Root<TopicSummaryEntity> root = criteriaQuery.from(TopicSummaryEntity.class);
        criteriaQuery.select(root);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<String> getAllPlcTopics() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<TopicSummaryEntity> root = criteriaQuery.from(TopicSummaryEntity.class);
        criteriaQuery.select(root.get(PLC_TOPIC));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<String> getAllBackendTopics() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<TopicSummaryEntity> root = criteriaQuery.from(TopicSummaryEntity.class);
        criteriaQuery.select(root.get(BACKEND_TOPIC));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<String> getAllTopics() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<TopicSummaryEntity> root = criteriaQuery.from(TopicSummaryEntity.class);
        criteriaQuery.multiselect(root.get(PLC_TOPIC), root.get(BACKEND_TOPIC));

        List<Tuple> tuples = entityManager.createQuery(criteriaQuery).getResultList();

        List<String> allTopics = new ArrayList<>();
        for (Tuple tuple : tuples) {
            allTopics.add(tuple.get(0, String.class));
            allTopics.add(tuple.get(1, String.class));
        }

        return allTopics;
    }
}
