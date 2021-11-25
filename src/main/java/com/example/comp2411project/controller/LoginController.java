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
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

@FXController(
        path = "controller/login.fxml"
)
@FXWindow(
        title = "login",
        mainStage = true
)
public class LoginController extends FXBaseController {
    @FXML
            private MFXTextField usernameField;

    @FXML
            private MFXPasswordField passwordField;

    @FXML
            private MFXButton loginButton;

    @FXML
            private MFXButton registerButton;

    @FXML
            private MFXButton backButton;

    String username;

    String password;

    @FXML
            private Label label;

    public LoginController(){}

    @FXML
    public void registerClick(){
        this.redirectToRegister();
    }

    @FXRedirect(
            close = false
    )
    public String redirectToRegister() { return "RegisterController"; }

    @FXRedirect
    public String redirectToCustomer() { return "CustomerController"; }

    @FXRedirect
    public String redirectToMerchant() { return "MerchantController"; }

    @FXRedirect
    public String redirectToDeliverman() { return "DelivermanController"; }


    public void initialize(){
        username = usernameField.getText();
        password = passwordField.getText();
    }

    @FXML
    private void loginOnAction(ActionEvent actionEvent) throws IllegalArgumentException{
        username = usernameField.getText();
        password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            throw new IllegalArgumentException();
        }

        Cache cache = Cache.getInstance();
        if(cache.getTableType() == 0){ //Customer
            try{
                long id = Customer.checkUsernameAndPassword(username, password);
                if(id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getCustomer(id));
                this.redirectToCustomer();
            }catch (IllegalArgumentException e){
                label.setText("Username or Password incorrect. Please try again. ");
                label.setTextFill(Color.color(255, 0, 0));
            }
        }else if(cache.getTableType() == 1)  { //Deliverman
            try{
                long id = Deliverman.checkUsernameAndPassword(username, password);
                if(id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getDeliverman(id));
                this.redirectToDeliverman();
            }catch (IllegalArgumentException e) {
                label.setText("Username or Password incorrect. Please try again. ");
                label.setTextFill(Color.color(255, 0, 0));
            }
        }else if(cache.getTableType() == 2) { //Merchant
            try{
                long id = Merchant.checkUsernameAndPassword(username, password);
                if(id == 0)
                    throw new IllegalArgumentException();
                cache.setLocalTable(cache.getMerchant(id));
                this.redirectToMerchant();
            }catch (IllegalArgumentException e){
                label.setText("Username or Password incorrect. Please try again. ");
                label.setTextFill(Color.color(255, 0, 0));
            }
        }
    }

}
