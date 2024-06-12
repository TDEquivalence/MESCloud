package com.alcegory.mescloud.service.topic;

import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;
import com.alcegory.mescloud.repository.topic.TopicRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public List<TopicSummaryEntity> getAllTopics() {
        return topicRepository.findAllSummary();
    }

    @Override
    public List<String> getAllPlcTopics() {
        return topicRepository.getAllPlcTopics();
    }

    @Override
    public List<String> getAllBackendTopics() {
        return topicRepository.getAllBackendTopics();
    }
}
