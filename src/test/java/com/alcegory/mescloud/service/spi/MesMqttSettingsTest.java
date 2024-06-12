package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.entity.topic.TopicSummaryEntity;
import com.alcegory.mescloud.protocol.MesMqttSettings;
import com.alcegory.mescloud.service.topic.TopicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesMqttSettingsTest {

    @Mock
    private TopicService topicService;

    @InjectMocks
    private MesMqttSettings mesMqttSettings;

    @BeforeEach
    public void setUp() {
        // Initialize mock topics
        List<TopicSummaryEntity> mockTopics = Arrays.asList(
                createMockTopic("company1", "section1", "topic1/BE", "topic1/PLC"),
                createMockTopic("company2", "section2", "topic2/BE", "topic2/PLC")
        );

        // Stub the topicService to return mock topics
        when(topicService.getAllTopics()).thenReturn(mockTopics);
    }

    @Test
    void testInitTopics() {
        // Call method to initialize topics
        mesMqttSettings.initTopics();

        // Assertions
        Assertions.assertNotNull(mesMqttSettings.getBackendTopics());
        Assertions.assertEquals(2, mesMqttSettings.getBackendTopics().size());
        Assertions.assertTrue(mesMqttSettings.getBackendTopics().contains("topic1/BE"));
        Assertions.assertTrue(mesMqttSettings.getBackendTopics().contains("topic2/BE"));
    }

    private TopicSummaryEntity createMockTopic(String companyPrefix, String sectionPrefix, String backendTopic, String plcTopic) {
        TopicSummaryEntity topic = new TopicSummaryEntity();
        topic.setCompanyPrefix(companyPrefix);
        topic.setSectionPrefix(sectionPrefix);
        topic.setBackendTopic(backendTopic);
        topic.setPlcTopic(plcTopic);
        return topic;
    }
}