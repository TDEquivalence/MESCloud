package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.AlarmRecordNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.dto.RequestAlarmRecordRecognizeDto;
import com.alcegory.mescloud.model.entity.AlarmRecordCounts;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import com.alcegory.mescloud.service.AlarmRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarm-record")
@AllArgsConstructor
public class AlarmRecordController {

    private final AlarmRecordService service;

    @PostMapping
    public ResponseEntity<List<AlarmRecordDto>> findAlarmRecords(@RequestBody AlarmRecordFilter filter) {
        List<AlarmRecordDto> alarmRecords = service.findAlarmRecords(filter);
        return new ResponseEntity<>(alarmRecords, HttpStatus.OK);
    }

    @PutMapping("/{alarmRecordId}/recognize")
    public ResponseEntity<AlarmRecordDto> recognizeAlarmRecord(@PathVariable Long alarmRecordId,
                                                               @RequestBody RequestAlarmRecordRecognizeDto alarmRecordRecognizeRequest,
                                                               Authentication authentication) {
        try {
            AlarmRecordDto updatedAlarmRecord = service.recognizeAlarmRecord(alarmRecordId, alarmRecordRecognizeRequest, authentication);
            return new ResponseEntity<>(updatedAlarmRecord, HttpStatus.OK);

        } catch (AlarmRecordNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAlarmStatusException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/counts")
    public ResponseEntity<AlarmRecordCounts> getAlarmCounts(AlarmRecordFilter filter) {
        AlarmRecordCounts alarmRecordCounts = service.getAlarmCounts(filter);
        return new ResponseEntity<>(alarmRecordCounts, HttpStatus.OK);
    }
}