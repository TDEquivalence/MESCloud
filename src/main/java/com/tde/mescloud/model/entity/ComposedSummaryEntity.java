package com.tde.mescloud.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name = "composed_summary")
@Getter
@Setter
public class ComposedSummaryEntity {

    @Id
    private Integer id;
    private Date createdAt;
    private Integer amount;
    private Integer reliability;
    private String inputBatch;
    private String source;
    private String gauge;
    private String category;
    private String washingProcess;
//    private Boolean isBatchApproved;
}
