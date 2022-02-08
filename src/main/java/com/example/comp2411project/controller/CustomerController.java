package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import cn.edu.scau.biubiusuisui.entity.FXPlusContext;
import cn.edu.scau.biubiusuisui.factory.FXControllerFactory;
import com.jfoenix.controls.JFXToggleNode;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

@FXController(path = "fxml/customer.fxml")
@FXWindow(
        title = "PolyUBaz-Customer",
        icon = "image/money.png"
)
public class CustomerController extends FXBaseController {
    public CustomerController(){

    }

    @FXML
    private PlaceOrderController placeOrderController;

    @FXML
    private ViewListController viewListController;

    @FXML
    private InfoController infoController;


    @Override
    public void onShow(){
        try {
            if (makeOrderNode.isSelected()) {
                placeOrderController.onShow();
                borderPane.setCenter(placeOrderController);
            } else if (orderListNode.isSelected()) {
                viewListController.onShow();
                borderPane.setCenter(viewListController);
            } else if (myInfoNode.isSelected()) {
                infoController.onShow();
                borderPane.setCenter(infoController);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(){
        makeOrderNode.setText("");
        orderListNode.setText("");
        myInfoNode.setText("");
        placeOrderController =  (PlaceOrderController) FXControllerFactory.getFXController(PlaceOrderController.class, "PlaceOrder");
        viewListController = (ViewListController) FXControllerFactory.getFXController(ViewListController.class, "ViewList");
        infoController = (InfoController) FXControllerFactory.getFXController(InfoController.class, "Info");
        FXPlusContext.registerController(placeOrderController);
        FXPlusContext.registerController(infoController);
        FXPlusContext.registerController(viewListController);
        borderPane.setCenter(placeOrderController);
        placeOrderController.setVisible(true);
        placeOrderController.setManaged(true);
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(makeOrderNode, orderListNode, myInfoNode);
        makeOrderNode.setSelected(true);
        orderListNode.setSelected(false);
        myInfoNode.setSelected(false);
        toggleGroup.selectedToggleProperty().addListener(
                ((observable, oldValue, newValue) ->{
                    if(oldValue == newValue)
                        return;
                    this.onShow();
                })
        );
    }


    @FXML
    private JFXToggleNode makeOrderNode;

    @FXML
    private JFXToggleNode orderListNode;

    @FXML
    private JFXToggleNode myInfoNode;

    @FXML
    private BorderPane borderPane;

    private ToggleGroup toggleGroup;

}

