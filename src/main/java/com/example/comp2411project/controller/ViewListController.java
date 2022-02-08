package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;
import com.example.comp2411project.util.Customer;
import com.example.comp2411project.util.Order;
import com.example.comp2411project.util.Table;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXToggleNode;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

@FXController(path = "fxml/viewlist.fxml")
public class ViewListController extends FXBaseController {
    public ViewListController(){
    }

    ArrayList<newOrder> disabled = new ArrayList<>();
    ArrayList<newOrder> newOrders = new ArrayList<>();

    @Override
    public void initialize(){
        merchantColumn.setCellValueFactory(
                new PropertyValueFactory<>("merchant")
        );
        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );
        createTimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("create_time")
        );
        modifyTimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("modify_time")
        );
        goodsColumn.setCellValueFactory(
                new PropertyValueFactory<>("goods")
        );
        displays_shipping.disabledProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    table.getItems().clear();
                    table.getItems().addAll(FXCollections.observableArrayList(newOrders));
                }
                else{
                    table.getItems().clear();
                    table.getItems().addAll(FXCollections.observableArrayList(disabled));
                }
            }
        });

    }

    @Override
    public void onShow(){
        afterRefresh();
    }

    @FXML
    private JFXToggleButton displays_shipping;

    @FXML
    private TableView<newOrder> table;

    @FXML
    private TableColumn<newOrder, String> merchantColumn;

    @FXML
    private TableColumn<newOrder, Double> priceColumn;

    @FXML
    private TableColumn<newOrder, Integer> statusColumn;

    @FXML
    private TableColumn<newOrder, String> createTimeColumn;

    @FXML
    private TableColumn<newOrder, String> modifyTimeColumn;

    @FXML
    private TableColumn<newOrder, String> goodsColumn;

    @FXML
    private JFXButton refresh;

    @FXML
    public void afterRefresh() {
        Cache cache = Cache.getInstance();
        HashMap<Long, Order> orderHashMap = ((Customer) cache.getLocalTable()).getOrderList();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for(Order order : orderHashMap.values()){
            StringBuilder s = new StringBuilder();
            order.getGoodIDs().forEach((k) -> s.append(cache.getGoods(k).toString()).append(","));
            AppLog.getInstance().debug("s = %s", s.toString());
            String merchantName = cache.getMerchant(order.getMerchantID()).getUsername();
            AppLog.getInstance().debug("%s %f %d %s %s", merchantName, order.getPrice(),
                    order.getStatus(),
                    sdf.format(order.getCreateTime()),
                    sdf.format(order.getModifyTime()),
                    s.toString() );
            newOrder candidate = new newOrder(
                    merchantName,
                    order.getPrice(),
                    order.getStatus(),
                    sdf.format(order.getCreateTime()),
                    sdf.format(order.getModifyTime()),
                    s.toString());
            AppLog.getInstance().debug("order id = %d", order.getOrderId());
            newOrders.add(candidate);
            if(candidate.getStatus() == 3){
                disabled.add(candidate);
            }
        }
        table.getItems().clear();
        AppLog.getInstance().debug("tabel.size = %d, newOrders.size = %d", table.getItems().size(), newOrders.size());
        if(displays_shipping.isDisable())
            table.getItems().addAll(FXCollections.observableArrayList(disabled));
        else
            table.getItems().addAll(FXCollections.observableArrayList(newOrders));
        AppLog.getInstance().debug("disable? %b   tabel.size = %d, newOrders.size = %d", displays_shipping.isDisable(), table.getItems().size(), newOrders.size());
    }

}
