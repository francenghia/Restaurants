package com.example.restaurants.OrderActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.restaurants.R;

import java.util.HashMap;
import java.util.TreeMap;

import static com.example.common.Shared.CUSTOMER_ID;
import static com.example.common.Shared.ORDER_ID;
import static com.example.restaurants.OrderActivities.SelectionShipperCommon.reservationConfirm;

public class MapsActivity extends AppCompatActivity implements ListShipperFragment.OnFragmentInteractionListener,
        MapsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MapsActivity";
    private HashMap<String, String> shipperMap;
    private TreeMap<Double, String> distanceMap;

    private static boolean mapsFragVisible = false;
    private boolean mLocationPermissionGranted;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
        if (checkMapServices() && mLocationPermissionGranted) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_maps_container,
                    new MapsFragment()).commit();
            mapsFragVisible = true;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.near_rider:
                chooseNearestShipper();
                return true;
            case R.id.list_riders:
                if (mapsFragVisible) {
                    mapsFragVisible = false;
                    item.setIcon(R.drawable.icon_map);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_maps_container,
                            new ListShipperFragment()).commit();
                }
                else {
                    mapsFragVisible = true;
                    item.setIcon(R.drawable.showlist_map);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_maps_container,
                            new MapsFragment()).commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseNearestShipper() {
        AlertDialog choiceDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.reservation_dialog, null);

        view.findViewById(R.id.button_confirm).setOnClickListener(e -> {
            choiceDialog.dismiss();
            selectShipper(distanceMap.firstEntry().getValue(),
                    getIntent().getStringExtra(ORDER_ID),
                    getIntent().getStringExtra(CUSTOMER_ID),choiceDialog);
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(e -> choiceDialog.dismiss());

        choiceDialog.setView(view);
        choiceDialog.setTitle("Do you want to choose automatically the nearest rider?");
        choiceDialog.show();
    }

    private void selectShipper(String shipperId, String orderId, String customerId, AlertDialog choiceDialog) {
        reservationConfirm(shipperId,orderId,customerId,choiceDialog,getApplicationContext(),TAG);
    }

    private boolean checkMapServices(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }

        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setNegativeButton("No", (dialog, id) -> finish())
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void saveRidersList(HashMap<String, String> ridersMap){
        this.shipperMap = ridersMap;
    }

    public void saveDistanceMap(TreeMap<Double, String> distanceMap){
        this.distanceMap = distanceMap;
    }

    public TreeMap<Double, String> getDistanceMap(){
        return this.distanceMap;
    }

    public HashMap<String, String> getRidersMap(){
        return this.shipperMap;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
