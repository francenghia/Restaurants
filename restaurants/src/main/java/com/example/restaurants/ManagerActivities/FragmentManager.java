package com.example.restaurants.ManagerActivities;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurants.FoodActivities.OfferFragment;
import com.example.restaurants.HomeActivities.HomeFragment;
import com.example.restaurants.OrderActivities.OrderFragment;
import com.example.restaurants.ProfileActivities.ProfileFragment;
import com.example.restaurants.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.common.Shared.RESERVATION_PATH;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;

public class FragmentManager extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        OfferFragment.OnFragmentInteractionListener,
        OrderFragment.OnFragmentInteractionListener{

    private View notificationBadge;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_manager);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        addBadgeView();
        hideBadgeView();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item ->  {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if(!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                }
                return true;
            case R.id.navigation_profile:
                if(!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ProfileFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                return true;
            case R.id.navigation_dailyoffer:
                if(!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof OfferFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfferFragment()).commit();
                }
                return true;
            case R.id.navigation_reservation:
                if(!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof OrderFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
                }
                return true;
        }

        return false;
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkBadge();
    }

    private void checkBadge(){
        Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO + "/" + ROOT_UID
                + "/" + RESERVATION_PATH);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long count = dataSnapshot.getChildrenCount();

                    ((TextView)notificationBadge.findViewById(R.id.count_badge)).setText(Long.toString(count));
                    refreshBadgeView();
                }
                else{
                    hideBadgeView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addBadgeView() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3);

        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);

        itemView.addView(notificationBadge);
    }

    private void refreshBadgeView() {
        notificationBadge.setVisibility(VISIBLE);
    }

    private void hideBadgeView(){
        notificationBadge.setVisibility(INVISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
