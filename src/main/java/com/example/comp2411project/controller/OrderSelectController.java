package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.util.Customer;
import com.example.comp2411project.util.Goods;
import com.example.comp2411project.util.Merchant;
import com.example.comp2411project.util.Order;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;

public class OrderSelectController extends FXBaseController {
    public OrderSelectController(){}

    @Override
    public void initialize(){
        stringListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Long> observable, Long oldValue, Long newValue) -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm");
                    alert.setHeaderText("Are you sure to send this order to customer?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == ButtonType.OK){
                        Order choice = Cache.getInstance().getOrder(newValue);
                        Merchant merchant = (Merchant) Cache.getInstance().getLocalTable();
                        try{
                            merchant.updateOrder(choice);
                        }catch (Exception e){
                            return;
                        }
                    }
                    stringListView.getItems().remove(newValue);
                }
        );
        ArrayList<Long> list = new ArrayList<>();
        ((Merchant) Cache.getInstance().getLocalTable()).getOrderList().forEach((k, v) -> {list.add(v.getOrderId());});
        stringListView.setItems(FXCollections.observableArrayList(
                list
        ));
    }

    @FXML
    private JFXListView<Long> stringListView;

    @FXML
    private JFXButton refresh;

    @FXML
    private void afterButtonClick(){
        ArrayList<Long> list = new ArrayList<>();
        ((Merchant) Cache.getInstance().getLocalTable()).getOrderList().forEach((k, v) -> {list.add(v.getOrderId());});
        stringListView.setItems(FXCollections.observableArrayList(
                list
        ));
    }

}
