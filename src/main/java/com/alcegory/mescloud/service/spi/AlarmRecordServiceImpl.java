package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.constant.AlarmStatus;
import com.alcegory.mescloud.exception.AlarmRecordNotFoundException;
import com.alcegory.mescloud.exception.IllegalAlarmStatusException;
import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.dto.RequestAlarmRecordRecognizeDto;
import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import com.alcegory.mescloud.repository.AlarmRecordRepository;
import com.alcegory.mescloud.service.AlarmRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log
public class AlarmRecordServiceImpl implements AlarmRecordService {

    private GenericConverter<AlarmRecordEntity, AlarmRecordDto> converter;
    private AlarmRecordRepository repository;

    @Override
    public List<AlarmRecordDto> findAlarmRecords(AlarmRecordFilter filter) {
        List<AlarmRecordEntity> alarmRecords = repository.findAllByFilter(filter);
        return converter.toDto(alarmRecords, AlarmRecordDto.class);
    }

    @Override
    public AlarmRecordDto recognizeAlarmRecord(Long alarmRecordId,
                                               RequestAlarmRecordRecognizeDto alarmRecordRecognizeRequest,
                                               Authentication authentication) {

        Optional<AlarmRecordEntity> alarmRecordOpt = repository.findById(alarmRecordId);
        if (alarmRecordOpt.isEmpty()) {
            String msg = String.format("Unable to recognize alarm record: no alarm found with id [%s]", alarmRecordId);
            log.warning(msg);
            throw new AlarmRecordNotFoundException(msg);
        }

        AlarmRecordEntity alarmRecordToUpdate = alarmRecordOpt.get();
        if (AlarmStatus.RECOGNIZED.equals(alarmRecordToUpdate.getStatus()) || AlarmStatus.ACTIVE.equals(alarmRecordToUpdate.getStatus())) {
            String msg = String.format("Unable to recognize alarm record: current alarm record status is [%s]", alarmRecordToUpdate.getStatus().name());
            log.warning(msg);
            throw new IllegalAlarmStatusException(msg);
        }

        UserEntity user = (UserEntity) authentication.getPrincipal();
        alarmRecordToUpdate.setCompletedBy(user);
        alarmRecordToUpdate.setCompletedAt(new Date());
        alarmRecordToUpdate.setComment(alarmRecordRecognizeRequest.getComment());
        alarmRecordToUpdate.setStatus(AlarmStatus.RECOGNIZED);

        AlarmRecordEntity updatedAlarmRecord = repository.save(alarmRecordToUpdate);
        return converter.toDto(updatedAlarmRecord, AlarmRecordDto.class);
    }
}
