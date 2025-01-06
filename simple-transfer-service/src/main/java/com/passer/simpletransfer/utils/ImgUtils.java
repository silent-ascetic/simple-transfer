package com.passer.simpletransfer.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;

public class ImgUtils {

    public static final String IMAGE_TYPE_GIF = "gif";
    public static final String IMAGE_TYPE_JPG = "jpg";
    public static final String IMAGE_TYPE_JPEG = "jpeg";
    public static final String IMAGE_TYPE_BMP = "bmp";
    public static final String IMAGE_TYPE_PNG = "png";
    public static final String IMAGE_TYPE_PSD = "psd";


    public static RenderedImage castToRenderedImage(Image img, String imageType) {
        return img instanceof RenderedImage ? (RenderedImage)img : toBufferedImage(img, imageType);
    }

    public static BufferedImage toBufferedImage(Image image, String imageType) {
        return toBufferedImage(image, imageType, null);
    }

    public static BufferedImage toBufferedImage(Image image, String imageType, Color backgroundColor) {
        int type = "png".equalsIgnoreCase(imageType) ? 2 : 1;
        return toBufferedImage(image, type, backgroundColor);
    }

    public static BufferedImage toBufferedImage(Image image, int imageType, Color backgroundColor) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage)image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType, backgroundColor);
            }

            return bufferedImage;
        } else {
            bufferedImage = copyImage(image, imageType, backgroundColor);
            return bufferedImage;
        }
    }

    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        img = (new ImageIcon(img)).getImage();
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight((ImageObserver)null), imageType);
        Graphics2D bGr = bimage.createGraphics();
        if (null != backgroundColor) {
            bGr.setColor(backgroundColor);
            bGr.fillRect(0, 0, bimage.getWidth(), bimage.getHeight());
        }
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }


    public static BufferedImage toImage(byte[] imageBytes) throws IOException {
        return read(new ByteArrayInputStream(imageBytes));
    }


    public static String toBase64DataUri(Image image, String imageType) throws IOException {
        return URLUtils.getDataUri("image/" + imageType, "base64", toBase64(image, imageType));
    }

    public static String toBase64(Image image, String imageType) throws IOException {
        byte[] bytes = toBytes(image, imageType);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] toBytes(Image image, String imageType) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, imageType, out);
        return out.toByteArray();
    }


    public static void write(Image image, String imageType, OutputStream out) throws IOException {
        write(image, imageType, getImageOutputStream(out));
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) throws IOException {
        return write(image, imageType, destImageStream, 1.0F);
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) throws IOException {
        return write(image, imageType, destImageStream, quality, null);
    }

    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality, Color backgroundColor) throws IOException {
        if (StringUtils.isBlank(imageType)) {
            imageType = "jpg";
        }

        BufferedImage bufferedImage = toBufferedImage(image, imageType, backgroundColor);
        ImageWriter writer = getWriter(bufferedImage, imageType);
        return write(bufferedImage, writer, destImageStream, quality);
    }

    public static void write(Image image, File targetFile) throws IOException {
        FileUtils.touch(targetFile);

        try (ImageOutputStream out = getImageOutputStream(targetFile)) {
            write(image, FileUtils.extName(targetFile), out);
        }

    }

    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) throws IOException {
        if (writer == null) {
            return false;
        } else {
            writer.setOutput(output);
            RenderedImage renderedImage = castToRenderedImage(image, "jpg");
            ImageWriteParam imgWriteParams = null;
            if (quality > 0.0F && quality < 1.0F) {
                imgWriteParams = writer.getDefaultWriteParam();
                if (imgWriteParams.canWriteCompressed()) {
                    imgWriteParams.setCompressionMode(2);
                    imgWriteParams.setCompressionQuality(quality);
                    ColorModel colorModel = renderedImage.getColorModel();
                    imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
                }
            }

            try {
                if (null != imgWriteParams) {
                    writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
                } else {
                    writer.write(renderedImage);
                }

                output.flush();
            } finally {
                writer.dispose();
            }

            return true;
        }
    }

    public static BufferedImage read(File imageFile) throws IOException {
        BufferedImage result;
        result = ImageIO.read(imageFile);

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
        } else {
            return result;
        }
    }

    public static BufferedImage read(InputStream imageStream) throws IOException {
        BufferedImage result = ImageIO.read(imageStream);
        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        } else {
            return result;
        }
    }

    public static ImageOutputStream getImageOutputStream(OutputStream out) throws IOException {
        ImageOutputStream result = ImageIO.createImageOutputStream(out);

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        } else {
            return result;
        }
    }

    public static ImageOutputStream getImageOutputStream(File outFile) throws IOException {
        ImageOutputStream result = ImageIO.createImageOutputStream(outFile);

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
        } else {
            return result;
        }
    }


    public static ImageWriter getWriter(Image img, String formatName) {
        ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
        Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        return iter.hasNext() ? iter.next() : null;
    }

    public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
        return op.filter(image, null);
    }

    public static Image filter(ImageFilter filter, Image image) {
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
    }
}
