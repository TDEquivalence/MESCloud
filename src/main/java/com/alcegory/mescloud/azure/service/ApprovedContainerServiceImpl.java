package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.SerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent;
        try {
            jsonContent = objectMapper.writeValueAsString(containerInfoDto.getImageAnnotationDto());
        } catch (JsonProcessingException e) {
            log.error("Error serializing ImageAnnotationDto to JSON", e);
            throw new SerializationException("Error serializing ImageAnnotationDto to JSON", e);
        }

        String jsonBlobUrl = containerInfoDto.getJpeg().getJpeg() + ".json";

        String jpegBlobUrl = containerInfoDto.getJpeg().getJpeg();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(accountUrl)
                .sasToken(sasToken)
                .buildClient();

        BlobContainerClient approvedContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        
        BlobClient jsonBlobClient = approvedContainerClient.getBlobClient(jsonBlobUrl);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes())) {
            jsonBlobClient.upload(inputStream, jsonContent.length());
            log.info("ImageAnnotationDto saved to the approved container successfully");
        } catch (IOException e) {
            log.error("Error uploading ImageAnnotationDto to the approved container", e);
        }

        // Upload JPEG image to the approved container
        BlobClient jpegBlobClient = approvedContainerClient.getBlobClient(jpegBlobUrl);
        try (InputStream jpegInputStream = new URL(jpegBlobUrl).openStream()) {
            jpegBlobClient.upload(jpegInputStream, -1);
            log.info("JPEG image saved to the approved container successfully");
        } catch (IOException e) {
            log.error("Error uploading JPEG image to the approved container", e);
        }
    }
}
