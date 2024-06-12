package com.alcegory.mescloud.repository.topic;

import com.alcegory.mescloud.model.entity.topic.TopicEntity;
import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

    List<TopicSummaryEntity> findAllSummary();

    List<String> getAllPlcTopics();

    List<String> getAllBackendTopics();

    List<String> getAllTopics();
}
