package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class PendingContainerServiceImpl implements PendingContainerService {

    //@Value("${azure.storage.accountUrl}")
    private String accountUrl;

    //@Value("${azure.storage.pendingContainerName}")
    private String containerName;

    //@Value("${azure.storage.pendingSasToken}")
    private String sasToken;

    @Override
    public List<ImageAnnotationDto> editImageDecision(ImageAnnotationDto updatedImageAnnotationDto) {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        List<ImageAnnotationDto> updatedImageDecisions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (blobItem.getName().toLowerCase().endsWith(".json")) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                System.out.println("Reading image annotation from: " + blobClient.getBlobUrl());
                try (InputStream inputStream = blobClient.openInputStream()) {
                    String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    ImageAnnotationDto imageDecision = objectMapper.readValue(jsonContent, ImageAnnotationDto.class);
                    // Update the imageDecision object with new data
                    if (imageDecision.getData() != null && imageDecision.getData().getImage() != null &&
                            imageDecision.getData().getImage().equals(updatedImageAnnotationDto.getData().getImage())) {
                        imageDecision.setModelDecision(updatedImageAnnotationDto.getModelDecision());
                        imageDecision.setUserDecision(updatedImageAnnotationDto.getUserDecision());
                        updatedImageDecisions.add(imageDecision);
                        String updatedJsonContent = objectMapper.writeValueAsString(imageDecision);
                        blobClient.upload(new ByteArrayInputStream(updatedJsonContent.getBytes()), updatedJsonContent.length());
                    } else {
                        updatedImageDecisions.add(imageDecision);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return updatedImageDecisions;
    }

    private BlobClient getBlobClient(BlobContainerClient blobContainerClient, BlobItem blobItem) {
        if (blobItem.getSnapshot() != null) {
            return blobContainerClient.getBlobClient(blobItem.getName(), blobItem.getSnapshot());
        } else {
            return blobContainerClient.getBlobClient(blobItem.getName());
        }
    }
}
