package com.alcegory.mescloud.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "composed_summary")
@Getter
@Setter
public class ComposedSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
    private String code;
    private Timestamp createdAt;
    private Timestamp approvedAt;
    private Timestamp hitInsertedAt;
    private Integer sampleAmount;
    private Double reliability;
    private Boolean isBatchApproved;
    private String batchCode;
    private Integer amountOfHits;
    private Integer validAmount;
}
