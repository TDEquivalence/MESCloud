package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicContainerServiceImpl implements PublicContainerService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.publicContainerName}")
    private String containerName;

    @Value("${azure.storage.publicSasToken}")
    private String sasToken;


    @Override
    public List<ContainerInfoDto> getImageAnnotations() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        List<ContainerInfoDto> containerInfoList = new ArrayList<>();
        Map<String, ImageAnnotationDto> decisionMap = new HashMap<>(); // Map to store ImageDecisionDto objects by blob name
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Collect all JSON files and store them in the decisionMap
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobName.toLowerCase().endsWith(".json")) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try (InputStream inputStream = blobClient.openInputStream()) {
                    String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    ImageAnnotationDto imageDecision = objectMapper.readValue(jsonContent, ImageAnnotationDto.class);
                    decisionMap.put(blobName, imageDecision);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Match JPEG files with their corresponding ImageDecisionDto objects and create ContainerInfoDto objects
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobName.toLowerCase().endsWith(".jpg") || blobName.toLowerCase().endsWith(".jpeg")) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    // Find corresponding ImageDecisionDto object
                    String correspondingJsonBlobName = getCorrespondingJsonBlobName(blobName);
                    ImageAnnotationDto relatedImageDecision = decisionMap.get(correspondingJsonBlobName);

                    // Create ImageInfoDto object
                    ImageInfoDto imageInfo = new ImageInfoDto();
                    imageInfo.setJpeg(blobClient.getBlobUrl());

                    // Create ContainerInfoDto object and set ImageInfoDto and ImageDecisionDto
                    ContainerInfoDto containerInfo = new ContainerInfoDto();
                    containerInfo.setJpeg(imageInfo);
                    containerInfo.setImageAnnotationDto(relatedImageDecision);

                    // Add ContainerInfoDto object to the list
                    containerInfoList.add(containerInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return containerInfoList;
    }

    private String getCorrespondingJsonBlobName(String jpegBlobName) {
        return jpegBlobName.substring(0, jpegBlobName.lastIndexOf('.')) + ".json";
    }


    private BlobClient getBlobClient(BlobContainerClient blobContainerClient, BlobItem blobItem) {
        if (blobItem.getSnapshot() != null) {
            return blobContainerClient.getBlobClient(blobItem.getName(), blobItem.getSnapshot());
        } else {
            return blobContainerClient.getBlobClient(blobItem.getName());
        }
    }
}
