package com.biit.ks.core.files;

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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum MimeTypeToFFmpeg {
    MP4("video/mp4", "mp4"),
    QUICKTIME("video/quicktime", "mp4"),
    MPEG("video/mpeg", "mpegvideo"),
    F3G2("video/3gpp2", "3g2"),
    F3G("video/3gpp", "3gp"),
    ASF("video/x-ms-asf", "asf"),
    AVI("video/x-msvideo", "avi"),
    DVD("video/mpeg", "dvd"),
    FLV("video/x-flv", "flv"),
    H261("video/x-h261", "h261"),
    H263("video/x-h263", "h263"),
    M4V("video/x-m4v", "m4v"),
    MATROSKA("video/x-matroska", "matroska"),
    WEBM("video/webm", "webm"),
    MJPEG("video/x-mjpeg", "mjpeg"),
    OGV("video/ogg", "ogv");


    private final String mimeType;
    private final String ffmpegExtension;

    MimeTypeToFFmpeg(String mimeType, String ffmpegExtension) {
        this.mimeType = mimeType;
        this.ffmpegExtension = ffmpegExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFfmpegExtension() {
        return ffmpegExtension;
    }

    public static String getFFmpegExtension(String mimeType) {
        for (MimeTypeToFFmpeg mimeTypeToFFmpeg : MimeTypeToFFmpeg.values()) {
            if (mimeTypeToFFmpeg.mimeType.equalsIgnoreCase(mimeType)) {
                return mimeTypeToFFmpeg.ffmpegExtension;
            }
        }
        return MP4.ffmpegExtension;
    }

    public static Set<String> getFilteredExtensions(Set<String> ignoredExtensions) {
        return Arrays.stream(MimeTypeToFFmpeg.values()).filter(value -> !ignoredExtensions.contains(value.ffmpegExtension))
                .map(value -> value.ffmpegExtension).collect(Collectors.toSet());
    }

}
