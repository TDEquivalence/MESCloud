package com.alcegory.mescloud.model.entity;

import com.alcegory.mescloud.constant.AlarmStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "alarm_record")
public class AlarmRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alarm_id")
    private AlarmEntity alarm;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_order_id")
    private ProductionOrderEntity productionOrder;
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;
    private String comment;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;
    @ManyToOne
    @JoinColumn(name = "completed_by")
    private UserEntity completedBy;
}
