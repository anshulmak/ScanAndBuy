package com.minorproject.scanandbuy.Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable{

    private String name;
    private String email_id;
    private List<Product> Cart;
    private List<Order> Orders;
    private String user_id;

    public User(String name, String email_id, List<Product> cart, List<Order> orders, String user_id) {
        this.name = name;
        this.email_id = email_id;
        Cart = cart;
        Orders = orders;
        this.user_id = user_id;
    }

    public User() {

    }

    protected User(Parcel in) {
        name = in.readString();
        user_id = in.readString();
        email_id = in.readString();
    }
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public List<Order> getOrders() {
        return Orders;
    }

    public void setOrders(List<Order> orders) {
        Orders = orders;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public List<Product> getCart() {
        return Cart;
    }

    public void setCart(List<Product> cart) {
        Cart = cart;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email_id='" + email_id + '\'' +
                ", Cart=" + Cart +
                ", Orders=" + Orders +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(user_id);
        dest.writeString(email_id);
    }
}
