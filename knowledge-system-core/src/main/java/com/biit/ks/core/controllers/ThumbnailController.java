package com.biit.ks.core.controllers;

import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.files.MimeTypeToFFmpeg;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.FileEntry;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ThumbnailController {
    private static final int MIN_THUMBNAIL_SIZE = 200;
    private static final String THUMBNAIL_SEAWEED_FOLDER = "/thumbnails";
    private static final String THUMBNAIL_SERVICE_URL = "/public/download/filename:";

    private final SeaweedClient seaweedClient;

    public ThumbnailController(SeaweedClient seaweedClient) {
        this.seaweedClient = seaweedClient;
        FFmpegLogCallback.set();
        FFmpegLogCallback.setLevel(avutil.AV_LOG_ERROR);
    }


    public byte[] toByteArray(BufferedImage image) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            if (image != null) {
                ImageIO.write(image, "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            KnowledgeSystemLogger.errorMessage(this.getClass(), e);
        }
        return null;
    }


    public byte[] getThumbnail(FileEntry fileEntry) throws IOException {
        return getThumbnail(fileEntry.getUuid());
    }


    public byte[] getThumbnail(UUID uuid) {
        try {
            return seaweedClient.getBytes(THUMBNAIL_SEAWEED_FOLDER + File.separator, uuid.toString());
        } catch (IOException e) {
            throw new SeaweedClientException(this.getClass(), e);
        }
    }


    public void setThumbnail(FileEntry fileEntry) throws IOException {
        if (fileEntry == null) {
            return;
        }
        KnowledgeSystemLogger.debug(this.getClass(), "Updating thumbnail for '{}'", fileEntry);
        if (fileEntry.getMimeType() != null) {
            if (fileEntry.getMimeType().startsWith("image/")) {
                KnowledgeSystemLogger.debug(this.getClass(), "FileEntry is an image '{}'.", fileEntry.getMimeType());
                setThumbnail(fileEntry, toByteArray(createThumbFromImage(fileEntry)));
            } else if (fileEntry.getMimeType().startsWith("video/")) {
                KnowledgeSystemLogger.debug(this.getClass(), "FileEntry is a video '{}'.", fileEntry.getMimeType());
                setThumbnail(fileEntry, toByteArray(createThumbFromImage(fileEntry)));
            } else {
                KnowledgeSystemLogger.debug(this.getClass(), "Unknown mimetype '{}' for fileEntry.", fileEntry.getMimeType());
                setThumbnail(fileEntry, null);
            }
        } else {
            KnowledgeSystemLogger.debug(this.getClass(), "FileEntry has no mimetype");
            setThumbnail(fileEntry, null);
        }
    }


    private void setThumbnail(FileEntry fileEntry, byte[] thumbnail) {
        if (thumbnail == null) {
            fileEntry.setThumbnailUrl("");
            return;
        }
        try {
            seaweedClient.addBytes(THUMBNAIL_SEAWEED_FOLDER + File.separator + fileEntry.getUuid().toString(), thumbnail);
            fileEntry.setThumbnailUrl(THUMBNAIL_SERVICE_URL + fileEntry.getUuid().toString());
        } catch (IOException e) {
            KnowledgeSystemLogger.errorMessage(this.getClass(), e);
        }
    }


    public BufferedImage createThumbFromImage(FileEntry fileEntry) throws IOException {
        final byte[] sourceImage = seaweedClient.getBytes(fileEntry.getFilePath(), fileEntry.getFileName());
        return createThumbFromImage(ImageIO.read(new ByteArrayInputStream(sourceImage)), MIN_THUMBNAIL_SIZE);
    }


    public BufferedImage createThumbFromImage(byte[] pngImage, int width, int height) throws IOException {
        return createThumbFromImage(ImageIO.read(new ByteArrayInputStream(pngImage)), width, height);
    }


    public BufferedImage createThumbFromImage(BufferedImage inputImage, int minSize) {
        final int height;
        final int width;

        if (inputImage.getWidth() > inputImage.getHeight()) {
            height = minSize;
            width = inputImage.getWidth() * (minSize / inputImage.getHeight());
        } else {
            width = minSize;
            height = inputImage.getHeight() * (minSize / inputImage.getWidth());
        }
        return createThumbFromImage(inputImage, width, height);
    }


    public BufferedImage createThumbFromImage(BufferedImage inputImage, int width, int height) {
        KnowledgeSystemLogger.debug(this.getClass(), "Creating thumbnail with width '{}' and height '{}'.", width, height);
        // scale width, height to keep aspect constant
        final double outputAspect = 1.0 * width / height;
        final double inputAspect = 1.0 * inputImage.getWidth() / inputImage.getHeight();
        if (outputAspect < inputAspect) {
            // width is a limiting factor; adjust height to keep the aspect ratio
            height = (int) (width / inputAspect);
        } else {
            // height is a limiting factor; adjust width to keep the aspect ratio
            width = (int) (height * inputAspect);
        }
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(inputImage, 0, 0, width, height, null);
        g2.dispose();
        return bi;
    }


    public BufferedImage createThumbFromVideo(FileEntry fileEntry) throws IOException {
        return createThumbFromVideo(fileEntry.getFilePath(), fileEntry.getFileName(), fileEntry.getMimeType());
    }


    /**
     * Will assume MP4 format.
     *
     * @param videoBytes
     * @return
     */
    public BufferedImage createThumbFromVideo(byte[] videoBytes, String mimeType) {
        return createThumbFromVideo(new FFmpegFrameGrabber(new ByteArrayInputStream(videoBytes)), mimeType);
    }


    public BufferedImage createThumbFromVideo(String seaweedPath, String resourceName, String mimeType) {
        final File file;
        try {
            file = File.createTempFile("downloadedVideo", "mp4");
            file.deleteOnExit();
            seaweedClient.getFile(seaweedPath + File.separator + resourceName, file);
            return createThumbFromVideo(file.getPath(), mimeType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public BufferedImage createThumbFromVideo(String resource, String mimeType) {
        return createThumbFromVideo(new FFmpegFrameGrabber(resource), mimeType);
    }


    public BufferedImage createThumbFromVideo(FFmpegFrameGrabber frameGrabber, String mimeType) {
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            try {
                final String detectedFormat = MimeTypeToFFmpeg.getFFmpegExtension(mimeType);
                frameGrabber.setFormat(detectedFormat);
                frameGrabber.start();
                final int frameCount = frameGrabber.getLengthInFrames();
                //final Frame frame = frameGrabber.grabKeyFrame();
                KnowledgeSystemLogger.debug(this.getClass(), "Creating thumbnail for frame '{}' with format.", frameCount / 2, detectedFormat);
                frameGrabber.setFrameNumber(frameCount / 2);
                final Frame frame = frameGrabber.grabImage();
                final BufferedImage bufferedImage = converter.convert(frame);
                KnowledgeSystemLogger.debug(this.getClass(), "Thumbnail height '{}' and width '{}'.", bufferedImage.getHeight(), bufferedImage.getWidth());
                return bufferedImage;
            } catch (Exception e) {
                KnowledgeSystemLogger.errorMessage(this.getClass(), e);
            } finally {
                try {
                    frameGrabber.stop();
                    frameGrabber.close();
                } catch (Exception e) {
                    KnowledgeSystemLogger.errorMessage(this.getClass(), e);
                }
            }
        }
        return null;
    }

}

