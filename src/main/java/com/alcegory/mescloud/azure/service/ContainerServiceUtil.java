package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ContainerInfoDto;
import com.alcegory.mescloud.azure.model.dto.ImageAnnotationDto;
import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class ContainerServiceUtil {

    private static final String JPG_EXTENSION = ".jpg";
    private static final String JSON_EXTENSION = ".json";
    private static final String IMAGE_URL_FORMAT = "%s%s?%s";
    private static final String ERROR_MESSAGE = "An error occurred while processing blob: {}";
    private static final Random random = new Random();

    private ContainerServiceUtil() {
    }

    public static List<ContainerInfoDto> getImageAnnotations(
            String accountUrl,
            String containerName,
            String sasToken) {

        String containerUriWithSAS = String.format(IMAGE_URL_FORMAT, accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        List<ContainerInfoDto> containerInfoList = new ArrayList<>();
        Map<String, ImageAnnotationDto> decisionMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobName.toLowerCase().endsWith(JSON_EXTENSION)) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try (InputStream inputStream = blobClient.openInputStream()) {
                    String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    ImageAnnotationDto imageDecision = objectMapper.readValue(jsonContent, ImageAnnotationDto.class);
                    decisionMap.put(blobName, imageDecision);
                } catch (IOException e) {
                    log.error(ERROR_MESSAGE, blobName, e);
                }
            }
        }

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobName.toLowerCase().endsWith(JPG_EXTENSION)) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    String correspondingJsonBlobName = getCorrespondingJsonBlobName(blobName);
                    ImageAnnotationDto relatedImageDecision = decisionMap.get(correspondingJsonBlobName);

                    ImageInfoDto imageInfo = new ImageInfoDto();
                    imageInfo.setPath(blobClient.getBlobUrl());

                    ContainerInfoDto containerInfo = new ContainerInfoDto();
                    containerInfo.setJpg(imageInfo);
                    containerInfo.setImageAnnotationDto(relatedImageDecision);

                    containerInfoList.add(containerInfo);
                } catch (Exception e) {
                    log.error(ERROR_MESSAGE, blobName, e);
                }
            }
        }

        return containerInfoList;
    }

    public static ImageInfoDto getRandomImageReference(
            String accountUrl,
            String containerName,
            String sasToken) {

        String containerUriWithSAS = String.format(IMAGE_URL_FORMAT, accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        Iterator<BlobItem> blobIterator = blobContainerClient.listBlobs().iterator();

        if (!blobIterator.hasNext()) {
            return null;
        }

        long blobCount = blobContainerClient.listBlobs().stream().count();
        long randomIndex = random.nextLong(blobCount);

        for (long i = 0; i < randomIndex; i++) {
            if (!blobIterator.hasNext()) {
                blobIterator = blobContainerClient.listBlobs().iterator();
            }
            blobIterator.next();
        }

        BlobItem randomBlob = blobIterator.next();
        String blobName = randomBlob.getName();

        if (!blobName.toLowerCase().endsWith(JPG_EXTENSION)) {
            return null;
        }

        BlobClient blobClient = getBlobClient(blobContainerClient, randomBlob);
        try {
            ImageInfoDto imageInfo = new ImageInfoDto();
            imageInfo.setPath(blobClient.getBlobUrl());
            return imageInfo;
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, blobName, e);
            return null;
        }
    }


    public static List<ImageInfoDto> getAllImageReference(
            String accountUrl,
            String containerName,
            String sasToken) {

        String containerUriWithSAS = String.format(IMAGE_URL_FORMAT, accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        List<ImageInfoDto> imageInfoList = new ArrayList<>();

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            String blobName = blobItem.getName();
            if (blobName.toLowerCase().endsWith(JPG_EXTENSION)) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                try {
                    ImageInfoDto imageInfo = new ImageInfoDto();
                    imageInfo.setPath(blobClient.getBlobUrl());
                    imageInfoList.add(imageInfo);
                } catch (Exception e) {
                    log.error(ERROR_MESSAGE, blobName, e);
                }
            }
        }

        return imageInfoList;
    }

    private static String getCorrespondingJsonBlobName(String jpegBlobName) {
        return jpegBlobName.substring(0, jpegBlobName.lastIndexOf('.')) + JSON_EXTENSION;
    }


    private static BlobClient getBlobClient(BlobContainerClient blobContainerClient, BlobItem blobItem) {
        if (blobItem.getSnapshot() != null) {
            return blobContainerClient.getBlobClient(blobItem.getName(), blobItem.getSnapshot());
        } else {
            return blobContainerClient.getBlobClient(blobItem.getName());
        }
    }

}
