package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PendingContainerServiceImpl implements PendingContainerService {

    private static final String JSON_EXTENSION = ".json";
    @Value("${azure.storage.accountUrl}")
    private String accountUrl;
    @Value("${azure.storage.pendingContainerName}")
    private String containerName;
    @Value("${azure.storage.pendingSasToken}")
    private String sasToken;

    @Override
    public List<ContainerInfoDto> getPendingImageAnnotations() {
        return ContainerServiceUtil.getImageAnnotations(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public ContainerInfoDto getImageAnnotationDtoByImageInfo(ImageInfoDto imageInfoDto) {
        ImageAnnotationDto imageAnnotationDto = getImageAnnotationFromContainer(imageInfoDto.getPath());
        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
        containerInfoDto.setJpg(imageInfoDto);
        containerInfoDto.setImageAnnotationDto(imageAnnotationDto);

        return containerInfoDto;
    }

    public ImageAnnotationDto getImageAnnotationFromContainer(String imageUrl) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();

        String blobName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.lastIndexOf('.'));
        String jsonName = blobName + JSON_EXTENSION;

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (blobItem == null || blobItem.getName() == null) {
                continue;
            }

            if (blobItem.getName().equals(jsonName)) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                     InputStream inputStream = blobContainerClient.getBlobClient(blobItem.getName()).openInputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    String jsonContent = outputStream.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(jsonContent, ImageAnnotationDto.class);
                } catch (IOException e) {
                    log.error("Error reading JSON content from blob: {}", e.getMessage());
                }
            }
        }
        return null;
    }


    @Override
    public void deleteJpgAndJsonBlobs(String blobUrl) {
        String blobName = extractBlobName(blobUrl);

        deleteBlob(blobName);

        String jsonBlobName = constructJsonBlobName(blobName);
        deleteBlob(jsonBlobName);
    }

    private String extractBlobName(String blobUrl) {
        int lastSlashIndex = blobUrl.lastIndexOf('/');
        return blobUrl.substring(lastSlashIndex + 1);
    }

    private String constructJsonBlobName(String blobName) {
        int extensionIndex = blobName.lastIndexOf('.');
        if (extensionIndex != -1) {
            return blobName.substring(0, extensionIndex) + JSON_EXTENSION;
        } else {
            log.warn("Unable to determine JSON blob name for blob '{}'.", blobName);
            return null;
        }
    }

    public void deleteBlob(String blobName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

        try {
            blobClient.delete();
            log.info("Blob '{}' deleted successfully.", blobName);
        } catch (BlobStorageException ex) {
            log.error("Error deleting blob '{}': {}", blobName, ex.getMessage());
        }
    }


    private BlobContainerClient getBlobContainerClient() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        return new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();
    }
}
