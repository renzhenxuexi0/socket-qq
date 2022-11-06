module com.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires fastjson;
    requires org.mybatis;
    requires druid;
    requires java.sql;

    exports com.server.application;
    exports com.server.pojo;
    exports com.server.mapper;
    exports com.server.controller;
    exports com.server.utils;
    exports com.server.service;

    opens com.server.controller to javafx.fxml, fastjson;
    opens com.server.utils to druid;
}