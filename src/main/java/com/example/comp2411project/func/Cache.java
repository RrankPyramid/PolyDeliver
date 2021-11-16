package com.example.comp2411project.func;

import com.example.comp2411project.util.Deliverman;
import com.example.comp2411project.util.Goods;
import com.example.comp2411project.util.Merchant;
import com.example.comp2411project.util.Order;

import java.util.HashMap;

public class Cache {
    private static Cache instance = new Cache();
    public static Cache getInstance(){
        return instance;
    }

    private Cache(){
        merchantHashMap = new HashMap<>();
        orderHashMap = new HashMap<>();
        delivermanHashMap = new HashMap<>();
    }

    HashMap<Long, Merchant> merchantHashMap;
    HashMap<Long, Order> orderHashMap;
    HashMap<Long, Deliverman> delivermanHashMap;
    HashMap<Long, Goods> goodsHashMap;

    public HashMap<Long, Merchant> getMerchantHashMap() {
        return merchantHashMap;
    }

    public HashMap<Long, Order> getOrderHashMap() {
        return orderHashMap;
    }

    public HashMap<Long, Deliverman> getDelivermanHashMap() {
        return delivermanHashMap;
    }

    public HashMap<Long, Goods> getGoodsHashMap() {
        return goodsHashMap;
    }
}
