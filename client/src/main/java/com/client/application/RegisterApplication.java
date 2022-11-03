package com.client.application;

import com.client.utils.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class RegisterApplication {

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/register.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("注册界面");
        primaryStage.setScene(scene);
        StageManager.addStage("注册界面", primaryStage);
    }

}
