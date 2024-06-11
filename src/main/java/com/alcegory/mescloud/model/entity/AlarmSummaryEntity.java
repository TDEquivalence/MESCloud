package com.alcegory.mescloud.model.entity;

import com.alcegory.mescloud.constant.AlarmStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "alarm_summary")
public class AlarmSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String configurationCode;
    private String configurationDescription;
    private String equipmentAlias;
    private String productionOrderCode;
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;
    private String comment;
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date recognizedAt;
    private String recognizedByFirstName;
    private String recognizedByLastName;
    private Long duration;
}
