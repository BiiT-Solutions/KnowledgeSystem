package com.biit.ks.core.files;

import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public final class MediaTypeCalculator {

    private MediaTypeCalculator() {

    }

    public static String getRealMimeType(MultipartFile file) {
        final AutoDetectParser parser = new AutoDetectParser();
        final Detector detector = parser.getDetector();
        try {
            final Metadata metadata = new Metadata();
            final TikaInputStream stream = TikaInputStream.get(file.getInputStream());
            final MediaType mediaType = detector.detect(stream, metadata);
            return mediaType.toString();
        } catch (IOException e) {
            return MimeTypes.OCTET_STREAM;
        }
    }

}
