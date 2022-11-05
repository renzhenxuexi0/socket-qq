package com.client.application;

import com.client.utils.StageManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class UserApplication extends Application {

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/user.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("用户界面");
        primaryStage.setScene(scene);
        // 结束子线程
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        StageManager.addStage("用户界面", primaryStage);
    }

}
