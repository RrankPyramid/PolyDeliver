package com.example.comp2411project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PolyUBazController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}