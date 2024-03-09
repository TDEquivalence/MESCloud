package com.alcegory.mescloud.service;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.exception.AlarmConfigurationNotFoundException;
import com.alcegory.mescloud.exception.AlarmNotFoundException;
import com.alcegory.mescloud.exception.EquipmentNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.dto.AlarmCountsDto;
import com.alcegory.mescloud.model.dto.AlarmDto;
import com.alcegory.mescloud.model.dto.PlcMqttDto;
import com.alcegory.mescloud.model.entity.AlarmSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestAlarmRecognitionDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AlarmService {

    List<AlarmSummaryEntity> findByFilter(Filter filter);

    List<AlarmDto> findByEquipmentIdAndStatus(Long equipmentId, AlarmStatus status);

    AlarmDto recognizeAlarm(Long alarmId, RequestAlarmRecognitionDto alarmRecognition, Authentication authentication)
            throws AlarmNotFoundException, IllegalAlarmStatusException;

    AlarmCountsDto getAlarmCounts(Filter filter);

    void processAlarms(PlcMqttDto plcMqttDto) throws AlarmConfigurationNotFoundException, EquipmentNotFoundException;
}
