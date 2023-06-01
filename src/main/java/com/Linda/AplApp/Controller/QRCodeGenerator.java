package com.Linda.AplApp.Controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QRCodeGenerator {
    private static final int WIDTH = 300; // Bredd på QR-koden
    private static final int HEIGHT = 300; // Höjd på QR-koden

    public static void main(String[] args) {
        LocalDateTime attendanceTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedAttendanceTime = attendanceTime.format(formatter);

        try {
            BufferedImage qrCodeImage = generateQRCodeImage(formattedAttendanceTime);
            File outputFile = new File("qrcode.png");
            ImageIO.write(qrCodeImage, "PNG", outputFile);
            System.out.println("QR-koden har genererats och sparats som qrcode.png");
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage generateQRCodeImage(String attendanceTime) throws WriterException {
        String data = "Närvarotid: " + attendanceTime;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
        int matrixWidth = bitMatrix.getWidth();
        BufferedImage qrCodeImage = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        qrCodeImage.createGraphics();

        Graphics2D graphics = (Graphics2D) qrCodeImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        return qrCodeImage;
    }
}


