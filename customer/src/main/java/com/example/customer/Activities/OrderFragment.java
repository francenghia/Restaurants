package com.example.customer.Activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.OrderCustomerItem;
import com.example.customer.R;
import com.example.customer.ViewHolders.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.common.Shared.CUSTOMER_PATH;
import static com.example.common.Shared.ROOT_UID;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<OrderCustomerItem, OrderViewHolder> mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView_ordered;
    private static FirebaseRecyclerOptions<OrderCustomerItem> options =
            new FirebaseRecyclerOptions.Builder<OrderCustomerItem>()
                    .setQuery(FirebaseDatabase.getInstance().getReference(CUSTOMER_PATH).child(ROOT_UID).child("orders").orderByChild("sort"),
                            OrderCustomerItem.class).build();

    private OnFragmentInteractionListener mListener;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.ordered_list);
        mAdapter = new FirebaseRecyclerAdapter<OrderCustomerItem, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull OrderCustomerItem model) {
                String orderkey = getRef(position).getKey();
                holder.setData(model, position, orderkey);
                holder.getView().findViewById(R.id.order_details_button).setOnClickListener(a->{
                    Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                    intent.putExtra("order_item", model);
                    startActivity(intent);
                });
            }
        };

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
