module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.qq.app.socktqq to javafx.fxml;
    exports com.qq.app.socktqq;
}