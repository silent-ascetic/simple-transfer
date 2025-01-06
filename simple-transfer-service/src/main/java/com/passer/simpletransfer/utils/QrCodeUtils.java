package com.passer.simpletransfer.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.passer.simpletransfer.config.QrConfig;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class QrCodeUtils {

    public static String generateAsSvg(String content, QrConfig qrConfig) throws IOException, WriterException {
        BitMatrix bitMatrix = encode(content, qrConfig);
        return toSVG(bitMatrix, qrConfig);
    }

    public static BitMatrix encode(String content, QrConfig config) throws WriterException {
        return encode(content, BarcodeFormat.QR_CODE, config);
    }

    public static BitMatrix encode(String content, BarcodeFormat format, QrConfig config) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        if (null == config) {
            config = new QrConfig();
        }
        return multiFormatWriter.encode(content, format, config.getWidth(), config.getHeight(), config.toHints(format));
    }

    public static String toSVG(BitMatrix matrix, QrConfig qrConfig) throws IOException {
        return toSVG(matrix, qrConfig.getForeColor(), qrConfig.getBackColor(), qrConfig.getImg(), qrConfig.getRatio());
    }

    public static String toSVG(BitMatrix matrix, Integer foreColor, Integer backColor, Image logoImg, int ratio) throws IOException {
        StringBuilder sb = new StringBuilder();
        int qrWidth = matrix.getWidth();
        int qrHeight = matrix.getHeight();
        int moduleHeight = qrHeight == 1 ? qrWidth / 2 : 1;

        int logoWidth;
        for(int y = 0; y < qrHeight; ++y) {
            for(logoWidth = 0; logoWidth < qrWidth; ++logoWidth) {
                if (matrix.get(logoWidth, y)) {
                    sb.append(" M").append(logoWidth).append(",").append(y).append("h1v").append(moduleHeight).append("h-1z");
                }
            }
        }

        qrHeight *= moduleHeight;
        String logoBase64 = "";
        logoWidth = 0;
        int logoHeight = 0;
        int logoX = 0;
        int logoY = 0;
        if (logoImg != null) {
            logoBase64 = ImgUtils.toBase64DataUri(logoImg, "png");
            if (qrWidth < qrHeight) {
                logoWidth = qrWidth / ratio;
                logoHeight = logoImg.getHeight(null) * logoWidth / logoImg.getWidth((ImageObserver)null);
            } else {
                logoHeight = qrHeight / ratio;
                logoWidth = logoImg.getWidth(null) * logoHeight / logoImg.getHeight((ImageObserver)null);
            }

            logoX = (qrWidth - logoWidth) / 2;
            logoY = (qrHeight - logoHeight) / 2;
        }

        StringBuilder result = StringUtils.builder();
        result.append("<svg width=\"").append(qrWidth).append("\" height=\"").append(qrHeight).append("\" \n");
        Color fore;
        if (backColor != null) {
            fore = new Color(backColor, true);
            result.append("style=\"background-color:rgba(").append(fore.getRed()).append(",").append(fore.getGreen()).append(",").append(fore.getBlue()).append(",").append(fore.getAlpha()).append(")\"\n");
        }

        result.append("viewBox=\"0 0 ").append(qrWidth).append(" ").append(qrHeight).append("\" \n");
        result.append("xmlns=\"http://www.w3.org/2000/svg\" \n");
        result.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" >\n");
        result.append("<path d=\"").append(sb).append("\" ");
        if (foreColor != null) {
            fore = new Color(foreColor, true);
            result.append("stroke=\"rgba(").append(fore.getRed()).append(",").append(fore.getGreen()).append(",").append(fore.getBlue()).append(",").append(fore.getAlpha()).append(")\"");
        }

        result.append(" /> \n");
        if (StringUtils.isNotBlank(logoBase64)) {
            result.append("<image xlink:href=\"").append(logoBase64).append("\" height=\"").append(logoHeight).append("\" width=\"").append(logoWidth).append("\" y=\"").append(logoY).append("\" x=\"").append(logoX).append("\" />\n");
        }

        result.append("</svg>");
        return result.toString();
    }

}
