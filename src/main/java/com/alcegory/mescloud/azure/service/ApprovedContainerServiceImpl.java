package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobItem;
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
            ImageAnnotationDto imageAnnotationDto = containerInfoDto.getImageAnnotationDto();
            String blobName = imageAnnotationDto.getData().getImage();
            String jsonContent = convertImageAnnotationDtoToJson(imageAnnotationDto);

            uploadJsonBlob(blobName, jsonContent);
            uploadJpgImage(containerInfoDto.getJpg().getPath());
        } catch (IOException e) {
            log.error("Error saving to approved container", e);
        }
    }

    private String convertImageAnnotationDtoToJson(ImageAnnotationDto imageAnnotationDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(imageAnnotationDto);
    }

    private void uploadJsonBlob(String jsonBlobUrl, String jsonContent) throws IOException {
        uploadBlob(jsonBlobUrl, jsonContent.getBytes());
        log.info("ImageAnnotationDto JSON saved to the approved container successfully");
    }

    private void uploadJpgImage(String jpegUrl) throws IOException {
        uploadBlob(jpegUrl, new byte[0]);
        log.info("JPEG image saved to the approved container successfully");
    }

    private void uploadBlob(String blobName, byte[] content) throws IOException {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(accountUrl)
                .sasToken(sasToken)
                .buildClient();
        BlobContainerClient approvedContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = approvedContainerClient.getBlobClient(blobName);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            blobClient.upload(inputStream, content.length, true);
            log.info("Blob '{}' uploaded successfully.", blobName);
        }
    }

    @Override
    public void deleteAllBlobsInContainer() {
        BlobContainerClient containerClient = getBlobContainerClient();
        for (BlobItem blobItem : containerClient.listBlobs()) {
            BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());
            try {
                blobClient.delete();
                log.info("Blob '{}' deleted successfully.", blobItem.getName());
            } catch (Exception e) {
                log.error("Error deleting blob '{}': {}", blobItem.getName(), e.getMessage());
            }
        }
    }

    private BlobContainerClient getBlobContainerClient() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        return new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();
    }
}
