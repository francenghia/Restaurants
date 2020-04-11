package com.example.customer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.common.Shared.RESTAURATEUR_INFO;

public class OrderDetailsRecyclerAdapter extends RecyclerView.Adapter<OrderDetailsRecyclerAdapter.MyViewHolder> {
    private ArrayList<String> keys;
    private ArrayList<String> nums;
    private String key;
    LayoutInflater mInflater;

    public OrderDetailsRecyclerAdapter(Context context, ArrayList<String> keys, ArrayList<String> nums, String key) {
        mInflater = LayoutInflater.from(context);
        this.keys = keys;
        this.nums = nums;
        this.key = key;
    }

    @NonNull
    @Override
    public OrderDetailsRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  mInflater.inflate(R.layout.detailorder_dishitem, parent, false);
        return new OrderDetailsRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsRecyclerAdapter.MyViewHolder holder, int position) {
        holder.setData(keys.get(position), nums.get(position));
    }

    @Override
    public int getItemCount() {
        return this.keys.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dish_name;
        TextView dish_quant;
        TextView dish_price;
        View view_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view_item = itemView;
            dish_name = itemView.findViewById(R.id.orderdetail_dishname);
            dish_price = itemView.findViewById(R.id.orderdetail_price);
            dish_quant = itemView.findViewById(R.id.orderdetail_quantity);

        }

        public View getView_item (){
            return this.view_item;
        }

        public void setData(String key_dish, String num){
            Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO).child(key).child("dishes").child(key_dish);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dish_name.setText((String)dataSnapshot.child("name").getValue());
                    double amount = BigDecimal.valueOf((Long) dataSnapshot.child("price").getValue()).doubleValue();


                    DecimalFormat formatter = new DecimalFormat("#,###");
                    dish_price.setText(formatter.format(amount)+" vnd");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            dish_quant.setText(num);
        }

    }
}
