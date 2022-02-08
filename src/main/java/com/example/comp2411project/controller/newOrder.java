package com.example.comp2411project.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class newOrder{

    SimpleStringProperty merchant;

    SimpleDoubleProperty price;

    SimpleIntegerProperty status;

    SimpleStringProperty create_time;

    SimpleStringProperty modify_time;

    SimpleStringProperty goods;

    public newOrder(String merchant, double price, int status, String create_time, String modify_time, String goods) {
        this.merchant = new SimpleStringProperty(merchant);
        this.price = new SimpleDoubleProperty(price);
        this.status = new SimpleIntegerProperty(status);
        this.create_time = new SimpleStringProperty(create_time);
        this.modify_time = new SimpleStringProperty(modify_time);
        this.goods = new SimpleStringProperty(goods);
    }

    public String getMerchant() {
        return merchant.get();
    }

    public SimpleStringProperty merchantProperty() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant.set(merchant);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getStatus() {
        return status.get();
    }

    public SimpleIntegerProperty statusProperty() {
        return status;
    }

    public void setStatus(int status) {
        this.status.set(status);
    }

    public String getCreate_time() {
        return create_time.get();
    }

    public SimpleStringProperty create_timeProperty() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time.set(create_time);
    }

    public String getModify_time() {
        return modify_time.get();
    }

    public SimpleStringProperty modify_timeProperty() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time.set(modify_time);
    }

    public String getGoods() {
        return goods.get();
    }

    public SimpleStringProperty goodsProperty() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods.set(goods);
    }
}

