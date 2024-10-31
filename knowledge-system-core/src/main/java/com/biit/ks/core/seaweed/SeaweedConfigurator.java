package com.biit.ks.core.seaweed;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SeaweedConfigurator {

    public static final String UPLOADS_PATH = "/uploads";
    public static final String THUMBNAIL_SEAWEED_FOLDER = "/thumbnails";
    private static final int DIRECTORY_PERMISSIONS = 0755;

    private final SeaweedClient seaweedClient;

    public SeaweedConfigurator(SeaweedClient seaweedClient) {
        this.seaweedClient = seaweedClient;
    }

    @PostConstruct
    public void createFolders() {
        seaweedClient.createFolder(UPLOADS_PATH, DIRECTORY_PERMISSIONS);
        seaweedClient.createFolder(THUMBNAIL_SEAWEED_FOLDER, DIRECTORY_PERMISSIONS);
    }


    public String getUploadsPath() {
        return UPLOADS_PATH;
    }

    public String getThumbnailsPath() {
        return THUMBNAIL_SEAWEED_FOLDER;
    }
}
