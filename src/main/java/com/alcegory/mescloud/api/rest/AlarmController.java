package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.exception.AlarmNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.dto.AlarmCountsDto;
import com.alcegory.mescloud.model.dto.AlarmDto;
import com.alcegory.mescloud.model.entity.AlarmSummaryEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestAlarmRecognitionDto;
import com.alcegory.mescloud.service.AlarmService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarm")
@AllArgsConstructor
public class AlarmController {

    private final AlarmService service;

    @PostMapping
    public ResponseEntity<List<AlarmSummaryEntity>> findByFilter(@RequestBody Filter filter) {
        List<AlarmSummaryEntity> alarmRecords = service.findByFilter(filter);
        return new ResponseEntity<>(alarmRecords, HttpStatus.OK);
    }

    @PutMapping("/{alarmId}/recognize")
    public ResponseEntity<AlarmDto> recognizeAlarmRecord(@PathVariable Long alarmId,
                                                         @RequestBody RequestAlarmRecognitionDto alarmRecognition,
                                                         Authentication authentication) {
        try {
            AlarmDto updatedAlarmRecord = service.recognizeAlarm(alarmId, alarmRecognition, authentication);
            return new ResponseEntity<>(updatedAlarmRecord, HttpStatus.OK);

        } catch (AlarmNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAlarmStatusException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/counts")
    public ResponseEntity<AlarmCountsDto> getAlarmCounts(@RequestBody Filter filter) {
        AlarmCountsDto alarmCounts = service.getAlarmCounts(filter);
        return new ResponseEntity<>(alarmCounts, HttpStatus.OK);
    }
}