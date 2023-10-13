package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import com.alcegory.mescloud.service.AlarmRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alarm")
@AllArgsConstructor
public class AlarmRecordController {

    private final AlarmRecordService alarmRecordService;

    @PostMapping
    public ResponseEntity<List<AlarmRecordDto>> findAlarmRecords(AlarmRecordFilter filter) {
        List<AlarmRecordDto> alarmRecords = alarmRecordService.findAlarmRecords(filter);
        return new ResponseEntity<>(alarmRecords, HttpStatus.OK);
    }
}
