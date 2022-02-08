package com.example.comp2411project;

import cn.edu.scau.biubiusuisui.annotation.FXScan;
import cn.edu.scau.biubiusuisui.config.FXPlusApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

@FXScan(
         base = {"com.example.comp2411project.controller"}
)
public class PolyUBazApplication extends Application {

    public PolyUBazApplication() {}

    @Override
    public void start(Stage stage) throws IOException {
        FXPlusApplication.start(PolyUBazApplication.class);
    }
}
