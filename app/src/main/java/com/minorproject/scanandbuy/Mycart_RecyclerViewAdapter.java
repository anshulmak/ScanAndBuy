package com.minorproject.scanandbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minorproject.scanandbuy.Models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mycart_RecyclerViewAdapter extends RecyclerView.Adapter<Mycart_RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Product> my_cartList;
    private DatabaseReference mCartReference;

    public Mycart_RecyclerViewAdapter(Context mContext, ArrayList<Product> my_cartList) {
        this.mContext = mContext;
        this.my_cartList = my_cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.my_cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.product_name_cart.setText(my_cartList.get(position).getProduct_name());
        holder.product_price_cart.setText(my_cartList.get(position).getProduct_price());
        holder.product_details_cart.setText(my_cartList.get(position).getProduct_details());

        Glide.with(mContext)
                .load(my_cartList.get(position).getProduct_image_url())
                .into(holder.product_image_cart);

        holder.remove_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Cart")
                        .child(my_cartList.get(position).getProduct_id())
                        .setValue(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return my_cartList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView product_image_cart;
        TextView product_name_cart , product_price_cart , product_details_cart ;
        Button remove_cart ;


        public MyViewHolder(View itemView) {
            super(itemView);

            product_image_cart = (ImageView) itemView.findViewById(R.id.product_image_cart);
            product_name_cart = (TextView) itemView.findViewById(R.id.product_name_cart);
            product_price_cart = (TextView) itemView.findViewById(R.id.product_price_cart1);
            product_details_cart = (TextView) itemView.findViewById(R.id.product_details_cart);
            remove_cart = (Button) itemView.findViewById(R.id.remove_cart);


        }
    }

}