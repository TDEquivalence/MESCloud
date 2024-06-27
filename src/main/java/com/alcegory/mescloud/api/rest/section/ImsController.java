package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.model.dto.ImsDto;
import com.alcegory.mescloud.service.equipment.ImsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class ImsController extends SectionBaseController {

    private static final String IMS_URL = "/ims";

    private ImsService imsService;

    @PostMapping(IMS_URL)
    public ResponseEntity<ImsDto> create(@RequestBody ImsDto imsDto) {
        try {
            if (imsDto == null) {
                return ResponseEntity.badRequest().build();
            }

            ImsDto createdIms = imsService.create(imsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(IMS_URL)
    public ResponseEntity<List<ImsDto>> findAll() {
        try {
            List<ImsDto> imsDtos = imsService.getAll();
            return ResponseEntity.ok(imsDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
