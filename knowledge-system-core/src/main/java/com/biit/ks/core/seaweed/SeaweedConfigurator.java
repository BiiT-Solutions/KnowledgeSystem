package com.biit.ks.core.seaweed;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
