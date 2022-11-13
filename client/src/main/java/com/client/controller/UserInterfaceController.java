package com.client.controller;

import com.client.ClientApp;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import com.client.view.DialogBoxView;
import com.client.view.RegisterView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class UserInterfaceController implements Initializable {

    @FXML
    public Pane mine;

    @FXML
    public ImageView head;

    @FXML
    public Label user_name;

    @FXML
    public Label user_label;

    private Stage primaryStage;

    @Autowired
    private UserService userService;

    // 用户界面的容器
    @FXML
    private GridPane ui;


    private GridPane gridPane;

    void buildUserList() {
        // 不断生成新的用户状态栏
        for (int i = 0; i < UserMemory.users.size(); i++) {
            gridPane.add(new Label(UserMemory.users.get(i).getUsername()), 0, i);
            if (UserMemory.users.get(i).getLogin() == 1) {
                gridPane.add(new Circle(10, Color.rgb(30, 144, 255)), 1, i);
            } else {
                gridPane.add(new Circle(10, Color.rgb(172, 172, 172)), 1, i);
            }
            // 设置每一行的高度
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(40);
            gridPane.getRowConstraints().add(i, rowConstraints);
        }
        // 设置每列布局
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setPrefWidth(274);
        gridPane.getColumnConstraints().add(0, columnConstraints);

        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setHalignment(HPos.CENTER);
        columnConstraints2.setPrefWidth(30);
        gridPane.getColumnConstraints().add(1, columnConstraints2);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane = new GridPane();// 创建一个gridPane
        ScrollPane scrollPane = new ScrollPane();// 同上
        ui.add(scrollPane, 0, 1);
        scrollPane.setContent(gridPane);
        buildUserList();
        primaryStage = ClientApp.getStage();
        // 开启定时任务
//        ScheduledService<Void> scheduledService = new ScheduledService<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                buildUserList();
//                return null;
//            }
//        };
//        scheduledService.setPeriod(Duration.seconds(5));
//        scheduledService.start();
    }


    public void clickMyHeadButton(MouseEvent mouseEvent) {
    }

    public void turnToDialogBoxEvent(ActionEvent event) {
        primaryStage.setHeight(400);
        primaryStage.setWidth(600);
        primaryStage.setTitle("聊天界面");
        ClientApp.showView(DialogBoxView.class);
    }
}
