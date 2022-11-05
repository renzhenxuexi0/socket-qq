package com.server.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ServerApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/server.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("服务端");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
