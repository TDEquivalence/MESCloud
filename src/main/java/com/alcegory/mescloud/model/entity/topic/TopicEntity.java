package com.alcegory.mescloud.model.entity.topic;

import com.alcegory.mescloud.model.entity.company.CompanyEntity;
import com.alcegory.mescloud.model.entity.company.SectionEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "topic")
public class TopicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private SectionEntity section;

    private String protocolName;

    public TopicEntity() {
    }

    public TopicEntity(CompanyEntity company, SectionEntity section, String protocolName) {
        this.company = company;
        this.section = section;
        this.protocolName = protocolName;
    }
}
