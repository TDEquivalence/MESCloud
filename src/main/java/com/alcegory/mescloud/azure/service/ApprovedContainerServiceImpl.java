package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ApprovedContainerServiceImpl implements ApprovedContainerService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.approvedContainerName}")
    private String containerName;

    @Value("${azure.storage.approvedSasToken}")
    private String sasToken;

    @Override
    public List<ContainerInfoDto> getApprovedImageAnnotations() {
        return ContainerServiceUtil.getImageAnnotations(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public void saveToApprovedContainer(ContainerInfoDto containerInfoDto) {
        try {
            String jsonContent = serializeImageAnnotationToJson(containerInfoDto.getImageAnnotationDto());
            String jsonBlobUrl = containerInfoDto.getImageAnnotationDto().getData().getImage();
            uploadJsonBlob(jsonBlobUrl, jsonContent);
            uploadJpegImage(containerInfoDto.getJpeg().getJpeg());
        } catch (IOException e) {
            log.error("Error saving to approved container", e);
        }
    }

    private String serializeImageAnnotationToJson(ImageAnnotationDto imageAnnotationDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(imageAnnotationDto);
    }

    private void uploadJsonBlob(String jsonBlobUrl, String jsonContent) throws IOException {
        uploadBlob(jsonBlobUrl, jsonContent.getBytes());
        log.info("ImageAnnotationDto JSON saved to the approved container successfully");
    }

    private void uploadJpegImage(String jpegUrl) throws IOException {
        uploadBlob(jpegUrl, new byte[0]); // Placeholder byte array for JPEG content
        log.info("JPEG image saved to the approved container successfully");
    }

    private void uploadBlob(String blobUrl, byte[] content) throws IOException {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(accountUrl)
                .sasToken(sasToken)
                .buildClient();
        BlobContainerClient approvedContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = approvedContainerClient.getBlobClient(blobUrl);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            blobClient.upload(inputStream, content.length);
        }
    }
}
