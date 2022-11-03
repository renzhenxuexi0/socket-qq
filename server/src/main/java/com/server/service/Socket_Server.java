package com.server.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Socket_Server {


    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = new ServerSocket(8888);

        while (true) {

            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = serverSocket.accept();
                        socket.getInputStream();
                        InputStream is = socket.getInputStream();
                        Reader reader = new InputStreamReader(is);
                        BufferedReader bfr = new BufferedReader(reader);

                        String msg;
                        while ((msg = bfr.readLine()) != null) {
                            System.out.println(msg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}

