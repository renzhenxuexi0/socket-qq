module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports com.client.application;
    exports com.client.pojo;
    opens com.client.controller to javafx.fxml;
}