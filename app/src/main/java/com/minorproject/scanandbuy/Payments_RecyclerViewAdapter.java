package com.minorproject.scanandbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minorproject.scanandbuy.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class Payments_RecyclerViewAdapter extends RecyclerView.Adapter<Payments_RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Product> my_cartList;
    private ArrayList<String> payment_names;
    private ArrayList<Drawable> payment_logos;
    private DatabaseReference mCartReference;

    public Payments_RecyclerViewAdapter(Context mContext, ArrayList<Product> my_cartList, ArrayList<String> payment_names, ArrayList<Drawable> payment_logos) {
        this.mContext = mContext;
        this.my_cartList = my_cartList;
        this.payment_names = payment_names;
        this.payment_logos = payment_logos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.payment_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.payment_name.setText(payment_names.get(position));
        holder.payment_logo.setImageDrawable(payment_logos.get(position));
        if (position==0 || position == 1){
            holder.payment_button.setText("Add Card");
        }
    }

    @Override
    public int getItemCount() {
        return payment_names.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView payment_logo;
        TextView payment_name , payment_button;


        public MyViewHolder(View itemView) {
            super(itemView);

            payment_logo = (ImageView) itemView.findViewById(R.id.payment_logo);
            payment_name = (TextView) itemView.findViewById(R.id.payment_name);
            payment_button = (TextView) itemView.findViewById(R.id.payment_button);


        }
    }

}