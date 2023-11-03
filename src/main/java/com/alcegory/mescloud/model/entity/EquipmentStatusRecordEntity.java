package com.alcegory.mescloud.model.entity;

import com.alcegory.mescloud.constant.EquipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity(name = "equipment_status_record")
@Getter
@Setter
public class EquipmentStatusRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private CountingEquipmentEntity countingEquipment;
    private EquipmentStatus equipmentStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp registeredAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp activeTime;
}
