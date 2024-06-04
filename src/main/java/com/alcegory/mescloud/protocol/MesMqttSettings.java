package com.alcegory.mescloud.protocol;

import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;
import com.alcegory.mescloud.service.topic.TopicService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@AllArgsConstructor
public class MesMqttSettings {

    private static final String BE_DEVICE = "/BE";
    private static final String PLC_DEVICE = "/PLC";
    private static final String DELIMITER = "/";

    private final TopicService topicService;
    private List<String> backendTopics;
    private Map<String, List<String>> companySectionTopicsMap;

    @PostConstruct
    private void initTopics() {
        List<TopicSummaryEntity> topics = topicService.getAllTopics();
        backendTopics = new ArrayList<>();
        companySectionTopicsMap = new HashMap<>();

        for (TopicSummaryEntity topic : topics) {
            if (topic.getBackendTopic().endsWith(BE_DEVICE)) {
                backendTopics.add(topic.getBackendTopic());
            }

            String companyAndSectionKey = topic.getCompanyPrefix() + DELIMITER + topic.getSectionPrefix();
            companySectionTopicsMap.computeIfAbsent(companyAndSectionKey, k -> new ArrayList<>()).add(topic.getPlcTopic());
            companySectionTopicsMap.computeIfAbsent(companyAndSectionKey, k -> new ArrayList<>()).add(topic.getBackendTopic());
        }
    }

    public List<String> getAllBackendTopics() {
        return backendTopics;
    }

    public String getPlcTopicByCompanyAndSection(String companyPrefix, String sectionPrefix) {
        String key = companyPrefix + DELIMITER + sectionPrefix;
        List<String> topics = companySectionTopicsMap.get(key);
        if (topics != null && !topics.isEmpty()) {
            for (String topic : topics) {
                if (topic.endsWith(PLC_DEVICE)) {
                    return topic;
                }
            }
        }
        return null;
    }

    public String getOppositeTopic(String topic) {
        if (topic.endsWith(PLC_DEVICE)) {
            return topic.replace(PLC_DEVICE, BE_DEVICE);
        } else if (topic.endsWith(BE_DEVICE)) {
            return topic.replace(BE_DEVICE, PLC_DEVICE);
        }
        return null;
    }
}
