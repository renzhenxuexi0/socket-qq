package com.client.controller;

import com.client.ClientApp;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.utils.UserMemory;
import com.client.view.DialogBoxView;
import com.jfoenix.controls.JFXListView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;

@FXMLController
public class UserInterfaceController implements Initializable {

    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @FXML
    public ImageView backgroundImage;

    @Autowired
    private DialogBoxController dialogBoxController;

    @FXML
    private Pane userPane;
    @FXML
    private Label userName;
    @FXML
    private ImageView userHead;
    @FXML
    private JFXListView<Label> userListView;
    private Stage primaryStage;
    @Autowired
    private UserService userService;

    void buildUserList() {

        ObservableList<Label> userListLabel = FXCollections.observableArrayList();
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.5);

        for (int i = 0; i < UserMemory.users.size(); i++) {
            User user = UserMemory.users.get(i);
            if (!Objects.equals(user.getId(), UserMemory.myUser.getId())) {
                Label label = new Label();
                label.setText(user.getUsername());
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        // 获取对于的user
                        String text = label.getText();
                        System.out.println(text);
                        for (int i = 0; i < UserMemory.users.size(); i++) {
                            User user2 = UserMemory.users.get(i);
                            if (text.equals(user2.getUsername())){
                                UserMemory.talkUser = user2;
                            }
                        }
                        System.out.println(UserMemory.talkUser);

                        ClientApp.showView(DialogBoxView.class, Modality.NONE);
                    }
                });
                ImageView imageView = new ImageView(String.valueOf(getClass().getResource("headImage/head.png")));
                if (user.getLogin() == 0) {
                    imageView.setEffect(colorAdjust);
                }

                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                label.setGraphic(imageView);
                userListLabel.add(label);
            }
        }
        userListView.setItems(userListLabel);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userName.setText(UserMemory.myUser.getUsername());
        Image image = new Image(String.valueOf(getClass().getResource("headImage/head.png")));
        userHead.setImage(image);
        buildUserList();
        primaryStage = ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */

        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Collections.singletonList(userPane));
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
}
