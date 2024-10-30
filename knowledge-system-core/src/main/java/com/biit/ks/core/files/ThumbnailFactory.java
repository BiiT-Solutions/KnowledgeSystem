package com.biit.ks.core.files;

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

@Component
public class ThumbnailFactory {
    private static final int MIN_THUMBNAIL_SIZE = 200;

    private final SeaweedClient seaweedClient;

    public ThumbnailFactory(SeaweedClient seaweedClient) {
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

    public void setThumbnail(FileEntry fileEntry) throws IOException {
        if (fileEntry == null) {
            return;
        }
        if (fileEntry.getMimeType() != null) {
            if (fileEntry.getMimeType().startsWith("image/")) {
                fileEntry.setThumbnail(toByteArray(createThumbFromImage(fileEntry)));
            } else if (fileEntry.getMimeType().startsWith("video/")) {
                fileEntry.setThumbnail(toByteArray(createThumbFromVideo(fileEntry)));
            }
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
        return createThumbFromVideo(fileEntry.getFilePath(), fileEntry.getFileName());
    }


    public BufferedImage createThumbFromVideo(byte[] videoBytes) {
        return createThumbFromVideo(new FFmpegFrameGrabber(new ByteArrayInputStream(videoBytes)));
    }


    public BufferedImage createThumbFromVideo(String seaweedPath, String resourceName) {
        final File file;
        try {
            file = File.createTempFile("downloadedVideo", "mp4");
            file.deleteOnExit();
            seaweedClient.getFile(seaweedPath + File.separator + resourceName, file);
            return createThumbFromVideo(file.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public BufferedImage createThumbFromVideo(String resource) {
        return createThumbFromVideo(new FFmpegFrameGrabber(resource));
    }


    public BufferedImage createThumbFromVideo(FFmpegFrameGrabber frameGrabber) {
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            try {
                frameGrabber.setFormat("mp4");
                frameGrabber.start();
                final int frameCount = frameGrabber.getLengthInFrames();
                //final Frame frame = frameGrabber.grabKeyFrame();
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

