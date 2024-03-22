package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PublicContainerServiceImpl implements PublicContainerService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.publicContainerName}")
    private String containerName;

    @Value("${azure.storage.publicSasToken}")
    private String sasToken;

    @Override
    public List<ContainerInfoDto> getImageAnnotations() {
        return ContainerServiceUtil.getImageAnnotations(
                accountUrl,
                containerName,
                sasToken
        );
    }
}
