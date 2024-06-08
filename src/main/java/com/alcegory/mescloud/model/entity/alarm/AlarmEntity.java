package com.alcegory.mescloud.model.entity.alarm;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity(name = "alarm")
public class AlarmEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alarm_configuration_id")
    private AlarmConfigurationEntity alarmConfiguration;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    private CountingEquipmentEntity equipment;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date recognizedAt;
    @ManyToOne
    @JoinColumn(name = "recognized_by")
    private UserEntity recognizedBy;
}
