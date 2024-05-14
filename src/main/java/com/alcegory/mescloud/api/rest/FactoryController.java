package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.company.CompanyDto;
import com.alcegory.mescloud.service.company.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/company")
@AllArgsConstructor
public class FactoryController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable("id") Long id) {
        try {
            CompanyDto companyDtoOptional = companyService.getCompanyDtoById(id);
            if (companyDtoOptional != null) {
                return ResponseEntity.ok(companyDtoOptional);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/save")
    public ResponseEntity<CompanyDto> saveCompany(@RequestBody CompanyDto companyDto) {
        try {
            if (companyDto == null) {
                return ResponseEntity.badRequest().build();
            }

            CompanyDto savedCompanyDto = companyService.saveCompany(companyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCompanyDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
