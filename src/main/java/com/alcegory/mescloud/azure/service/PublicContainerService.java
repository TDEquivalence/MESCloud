package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;

import java.util.List;

public interface PublicContainerService {

    List<ImageInfoDto> getAllImageReference();

    ImageInfoDto getRandomImageReference();

    void deleteBlob(String blobName);

    String getSasToken();
}
