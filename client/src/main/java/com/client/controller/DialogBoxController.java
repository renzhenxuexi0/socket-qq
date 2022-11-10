package com.client.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class DialogBoxController implements Initializable {

    private Stage stage;

    Scene scene;

    @FXML
    private TextArea input;

    @FXML
    private Button send;

    @FXML
    private TextArea inputArea;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

//    @FXML
//    void closeThePage(ActionEvent event) {
//
//    }
//
//
//    public void closeThePage(ActionEvent actionEvent) {
//    }
}