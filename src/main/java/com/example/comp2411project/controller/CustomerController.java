package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import cn.edu.scau.biubiusuisui.entity.FXPlusContext;
import cn.edu.scau.biubiusuisui.factory.FXControllerFactory;
import com.jfoenix.controls.JFXToggleNode;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

@FXController(path = "fxml/customer.fxml")
@FXWindow(
        title = "PolyUBaz-Customer",
        icon = "image/money.png"
)
public class CustomerController extends FXBaseController {
    public CustomerController(){

    }

    @Override
    public void initialize(){
        makeOrderNode.setText("");
        orderListNode.setText("");
        myInfoNode.setText("");
  //      borderPane.setCenter((PlaceOrderController) FXPlusContext.getControllers("PlaceOrderController").get(0));
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(makeOrderNode, orderListNode, myInfoNode);
        toggleGroup.selectedToggleProperty().addListener(
                ((observable, oldValue, newValue) ->{
                    if(makeOrderNode.isSelected()){
                        PlaceOrderController controller = (PlaceOrderController) FXPlusContext.getControllers("PlaceOrderController").get(0);
                                //FXControllerFactory.getFXController(PlaceOrderController.class);
                        borderPane.setCenter(controller);
                    }
                    else if(orderListNode.isSelected()){
                        ViewListController controller = (ViewListController) FXControllerFactory.getFXController(ViewListController.class);
                        borderPane.setCenter(controller);
                    }
                    else if(myInfoNode.isSelected()){
                        InfoController controller = (InfoController) FXControllerFactory.getFXController(InfoController.class);
                        borderPane.setCenter(controller);
                    }
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

