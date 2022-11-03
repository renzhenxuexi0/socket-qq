module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    exports com.client.application;
    opens com.client.controller to javafx.fxml;
}