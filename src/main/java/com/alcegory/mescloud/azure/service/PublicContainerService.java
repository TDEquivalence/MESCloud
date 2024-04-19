package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ImageInfoDto;

import java.util.List;

public interface PublicContainerService {

    List<ImageInfoDto> getAllImageReference();

    ImageInfoDto getImageReference();

    ImageInfoDto getRandomImageReference();

    void deleteBlob(String blobName);

    String getSasToken();
}
