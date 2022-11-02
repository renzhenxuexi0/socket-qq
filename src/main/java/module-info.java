module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;
    requires druid;

    exports com.qq;
    opens com.qq to javafx.fxml;
}