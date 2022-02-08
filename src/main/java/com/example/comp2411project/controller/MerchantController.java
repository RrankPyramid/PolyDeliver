package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import cn.edu.scau.biubiusuisui.factory.FXControllerFactory;
import com.jfoenix.controls.JFXToggleNode;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

@FXController(
        path = "fxml/merchant.fxml"
)
@FXWindow(
        title = "Merchant",
        icon = "image/money.png"
)
public class MerchantController extends FXBaseController {
    public MerchantController(){}

    OrderSelectController orderSelectController;
    InfoController infoController;

    @Override
    public void initialize(){
        ToggleGroup toggleGroup =  new ToggleGroup();
        toggleGroup.getToggles().addAll(orderListNode, myInfoNode);
        orderSelectController = (OrderSelectController) FXControllerFactory.getFXController(OrderSelectController.class);
        infoController = (InfoController) FXControllerFactory.getFXController(InfoController.class);
        orderListNode.setSelected(true);
        myInfoNode.setSelected(false);
        toggleGroup.selectedToggleProperty().addListener(
                ((observable, oldValue, newValue) ->{
                    if(oldValue == newValue)
                        return;
                    if(orderListNode.isSelected()){
                        borderPane.setCenter(orderSelectController);
                    }
                    else if(myInfoNode.isSelected()){
                        borderPane.setCenter(infoController);
                    }
                })
        );
    }

    @FXML
    BorderPane borderPane;

    @FXML
    JFXToggleNode orderListNode;

    @FXML
    JFXToggleNode myInfoNode;

    private ToggleGroup toggleGroup;

}
