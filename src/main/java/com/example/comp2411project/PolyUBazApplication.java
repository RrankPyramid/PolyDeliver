package com.example.comp2411project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PolyUBazApplication extends Application {
    private Stage primaryStage;
    private VBox rootLayout;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("PolyUBaz");
        initRootLayout();
    }

    public void initRootLayout(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(PolyUBazApplication.class.getResource("login.fxml"));
            rootLayout = fxmlLoader.load();
            Scene scene = new Scene(rootLayout, 320, 240);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch();
    }
}