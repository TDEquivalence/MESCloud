package com.alcegory.mescloud.model.entity.composed;

import com.alcegory.mescloud.model.converter.InstructionsConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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

    @Convert(converter = InstructionsConverter.class)
    private List<Map<String, Object>> instructions;
}
