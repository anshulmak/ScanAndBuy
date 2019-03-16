package com.minorproject.scanandbuy;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Scan_Product_Activity extends AppCompatActivity {


    private TextView product_name , product_price , product_barcodeid , product_details;
    private ImageView product_image;
    private ProgressBar progressBar;
    private Button cancel_product , add_to_cart;
    private RelativeLayout scanned_product_layout;

    private List<Product> productslist;

    String scanned_barcodeid ;
    private Product scanned_product;
    private DatabaseReference ProductsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan__product);

        product_name = (TextView) findViewById(R.id.product_name);
        product_price = (TextView) findViewById(R.id.product_price);
        product_barcodeid = (TextView) findViewById(R.id.product_barcodeid);
        product_details = (TextView) findViewById(R.id.product_details);
        product_image = (ImageView) findViewById(R.id.product_image);
        progressBar = (ProgressBar) findViewById(R.id.scan_product_progressbar);
        cancel_product = (Button) findViewById(R.id.cancel_product);
        add_to_cart = (Button) findViewById(R.id.add_to_cart);
        scanned_product_layout = (RelativeLayout) findViewById(R.id.scanned_product_layout);

        cancel_product.setEnabled(false);
        add_to_cart.setEnabled(false);
        scanned_product_layout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        scanned_barcodeid = getIntent().getExtras().getString("barcodeid");

        enableProductsListener();

        cancel_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Scan_Product_Activity.this,Main2Activity.class);
                startActivity(i);
                finish();
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Cart")
                        .child(scanned_product.getProduct_id())
                        .setValue(scanned_product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Scan_Product_Activity.this,"Product added to cart successfully",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Scan_Product_Activity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

            }
        });

    }

    private void enableProductsListener() {
        ProductsReference = FirebaseDatabase.getInstance().getReference().child("Products");

        ProductsReference.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getProducts();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getProducts() {
        productslist = new ArrayList<>();
        if (productslist.size()>0){
            productslist.clear();
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("Products");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
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

                            productslist.add(product);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Snackbar.make(Scan_Product_Activitythis, "Check Your Internet Connection", Snackbar.LENGTH_LONG).show();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scanned_product = new Product();
                                    for (int i=0; i < productslist.size() ; i++){
                                        if (productslist.get(i).getProduct_barcode_id().trim().equals(scanned_barcodeid)){
                                            progressBar.setVisibility(View.INVISIBLE);
                                            cancel_product.setEnabled(true);
                                            add_to_cart.setEnabled(true);
                                            add_to_cart.setAlpha(1);
                                            scanned_product_layout.setVisibility(View.VISIBLE);
                                            scanned_product = productslist.get(i);
                                            product_name.setText(scanned_product.getProduct_name());
                                            product_price.setText(scanned_product.getProduct_price());
                                            product_barcodeid.setText(scanned_product.getProduct_barcode_id());
                                            product_details.setText(scanned_product.getProduct_details());

                                            Glide.with(Scan_Product_Activity.this.getApplicationContext())
                                                    .load(scanned_product.getProduct_image_url())
                                                    .into(product_image);

                                        }
                                    }
                                    if (product_name.getText().length()==0){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        cancel_product.setEnabled(true);
                                        Toast.makeText(Scan_Product_Activity.this, "Your Item is Not Found. Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                20);

    }
}
