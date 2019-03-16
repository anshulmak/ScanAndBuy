package com.minorproject.scanandbuy;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.util.Map;

public class GatePassActivity extends AppCompatActivity {

    private String barcode;
    private String status,user_id,order_id;
    private DatabaseReference mProductsReference;
    private ArrayList<String> myrfid_list;
    private long items;

    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_pass);

        progressBar = (ProgressBar) findViewById(R.id.gatepass_progressbar);
        relativeLayout = (RelativeLayout) findViewById(R.id.gatepass_layout);

        Toast.makeText(GatePassActivity.this,"Checking your QR code details",Toast.LENGTH_SHORT).show();

        barcode = getIntent().getExtras().getString("barcodeid");

        int lastspace = barcode.lastIndexOf(" ");

        user_id = barcode.substring(lastspace + 1, barcode.length());
        String s = barcode.substring(0, lastspace);
        int space = s.indexOf(" ");
        status = s.substring(0, space);
        order_id = s.substring(space + 1, s.length());

        if (status.equals("paid")){
            enableproducts_listner();
        }

    }
        private void enableproducts_listner() {
            mProductsReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id)
                    .child("Orders")
                    .child(order_id)
                    .child("products");

            mProductsReference.addValueEventListener(mValueEventListener);
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
            myrfid_list = new ArrayList<>();
            if (myrfid_list.size()>0){
                myrfid_list.clear();
            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query1 = reference.child("users").child(user_id)
                    .child("Orders")
                    .child(order_id)
                    .child("products");
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                        Map<String, Object> objectMap = (HashMap<String, Object>)
                                dataSnapshot.getValue();

                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> mapObj = (Map<String, Object>) obj;

                                myrfid_list.add((String) mapObj.get("product_rfid"));


                            }
                        }
                        items = dataSnapshot.getChildrenCount();
                    }else {
                        Toast.makeText(GatePassActivity.this,"Invalid QR Code",Toast.LENGTH_SHORT).show();
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
                                        FirebaseDatabase.getInstance().getReference().child("rfids")
                                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance().getReference().child("rfids")
                                                        .child("total_items")
                                                        .setValue(String.valueOf(items)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        for (int i=0 ; i<myrfid_list.size();i++){
                                                            FirebaseDatabase.getInstance().getReference().child("rfids")
                                                                    .child("product".concat(String.valueOf(i+1)))
                                                                    .setValue(myrfid_list.get(i));

                                                            if (i==myrfid_list.size()-1){
                                                                Toast.makeText(GatePassActivity.this,"Pass the scanner gate",Toast.LENGTH_SHORT).show();
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                relativeLayout.setVisibility(View.VISIBLE);

                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                },
                    50);
        }

    }

