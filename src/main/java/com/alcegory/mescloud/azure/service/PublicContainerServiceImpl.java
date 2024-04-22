package com.alcegory.mescloud.azure.service;

import com.alcegory.mescloud.azure.model.dto.ImageInfoDto;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PublicContainerServiceImpl implements PublicContainerService {

    @Value("${azure.storage.accountUrl}")
    private final String accountUrl;

    @Value("${azure.storage.publicContainerName}")
    private final String containerName;

    @Getter
    @Value("${azure.storage.publicSasToken}")
    private final String sasToken;

    public PublicContainerServiceImpl(@Value("${azure.storage.accountUrl}") String accountUrl,
                                      @Value("${azure.storage.publicContainerName}") String containerName,
                                      @Value("${azure.storage.publicSasToken}") String sasToken) {
        this.accountUrl = accountUrl;
        this.containerName = containerName;
        this.sasToken = sasToken;
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
    public ImageInfoDto getImageReference() {
        return ContainerServiceUtil.getImageReference(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public ImageInfoDto getRandomImageReference() {
        return ContainerServiceUtil.getRandomImageReference(
                accountUrl,
                containerName,
                sasToken
        );
    }

    @Override
    public void deleteBlob(String blobUrl) {
        String blobName = extractBlobName(blobUrl);
        BlobClient blobClient = getBlobClient(blobName);
        try {
            blobClient.delete();
            log.info("Blob '{}' deleted successfully.", blobName);
        } catch (BlobStorageException ex) {
            log.error("Error deleting blob '{}': {}", blobName, ex.getMessage());
        }
    }

    private String extractBlobName(String blobUrl) {
        int lastIndex = blobUrl.lastIndexOf('/');
        return blobUrl.substring(lastIndex + 1);
    }

    private BlobClient getBlobClient(String blobName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient();
        return blobContainerClient.getBlobClient(blobName);
    }

    private BlobContainerClient getBlobContainerClient() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        return new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();
    }
}
