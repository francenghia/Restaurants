package com.example.restaurants.HomeActivities;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.common.DishItem;
import com.example.restaurants.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.example.common.Shared.DISHES_PATH;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<DishItem, ViewHolderDailyOfferMostFavourite> mAdapter;

    private Query query = FirebaseDatabase.getInstance().getReference().child(RESTAURATEUR_INFO + "/" +
            ROOT_UID + "/" + DISHES_PATH).orderByChild("frequency");
    private FirebaseRecyclerOptions<DishItem> options =
            new FirebaseRecyclerOptions.Builder<DishItem>()
                    .setQuery(query, DishItem.class).build();
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.dish_list);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FirebaseRecyclerAdapter<DishItem, ViewHolderDailyOfferMostFavourite>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderDailyOfferMostFavourite viewHolderDailyOfferMostFavourite, int i, @NonNull DishItem dishItem) {
                viewHolderDailyOfferMostFavourite.setData(dishItem, i);
                Log.d("Kiem tra",dishItem.getName());
            }

            @NonNull
            @Override
            public ViewHolderDailyOfferMostFavourite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dailyoffer_listview, parent, false);

                view.findViewById(R.id.delete_offer).setVisibility(View.GONE);
                view.findViewById(R.id.edit_offer).setVisibility(View.GONE);

                return new ViewHolderDailyOfferMostFavourite(view);
            }
        };

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.favourite_dishes:
                if(!HomeFragment.this.isVisible())
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                return true;
            case R.id.advanced_stats:
                if(HomeFragment.this.isVisible())
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeOptionFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

class ViewHolderDailyOfferMostFavourite extends RecyclerView.ViewHolder{

    private ImageView dishPhoto;
    private TextView dishName, dishDesc, dishPrice, dishQuantity;
    private DishItem current;
    private int position;

    public ViewHolderDailyOfferMostFavourite(@NonNull View itemView) {
        super(itemView);

        dishName = itemView.findViewById(R.id.dish_name);
        dishDesc = itemView.findViewById(R.id.dish_desc);
        dishPrice = itemView.findViewById(R.id.dish_price);
        dishQuantity = itemView.findViewById(R.id.dish_quant);
        dishPhoto = itemView.findViewById(R.id.dish_image);
    }

    void setData(DishItem current, int position){
        this.dishName.setText(current.getName());
        this.dishDesc.setText(current.getDesc());
        this.dishPrice.setText(current.getPrice() + " đồng");
        this.dishQuantity.setText(String.valueOf(current.getQuantity()));

        if(current.getPhoto() != null)
            Glide.with(itemView.getContext()).load(current.getPhoto()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(dishPhoto);
        else
            Glide.with(itemView.getContext()).load(R.drawable.sallad).into(dishPhoto);

        this.position = position;
        this.current = current;
    }
}