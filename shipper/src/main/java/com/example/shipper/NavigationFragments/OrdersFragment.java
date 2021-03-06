package com.example.shipper.NavigationFragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.common.OrderShipperItem;
import com.example.common.Restaurateur;
import com.example.common.Utilities;
import com.example.shipper.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.common.Shared.CUSTOMER_PATH;
import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.SHIPPERS_PATH;
import static com.example.common.Shared.STATUS_DELIVERED;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment implements OnMapReadyCallback {

    private  boolean available;
    private boolean restaurantReached;
    private OrderShipperItem order;

    private DatabaseReference query1;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeoApiContext mGeoApiContext;
    private GoogleMap mMap;
    int col = 0;
    private ScrollView mScrollView;
    private DatabaseReference query;
    private ValueEventListener listenerQuery;
    private FirebaseDatabase database;
    private LatLng latLng_restaurant;
    private Restaurateur restaurateur;
    private LatLng pos_restaurant;
    private Long distance;
    private String orderKey;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        distance = 0L;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        MapView mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        restaurantReached = false;

        query1 = FirebaseDatabase.getInstance().getReference(SHIPPERS_PATH + "/" + ROOT_UID)
                .child("available");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO: STATUS
                available = (boolean) dataSnapshot.getValue();
                if(!available && null == order){
                    Button btn = view.findViewById(R.id.accept_button);
                    btn.setText("Lấy đơn hàng");
                    TextView text = view.findViewById(R.id.status);
                    text.setText("Đang giao hàng..");
                }
                else{
                    Button btn = view.findViewById(R.id.accept_button);
                    btn.setText("Không có đơn hàng");
                    TextView text = view.findViewById(R.id.status);
                    text.setText("Chưa có đơn");
                    cancelOrderView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // BUTTON ACCEPTED ORDER
        Button b = view.findViewById(R.id.accept_button);
        b.setOnClickListener(e->{
            if(available)
                acceptOrder();
            else{
                if(!restaurantReached){
                    restaurantReachedByShipper(b);
                }
                else{
                    deliveredOrder();
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        query = database
                .getReference(SHIPPERS_PATH + "/" + ROOT_UID + "/pending/");

        listenerQuery = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    orderKey = d.getKey();
                    order = d.getValue(OrderShipperItem.class);
                    setOrderView(view, order);
                    String restaurantAddress = order.getAddrRestaurant();
                    String customerAddress = order.getAddrCustomer();
                    Log.d("QUERY", customerAddress);

                    pos_restaurant = (LatLng) getLocationFromAddress(restaurantAddress);
                    getLastKnownLocation(pos_restaurant);
                    b.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                cancelOrderView(view);
                b.setText("Không có đơn đặt hàng");
                b.setEnabled(false);
            }
        };

        query.addListenerForSingleValueEvent(listenerQuery);

        return view;
    }

    private void setOrderView(View view,OrderShipperItem order)
    {
        TextView r_addr = view.findViewById(R.id.restaurant_text);
        TextView c_addr = view.findViewById(R.id.customer_text);
        TextView time_text = view.findViewById(R.id.time_text);
        TextView cash_text = view.findViewById(R.id.cash_text);

        r_addr.setText(order.getAddrRestaurant());
        c_addr.setText(order.getAddrCustomer());
        time_text.setText(Utilities.getDateFromTimestamp(order.getTime()));
        String totPrice = Utilities.formatCurrency(order.getTotPrice());
        cash_text.setText(totPrice + " vnd");
    }

    private void deliveredOrder() {
        AlertDialog reservationDialog = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        final View view = inflater.inflate(R.layout.reservation_dialog, null);


        view.findViewById(R.id.button_confirm).setOnClickListener(e ->{
            if(null != order){
                DatabaseReference query = FirebaseDatabase.getInstance()
                        .getReference(SHIPPERS_PATH + "/" + ROOT_UID + "/pending/");
                query.removeValue();

                DatabaseReference query2 = FirebaseDatabase.getInstance()
                        .getReference(SHIPPERS_PATH + "/" + ROOT_UID);

                Map<String, Object> status = new HashMap<String, Object>();
                status.put("available", true);
                query2.updateChildren(status);

                //SET STATUS TO CUSTOMER ORDER
                DatabaseReference refCustomerOrder = FirebaseDatabase.getInstance()
                        .getReference().child(CUSTOMER_PATH).child(order.getKeyCustomer()).child("orders").child(orderKey);
                HashMap<String, Object> order_status = new HashMap<>();
                order_status.put("status", STATUS_DELIVERED);
                refCustomerOrder.updateChildren(order_status);
                mMap.clear();

                DatabaseReference query3 = FirebaseDatabase.getInstance()
                        .getReference(SHIPPERS_PATH + "/" + ROOT_UID).child("delivered");

                Map<String,Object> delivered = new HashMap<>();
                delivered.put(UUID.randomUUID().toString(),distance);
                query3.updateChildren(delivered);

                distance = 0L;
            }

            reservationDialog.dismiss();

        });

        view.findViewById(R.id.button_cancel).setOnClickListener(e ->{
            reservationDialog.dismiss();
        });

        reservationDialog.setView(view);
        reservationDialog.setTitle("Đơn hàng đã lấy?");

        reservationDialog.show();
    }

    private void restaurantReachedByShipper(Button b) {
        AlertDialog reservationDialog = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        final View view = inflater.inflate(R.layout.reservation_dialog, null);

        view.findViewById(R.id.button_confirm).setOnClickListener(e ->{
            restaurantReached = true;
            if(null != order){
                b.setText("Đã giao hàng");
                String customerAddr = order.getAddrCustomer();
                mMap.clear();
                getLastKnownLocation((LatLng) getLocationFromAddress(customerAddr));
                reservationDialog.dismiss();
            }
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(e -> {
            reservationDialog.dismiss();
        });

        reservationDialog.setView(view);
        reservationDialog.setTitle("Lấy đơn hàng?");

        reservationDialog.show();
    }

    private void getLastKnownLocation(LatLng restaurantPos) {
        Log.d("DEBUG MAP", "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mUserLocation = task.getResult();
                    LatLng mUserPosition = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mUserPosition, 16.0f));
                    calculateDirections(mUserPosition, restaurantPos);
                }
            }
        });
    }

    private void calculateDirections(LatLng start, LatLng end) {
        Log.d("MAP DEBUG", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                end.latitude,
                end.longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        start.latitude,
                        start.longitude
                )
        );
        Log.d("DEBUG", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("DEBUG", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("DEBUG", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("DEBUG", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("DEBUG", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result, end);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("DEBuG", "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
    }

    private void addPolylinesToMap(DirectionsResult result, LatLng finalPos) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("DEBUG", "run: result routes: " + result.routes.length);

                DirectionsRoute route= result.routes[0];
                Log.d("DEBUG", "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                for(com.google.maps.model.LatLng latLng: decodedPath){

                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }

                Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                if(col==0) {
                    polyline.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    col++;
                }
                else{
                    polyline.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    col--;
                }

                polyline.setClickable(true);
                mMap.addMarker(new MarkerOptions()
                        .position(finalPos)
                        //TODO: FIX NAME
                        .title("NAME")
                        .snippet("Duration: " + route.legs[0].duration
                        ));
                distance += route.legs[0].distance.inMeters;
            }
        });
    }

    private Object getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((double) (location.getLatitude()),
                    (double) (location.getLongitude()));

            return p1;
        }
        catch (IOException ex) {

            ex.printStackTrace();
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void acceptOrder(){

        AlertDialog reservationDialog = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        final View view = inflater.inflate(R.layout.reservation_dialog, null);


        view.findViewById(R.id.button_confirm).setOnClickListener(e ->{
            if(!available){
                Toast.makeText(getContext(),
                        "You have alredy accepted this order!",Toast.LENGTH_LONG).show();
            }else {
                DatabaseReference query = FirebaseDatabase.getInstance()
                        .getReference(SHIPPERS_PATH + "/" + ROOT_UID);

                Map<String, Object> status = new HashMap<String, Object>();
                status.put("available", false);
                query.updateChildren(status);
            }
            reservationDialog.dismiss();
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(e -> {

            DatabaseReference query = FirebaseDatabase
                    .getInstance().getReference(SHIPPERS_PATH + "/" + ROOT_UID + "/pending/");
            query.removeValue();
            reservationDialog.dismiss();

        });

        reservationDialog.setView(view);
        reservationDialog.setTitle("Xác nhận đơn hàng?");

        reservationDialog.show();
    }

    private void cancelOrderView(View view)
    {
        TextView r_addr = view.findViewById(R.id.restaurant_text);
        TextView c_addr = view.findViewById(R.id.customer_text);
        TextView time_text = view.findViewById(R.id.time_text);
        TextView cash_text = view.findViewById(R.id.cash_text);

        r_addr.setText("");
        c_addr.setText("");
        time_text.setText("");
        cash_text.setText("");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("MAP_DEBUG", "Sto visualizzando la posizione");
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        query1.removeEventListener(listenerQuery);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
