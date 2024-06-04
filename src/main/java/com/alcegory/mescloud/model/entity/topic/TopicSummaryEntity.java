package com.alcegory.mescloud.model.entity.topic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "topic_summary")
public class TopicSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_prefix")
    private String companyPrefix;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "section_prefix")
    private String sectionPrefix;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "protocol_name")
    private String protocolName;

    @Column(name = "plc_topic")
    private String plcTopic;

    @Column(name = "backend_topic")
    private String backendTopic;
}
