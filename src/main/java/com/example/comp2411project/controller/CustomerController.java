package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.jfoenix.controls.JFXToggleNode;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

@FXController(path = "fxml/customer.fxml")
public class CustomerController extends FXBaseController {
    public CustomerController(){

    }

    @Override
    public void initialize(){
        makeOrderNode.setText("");
        orderListNode.setText("");
        myInfoNode.setText("");
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(makeOrderNode, orderListNode, myInfoNode);
        toggleGroup.selectedToggleProperty().addListener(
                ((observable, oldValue, newValue) ->{
                    if(makeOrderNode.isSelected()){

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

    private ToggleGroup toggleGroup;

}
