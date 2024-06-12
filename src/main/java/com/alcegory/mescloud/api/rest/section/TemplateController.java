package com.alcegory.mescloud.api.rest.section;

import com.alcegory.mescloud.api.rest.base.SectionBaseController;
import com.alcegory.mescloud.model.entity.equipment.TemplateEntity;
import com.alcegory.mescloud.service.production.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TemplateController extends SectionBaseController {

    private static final String TEMPLATE_URL = "/template";

    private final TemplateService templateService;

    @GetMapping(TEMPLATE_URL)
    public ResponseEntity<TemplateEntity> getTemplate() {
        try {
            TemplateEntity template = templateService.getTemplateWithFields();
            if (template != null) {
                return ResponseEntity.ok(template);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
