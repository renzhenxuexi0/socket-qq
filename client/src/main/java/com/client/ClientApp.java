package com.client;

import com.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@SpringBootApplication
public class ClientApp extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ClientApp.class, LoginView.class, args);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public Collection<Image> loadDefaultIcons() {
        return Collections.singleton(new Image(Objects.requireNonNull(ClientApp.class.getResource("icons/logo.png")).toExternalForm()));
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.setTitle("登录");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
    }
}
