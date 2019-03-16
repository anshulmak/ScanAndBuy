package com.minorproject.scanandbuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minorproject.scanandbuy.Models.Order;
import com.minorproject.scanandbuy.Models.Product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Myorders_RecyclerViewAdapter extends RecyclerView.Adapter<Myorders_RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Order> my_orderList;
    private String total_items;
    private String[] weekdays = {"","","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private String[] Months = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};

    public Myorders_RecyclerViewAdapter(Context mContext, ArrayList<Order> my_orderList,String total_items) {
        this.mContext = mContext;
        this.my_orderList = my_orderList;
        this.total_items = total_items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.myorders_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.order_amount.setText("\u20b9".concat(my_orderList.get(position).getBill_paid()));
        holder.order_id.setText("Bill Id - ".concat(my_orderList.get(position).getOrder_id()));
        holder.order_items.setText(total_items);

        String time = my_orderList.get(position).getOrder_time();
        int pos = time.indexOf(" ");
        final String date = time.substring(pos+1,time.length());

        try {
            Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(date1);

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            holder.order_day.setText(date.substring(0,2));
            holder.order_weekday.setText(weekdays[dayOfWeek]);
            holder.order_month.setText(Months[Integer.parseInt(date.substring(3,5))].concat(" ").concat(date.substring(6,10)));

            holder.order_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, order_activity.class);
                    intent.putExtra("order id", my_orderList.get(position).getOrder_id());
                    intent.putExtra("order date", date);
                    mContext.startActivity(intent);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return my_orderList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView order_day , order_month , order_weekday,order_amount,order_items,order_id ;
        RelativeLayout order_layout;


        public MyViewHolder(View itemView) {
            super(itemView);

            order_day = (TextView) itemView.findViewById(R.id.order_day);
            order_month = (TextView) itemView.findViewById(R.id.order_month);
            order_weekday = (TextView) itemView.findViewById(R.id.order_weekday);
            order_amount = (TextView) itemView.findViewById(R.id.order_amount);
            order_items = (TextView) itemView.findViewById(R.id.order_items);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_layout = (RelativeLayout) itemView.findViewById(R.id.order_layout);


        }
    }

}