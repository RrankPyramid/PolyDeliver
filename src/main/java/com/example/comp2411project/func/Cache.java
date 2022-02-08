package com.example.comp2411project.func;

import com.example.comp2411project.util.*;

import java.util.HashMap;

public class Cache {
    private static Cache instance = new Cache();
    public static Cache getInstance(){
        return instance;
    }

    private Cache(){
        merchantHashMap = new HashMap<>();
        orderHashMap = new HashMap<>();
        goodsHashMap = new HashMap<>();
        delivermanHashMap = new HashMap<>();
        customerHashMap = new HashMap<>();
    }

    HashMap<Long, Merchant> merchantHashMap;
    HashMap<Long, Order> orderHashMap;
    HashMap<Long, Goods> goodsHashMap;
    HashMap<Long, Deliverman> delivermanHashMap;
    HashMap<Long, Customer> customerHashMap;

    Table localTable;


    /**
     * 0 : Customer
     * 1 : Deliver Man
     * 2 : Merchant
     */
    int TableType;

    public Table getLocalTable(){
        return localTable;
    }

    public void setLocalTable(Table localTable) {
        this.localTable = localTable;
    }

    public int getTableType() {
        return TableType;
    }

    public void setTableType(int tableType) {
        TableType = tableType;
    }

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

    public Deliverman getDeliverman(long id){
        if(delivermanHashMap.containsKey(id)){
            return (Deliverman) delivermanHashMap.get(id).pullUpdate();
        }
        Deliverman deliverman = (Deliverman) new Deliverman(id).pullUpdate();
        delivermanHashMap.put(id, deliverman);
        return deliverman;
    }

    public Customer getCustomer(long id){
        if(customerHashMap.containsKey(id)){
            return (Customer) customerHashMap.get(id).pullUpdate();
        }
        Customer customer = (Customer) new Customer(id).pullUpdate();
        customerHashMap.put(id, customer);
        return customer;
    }

    public void addToCache(Customer customer){
        long id = customer.getCustomerId();
        customerHashMap.put(id, customer);
    }

    public void addToCache(Deliverman deliverman){
        long id = deliverman.getDelivermanId();
        delivermanHashMap.put(id, deliverman);
    }

    public void addToCache(Merchant merchant){
        long id = merchant.getMerchantId();
        merchantHashMap.put(id, merchant);
    }

    public void addToCache(Goods goods){
        long id = goods.getGoodId();
        goodsHashMap.put(id, goods);
    }
}
