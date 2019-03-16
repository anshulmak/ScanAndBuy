package com.minorproject.scanandbuy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DatabaseReference mCartReference;

    private TextView cart_count,user_name,user_emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button scan_barcode = (Button) findViewById(R.id.scan_barcode);
        Button scan_barcode1 = (Button) findViewById(R.id.scan_barcode1);
        Button add_product = (Button) findViewById(R.id.add_product);
        Button gate_scanner = (Button) findViewById(R.id.gate_scanner);
        ImageButton cart_button = (ImageButton) findViewById(R.id.cart_button);
        cart_count = (TextView) findViewById(R.id.cart_count);


        cart_count.setVisibility(View.INVISIBLE);

        enablecart_listner();

        cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this , Cart_Activity.class);
                startActivity(intent);
            }
        });

        cart_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this , Cart_Activity.class);
                startActivity(intent);
            }
        });
        scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,HomeActivity.class);
                startActivity(intent);

            }
        });

        scan_barcode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,HomeActivity.class);
                startActivity(intent);

            }
        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,Add_Product_Activity.class);
                startActivity(intent);
            }
        });
        gate_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(Main2Activity.this,GateScannerActivity.class);
            startActivity(intent);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent intent = new Intent(Main2Activity.this,Myorders_Activity.class);
            startActivity(intent);
            
        } else if (id == R.id.nav_wishlist) {

        } else if (id == R.id.nav_cart) {
            Intent intent = new Intent(Main2Activity.this,Cart_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_share) {

        }else if (id == R.id.legal_about) {

        }else if (id == R.id.customer_Service) {

        }else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Main2Activity.this,Login_Activity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    cart_count.setVisibility(View.VISIBLE);
                    cart_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }else {
                    cart_count.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Snackbar.make(mContext.findViewById(android.R.id.content),"Check Your Internet Connection",Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
