package com.alcegory.mescloud.api.rest;

import com.alcegory.mescloud.model.dto.CompanyDto;
import com.alcegory.mescloud.service.CompanyService;
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
        CompanyDto companyDto = companyService.getCompanyDtoById(id);
        if (companyDto != null) {
            return ResponseEntity.ok(companyDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<CompanyDto> saveComapny(@RequestBody CompanyDto companyDto) {
        CompanyDto savedCompanyDto = companyService.saveCompany(companyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompanyDto);
    }
}
