package com.client.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtils {
    public static void main(String[] args) throws Exception {
        String originPath = "E:\\workspace_java\\socket-qq\\client\\src\\main\\resources\\com\\client\\controller\\headImage\\head.png";
        String outputPath = "E:\\workspace_java\\socket-qq\\client\\src\\main\\resources\\com\\client\\controller\\headImage\\head-black.png";

        //1、图片转黑白
        binaryImage(ImageIO.read(new File(originPath)), outputPath);
    }

    public static void binaryImage(BufferedImage image, String outputFilePath) throws Exception {

        //如传入原图路径 将以下两行代码打开即可，并将参数设置位inputFilePath
//        File file = new File(inputFilePath);
//        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        File newFile = new File(outputFilePath);
        //输出图片格式可随便定义 如: jpg\png\bmp等
        ImageIO.write(grayImage, "bmp", newFile);
    }
}
