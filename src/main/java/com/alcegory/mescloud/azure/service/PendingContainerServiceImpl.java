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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class PendingContainerServiceImpl implements PendingContainerService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.pendingContainerName}")
    private String containerName;

    @Value("${azure.storage.pendingSasToken}")
    private String sasToken;

    private static final String JSON = ".json";
    private final ApprovedContainerService approvedContainerService;

    public PendingContainerServiceImpl(ApprovedContainerService approvedContainerService) {
        this.approvedContainerService = approvedContainerService;
    }

    @Override
    public List<ContainerInfoDto> getPendingImageAnnotations() {
        return ContainerServiceUtil.getImageAnnotations(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public ContainerInfoDto processUpdateImage(ImageAnnotationDto updatedImageAnnotationDto) {
        ImageAnnotationDto imageAnnotationDto = updateImageDecision(updatedImageAnnotationDto);
        assert imageAnnotationDto != null;
        ContainerInfoDto containerInfoDto = getImageAnnotationFromImageUrl(imageAnnotationDto.getData().getImage());
        approvedContainerService.saveToApprovedContainer(containerInfoDto);
        assert containerInfoDto != null;
        deleteFilesFromPendingContainer(containerInfoDto);

        return containerInfoDto;
    }

    private ImageAnnotationDto updateImageDecision(ImageAnnotationDto updatedImageAnnotationDto) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (blobItem.getName().toLowerCase().endsWith(JSON)) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    ImageAnnotationDto blobImageAnnotation = getImageAnnotationDtoFromBlob(blobClient);

                    if (shouldUpdateImageDecision(blobImageAnnotation, updatedImageAnnotationDto)) {
                        updateImageDecision(blobImageAnnotation, updatedImageAnnotationDto);
                        uploadUpdatedBlob(blobClient, blobImageAnnotation);
                        return blobImageAnnotation;
                    }
                } catch (IOException e) {
                    log.error("An error occurred while processing blob: {}", blobItem, e);
                }
            }
        }

        return null;
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

    private boolean shouldUpdateImageDecision(ImageAnnotationDto blobImageAnnotation, ImageAnnotationDto updatedImageAnnotationDto) {
        return blobImageAnnotation.getData() != null && blobImageAnnotation.getData().getImage() != null &&
                blobImageAnnotation.getData().getImage().equals(updatedImageAnnotationDto.getData().getImage());
    }

    private void updateImageDecision(ImageAnnotationDto blobImageAnnotation, ImageAnnotationDto updatedImageAnnotationDto) {
        Field[] fields = updatedImageAnnotationDto.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(updatedImageAnnotationDto);
                if (value != null) {
                    Field imageDecisionField = blobImageAnnotation.getClass().getDeclaredField(field.getName());
                    imageDecisionField.setAccessible(true);
                    imageDecisionField.set(blobImageAnnotation, value);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                log.error("An error occurred while processing field blob: {}", field, e);
            }
        }
    }

    private ContainerInfoDto getImageAnnotationFromImageUrl(String imageUrl) {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String jsonBlobName = imageUrl.substring(0, imageUrl.lastIndexOf('.')) + JSON;
        BlobClient jsonBlobClient = blobContainerClient.getBlobClient(jsonBlobName);

        try (InputStream inputStream = jsonBlobClient.openInputStream()) {
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ImageAnnotationDto imageDecision = objectMapper.readValue(jsonContent, ImageAnnotationDto.class);

            ImageInfoDto imageInfo = new ImageInfoDto();
            imageInfo.setJpeg(jsonContent);

            ContainerInfoDto containerInfo = new ContainerInfoDto();
            containerInfo.setJpeg(imageInfo);
            containerInfo.setImageAnnotationDto(imageDecision);

            return containerInfo;
        } catch (IOException e) {
            log.error("An error occurred while processing blob for image URL: {}", imageUrl, e);
            return null;
        }
    }

    private void uploadUpdatedBlob(BlobClient blobClient, ImageAnnotationDto imageDecision) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedJsonContent = objectMapper.writeValueAsString(imageDecision);
        blobClient.upload(new ByteArrayInputStream(updatedJsonContent.getBytes()), updatedJsonContent.length(), true);
    }

    private void deleteFilesFromPendingContainer(ContainerInfoDto containerInfoDto) {
        String jpegUrl = containerInfoDto.getJpeg().getJpeg();
        String jsonFileName = jpegUrl.substring(0, jpegUrl.lastIndexOf('.')) + JSON;

        deleteBlob(jpegUrl);
        deleteBlob(jsonFileName);
    }

    private void deleteBlob(String blobName) {
        BlobClient blobClient = getBlobClient(blobName);
        try {
            blobClient.delete();
            log.info("Blob '{}' deleted successfully.", blobClient.getBlobName());
        } catch (BlobStorageException ex) {
            log.error("Error deleting blob '{}': {}", blobClient.getBlobName(), ex.getMessage());
        }
    }

    private BlobClient getBlobClient(String blobName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();
        return blobContainerClient.getBlobClient(blobName);
    }
}
