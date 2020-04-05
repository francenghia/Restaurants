package com.example.customer.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.common.User;
import com.example.customer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import static com.example.common.Shared.CUSTOMER_PATH;
import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.orderToTrack;
import static com.example.common.Shared.user;

public class HomeActivity extends AppCompatActivity implements
        RestaurantFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        OrderFragment.OnFragmentInteractionListener {

    public static final String PREFERENCE_NAME = "ORDER_LIST";

    private SharedPreferences order_to_listen;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof RestaurantFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RestaurantFragment()).commit();
                }
                return true;
            case R.id.navigation_profile:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ProfileFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                return true;
            case R.id.navigation_reservation:
                //onRefuseOrder();
                if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof OrderFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
                }
                return true;
        }

        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RestaurantFragment()).commit();
        }

        order_to_listen = this.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String storedHashMapString = order_to_listen.getString("HashMap",null);
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        orderToTrack = gson.fromJson(storedHashMapString, type);

        getUserInfo();

    }

    public void getUserInfo() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(CUSTOMER_PATH).child(ROOT_UID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("customer_info").getValue(User.class);
                Log.d("user", ""+user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("MAIN", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
