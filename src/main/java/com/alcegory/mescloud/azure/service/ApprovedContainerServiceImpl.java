package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ApprovedContainerServiceImpl implements ApprovedContainerService {

    @Value("${azure.storage.accountUrl}")
    private final String accountUrl;

    @Value("${azure.storage.approvedContainerName}")
    private final String containerName;

    @Value("${azure.storage.approvedSasToken}")
    private final String sasToken;

    public ApprovedContainerServiceImpl(@Value("${azure.storage.accountUrl}") String accountUrl,
                                        @Value("${azure.storage.approvedContainerName}") String containerName,
                                        @Value("${azure.storage.approvedSasToken}") String sasToken) {
        this.accountUrl = accountUrl;
        this.containerName = containerName;
        this.sasToken = sasToken;
    }

    @Override
    public List<ContainerInfoDto> getApprovedImageAnnotations() {
        return ContainerServiceUtil.getImageAnnotations(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public List<ImageInfoDto> getAllImageReference() {
        return ContainerServiceUtil.getAllImageReference(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public ImageAnnotationDto saveToApprovedContainer(ImageAnnotationDto imageAnnotationDto) {
        try {
            BlobContainerClient blobContainerClient = getBlobContainerClient();

            String uploadedData = saveJsonAnnotation(blobContainerClient, imageAnnotationDto);
            return convertJsonToImageAnnotation(uploadedData);
        } catch (IOException e) {
            log.error("Error saving to approved container", e);
            return null; // Or return a default ImageAnnotationDto object as needed
        }
    }

    private ImageAnnotationDto convertJsonToImageAnnotation(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ImageAnnotationDto.class);
    }

    private String saveJsonAnnotation(BlobContainerClient blobContainerClient, ImageAnnotationDto imageAnnotationDto) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(imageAnnotationDto);

        String imageName = imageAnnotationDto.getData().getImage();
        String imageNameWithExtension = imageName.substring(imageName.lastIndexOf('/') + 1);
        String imageNameWithoutExtension = imageNameWithExtension.substring(0, imageNameWithExtension.lastIndexOf('.'));
        BlobClient jsonBlobClient = blobContainerClient.getBlobClient(imageNameWithoutExtension + ".json");
        try (InputStream dataStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            jsonBlobClient.upload(dataStream, json.length(), true);
        }

        // Return the JSON content instead of just the blob URL
        // Retrieve and return JSON content from the blob
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = jsonBlobClient.openInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return outputStream.toString(StandardCharsets.UTF_8);
    }


    private BlobContainerClient getBlobContainerClient() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        return new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();
    }
}
