package com.client;

import com.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientMain extends AbstractJavaFxApplicationSupport {
    public static void main(String[] args) {
        launch(ClientMain.class, LoginView.class, args);
    }
}
