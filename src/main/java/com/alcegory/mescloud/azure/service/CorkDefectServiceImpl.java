package com.alcegory.mescloud.azure.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorkDefectServiceImpl implements CorkDefectService {

    @Value("${azure.storage.accountUrl}")
    private String accountUrl;

    @Value("${azure.storage.containerName}")
    private String containerName;

    @Value("${azure.storage.sasToken}")
    private String sasToken;


    @Override
    public List<String> getJpegImageUrls() {
        String containerUriWithSAS = String.format("%s%s?%s", accountUrl, containerName, sasToken);
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(containerUriWithSAS)
                .buildClient();

        List<String> jpegImageUrls = new ArrayList<>();
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            if (isJpegImage(blobItem.getName())) {
                BlobClient blobClient = getBlobClient(blobContainerClient, blobItem);
                System.out.println("Reference image URI: " + blobClient.getBlobUrl());
                jpegImageUrls.add(blobClient.getBlobUrl());
            }
        }

        return jpegImageUrls;
    }

    private BlobClient getBlobClient(BlobContainerClient blobContainerClient, BlobItem blobItem) {
        if (blobItem.getSnapshot() != null) {
            return blobContainerClient.getBlobClient(blobItem.getName(), blobItem.getSnapshot());
        } else {
            return blobContainerClient.getBlobClient(blobItem.getName());
        }
    }

    private boolean isJpegImage(String blobName) {
        return blobName.toLowerCase().endsWith(".jpg");
    }
}
