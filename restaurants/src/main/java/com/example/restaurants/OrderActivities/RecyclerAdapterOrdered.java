package com.example.restaurants.OrderActivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurants.R;

import java.util.ArrayList;

public class RecyclerAdapterOrdered extends RecyclerView.Adapter<RecyclerAdapterOrdered.MyViewHolder> {

    ArrayList<String> dishes;
    LayoutInflater mInflater;

    public RecyclerAdapterOrdered(Context context, ArrayList<String> dishes){
        mInflater = LayoutInflater.from(context);
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public RecyclerAdapterOrdered.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  mInflater.inflate(R.layout.dish_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterOrdered.MyViewHolder holder, int position) {
        String mCurrent = dishes.get(position);
        holder.dish_name.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dish_name;

        public MyViewHolder(View itemView){
            super(itemView);
            dish_name = itemView.findViewById(R.id.label);
        }
    }
}
