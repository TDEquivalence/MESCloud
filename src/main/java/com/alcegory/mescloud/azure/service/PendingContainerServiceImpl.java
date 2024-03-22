package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
        ContainerInfoDto containerInfoDtoUpdated = getImageAnnotationFromContainerInfo(imageAnnotationDto);
        approvedContainerService.saveToApprovedContainer(containerInfoDtoUpdated);
        assert containerInfoDtoUpdated != null;
        deleteFilesFromPendingContainer(containerInfoDtoUpdated);

        return containerInfoDtoUpdated;
    }

    private ImageAnnotationDto updateImageDecision(ImageAnnotationDto updatedImageAnnotationDto) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (blobItem.getName().toLowerCase().endsWith(JSON)) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    ImageAnnotationDto blobImageAnnotation = getImageAnnotationDtoFromBlob(blobClient);

                    if (shouldUpdateImageDecision(blobImageAnnotation, updatedImageAnnotationDto)) {
                        // Update fields using the updateFields method
                        updateFields(blobImageAnnotation, updatedImageAnnotationDto);

                        uploadUpdatedBlob(blobClient, blobImageAnnotation);

                        // Update jpeg field in ContainerInfoDto
                        ContainerInfoDto containerInfoDto = new ContainerInfoDto();
                        ImageInfoDto imageInfoDto = new ImageInfoDto();
                        imageInfoDto.setJpeg(updatedImageAnnotationDto.getData().getImage());
                        containerInfoDto.setJpeg(imageInfoDto);
                        containerInfoDto.setImageAnnotationDto(blobImageAnnotation);

                        return containerInfoDto.getImageAnnotationDto();
                    }
                } catch (IOException e) {
                    log.error("An error occurred while processing blob: {}", blobItem, e);
                }
            }
        }

        return null;
    }

    private byte[] retrieveBlobContent(String blobName, String accountUrl, String sasToken) {
        try {
            BlobContainerClient blobContainerClient = new BlobServiceClientBuilder()
                    .endpoint(accountUrl)
                    .sasToken(sasToken)
                    .buildClient()
                    .getBlobContainerClient(containerName);

            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);

            return blobClient.downloadContent().toBytes();
        } catch (Exception e) {
            log.error("An error occurred while retrieving blob content for blob: {}", blobName, e);
            return null;
        }
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

    private void updateFields(ImageAnnotationDto blobImageAnnotation, ImageAnnotationDto updatedImageAnnotationDto) {
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

    public ContainerInfoDto getImageAnnotationFromContainerInfo(ImageAnnotationDto imageAnnotationDto) {
        try {
            String imageUrl = imageAnnotationDto.getData().getImage();
            String jpegFileName = getImageFileNameFromUrl(imageUrl);
            String jsonBlobName = getJsonBlobName(jpegFileName);

            // Retrieve the JSON data
            String jsonContent = retrieveBlobContent(jsonBlobName);
            if (jsonContent != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ImageAnnotationDto parsedImageAnnotationDto = objectMapper.readValue(jsonContent, ImageAnnotationDto.class);

                // Retrieve the JPEG data
                String jpegContent = retrieveJpegReference(jpegFileName);
                if (jpegContent != null) {
                    ImageInfoDto imageInfoDto = new ImageInfoDto();
                    imageInfoDto.setJpeg(jpegContent);

                    ContainerInfoDto containerInfoDto = new ContainerInfoDto();
                    containerInfoDto.setJpeg(imageInfoDto);
                    containerInfoDto.setImageAnnotationDto(parsedImageAnnotationDto);

                    return containerInfoDto;
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while processing image annotation: {}", imageAnnotationDto, e);
        }
        return null;
    }

    private String getImageFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }

    private String getJsonBlobName(String jpegFileName) {
        return jpegFileName.substring(0, jpegFileName.lastIndexOf('.')) + JSON;
    }

    private String retrieveBlobContent(String blobName) {
        try {
            BlobContainerClient blobContainerClient = getBlobContainerClient(); // Assuming this method returns the BlobContainerClient
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("An error occurred while retrieving blob content for blob: {}", blobName, e);
            return null;
        }
    }

    private String retrieveJpegReference(String jpegFileName) {
        return String.format("%s/%s/%s", accountUrl, containerName, jpegFileName);
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
