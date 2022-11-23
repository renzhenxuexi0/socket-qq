package com.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.controller.ChatInterfaceController;
import com.client.controller.FileMsgVBox;
import com.client.pojo.Code;
import com.client.pojo.FileMsg;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.FileMsgService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class SendFile {

    // 离线
    public static void sendFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, ThreadPoolExecutor poolExecutor, FileMsgService fileMsgService, ProgressBar progressBar, Label progressLabel, Label sendStateLabel, Image headImage, VBox msgVBox) {
        if (aimFile.isFile()) {
            Socket socket = fileMsgService.sendOfflineFileMsg();
            if (socket != null) {
                poolExecutor.execute(() -> {
                    long length;
                    PrintStream socketPrintStream;
                    DataOutputStream socketOutputStream;
                    File logFile;
                    try {
                        // 文件总大小
                        length = aimFile.length();

                        // 一些流的获取
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();

                        // 读写文本消息的流
                        socketPrintStream = new PrintStream(os);
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

                        // 发送文件流，字节缓冲流即可
                        socketOutputStream = new DataOutputStream(socket.getOutputStream());


                        logFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + aimFile.getName().split("\\.")[0] + ".log");
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        try (
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            // 读取上次传送的位置
                            String s = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
                            long pos;
                            if (!Objects.equals(s, "")) {
                                pos = Long.parseLong(s);
                            } else {
                                pos = 0L;
                            }
                            // 发送 开始发送
                            Result resultStart = new Result();
                            resultStart.setCode(Code.SEND_OFFLINE_FILE_MSG);
                            FileMsg fileMsg = creatFileMsg(aimFile, simpleDateFormat, pos, UserMemory.talkUser);
                            fileMsg.setOnline(0);
                            fileMsg.setSize(length);

                            resultStart.setObject(fileMsg);
                            socketPrintStream.println(JSON.toJSONString(resultStart, SerializerFeature.WriteMapNullValue));

                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            long accumulationSize = pos;


                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                double progress = accumulationSize / (double) length;
                                FileUtils.writeStringToFile(logFile, String.valueOf(accumulationSize), StandardCharsets.UTF_8, false);
                                Platform.runLater(() -> {
                                    progressBar.setProgress(progress);
                                    progressLabel.setText(progress * 100 + "%");
                                });
                            }

                            if (accumulationSize == length) {
                                Platform.runLater(() -> sendStateLabel.setText("离线文件发送完成"));
                                CreatFileMsgPane(headImage, msgVBox, logFile, fileMsg);
                            }


                            fileMsg.setEndPoint(accumulationSize + pos);
                            UserMemory.fileMsgList.add(fileMsg);

                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "未知错误").showAndWait());
                        e.printStackTrace();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误！");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }

    // 群发离线信息
    public static void sendFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, ThreadPoolExecutor poolExecutor, FileMsgService fileMsgService, List<User> users) {
        if (aimFile.isFile()) {
            Socket socket = fileMsgService.sendOfflineFileMsg();
            if (socket != null) {
                poolExecutor.execute(() -> {
                    long length;
                    PrintStream socketPrintStream;
                    DataOutputStream socketOutputStream;
                    File logFile;
                    try {
                        // 文件总大小
                        length = aimFile.length();

                        // 一些流的获取
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();

                        // 读写文本消息的流
                        socketPrintStream = new PrintStream(os);
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

                        // 发送文件流，字节缓冲流即可
                        socketOutputStream = new DataOutputStream(socket.getOutputStream());


                        logFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + aimFile.getName().split("\\.")[0] + ".log");
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        try (
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            // 读取上次传送的位置
                            String s = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
                            long pos;
                            if (!Objects.equals(s, "")) {
                                pos = Long.parseLong(s);
                            } else {
                                pos = 0L;
                            }
                            // 发送 开始发送
                            Result resultStart = new Result();
                            resultStart.setCode(Code.SEND_GROUP_OFFLINE_FILE_MSG);
                            Date date = new Date();//获得当前时间
                            String msgTime = simpleDateFormat.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中
                            List<FileMsg> fileMsgList = new ArrayList<>();
                            for (User user : users) {
                                FileMsg fileMsg = new FileMsg();
                                fileMsg.setMessageTime(msgTime);
                                fileMsg.setSenderId(UserMemory.myUser.getId());
                                fileMsg.setReceiverId(UserMemory.talkUser.getId());
                                fileMsg.setFileName(aimFile.getName());
                                fileMsg.setStartPoint(pos);
                                fileMsg.setOnline(0);
                                fileMsg.setSize(length);
                                fileMsgList.add(fileMsg);
                            }

                            resultStart.setObject(fileMsgList);
                            socketPrintStream.println(JSON.toJSONString(resultStart, SerializerFeature.WriteMapNullValue));

                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            long accumulationSize = pos;


                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                double progress = accumulationSize / (double) length;
                                FileUtils.writeStringToFile(logFile, String.valueOf(accumulationSize), StandardCharsets.UTF_8, false);
                            }

                            for (FileMsg fileMsg : fileMsgList) {
                                fileMsg.setEndPoint(accumulationSize + pos);
                            }

                            UserMemory.fileMsgList.addAll(fileMsgList);
                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "未知错误").showAndWait());
                        e.printStackTrace();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误！");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }

    private static FileMsg creatFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, long pos, User talkUser) {
        Date date = new Date();//获得当前时间
        String msgTime = simpleDateFormat.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

        FileMsg fileMsg = new FileMsg();
        fileMsg.setMessageTime(msgTime);
        fileMsg.setSenderId(UserMemory.myUser.getId());
        fileMsg.setReceiverId(talkUser.getId());
        fileMsg.setFileName(aimFile.getName());
        fileMsg.setStartPoint(pos);
        return fileMsg;
    }

    // 在线
    public static void sendFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, ThreadPoolExecutor poolExecutor, FileMsgService fileMsgService, Integer port, ProgressBar progressBar, Label progressLabel, Label sendStateLabel, Image headImage, VBox msgVBox) {
        if (aimFile.isFile()) {

            Socket socket = fileMsgService.sendOfflineFileMsg(UserMemory.talkUser.getIp(), port);
            if (socket != null) {
                poolExecutor.execute(() -> {
                    long length;
                    PrintStream socketPrintStream;
                    DataOutputStream socketOutputStream;
                    File logFile;
                    try {
                        // 文件总大小
                        length = aimFile.length();

                        // 一些流的获取
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();

                        // 读写文本消息的流
                        socketPrintStream = new PrintStream(os);
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

                        // 发送文件流，字节缓冲流即可
                        socketOutputStream = new DataOutputStream(socket.getOutputStream());

                        logFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + aimFile.getName().split("\\.")[0] + ".log");
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        try (
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            // 读取上次传送的位置
                            String s = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
                            long pos;
                            if (!Objects.equals(s, "")) {
                                pos = Long.parseLong(s);
                            } else {
                                pos = 0L;
                            }

                            // 发送 开始发送
                            Result resultStart = new Result();
                            resultStart.setCode(Code.SEND_FILE_MSG);

                            FileMsg fileMsg = creatFileMsg(aimFile, simpleDateFormat, pos, UserMemory.talkUser);
                            fileMsg.setOnline(1);
                            fileMsg.setSize(length);

                            resultStart.setObject(fileMsg);
                            socketPrintStream.println(JSON.toJSONString(resultStart, SerializerFeature.WriteMapNullValue));

                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            long accumulationSize = pos;
                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                FileUtils.writeStringToFile(logFile, String.valueOf(accumulationSize), StandardCharsets.UTF_8, false);
                                double progress = (double) accumulationSize / (double) length;
                                Platform.runLater(() -> {
                                    progressBar.setProgress(progress);
                                    progressLabel.setText(progress * 100 + "%");
                                });
                            }

                            if (accumulationSize == length) {
                                Platform.runLater(() -> sendStateLabel.setText("在线文件发送完成"));
                                CreatFileMsgPane(headImage, msgVBox, logFile, fileMsg);
                            }

                            fileMsg.setEndPoint(accumulationSize + pos);
                            UserMemory.fileMsgList.add(fileMsg);

                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "未知错误").showAndWait());
                        e.printStackTrace();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误！");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }

    public static void sendFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, ThreadPoolExecutor poolExecutor, FileMsgService fileMsgService, Integer port, User talkUser) {
        if (aimFile.isFile()) {

            Socket socket = fileMsgService.sendOfflineFileMsg(UserMemory.talkUser.getIp(), port);
            if (socket != null) {
                poolExecutor.execute(() -> {
                    long length;
                    PrintStream socketPrintStream;
                    DataOutputStream socketOutputStream;
                    File logFile;
                    try {
                        // 文件总大小
                        length = aimFile.length();

                        // 一些流的获取
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();

                        // 读写文本消息的流
                        socketPrintStream = new PrintStream(os);
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

                        // 发送文件流，字节缓冲流即可
                        socketOutputStream = new DataOutputStream(socket.getOutputStream());

                        logFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + aimFile.getName().split("\\.")[0] + ".log");
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        try (
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            // 读取上次传送的位置
                            String s = FileUtils.readFileToString(logFile, StandardCharsets.UTF_8);
                            long pos;
                            if (!Objects.equals(s, "")) {
                                pos = Long.parseLong(s);
                            } else {
                                pos = 0L;
                            }

                            // 发送 开始发送
                            Result resultStart = new Result();
                            resultStart.setCode(Code.SEND_FILE_MSG);

                            FileMsg fileMsg = creatFileMsg(aimFile, simpleDateFormat, pos, talkUser);
                            fileMsg.setOnline(1);
                            fileMsg.setSize(length);

                            resultStart.setObject(fileMsg);
                            socketPrintStream.println(JSON.toJSONString(resultStart, SerializerFeature.WriteMapNullValue));

                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            long accumulationSize = pos;
                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                FileUtils.writeStringToFile(logFile, String.valueOf(accumulationSize), StandardCharsets.UTF_8, false);
                                double progress = (double) accumulationSize / (double) length;
                            }
                            if (accumulationSize == length) {
                                logFile.delete();
                            }

                            fileMsg.setEndPoint(accumulationSize + pos);
                            UserMemory.fileMsgList.add(fileMsg);

                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "未知错误").showAndWait());
                        e.printStackTrace();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误！");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }

    private static void CreatFileMsgPane(Image headImage, VBox msgVBox, File logFile, FileMsg fileMsg) throws IOException {
        logFile.delete();
        Parent root = FXMLLoader.load(Objects.requireNonNull(ChatInterfaceController.class.getResource("fxml/msgCell.fxml")));
        Label userName = (Label) root.lookup("#userName");
        Label sendTime = (Label) root.lookup("#sendTime");
        Label sendContent = (Label) root.lookup("#sendContent");
        ImageView senderImage = (ImageView) root.lookup("#senderImage");

        FileMsgVBox fileMsgVBox = new FileMsgVBox();
        sendContent.setGraphic(fileMsgVBox.fileMsgVBox(true, fileMsg.getFileName()));
        sendContent.setGraphicTextGap(0);
        sendContent.setStyle("-fx-background-color:  #95EC69; -fx-border-radius: 45; -fx-background-radius: 45;");

        fileMsgVBox.setFileImage(new Image(String.valueOf(new File(System.getProperty("user.home") + "\\.socket\\"
                + UserMemory.myUser.getAccount() + "\\" + fileMsg.getFileName() + ".png").toURI())));
        fileMsgVBox.setProgressBarState("已经发送完信息");

        userName.setText(UserMemory.myUser.getUsername());
        sendTime.setText(fileMsg.getMessageTime());
        senderImage.setImage(headImage);

        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Platform.runLater(() -> msgVBox.getChildren().add(root));
    }
}
