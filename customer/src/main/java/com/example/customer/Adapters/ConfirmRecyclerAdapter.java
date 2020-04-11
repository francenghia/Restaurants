package com.example.customer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer.Activities.ConfirmActivity;
import com.example.customer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmRecyclerAdapter extends RecyclerView.Adapter<ConfirmRecyclerAdapter.MyViewHolder> {

    ArrayList<String> names;
    ArrayList<String> prices;
    ArrayList<String> quantities;
    ConfirmActivity confirm;
    LayoutInflater mInflater;

    public ConfirmRecyclerAdapter(Context context, ArrayList<String> names, ArrayList<String> prices, ArrayList<String> quantities, ConfirmActivity confirm){
        mInflater = LayoutInflater.from(context);
        this.confirm = confirm;
        this.names = names;
        this.prices = prices;
        this.quantities = quantities;
    }

    @NonNull
    @Override
    public ConfirmRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  mInflater.inflate(R.layout.dish_confirm_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmRecyclerAdapter.MyViewHolder holder, int position) {
        String name = names.get(position);
        String price = prices.get(position);
        String quantity  = quantities.get(position);
        holder.dish_name.setText(name);
        holder.dish_quant.setText(quantity);
        double amount = Double.parseDouble(price);
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.dish_price.setText(formatter.format(amount).toString() + " vnd");
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dish_name;
        TextView dish_quant;
        TextView dish_price;
        View view_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view_item = itemView;
            dish_name = itemView.findViewById(R.id.dish_conf_name);
            dish_price = itemView.findViewById(R.id.dish_conf_price);
            dish_quant = itemView.findViewById(R.id.dish_conf_quantity);

        }

        public View getView_item (){
            return this.view_item;
        }
    }
}
