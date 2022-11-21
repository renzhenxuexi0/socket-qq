package com.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.controller.FileMsgVBox;
import com.client.pojo.Code;
import com.client.pojo.FileMsg;
import com.client.pojo.Result;
import com.client.pojo.SendMsg;
import com.client.service.FileMsgService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public class SendFile {
    public static void sendFileMsg(File aimFile, SimpleDateFormat simpleDateFormat, ThreadPoolExecutor poolExecutor, FileMsgService fileMsgService, File fileMsgLog, ListView<SendMsg> msgListView) {
        if (aimFile.isFile()) {
            SendMsg sendMsg = new SendMsg();
            sendMsg.setType(1);

            FileMsgVBox fileMsgVBox = new FileMsgVBox();
            sendMsg.setVBox(fileMsgVBox.fileMsgVBox(true, aimFile.getName()));
            // 获取文件图标
            WritableImage fileIcon = GetFileIcon.getFileIcon(aimFile);
            fileMsgVBox.setFileImage(fileIcon);

            Date date = new Date();//获得当前时间
            String msgTime = simpleDateFormat.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

            FileMsg fileMsg = new FileMsg();
            fileMsg.setMessageTime(msgTime);
            fileMsg.setSenderId(UserMemory.myUser.getId());
            fileMsg.setReceiveId(UserMemory.talkUser.getId());
            fileMsg.setFileName(aimFile.getName());

            sendMsg.setMsg(fileMsg);

            Socket socket = fileMsgService.sendOfflineFileMsg();
            if (socket != null) {
                poolExecutor.execute(() -> {
                    long length = 0;
                    PrintStream socketPrintStream = null;
                    DataOutputStream socketOutputStream = null;
                    File logFile = null;
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


                        logFile = new File(System.getProperty("user.home") + "\\.socket\\" + aimFile.getName().split("\\.")[0] + ".log");
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        try (
                                // 操作日志文件
                                BufferedReader logFileReader = new BufferedReader(new FileReader(logFile));
                                PrintWriter logFileWriter = new PrintWriter(new FileWriter(fileMsgLog), false);
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            // 读取上次传送的位置
                            String s = logFileReader.readLine();
                            long pos;
                            if (s != null) {
                                pos = Long.parseLong(s);
                            } else {
                                pos = 0L;
                            }
                            // 发送 开始发送
                            Result resultStart = new Result();
                            resultStart.setCode(Code.SEND_OFFLINE_FILE_MSG);
                            fileMsg.setStartPoint(pos);
                            resultStart.setObject(fileMsg);
                            socketPrintStream.println(JSON.toJSONString(resultStart, SerializerFeature.WriteMapNullValue));

                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            long accumulationSize = 0L;
                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                logFileWriter.println(accumulationSize);
                                logFileWriter.flush();
                                double schedule = (double) accumulationSize / (double) length;
                                Platform.runLater(() -> fileMsgVBox.setProgressBarProgress(schedule));
                                // 暂停 停止传输
                            }
                            fileMsg.setEndPoint(accumulationSize + pos);
                            FileUtils.writeStringToFile(fileMsgLog, JSON.toJSONString(fileMsg) + "\n", "UTF-8", true);

                            if (accumulationSize == length) {
                                fileMsgVBox.setProgressBarState("发送完成");
                            }

                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "未知错误").showAndWait());
                        e.printStackTrace();
                    }
                });
                MsgMemory.sendMsgList.add(sendMsg);
                MsgMemory.sendMsgListSort(simpleDateFormat);
                msgListView.setItems(FXCollections.observableArrayList(MsgMemory.sendMsgList));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误！");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }
}
