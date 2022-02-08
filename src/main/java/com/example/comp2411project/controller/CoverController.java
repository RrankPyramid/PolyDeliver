package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXRedirect;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.util.Table;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


@FXWindow(
        mainStage = true,
        title = "PolyUBaz",
        icon = "image/money.png"
)
@FXController(
        path = "fxml/cover.fxml"
)

public class CoverController extends FXBaseController {
    public CoverController() {}

    @Override
    public void initialize() {
    }

    @FXML
    private Button customerButton;

    @FXML
    private Button deliverManButton;

    @FXML
    private Button merchantButton;

    @FXRedirect
    public String redirectToLogin(){
        return "LoginController";
    }

    @FXML
    private void afterCustomerButtonClick(){
        Cache cache = Cache.getInstance();
        cache.setTableType(Table.CUSTOMER);
        this.redirectToLogin();
    }

    @FXML
    private void afterDeliverButtonClick(){
        Cache cache = Cache.getInstance();
        cache.setTableType(Table.DELIVERMAN);
        this.redirectToLogin();
    }

    @FXML
    private void afterMerchantButtonClick(){
        Cache cache = Cache.getInstance();
        cache.setTableType(Table.MERCHANT);
        this.redirectToLogin();
    }
}
