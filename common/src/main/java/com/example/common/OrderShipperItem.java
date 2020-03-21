package com.example.common;

public class OrderShipperItem {
    private String keyRestaurant;
    private String keyCustomer;
    private String addrCustomer;
    private String addrRestaurant;
    private String totPrice;
    private Long time;

    public OrderShipperItem() {

    }

    public OrderShipperItem(String keyRestaurant, String keyCustomer, String addrCustomer, String addrRestaurant, Long time, String totPrice) {
        this.keyRestaurant = keyRestaurant;
        this.keyCustomer = keyCustomer;
        this.addrCustomer = addrCustomer;
        this.addrRestaurant = addrRestaurant;
        this.time = time;
        this.totPrice = totPrice;
    }

    public String getKeyRestaurant() {
        return keyRestaurant;
    }

    public String getKeyCustomer() {
        return keyCustomer;
    }

    public String getAddrCustomer() {
        return addrCustomer;
    }

    public String getAddrRestaurant() {
        return addrRestaurant;
    }

    public Long getTime() {
        return time;
    }

    public String getTotPrice() {
        return totPrice;
    }
}
