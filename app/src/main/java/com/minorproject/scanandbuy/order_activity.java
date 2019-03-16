package com.minorproject.scanandbuy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.minorproject.scanandbuy.Models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class order_activity extends AppCompatActivity {

    private ImageView order_qrcode;
    private String order_id,user_id;

    private Bitmap bitmap;
    private TextView total_items_order;
    private int QRcodeWidth =600;
    private DatabaseReference mProductsReference;
    private ArrayList<Product> mycart_list;
    private ProgressBar order_progresbar;
    private int sum = 0;

    private TextView product_price_cart , product_price_cart_sgst ,product_price_cart_cgst ,product_price_cart_payable;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        order_qrcode = (ImageView) findViewById(R.id.order_qrcode);
        final TextView orderid = (TextView) findViewById(R.id.order_id);
        TextView order_date = (TextView) findViewById(R.id.order_date);
        total_items_order = (TextView) findViewById(R.id.total_items_order);
        order_progresbar = (ProgressBar) findViewById(R.id.order_progressbar);

        order_progresbar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.orderitems_recyclerview);
        product_price_cart = (TextView) findViewById(R.id.product_price_order);
        product_price_cart_sgst = (TextView) findViewById(R.id.product_price_order_sgst);
        product_price_cart_cgst = (TextView) findViewById(R.id.product_price_order_cgst);
        product_price_cart_payable = (TextView) findViewById(R.id.product_price_order_payable);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        order_id= getIntent().getExtras().getString("order id");
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        enableorder_listner();

        orderid.setText("Bill id : ".concat(order_id));
        order_date.setText(getIntent().getExtras().getString("order date"));

        orderid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 600, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    private void enableorder_listner() {
        mProductsReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Orders")
                .child(order_id)
                .child("products");

        mProductsReference.addValueEventListener(mValueEventListener);
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
        mycart_list = new ArrayList<>();
        if (mycart_list.size()>0){
            mycart_list.clear();
        }
        sum = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                    total_items_order.setText("Total items : ".concat(String.valueOf(dataSnapshot.getChildrenCount())));
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
                                    String s = "paid "+order_id+" "+user_id;
                                    try {
                                        bitmap = TextToImageEncode(s);
                                        order_qrcode.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }

                                    orderitems_RecyclerViewAdapter myAdapter = new orderitems_RecyclerViewAdapter(order_activity.this, mycart_list);
                                    myAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(myAdapter);
                                    order_progresbar.setVisibility(View.INVISIBLE);

                                    product_price_cart_payable.setText(String.valueOf(sum));

                                    double tax = (0.05)*sum;
                                    product_price_cart_cgst.setText(String.valueOf(tax/2));
                                    product_price_cart_sgst.setText(String.valueOf(tax/2));
                                    product_price_cart.setText(String.valueOf(sum-tax));

                                }
                            },
                40);
    }

    public void onBackPressed(){
        Intent intent = new Intent(order_activity.this,Myorders_Activity.class);
        startActivity(intent);
    }
}
