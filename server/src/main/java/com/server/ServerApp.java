package com.server;

import com.server.view.ServerView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.bytedeco.javacv.CanvasFrame;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@SpringBootApplication
@MapperScan({"com.server.mapper"})
public class ServerApp extends AbstractJavaFxApplicationSupport implements ApplicationRunner {
    public static void main(String[] args) throws IOException {
        launch(ServerApp.class, ServerView.class, args);
        // 创建数据包套接字对象 接受端需要指定端口号， 如果端口号是被占用了的会报错
        DatagramSocket socket = new DatagramSocket(9090);
        // 创建数据包对象接受数据
        // 参数一 字节数组
        // 二 大小
        System.out.println("======服务端启动=======");
        byte[] buffer = new byte[1024 * 1000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        CanvasFrame cFrame = new CanvasFrame("欢迎来到直播间", CanvasFrame.getDefaultGamma());
        while (true) {
            socket.receive(packet);

            BufferedImage read = ImageIO.read(new ByteArrayInputStream(buffer, 0, packet.getLength()));

            // 关闭窗口的同时关闭程序
            cFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // 窗口置顶
            if (cFrame.isAlwaysOnTopSupported()) {
                cFrame.setAlwaysOnTop(true);
            }
            cFrame.showImage(read);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
