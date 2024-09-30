package com.biit.ks.core.files;

import com.biit.ks.logger.KnowledgeSystemLogger;
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
import java.io.IOException;

@Component
public class ThumbnailFactory {

    public ThumbnailFactory() {
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

    public BufferedImage createThumbFromImage(byte[] pngImage, int width, int height) throws IOException {
        return createThumbFromImage(ImageIO.read(new ByteArrayInputStream(pngImage)), width, height);
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


    public BufferedImage createThumbFromVideo(byte[] videoBytes) {
        //Download first MBs from a video in Seaweed.
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            final FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(videoBytes));
            try {
                frameGrabber.setFormat("mp4");
                frameGrabber.start();
                //Frame frame = frameGrabber.grabImage();
                //Frame frame = frameGrabber.grab();
                final Frame frame = frameGrabber.grabKeyFrame();
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

    public BufferedImage createThumbFromVideo(String resource) {
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            final FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(resource);
            try {
                frameGrabber.setFormat("mp4");
                frameGrabber.start();
                //Frame frame = frameGrabber.grabImage();
                //Frame frame = frameGrabber.grab();
                final Frame frame = frameGrabber.grabKeyFrame();
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
