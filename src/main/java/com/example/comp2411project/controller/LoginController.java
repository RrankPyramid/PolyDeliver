package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXRedirect;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.util.Customer;
import com.example.comp2411project.util.Deliverman;
import com.example.comp2411project.util.Merchant;
import com.example.comp2411project.util.Table;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

@FXController(
        path = "fxml/login.fxml"
)
@FXWindow(
        title = "login",
        icon = "image/money.png"
)
public class LoginController extends FXBaseController {
    @FXML
            private JFXTextField usernameField;

    @FXML
            private JFXPasswordField passwordField;

    @FXML
            private JFXButton loginButton;

    @FXML
            private JFXButton registerButton;

    @FXML
            private JFXButton backButton;

    String username;

    String password;

    @FXML
            private Label label;

    public LoginController(){}


    @FXRedirect
    public String redirectToCustomer() { return "CustomerController"; }

    @FXRedirect
    public String redirectToMerchant() { return "MerchantController"; }

    @FXRedirect
    public String redirectToDeliverman() { return "DelivermanController"; }

    @FXRedirect
    public String redirectToCoverPage() { return "CoverController"; }

    @FXRedirect(
            close = false
    )
    public String redirectToRegister(){
        return "RegisterController";
    }

    public void initialize(){
    }

    @FXML
    private void loginOnAction(ActionEvent actionEvent){
        username = usernameField.getText();
        password = passwordField.getText();
        try {
            if (username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException();
            }
            Cache cache = Cache.getInstance();
            if (cache.getTableType() == Table.CUSTOMER) { //Customer
                long id = Customer.checkUsernameAndPassword(username, password);
                if (id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getCustomer(id));
                this.redirectToCustomer();
            } else if (cache.getTableType() == Table.DELIVERMAN) { //Deliverman
                long id = Deliverman.checkUsernameAndPassword(username, password);
                if (id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getDeliverman(id));
                this.redirectToDeliverman();
            } else if (cache.getTableType() == Table.MERCHANT) { //Merchant
                long id = Merchant.checkUsernameAndPassword(username, password);
                if (id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getMerchant(id));
                this.redirectToMerchant();
            }
        }catch (IllegalArgumentException e){
            label.setText("Username or Password incorrect. Please try again. ");
            label.setTextFill(Color.color(1, 0, 0));
        }
    }

    @FXML
    private void afterBackClick(){
        this.redirectToCoverPage();
    }

    @FXML
    private void afterRegisterClick(){
        this.redirectToRegister();
    }
}
