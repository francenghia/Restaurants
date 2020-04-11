package com.example.customer.ViewHolders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.common.DishItem;
import com.example.customer.R;

import java.io.InputStream;
import java.text.DecimalFormat;

public class DailyOfferViewHolder extends RecyclerView.ViewHolder{
    private ImageView dishPhoto;
    private TextView dishName, dishDesc, dishPrice, dishQuantity;
    private View view;


    public DailyOfferViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        dishName = itemView.findViewById(R.id.dish_name);
        dishDesc = itemView.findViewById(R.id.dish_desc);
        dishPrice = itemView.findViewById(R.id.dish_price);
        dishPhoto = itemView.findViewById(R.id.dish_image);

    }

    public void   setData(DishItem current, int position) {
        InputStream inputStream = null;

        this.dishName.setText(current.getName());
        this.dishDesc.setText(current.getDesc());
        double amount = current.getPrice();
        DecimalFormat formatter = new DecimalFormat("#,###");
        this.dishPrice.setText(formatter.format(amount) + " vnd");
        if (current.getPhoto() != null) {
            Glide.with(view.getContext()).load(current.getPhoto()).override(80,80).into(dishPhoto);
        }
    }

    public View getView() {
        return view;
    }

}
