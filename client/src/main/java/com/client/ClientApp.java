package com.client;

import com.client.service.UserService;
import com.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApp extends AbstractJavaFxApplicationSupport {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        launch(ClientApp.class, LoginView.class, args);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setTitle("登录");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
    }
}
