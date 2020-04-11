package com.example.restaurants.OrderActivities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.common.ShipperInfo;
import com.example.restaurants.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.example.common.Shared.CUSTOMER_ID;
import static com.example.common.Shared.ORDER_ID;

class ListShipperAdapter extends RecyclerView.Adapter<ListShipperAdapter.MyViewHolder> {

    private ArrayList<ShipperInfo> mDataset;
    private LayoutInflater mInflater;
    private ListShipperFragment listShipperFragment;


    public ListShipperAdapter(Context context, ArrayList<ShipperInfo> myDataset, ListShipperFragment listShipperFragment) {
        mInflater = LayoutInflater.from(context);
        this.mDataset = myDataset;
        this.listShipperFragment = listShipperFragment;
    }

    @NonNull
    @Override
    public ListShipperAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  mInflater.inflate(R.layout.list_rider_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListShipperAdapter.MyViewHolder holder, int position) {
        ShipperInfo currentInfo =mDataset.get(position);
        DecimalFormat df = new DecimalFormat("#.##");

        holder.nameShipper.setText(currentInfo.getName());
        holder.distanceValue.setText((df.format(currentInfo.getDist())) + " km");
        holder.itemView.findViewById(R.id.confirm_rider).setOnClickListener(e -> listShipperFragment.selectShipper(currentInfo.getKey()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameShipper, distanceValue;
        View view_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view_item = itemView;
            this.nameShipper = itemView.findViewById(R.id.name_rider);
            this.distanceValue = itemView.findViewById(R.id.distance);
        }
    }
}

public class ListShipperFragment extends Fragment {
    private static final String TAG = "ListShipperFragment";

    private OnFragmentInteractionListener mListener;
    private TreeMap<Double, String> distanceMap;
    private HashMap<String, String> shipperMap;
    private ArrayList<ShipperInfo> shipperList;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public ListShipperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_shipper, container, false);

        shipperMap = ((MapsActivity) getActivity()).getRidersMap();
        distanceMap = ((MapsActivity) getActivity()).getDistanceMap();
        treeMapToList(shipperMap, distanceMap);

        recyclerView = view.findViewById(R.id.list_rider_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListShipperAdapter(getContext(), shipperList, this);
        recyclerView.setAdapter(mAdapter);



        return view;
    }

    private void treeMapToList(HashMap<String, String> shipperMap, TreeMap<Double, String> distanceMap) {
        shipperList = new ArrayList<>();

        for(Map.Entry<Double,String> entry : distanceMap.entrySet()) {
            Log.d("Shipper List :" , entry.getValue()+"/"+entry.getKey());
            shipperList.add(new ShipperInfo(shipperMap.get(entry.getValue()), entry.getValue(), entry.getKey()));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    void selectShipper(String shipperId) {
        String orderId = getActivity().getIntent().getStringExtra(ORDER_ID);
        String customerId = getActivity().getIntent().getStringExtra(CUSTOMER_ID);
        SelectionShipperCommon.selectShipper(shipperId,orderId,customerId,getContext(),TAG);
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
