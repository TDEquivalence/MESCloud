package com.alcegory.mescloud.service.topic;

import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;

import java.util.List;

public interface TopicService {

    List<TopicSummaryEntity> getAllTopics();

    List<String> getAllPlcTopics();

    List<String> getAllBackendTopics();
}
