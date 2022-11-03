package com.client.utils;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stage管理类
 */
public class StageManager {
    private static final Map<String, Stage> stageMap = new HashMap<>();//存放所有的Stage实例

    public static Stage getStage(String name){
        return stageMap.get(name);
    }

    public static void closeStage(String name){
        stageMap.get(name).close();
    }

    //实现Stage的跳转，从currentStage跳转到targetStage
    public static void jump(String currentStageName, String targetStageName){
        stageMap.get(currentStageName).close();
        stageMap.get(targetStageName).show();
    }

    public static void release(String name){
        stageMap.remove(name);
    }

    public static void addStage(String title, Stage stage) {
        stageMap.put(title, stage);
    }
}
