package com.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileMsgVBox {
    private Hyperlink hyperlink1;
    private Hyperlink hyperlink2;

    private ProgressBar progressBar;

    private ImageView fileImage;

    private VBox vBox;

    private Label describeLabel;

    private boolean isAccomplish;

    public VBox fileMsgVBox(boolean isAccomplish, String fileName) {
        this.isAccomplish = isAccomplish;

        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: white");

        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER_LEFT);

        fileImage = new ImageView();
        fileImage.setFitHeight(60);
        fileImage.setFitWidth(60);
        fileImage.setPickOnBounds(true);
        fileImage.setPreserveRatio(true);

        Label fileNameLabel = new Label(fileName);
        fileNameLabel.setLayoutX(105);
        fileNameLabel.setLayoutY(39);
        fileNameLabel.setFont(Font.font(14));
        fileNameLabel.setTextFill(Color.valueOf("#111111"));
        fileNameLabel.setWrapText(true);

        describeLabel = new Label("");
        describeLabel.setTextFill(Color.valueOf("#111111"));

        hBox1.getChildren().addAll(fileImage, fileNameLabel);

        hyperlink1 = new Hyperlink();
        hyperlink1.setText("接受");
        hyperlink1.setTextFill(Color.valueOf("#111111"));
        hyperlink2 = new Hyperlink();
        hyperlink2.setText("打开文件所在位置");
        hyperlink2.setTextFill(Color.valueOf("#111111"));

        progressBar = new ProgressBar();
        progressBar.setMaxHeight(10);
        progressBar.setMinHeight(10);
        progressBar.setPrefHeight(10);
        progressBar.setPrefWidth(240);
        progressBar.setProgress(0.0);

        if (!isAccomplish) {
            vBox.getChildren().addAll(hBox1, progressBar, hyperlink1, describeLabel);
        } else {
            vBox.getChildren().addAll(hBox1, progressBar, describeLabel);
        }

        return vBox;
    }

    public void setHyperlink1OnAction(EventHandler<ActionEvent> actionEventEventHandler) {
        hyperlink1.setOnAction(actionEventEventHandler);
    }

    public void setHyperlink2OnAction(EventHandler<ActionEvent> actionEventEventHandler) {
        hyperlink2.setOnAction(actionEventEventHandler);
    }

    public void setProgressBarProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void setFileImage(Image image) {
        fileImage.setImage(image);
    }

    public void setProgressBarState(String info) {
        Platform.runLater(() -> {
            describeLabel.setText(info);
            vBox.getChildren().remove(progressBar);
            if (!isAccomplish) {
                vBox.getChildren().remove(hyperlink1);
                vBox.getChildren().add(2, hyperlink2);
            }
        });
    }

    public Hyperlink getHyperlink1() {
        return hyperlink1;
    }

    public Hyperlink getHyperlink2() {
        return hyperlink2;
    }
}
