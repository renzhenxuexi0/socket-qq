package com.server;

import com.server.view.ServerView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.server.mapper"})
public class ServerMain extends AbstractJavaFxApplicationSupport {
    public static void main(String[] args) {
        launch(ServerMain.class, ServerView.class, args);
    }
}
