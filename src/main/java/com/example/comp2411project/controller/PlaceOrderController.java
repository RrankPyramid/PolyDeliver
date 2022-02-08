package com.example.comp2411project.controller;

import cn.edu.scau.biubiusuisui.annotation.FXController;
import cn.edu.scau.biubiusuisui.entity.FXBaseController;
import cn.edu.scau.biubiusuisui.log.FXPlusLoggerFactory;
import cn.edu.scau.biubiusuisui.log.IFXPlusLogger;
import com.example.comp2411project.AppLog;
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

    private ArrayList<Merchant> merchants;

    @Override
    public void onShow() throws SQLException{
        merchants = Merchant.getMerchantList();
        AppLog.getInstance().debug(String.format("Merchant Num: %d", merchants.size()));
        merchants.forEach((merchant -> nameList.put(merchant.getUsername(), merchant)));
        comboBox.setItems(FXCollections.observableArrayList(
                nameList.keySet()
        ));
    }

    @Override
    public void initialize() throws SQLException {
        comboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    AppLog.getInstance().log("New value is " + newValue);
                    HashMap<Long, Goods> goodList = nameList.get(newValue).getGoodList();
                    goodList.forEach((id, good) -> goodsNameList.put(good.getName(), good));
                    ArrayList<String> listViewContent = new ArrayList<>();
                    goodsNameList.forEach((names, good) -> listViewContent.add(good.getName()));
                    stringListView.setItems(FXCollections.observableArrayList(
                        listViewContent
                    ));
                }
        );
        stringListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    AppLog.getInstance().debug("newValue = " + newValue);
                    Goods goods = goodsNameList.get(newValue);
                    if(goods == null)
                        return;
                    if(goods.getCounts() == 0){
                        Alert dialog = new Alert(Alert.AlertType.ERROR);
                        dialog.setHeaderText("Error! ");
                        dialog.setContentText("Not enough goods!");
                        dialog.showAndWait();
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
                                Alert warn = new Alert(Alert.AlertType.ERROR);
                                warn.setHeaderText("Not enough goods!");
                                warn.showAndWait();
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
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setHeaderText("Error! ");
            dialog.setContentText("You still have not selected a food, please try again.");
            dialog.showAndWait();
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
                Merchant merchant = nameList.get(comboBox.getValue());
                AppLog.getInstance().debug(String.format("MID = %d GSIZE = %d", merchant.getMerchantId(), goodIds.size()));

                ((Customer) Cache.getInstance().getLocalTable()).makeOrder(merchant.getMerchantId(), goodIds);
            }catch (SQLException e){
                alert.close();
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

    HashMap<String, Merchant> nameList;
    HashMap<String, Goods> goodsNameList;

    ArrayList<Goods> orderGoods;


}
