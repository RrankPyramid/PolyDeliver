package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.annotation.FXWindow;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;
import com.example.comp2411project.util.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;


@FXController(
        path = "fxml/deliver.fxml"
)
@FXWindow(
        title = "Deliverman",
        icon = "image/motorbike.png"
)
public class DeliverController extends FXBaseController {
    public DeliverController() {}
    public void initialize(){

        confirm_button.setManaged(true);
        confirm_button.setVisible(true);
        order_id_text.setText("Order ID: 3");
        px_text.setText("Location of the Customer: (29,79)");
    }

    @FXML
    Label order_id_text;

    @FXML
    Label px_text;


    @FXML
    JFXButton refresh_button;

    @FXML
    JFXButton confirm_button;

    @FXML
    void afterRefreshButtonClick(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("Notice!");
        alert.setContentText("A new order is set to your account! ");
        alert.showAndWait();
        Cache cache = Cache.getInstance();
        deliverman = (Deliverman) cache.getLocalTable().pullUpdate();
        if(deliverman.getOrderNO() == null)
            return;
    //    Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText("Notice!");
        alert.setContentText("A new order is set to your account! ");
        order_id_text.setText(String.format("Order ID: %d", deliverman.getOrderNO()));
        order = Cache.getInstance().getOrder(deliverman.getOrderNO());
        customer = Cache.getInstance().getCustomer(order.getCustomerID());
        px_text.setText(String.format("Location of the customer: (%d,%d)", customer.getPx(), customer.getPy()));
        confirm_button.setManaged(true);
        confirm_button.setVisible(true);
        refresh_button.setVisible(false);
        refresh_button.setManaged(false);
    }

    @FXML
    void afterConfirmButtonClick() throws SQLException{
        order.setStatus(order.getStatus() + 1);
        order.pushInfo();
        customer = null;
        deliverman = null;
        order = null;
        order_id_text.setText("");
        px_text.setText("");
    }

    private Deliverman deliverman;
    private Customer customer;
    private Order order;
}
