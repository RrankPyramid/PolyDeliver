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
    }

    HashMap<Long, Merchant> merchantHashMap;
    HashMap<Long, Order> orderHashMap;
    HashMap<Long, Goods> goodsHashMap;

    public HashMap<Long, Merchant> getMerchantHashMap() {
        return merchantHashMap;
    }

    public HashMap<Long, Order> getOrderHashMap() {
        return orderHashMap;
    }

    public HashMap<Long, Goods> getGoodsHashMap() {
        return goodsHashMap;
    }

    public Order getOrder(long id){
        if(orderHashMap.containsKey(id))
            return (Order) orderHashMap.get(id).pullUpdate();
        Order order = (Order) new Order(id).pullUpdate();
        orderHashMap.put(id, order);
        return order;
    }

    public Goods getGoods(long id){
        if(goodsHashMap.containsKey(id))
            return (Goods) goodsHashMap.get(id).pullUpdate();
        Goods goods = (Goods) new Goods(id).pullUpdate();
        goodsHashMap.put(id, goods);
        return goods;
    }

    public Merchant getMerchant(long id){
        if(merchantHashMap.containsKey(id))
            return (Merchant) merchantHashMap.get(id).pullUpdate();
        Merchant merchant = (Merchant) new Merchant(id).pullUpdate();
        merchantHashMap.put(id, merchant);
        return merchant;
    }
}
