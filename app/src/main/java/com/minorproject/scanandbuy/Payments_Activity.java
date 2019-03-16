package com.minorproject.scanandbuy;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minorproject.scanandbuy.Models.Order;
import com.minorproject.scanandbuy.Models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Payments_Activity extends AppCompatActivity {

    private TextView bill_amout_payment , total_items;
    private ArrayList<Product> cart_list;
    private ArrayList<String> payment_modes;
    private ArrayList<Drawable> payment_logos;
    private RecyclerView payment_recyclerview;

    private ProgressBar payment_progressbar;
    private boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        bill_amout_payment = (TextView) findViewById(R.id.bill_amount_payment);
        total_items = (TextView) findViewById(R.id.total_items);
        payment_recyclerview = (RecyclerView) findViewById(R.id.payment_recyclerview);
        Button pay_button = (Button) findViewById(R.id.pay_button);
        Button payment_back = (Button) findViewById(R.id.payment_back);
        payment_progressbar = (ProgressBar) findViewById(R.id.payment_progressbar);

        payment_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        bill_amout_payment.setText("Amount \u20b9".concat(getIntent().getExtras().getString("bill amount")));
        pay_button.setText("Pay \u20b9".concat(getIntent().getExtras().getString("bill amount")));
        total_items.setText(String.valueOf(getIntent().getExtras().getLong("total items")).concat(" items"));

        cart_list = getIntent().getParcelableArrayListExtra("product list");

        payment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(Payments_Activity.this,Cart_Activity.class);
            startActivity(intent);
            finish();
            }
        });

        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payment_progressbar.setVisibility(View.VISIBLE);
                final String order_id = FirebaseDatabase.getInstance().getReference().push().getKey();

                final Order order = new Order();
                order.setOrder_id(order_id);
                order.setOrder_time(gettime().concat(" ").concat(getdate()));
                order.setBill_paid(getIntent().getExtras().getString("bill amount"));

                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Orders")
                        .child(order_id)
                        .setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        order.setProducts(cart_list);
                        Snackbar.make(findViewById(android.R.id.content),"Bill Paid Successfully",Snackbar.LENGTH_SHORT).show();

                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart")
                                .setValue(null);

                        for (int i=0;i<cart_list.size();i++){

                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Orders")
                                    .child(order_id)
                                    .child("products")
                                    .child(cart_list.get(i).getProduct_id())
                                    .setValue(cart_list.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                            FirebaseDatabase.getInstance().getReference().child("Products")
                                   .child(cart_list.get(i).getProduct_id())
                                  .setValue(null);
                            if (i ==cart_list.size()-1) {
                                b = true;
                            }
                        }
                        if (b) {
                            payment_progressbar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(Payments_Activity.this, order_activity.class);
                            intent.putExtra("order id", order_id);
                            intent.putExtra("order date", getdate());
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });

        payment_modes = new ArrayList<>();
        payment_modes.add("Credit Card");
        payment_modes.add("Debit Card");
        payment_modes.add("Paytm");
        payment_modes.add("Amazon Pay");
        payment_modes.add("Google Pay");
        payment_modes.add("Freecharge");

        payment_logos = new ArrayList<>();
        payment_logos.add(getResources().getDrawable(R.drawable.debitcard_logo));
        payment_logos.add(getResources().getDrawable(R.drawable.debitcard_logo));
        payment_logos.add(getResources().getDrawable(R.drawable.paytm_logo));
        payment_logos.add(getResources().getDrawable(R.drawable.amazonpay_logo));
        payment_logos.add(getResources().getDrawable(R.drawable.google_logo));
        payment_logos.add(getResources().getDrawable(R.drawable.freecharge_logo));

        Payments_RecyclerViewAdapter myAdapter = new Payments_RecyclerViewAdapter(Payments_Activity.this, cart_list,payment_modes,payment_logos);
        myAdapter.notifyDataSetChanged();
        payment_recyclerview.setAdapter(myAdapter);




    }

    private String getdate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    private String gettime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("kk:mm");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }
}
