module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.qq.app.sockteqq to javafx.fxml;
    exports com.qq.app.sockteqq;
    exports com.qq;
    opens com.qq to javafx.fxml;
}