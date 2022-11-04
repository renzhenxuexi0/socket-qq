package com.server.application;

import com.server.controller.ServerController;

public class ServerApplication {
    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.userServerController();
    }
}
