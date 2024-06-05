package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.exception.AlarmNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.dto.alarm.AlarmCountsDto;
import com.alcegory.mescloud.model.dto.alarm.AlarmDto;
import com.alcegory.mescloud.model.dto.pagination.PaginatedAlarmDto;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.model.request.RequestAlarmRecognitionDto;
import com.alcegory.mescloud.service.alarm.AlarmService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AlarmController extends SectionBaseController {

    private static final String ALARM_URL = "/alarm";

    private final AlarmService service;

    @PostMapping(ALARM_URL)
    public ResponseEntity<PaginatedAlarmDto> findByFilter(@PathVariable long sectionId, @RequestBody Filter filter) {
        try {
            PaginatedAlarmDto alarms = service.findByFilter(sectionId, filter);
            return new ResponseEntity<>(alarms, HttpStatus.OK);
        } catch (AlarmNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(ALARM_URL + "/{alarmId}/recognize")
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

    @PostMapping(ALARM_URL + "/counts")
    public ResponseEntity<AlarmCountsDto> getAlarmCounts(@RequestBody Filter filter) {
        try {
            AlarmCountsDto alarmCounts = service.getAlarmCounts(filter);
            return new ResponseEntity<>(alarmCounts, HttpStatus.OK);
        } catch (AlarmNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}