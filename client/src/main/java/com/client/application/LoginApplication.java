package com.client.application;

import com.client.utils.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginApplication extends Application {

    private static final StageManager stageManager = new StageManager();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/login.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("登录界面");
        primaryStage.setScene(scene);
        stageManager.addStage("登录界面", primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
