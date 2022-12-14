package com.client.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;
import sun.awt.shell.ShellFolder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Slf4j
public class GetFileIcon {

    public static WritableImage getFileIcon(File file) {
        try {
            Image icon = ShellFolder.getShellFolder(file).getIcon(true);
            BufferedImage im = new BufferedImage(icon.getWidth(null),
                    icon.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = im.createGraphics();
            g.drawImage(icon, 0, 0, null);
            g.dispose();
            WritableImage writableImage = SwingFXUtils.toFXImage(im, null);
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png",
                    new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + file.getName() + ".png"));
            return writableImage;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

}
