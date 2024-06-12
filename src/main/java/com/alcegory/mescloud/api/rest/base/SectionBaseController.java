package com.alcegory.mescloud.api.rest.base;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/{companyPrefix}/{companyId}/{factoryPrefix}/{factoryId}/{sectionPrefix}/{sectionId}")
public abstract class SectionBaseController {

    protected SectionBaseController() {
        //Abstract method
    }
}
