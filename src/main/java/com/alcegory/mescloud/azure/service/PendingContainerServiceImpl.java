package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class PendingContainerServiceImpl implements PendingContainerService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.pendingContainerName}")
    private String containerName;

    @Value("${azure.storage.pendingSasToken}")
    private String sasToken;

    @Override
    public List<ImageAnnotationDto> editImageDecision(ImageAnnotationDto updatedImageAnnotationDto) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();
        List<ImageAnnotationDto> updatedImageDecisions = new ArrayList<>();

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (blobItem.getName().toLowerCase().endsWith(".json")) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    ImageAnnotationDto imageDecision = getImageAnnotationDtoFromBlob(blobClient);

                    if (shouldUpdateImageDecision(imageDecision, updatedImageAnnotationDto)) {
                        updateImageDecision(imageDecision, updatedImageAnnotationDto);
                        uploadUpdatedBlob(blobClient, imageDecision);
                    }

                    updatedImageDecisions.add(imageDecision);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return updatedImageDecisions;
    }

    private BlobContainerClient getBlobContainerClient() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        return new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();
    }

    private BlobClient getBlobClient(BlobContainerClient blobContainerClient, BlobItem blobItem) {
        return blobContainerClient.getBlobClient(blobItem.getName());
    }

    private ImageAnnotationDto getImageAnnotationDtoFromBlob(BlobClient blobClient) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream inputStream = blobClient.openInputStream()) {
            return objectMapper.readValue(inputStream, ImageAnnotationDto.class);
        }
    }

    private boolean shouldUpdateImageDecision(ImageAnnotationDto imageDecision, ImageAnnotationDto updatedImageAnnotationDto) {
        return imageDecision.getData() != null && imageDecision.getData().getImage() != null &&
                imageDecision.getData().getImage().equals(updatedImageAnnotationDto.getData().getImage());
    }

    private void updateImageDecision(ImageAnnotationDto imageDecision, ImageAnnotationDto updatedImageAnnotationDto) {
        Field[] fields = updatedImageAnnotationDto.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(updatedImageAnnotationDto);
                if (value != null) {
                    Field imageDecisionField = imageDecision.getClass().getDeclaredField(field.getName());
                    imageDecisionField.setAccessible(true);
                    imageDecisionField.set(imageDecision, value);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadUpdatedBlob(BlobClient blobClient, ImageAnnotationDto imageDecision) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedJsonContent = objectMapper.writeValueAsString(imageDecision);
        blobClient.upload(new ByteArrayInputStream(updatedJsonContent.getBytes()), updatedJsonContent.length(), true);
    }
}
