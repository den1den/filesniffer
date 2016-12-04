package main;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

public class FileChecker {
    public static ImageAnalysisResult analyzeImage(final Path file) throws NoSuchAlgorithmException, IOException {
        final ImageAnalysisResult result = new ImageAnalysisResult();

        final InputStream is = Files.newInputStream(file);
        try {
            final ImageInputStream imageInputStream = ImageIO.createImageInputStream(is);
            final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
            if (!imageReaders.hasNext()) {
                result.setImage(false);
                return result;
            }
            final ImageReader imageReader = imageReaders.next();
            imageReader.setInput(imageInputStream);
            final BufferedImage image = imageReader.read(0);
            if (image == null) {
                return result;
            }
            image.flush();
            if (imageReader.getFormatName().equals("JPEG")) {
                imageInputStream.seek(imageInputStream.getStreamPosition() - 2);
                final byte[] lastTwoBytes = new byte[2];
                imageInputStream.read(lastTwoBytes);
                if (lastTwoBytes[0] != (byte)0xff || lastTwoBytes[1] != (byte)0xd9) {
                    result.setTruncated(true);
                } else {
                    result.setTruncated(false);
                }
            }
            result.setImage(true);
        } catch (final IndexOutOfBoundsException e) {
            result.setTruncated(true);
        } catch (final IIOException e) {
            if (e.getCause() instanceof EOFException) {
                result.setTruncated(true);
            }
        } finally {
            is.close();
        }
        return result;
    }

    public static class ImageAnalysisResult {
        boolean image;
        boolean truncated;
        public void setImage(boolean image) {
            this.image = image;
        }
        public void setTruncated(boolean truncated) {
            this.truncated = truncated;
        }

        @Override
        public String toString() {
            return "ImageAnalysisResult{" +
                    "image=" + image +
                    ", truncated=" + truncated +
                    '}';
        }
    }
}
