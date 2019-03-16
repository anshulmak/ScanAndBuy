package com.minorproject.scanandbuy;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.minorproject.scanandbuy.Models.Order;
import com.minorproject.scanandbuy.Models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Myorders_Activity extends AppCompatActivity {

    private RecyclerView myorders_recyclerview ;
    private DatabaseReference mOrdersReference;
    private ArrayList<Order> myorders_list;
    private String total_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorders_activity);

        myorders_recyclerview = (RecyclerView) findViewById(R.id.mycards_recyclerview);
        Button myorders_back = (Button) findViewById(R.id.myorders_back);

        myorders_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myorders_Activity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        myorders_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        enableorder_listner();

    }


    private void enableorder_listner() {
        mOrdersReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Orders");

        mOrdersReference.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getproductslist();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getproductslist() {
        myorders_list = new ArrayList<>();
        if (myorders_list.size()>0){
            myorders_list.clear();
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Orders");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> mapObj = (Map<String, Object>) obj;
                            final Order order = new Order();
                            order.setOrder_id((String) mapObj.get("order_id"));
                            order.setOrder_time((String) mapObj.get("order_time"));
                            order.setBill_paid((String) mapObj.get("bill_paid"));

                            myorders_list.add(order);
                        }
                    }
                    total_items = String.valueOf(dataSnapshot.getChildrenCount()).concat(" items");
                }else {
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
                                    Myorders_RecyclerViewAdapter myAdapter = new Myorders_RecyclerViewAdapter(Myorders_Activity.this, myorders_list,total_items);
                                    myAdapter.notifyDataSetChanged();
                                    myorders_recyclerview.setAdapter(myAdapter);

                                }
                            },
                40);
    }

    public void onBackPressed(){
    Intent intent = new Intent(Myorders_Activity.this,Main2Activity.class);
    startActivity(intent);
    finish();
    }
}
