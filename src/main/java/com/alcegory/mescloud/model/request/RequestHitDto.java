package com.alcegory.mescloud.model.request;

import com.alcegory.mescloud.model.dto.HitDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestHitDto {

    private List<HitDto> hits;
}
