package com.client.utils;

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

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author itqn
 */
public class ProgressStageUtil {

    private ThreadPoolExecutor threadPoolExecutor;
    private Stage stage;
    private Task<?> work;

    public ProgressStageUtil() {
    }

    /**
     * 创建
     *
     * @param parent
     * @param work
     * @param ad
     * @return
     */
    public static ProgressStageUtil of(Stage parent, ThreadPoolExecutor threadPoolExecutor, Task<?> work, String ad) {
        ProgressStageUtil ps = new ProgressStageUtil();
        ps.threadPoolExecutor = threadPoolExecutor;
        ps.work = Objects.requireNonNull(work);
        ps.initUI(parent, ad);
        return ps;
    }

    /**
     * 显示
     */
    public void show() {
        threadPoolExecutor.submit(work);
        stage.show();
    }

    private void initUI(Stage parent, String ad) {
        stage = new Stage();
        stage.initOwner(parent);
        // style
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        // message
        Label adLbl = new Label(ad);
        adLbl.setTextFill(Color.BLUE);


        // progress
//        ProgressIndicator indicator = new ProgressIndicator();
//        indicator.setProgress(-1);
//        indicator.progressProperty().bind(work.progressProperty());
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
        stage.setWidth(ad.length() * 10 + 10);
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