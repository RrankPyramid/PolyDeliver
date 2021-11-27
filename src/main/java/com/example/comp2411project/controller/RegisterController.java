package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXRedirect;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.util.Customer;
import com.example.comp2411project.util.Deliverman;
import com.example.comp2411project.util.Merchant;
import com.example.comp2411project.util.Table;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

@FXController(
        path = "fxml/customer_register.fxml"
)
@FXWindow(
        title = "register",
        icon = "image/money.png"
)
public class RegisterController extends FXBaseController {
    @Override
    public void initialize(){
        usernameField.setPromptText("Enter your username");
        passwordField.setPromptText("Enter your password");
        confirmPasswordField.setPromptText("Re-enter your password");
        phoneField.setPromptText("Enter your phone");
        usernameField.getParent().requestFocus();
        passwordField.getParent().requestFocus();
        confirmPasswordField.getParent().requestFocus();
        confirmPasswordField.focusedProperty().addListener(
                ((observable, oldValue, newValue) ->
                {
                    if(!newValue) {
                        if(!passwordField.getText().equals(confirmPasswordField.getText())){
                            confirmErrorText.setText("The password input twice does not match, please try again");
                            confirmErrorText.setTextFill(Color.RED);
                        }
                        else{
                            confirmErrorText.setText("");
                        }
                    }else{
                        confirmErrorText.setText("");
                    }
                })
        );
    }

    @FXML
    JFXTextField usernameField;

    @FXML
    JFXPasswordField passwordField;

    @FXML
    JFXPasswordField confirmPasswordField;

    @FXML
    JFXTextField phoneField;

    @FXML
    JFXButton backButton;

    @FXML
    JFXButton confirmButton;

    @FXML
    Label errorText;

    @FXML
    Label confirmErrorText;

    @FXML
    void afterBackButtonClick(){
        ((Stage) this.getScene().getWindow()).close();
    }

    @FXML
    void afterConfirmButtonClick(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phoneNo = phoneField.getText();
        if(username.isEmpty() || password.isEmpty() || phoneNo.isEmpty() || !password.equals(confirmPasswordField.getText())){
            errorText.setText("Invalid filling. Please check your filling. ");
            errorText.setTextFill(Color.RED);
            return;
        }

        Cache cache = Cache.getInstance();
        try{
            switch (cache.getTableType()){
                case Table.CUSTOMER:
                    cache.addToCache((Customer) new Customer(username, password, phoneNo).pushInfo());
                    break;
                case Table.DELIVERMAN:
                    cache.addToCache((Deliverman) new Deliverman(username, password, phoneNo).pushInfo());
                    break;
                case Table.MERCHANT:
                    cache.addToCache((Merchant) new Merchant(username, password, phoneNo).pushInfo());
                    break;
                default:
                    throw new IllegalAccessError();
            }
        }catch (SQLException e){
            errorText.setText("Cannot create user. Please try again.");
            errorText.setTextFill(Color.RED);
            return;
        }
        ((Stage) this.getScene().getWindow()).close();
    }
}
