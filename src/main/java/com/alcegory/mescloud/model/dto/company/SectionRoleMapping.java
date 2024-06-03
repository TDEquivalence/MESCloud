package com.alcegory.mescloud.model.dto.company;

import com.alcegory.mescloud.security.model.SectionRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRoleMapping {

    private Long sectionId;
    private SectionRole sectionRole;
}
