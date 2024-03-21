package com.alcegory.mescloud.azure.controller;

import com.alcegory.mescloud.azure.service.CorkDefectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cork-defect")
public class CorkDefectController {

    private final CorkDefectService corkDefectService;


    @GetMapping("/jpeg")
    public ResponseEntity<List<String>> getJpegImages() {
        List<String> imageUrls = corkDefectService.getJpegImageUrls();
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }
}
