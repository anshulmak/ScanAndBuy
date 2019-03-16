package com.minorproject.scanandbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.minorproject.scanandbuy.Models.Product;

import java.util.ArrayList;

public class orderitems_RecyclerViewAdapter extends RecyclerView.Adapter<orderitems_RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Product> my_cartList;


    public orderitems_RecyclerViewAdapter(Context mContext, ArrayList<Product> my_cartList) {
        this.mContext = mContext;
        this.my_cartList = my_cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.orderdetails_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.item_name.setText(my_cartList.get(position).getProduct_name());
        holder.item_price.setText("\u20b9".concat(my_cartList.get(position).getProduct_price()));

    }

    @Override
    public int getItemCount() {
        return my_cartList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name , item_price;


        public MyViewHolder(View itemView) {
            super(itemView);

            item_name = (TextView) itemView.findViewById(R.id.item_name);
            item_price = (TextView) itemView.findViewById(R.id.item_price);


        }
    }

}