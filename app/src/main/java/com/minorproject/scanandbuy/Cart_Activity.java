package com.minorproject.scanandbuy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minorproject.scanandbuy.Models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart_Activity extends AppCompatActivity {

    private String message = "";

    private RecyclerView recyclerView;
    private Button proceed_checkout,mycarts_back;
    private TextView product_price_cart , product_price_cart_sgst ,product_price_cart_cgst ,product_price_cart_payable , total_bill_amount,cart_items;
    private DatabaseReference mCartReference;
    int sum = 0;
    long items;
    private ArrayList<Product> mycart_list;
    private CardView price_details_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerview);
        product_price_cart = (TextView) findViewById(R.id.product_price_cart);
        product_price_cart_sgst = (TextView) findViewById(R.id.product_price_cart_sgst);
        product_price_cart_cgst = (TextView) findViewById(R.id.product_price_cart_cgst);
        product_price_cart_payable = (TextView) findViewById(R.id.product_price_cart_payable);
        total_bill_amount = (TextView) findViewById(R.id.total_bill_amount);
        cart_items = (TextView) findViewById(R.id.cart_items);
        proceed_checkout = (Button) findViewById(R.id.proceed_checkout);
        mycarts_back = (Button) findViewById(R.id.mycarts_back);
        price_details_card = (CardView) findViewById(R.id.price_deatils_card);

        price_details_card.setVisibility(View.INVISIBLE);
        proceed_checkout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enablecart_listner();

        mycarts_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Cart_Activity.this,Main2Activity.class);
               startActivity(intent);
               finish();
            }
        });

        proceed_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Cart_Activity.this,Payments_Activity.class);
                i.putExtra("bill amount",total_bill_amount.getText().toString());
                i.putExtra("total items",items);
                i.putParcelableArrayListExtra("product list",mycart_list);
                startActivity(i);
            }
        });


    }


    private void enablecart_listner() {
        mCartReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .child("Cart");

        mCartReference.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getpinslist();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getpinslist() {
        mycart_list = new ArrayList<>();
        if (mycart_list.size()>0){
            mycart_list.clear();
        }
        sum = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
                    proceed_checkout.setAlpha(1);
                    proceed_checkout.setEnabled(true);
                    price_details_card.setVisibility(View.VISIBLE);
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> mapObj = (Map<String, Object>) obj;
                            final Product product = new Product();
                            product.setProduct_id((String) mapObj.get("product_id"));
                            product.setProduct_name((String) mapObj.get("product_name"));
                            product.setProduct_image_url((String) mapObj.get("product_image_url"));
                            product.setProduct_barcode_id((String) mapObj.get("product_barcode_id"));
                            product.setProduct_rfid((String) mapObj.get("product_rfid"));
                            product.setProduct_price((String) mapObj.get("product_price"));
                            product.setProduct_details((String) mapObj.get("product_details"));

                            sum = sum + Integer.parseInt((String) mapObj.get("product_price"));

                            mycart_list.add(product);
                        }
                    }
                items = dataSnapshot.getChildrenCount();
                    cart_items.setText("(".concat(String.valueOf(dataSnapshot.getChildrenCount())).concat(")"));
                }else {
                    proceed_checkout.setAlpha((float) 0.5);
                    proceed_checkout.setEnabled(false);
                    price_details_card.setVisibility(View.INVISIBLE);
                    cart_items.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Snackbar.make(mContext.findViewById(android.R.id.content),"Check Your Internet Connection",Snackbar.LENGTH_LONG).show();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Mycart_RecyclerViewAdapter myAdapter = new Mycart_RecyclerViewAdapter(Cart_Activity.this, mycart_list);
                                    myAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(myAdapter);

                                    product_price_cart_payable.setText(String.valueOf(sum));
                                    total_bill_amount.setText(String.valueOf(sum));
                                    double tax = (0.05)*sum;
                                    product_price_cart_cgst.setText(String.valueOf(tax/2));
                                    product_price_cart_sgst.setText(String.valueOf(tax/2));
                                    product_price_cart.setText(String.valueOf(sum-tax));

                                }
                            },
                50);
    }

}
