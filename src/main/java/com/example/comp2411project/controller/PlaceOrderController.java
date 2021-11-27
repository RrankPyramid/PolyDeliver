package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.util.Customer;
import com.example.comp2411project.util.Goods;
import com.example.comp2411project.util.Merchant;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

@FXController(path = "fxml/placeorder.fxml")
public class PlaceOrderController extends FXBaseController {
    public PlaceOrderController(){
        nameList = new HashMap<>();
        goodsNameList = new HashMap<>();
        orderGoods = new ArrayList<>();
    }

    @Override
    public void initialize() throws SQLException {
        customer = (Customer) Cache.getInstance().getLocalTable();
        ArrayList<Merchant> merchants = Merchant.getMerchantList();
        merchants.forEach((merchant -> nameList.put(merchant.getUsername(), merchant)));
        comboBox.setItems(FXCollections.observableArrayList(
                nameList.keySet()
        ));
        comboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    HashMap<Long, Goods> goodList = nameList.get(newValue).getGoodList();
                    goodList.forEach((id, good) -> goodsNameList.put(good.getName(), good));
                    ArrayList<String> listViewContent = new ArrayList<>();
                    goodsNameList.forEach((names, good) -> listViewContent.add(good.toString()));
                    stringListView.setItems(FXCollections.observableArrayList(
                        listViewContent
                    ));
                }
        );
        stringListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    Goods goods = goodsNameList.get(newValue);
                    if(goods.getCounts() == 0){
                        JFXDialog dialog = new JFXDialog();
                        dialog.setContent(new Label("Not enough goods!"));
                        dialog.show();
                        return;
                    }
                    else {
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Make Order");
                        dialog.setHeaderText("Please input the number you want: ");
                        dialog.setContentText("Number: ");

                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(number -> {
                            int num = Integer.parseInt(number);
                            if(num == 0){
                                return;
                            }
                            if(goods.getCounts() < num){
                                JFXDialog warn = new JFXDialog();
                                warn.setContent(new Label("Not enough goods!"));
                                warn.show();
                            } else {
                                goods.setCounts(goods.getCounts() - num);
                                boolean hasGood = false;
                                for(Goods goods1 : orderGoods){
                                    if(goods1.getName().equals(goods.getName())){
                                        hasGood = true;
                                        goods1.setCounts(goods1.getCounts() + num);
                                    }
                                }
                                if(!hasGood)
                                    orderGoods.add(new Goods(goods.getPrice(), goods.getName(), num));
                            }
                        });
                    }
                }
        );
    }

    @FXML
    void afterConfirmButtonClick(){
        if(orderGoods.isEmpty()){
            JFXDialog dialog = new JFXDialog();
            dialog.setContent(new Label("You still have not selected a food, please try again."));
            dialog.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Are you sure to order these food? ");
        StringBuilder content = new StringBuilder("");
        for(Goods goods : orderGoods){
            content.append(goods.toString());
        }
        alert.setContentText(content.toString());

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            try {
                Set<Long> goodIds = new TreeSet<>();
                for(Goods goods : orderGoods){
                    Cache.getInstance().addToCache((Goods) goods.pushInfo());
                    goodIds.add(goods.getGoodId());
                }
                customer.makeOrder(nameList.get(comboBox.getValue()).getMerchantId(), goodIds);
            }catch (SQLException e){
                Alert sqlAlert = new Alert(Alert.AlertType.ERROR);
                sqlAlert.setTitle("SQL Error");
                sqlAlert.setHeaderText("SQL Error!");
                sqlAlert.setContentText("The exception backtrace was: ");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String exceptionText = sw.toString();

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);

// Set expandable Exception into the dialog pane.
                alert.getDialogPane().setExpandableContent(expContent);

                alert.showAndWait();
            }

        }
    }

    @FXML
    private void afterResetButtonClick(){
        orderGoods.clear();
    }

    @FXML
    private JFXComboBox<String> comboBox;

    @FXML
    private JFXListView<String> stringListView;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private JFXButton resetButton;

    private Customer customer;

    HashMap<String, Merchant> nameList;
    HashMap<String, Goods> goodsNameList;

    ArrayList<Goods> orderGoods;


}
