package com.server;

import com.server.view.ServerView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@MapperScan({"com.server.mapper"})
public class ServerApp extends AbstractJavaFxApplicationSupport implements ApplicationRunner {
    public static void main(String[] args) throws IOException {
        launch(ServerApp.class, ServerView.class, args);
        
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
