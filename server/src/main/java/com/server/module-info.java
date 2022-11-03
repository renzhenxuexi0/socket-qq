module com.qq.app.socktqq {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mybatis;
    requires druid;
    requires mysql.connector.java;
    exports com.server.application;
    opens com.server.controller to javafx.fxml;
}