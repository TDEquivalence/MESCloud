package com.alcegory.mescloud.azure.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainerInfoUpdate {

    private String fileName;
    private String classification;
    private boolean userApproval;
    private List<String> rejection;
    private String comments;
    private String mesUserDecision;
}
