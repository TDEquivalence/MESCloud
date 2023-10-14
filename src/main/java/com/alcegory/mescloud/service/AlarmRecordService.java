package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.dto.RequestAlarmRecordRecognizeDto;
import com.alcegory.mescloud.model.entity.AlarmRecordCounts;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AlarmRecordService {

    List<AlarmRecordDto> findAlarmRecords(AlarmRecordFilter filter);

    AlarmRecordDto recognizeAlarmRecord(Long alarmRecordId, RequestAlarmRecordRecognizeDto alarmRecordRecognizeRequest, Authentication authentication);

    AlarmRecordCounts getAlarmCounts(AlarmRecordFilter filter);
}
