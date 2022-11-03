module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mybatis;
    requires druid;

    exports com.server.application;
    opens com.server.controller to javafx.fxml;
}