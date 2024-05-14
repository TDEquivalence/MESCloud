package com.alcegory.mescloud.model.entity.equipment;

import com.alcegory.mescloud.constant.EquipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "equipment_status_record")
@Getter
@Setter
public class EquipmentStatusRecordEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private CountingEquipmentEntity countingEquipment;
    private EquipmentStatus equipmentStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp registeredAt;
}
