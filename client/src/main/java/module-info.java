module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.client.application;
    opens com.client.controller to javafx.fxml;
}