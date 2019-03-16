package com.minorproject.scanandbuy.Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Order{

    private String bill_paid;
    private String order_time;
    private List<Product> products;
    private String order_id;

    public Order(String bill_paid, String order_time, List<Product> products, String order_id) {
        this.bill_paid = bill_paid;
        this.order_time = order_time;
        this.products = products;
        this.order_id = order_id;
    }

    public Order() {

    }

    public String getBill_paid() {
        return bill_paid;
    }

    public void setBill_paid(String bill_paid) {
        this.bill_paid = bill_paid;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "bill_paid='" + bill_paid + '\'' +
                ", order_time='" + order_time + '\'' +
                ", products=" + products +
                ", order_id='" + order_id + '\'' +
                '}';
    }
}
