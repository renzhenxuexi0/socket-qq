package com.client;

import com.client.service.UserService;
import com.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
    public Collection<Image> loadDefaultIcons() {
        return Collections.singletonList(new Image(Objects.requireNonNull(getClass().getResource("icons/logo.ico")).toExternalForm()));
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setTitle("登录");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
    }
}
