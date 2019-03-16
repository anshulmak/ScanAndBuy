package com.minorproject.scanandbuy.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{

    private String product_name;
    private String product_barcode_id;
    private String product_rfid;
    private String product_price;
    private String product_details;
    private String product_image_url;
    private String product_id;

    public Product(String product_name, String product_barcode_id, String product_rfid, String product_price, String product_details, String product_image_url, String product_id) {
        this.product_name = product_name;
        this.product_barcode_id = product_barcode_id;
        this.product_rfid = product_rfid;
        this.product_price = product_price;
        this.product_details = product_details;
        this.product_image_url = product_image_url;
        this.product_id = product_id;
    }

    public Product() {

    }

    protected Product(Parcel in) {
        product_name = in.readString();
        product_barcode_id = in.readString();
        product_rfid = in.readString();
        product_price = in.readString();
        product_details = in.readString();
        product_image_url = in.readString();
        product_id = in.readString();
    }
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProduct_image_url() {
        return product_image_url;
    }

    public void setProduct_image_url(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    public String getProduct_barcode_id() {
        return product_barcode_id;
    }

    public void setProduct_barcode_id(String product_barcode_id) {
        this.product_barcode_id = product_barcode_id;
    }

    public String getProduct_rfid() {
        return product_rfid;
    }

    public void setProduct_rfid(String product_rfid) {
        this.product_rfid = product_rfid;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }


    public String getProduct_details() {
        return product_details;
    }

    public void setProduct_details(String product_details) {
        this.product_details = product_details;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
        }

    @Override
    public String toString() {
        return "Product{" +
                "product_name='" + product_name + '\'' +
                ", product_barcode_id='" + product_barcode_id + '\'' +
                ", product_rfid='" + product_rfid + '\'' +
                ", product_price='" + product_price + '\'' +
                ", product_details='" + product_details + '\'' +
                ", product_image_url='" + product_image_url + '\'' +
                ", product_id='" + product_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_name);
        dest.writeString(product_barcode_id);
        dest.writeString(product_rfid);
        dest.writeString(product_price);
        dest.writeString(product_details);
        dest.writeString(product_image_url);
        dest.writeString(product_id);
    }
}

