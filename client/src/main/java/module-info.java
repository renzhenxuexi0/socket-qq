module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires fastjson;

    exports com.client.application;
    exports com.client.pojo;
    exports com.client.service;
    exports com.client.utils;

    opens com.client.controller to javafx.fxml;
}