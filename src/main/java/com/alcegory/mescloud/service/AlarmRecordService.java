package com.alcegory.mescloud.service;

import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;

import java.util.List;

public interface AlarmRecordService {

    List<AlarmRecordDto> findAlarmRecords(AlarmRecordFilter filter);
}
