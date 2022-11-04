module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires fastjson;

    exports com.client.application;
    exports com.client.pojo;
    opens com.client.controller to javafx.fxml;
}