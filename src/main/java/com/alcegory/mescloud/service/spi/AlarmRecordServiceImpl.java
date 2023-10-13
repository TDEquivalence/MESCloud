package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.GenericConverter;
import com.alcegory.mescloud.model.dto.AlarmRecordDto;
import com.alcegory.mescloud.model.entity.AlarmRecordEntity;
import com.alcegory.mescloud.model.filter.AlarmRecordFilter;
import com.alcegory.mescloud.repository.AlarmRecordRepository;
import com.alcegory.mescloud.service.AlarmRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Log
public class AlarmRecordServiceImpl implements AlarmRecordService {

    private GenericConverter<AlarmRecordEntity, AlarmRecordDto> converter;
    private AlarmRecordRepository repository;

    @Override
    public List<AlarmRecordDto> findAlarmRecords(AlarmRecordFilter filter) {
        List<AlarmRecordEntity> alarmRecords = repository.findAll();
        return converter.toDto(alarmRecords, AlarmRecordDto.class);
    }
}
