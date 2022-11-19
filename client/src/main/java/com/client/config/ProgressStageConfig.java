package com.client.config;

import com.jfoenix.controls.JFXSpinner;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author itqn
 */
@Data
@Configuration
public class ProgressStageConfig {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private Stage stage;

    private Stage parent;

    private String text;

    private Task<?> work;


    /**
     * 显示
     */
    public void show() {
        initUI();
        threadPoolExecutor.submit(work);
        stage.show();
    }

    private void initUI() {
        stage = new Stage();
        stage.initOwner(parent);
        // style
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        // message
        Label adLbl = new Label(text);
        adLbl.setTextFill(Color.BLUE);

        // 组件库组件
        JFXSpinner jfxSpinner = new JFXSpinner();
        jfxSpinner.setRadius(30);
        jfxSpinner.setStartingAngle(20);

        // pack
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().addAll(jfxSpinner, adLbl);

        // scene
        Scene scene = new Scene(vBox);
        scene.setFill(null);
        stage.setScene(scene);
        stage.setWidth(text.length() * 10 + 10);
        stage.setHeight(100);

        // show center of parent
        double x = parent.getX() + (parent.getWidth() - stage.getWidth()) / 2;
        double y = parent.getY() + (parent.getHeight() - stage.getHeight()) / 2;
        stage.setX(x);
        stage.setY(y);

        // close if work finish
        work.setOnSucceeded(e -> stage.close());
    }
}