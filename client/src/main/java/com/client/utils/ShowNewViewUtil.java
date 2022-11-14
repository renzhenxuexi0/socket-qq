package com.client.utils;

import de.felixroske.jfxsupport.AbstractFxmlView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ShowNewViewUtil {
    private static Stage newStage;

    public static Stage showView(AbstractFxmlView view, Stage primaryStage) {
        Scene newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }

        if (newStage == null) {
            newStage = new Stage();
            newStage.setScene(newScene);
            newStage.initModality(Modality.NONE);
            newStage.initOwner(primaryStage);
            newStage.setTitle("MIQ聊天界面");
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.show();
            newStage.close();
        }
        return newStage;
    }
}
