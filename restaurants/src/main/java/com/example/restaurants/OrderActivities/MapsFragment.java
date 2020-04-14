package com.example.restaurants.OrderActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.common.Position;
import com.example.restaurants.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static com.example.common.Shared.CUSTOMER_ID;
import static com.example.common.Shared.ORDER_ID;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.SHIPPERS_PATH;
import static com.example.common.Shared.SHIPPER_INFO;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "MapsFragment";
    //Map
    private MapView mapView;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted = true;
    private static final int DEFAULT_ZOOM = 15;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private PlaceDetectionClient mPlaceDetectionClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085); //default location

    //position
    private double longitude, latitude;

    //Query
    private Query queryRiderPos;
    private ValueEventListener riderPosListener;

    //Restaurants
    private String restaurantName;
    private HashMap<String, Position> posMap;
    private HashMap<String, String> shipperName;
    private TreeMap<Double, String> distanceMap;
    private HashMap<String, Marker> markerMap = new HashMap<>();
    private HashSet<String> shipperKey = new HashSet<>();

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this.getContext(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = (Location) task.getResult();

                        Query getRestaurantInfo = FirebaseDatabase.getInstance().getReference()
                                .child(RESTAURATEUR_INFO + "/" + ROOT_UID);

                        getRestaurantInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Position position = dataSnapshot.child("info_pos").getValue(Position.class);
                                    restaurantName = dataSnapshot.child("info").child("name").getValue(String.class);

                                    Log.d("Kiem tra::", position.latitude +  "");

                                    latitude = position.getLatitude();
                                    longitude = position.getLongitude();

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(latitude, longitude), DEFAULT_ZOOM));

                                    mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(latitude, longitude))
                                            .radius(10000)
                                            .strokeColor(0xFFBC7362)
                                            .fillColor(0x32FFC8C8));

                                    setShipperOnMapReady();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w("MAPS FRAGMENT", "Failed to read value.", databaseError.toException());
                            }
                        });
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.");
                        Log.e("TAG", "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("SecurityException :", e.getMessage());
        }

    }

    private void setShipperOnMapReady() {
        queryRiderPos = FirebaseDatabase.getInstance().getReference(SHIPPERS_PATH);
        queryRiderPos.addValueEventListener(riderPosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    distanceMap = new TreeMap<>();
                    posMap = new HashMap<>();
                    shipperName = new HashMap<>();

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if(d.exists()) {
                            Object tempCheckBoolean = d.child("available").getValue();
                            if(tempCheckBoolean != null){
                                boolean cstObject= ((Boolean) tempCheckBoolean).booleanValue();
                                if (cstObject) {
                                    shipperName.put(d.getKey(), d.child(SHIPPER_INFO).child("name").getValue(String.class));
                                    posMap.put(d.getKey(), d.child("shipper_pos").getValue(Position.class));
                                    distanceMap.put(Distance.distance(latitude, longitude,
                                            posMap.get(d.getKey()).latitude,
                                            posMap.get(d.getKey()).longitude), d.getKey());
                                }
                            }

                        }
                    }

                    if (distanceMap.isEmpty()) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setMessage("No shipper available. Retry later!")
                                .setCancelable(false)
                                .setNeutralButton("Ok", (dialog, id) -> getActivity().finish());

                        final AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        boolean flag = true;

                        for (Map.Entry<Double, String> entry : distanceMap.entrySet()) {
                            if (!shipperKey.contains(entry.getValue())) {
                                Log.d("Check entry.getKey :", entry.getKey().toString());
                                shipperKey.add(entry.getValue());
                                if (flag) {
                                    flag = false;
                                    Marker m = mMap.addMarker(new MarkerOptions().position(new
                                            LatLng(posMap.get(entry.getValue()).latitude,posMap.get(entry.getValue()).longitude))
                                            .title(shipperName.get(entry.getValue()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.nearest_icon))
                                            .snippet(new DecimalFormat("#.##").format(entry.getKey()) + " km"));
                                    m.setTag(entry.getValue());
                                    markerMap.put(entry.getValue(), m);
                                } else {
                                    Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(posMap.get(entry.getValue()).latitude, posMap.get(entry.getValue()).longitude))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shipper))
                                            .title(shipperName.get(entry.getValue()))
                                            .snippet(new DecimalFormat("#.##").format(entry.getKey()) + " km"));
                                    m.setTag(entry.getValue());
                                    markerMap.put(entry.getValue(), m);
                                }
                            } else {
                                if (flag) {
                                    flag = false;

                                    markerMap.get(entry.getValue()).setPosition(new LatLng(posMap.get(entry.getValue()).latitude, posMap.get(entry.getValue()).longitude));
                                    markerMap.get(entry.getValue()).setSnippet(new DecimalFormat("#.##").format(entry.getKey()) + " km");
                                    markerMap.get(entry.getValue()).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.nearest_icon));
                                } else {
                                    markerMap.get(entry.getValue()).setPosition(new LatLng(posMap.get(entry.getValue()).latitude, posMap.get(entry.getValue()).longitude));
                                    markerMap.get(entry.getValue()).setSnippet(new DecimalFormat("#.##").format(entry.getKey()) + " km");
                                    markerMap.get(entry.getValue()).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_shipper));
                                }
                            }
                        }

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                .title(restaurantName));

                        mMap.setOnInfoWindowClickListener(marker ->
                                selectShipper(marker.getTag().toString(),
                                        getActivity().getIntent().getStringExtra(ORDER_ID),
                                        getActivity().getIntent().getStringExtra(CUSTOMER_ID)));

                        ((MapsActivity) getActivity()).saveDistanceMap(distanceMap);
                        ((MapsActivity) getActivity()).saveRidersList(shipperName);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MAPS FRAGMENT", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void checkFormatVariable(){

    }

    private void selectShipper(String shipperId, String orderId, String customerId) {
        SelectionShipperCommon.selectShipper(shipperId,orderId,customerId,getContext(),TAG);
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onPause() {
        queryRiderPos.removeEventListener(riderPosListener);
        super.onPause();
    }

    @Override
    public void onStop() {
        queryRiderPos.removeEventListener(riderPosListener);
        super.onStop();
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

class Distance {

    private static final int EARTH_RADIUS = 6371; // Earth radius by KM

    public static double distance(double startLat, double startLong,
                                  double endLat, double endLong) {

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = distanceFormula(dLat) + Math.cos(startLat) * Math.cos(endLat) * distanceFormula(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    public static double distanceFormula(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

}
