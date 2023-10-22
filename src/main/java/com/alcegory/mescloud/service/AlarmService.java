package com.alcegory.mescloud.service;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.exception.AlarmConfigurationNotFoundException;
import com.alcegory.mescloud.exception.AlarmNotFoundException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.dto.AlarmDto;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.dto.RequestAlarmRecognitionDto;
import com.alcegory.mescloud.model.entity.AlarmCounts;
import com.alcegory.mescloud.model.filter.AlarmFilter;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AlarmService {

    List<AlarmDto> findByFilter(AlarmFilter filter);

    List<AlarmDto> findByEquipmentIdAndStatus(Long equipmentId, AlarmStatus status);

    AlarmDto recognizeAlarm(Long alarmId, RequestAlarmRecognitionDto alarmRecognition, Authentication authentication)
            throws AlarmNotFoundException, IllegalAlarmStatusException;

    AlarmCounts getAlarmCounts(AlarmFilter filter);

    void processAlarms(PlcMqttDto plcMqttDto) throws AlarmConfigurationNotFoundException, EquipmentNotFoundException;
}
