package com.example.customer.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.common.ReviewItem;
import com.example.common.StarItem;
import com.example.common.User;
import com.example.customer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hsalf.smilerating.SmileRating;

import java.lang.reflect.Type;
import java.util.HashMap;

import static com.example.common.Shared.CUSTOMER_PATH;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.STATUS_DELIVERED;
import static com.example.common.Shared.STATUS_DELIVERING;
import static com.example.common.Shared.STATUS_DISCARDED;
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

    private void onRefuseOrder (){

        if(orderToTrack!=null){
            for(HashMap.Entry<String, Integer> entry : orderToTrack.entrySet()){
                Query query = FirebaseDatabase.getInstance().getReference(CUSTOMER_PATH).child(ROOT_UID).child("orders").child(entry.getKey());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Long changed_statusi = (Long) dataSnapshot.child("status").getValue();
                            Integer changed_status = changed_statusi.intValue();
                            if(!changed_status.equals(entry.getValue())) {
                                if (changed_status == STATUS_DISCARDED) {
                                    entry.setValue(changed_status);
                                    orderToTrack.put(entry.getKey(),changed_status);
                                    showAlertDialogDiscarded((String)dataSnapshot.child("key").getValue(), dataSnapshot.getKey());
                                }
                                else if (changed_status==STATUS_DELIVERING){
                                    entry.setValue(changed_status);
                                    orderToTrack.put(entry.getKey(), changed_status);

                                }
                                else if (changed_status==STATUS_DELIVERED){
                                    entry.setValue(changed_status);
                                    orderToTrack.put(entry.getKey(), changed_status);
                                    setRated(dataSnapshot.getKey(), false);
                                    showAlertDialogDelivered((String)dataSnapshot.child("key").getValue(), dataSnapshot.getKey());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void showAlertDialogDiscarded (String resKey, String orderKey){
        Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO).child(resKey).child("info");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
                final View view = factory.inflate(R.layout.discarded_dialog, null);

                alertDialog.setView(view);
                if(dataSnapshot.child("photoUri").exists()){
                    Glide.with(view).load(dataSnapshot.child("photoUri").getValue()).into((ImageView) view.findViewById(R.id.dialog_discarded_rating_icon));
                }
                ((TextView)view.findViewById(R.id.discarded_res_name)).setText((String)dataSnapshot.child("name").getValue());
                alertDialog.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showAlertDialogDelivered (String resKey, String orderKey){
        Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO).child(resKey).child("info");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                LayoutInflater factory = LayoutInflater.from(HomeActivity.this);
                final View view = factory.inflate(R.layout.rating_dialog, null);

                alertDialog.setView(view);
                if(dataSnapshot.child("photoUri").exists()){
                    Glide.with(view).load(dataSnapshot.child("photoUri").getValue()).into((ImageView) view.findViewById(R.id.dialog_rating_icon));
                }
                SmileRating smileRating = (SmileRating) view.findViewById(R.id.dialog_rating_rating_bar);
                //Button confirm pressed
                view.findViewById(R.id.dialog_rating_button_positive).setOnClickListener(a->{
                    if(smileRating.getRating()!=0) {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO + "/" + resKey).child("review");
                        HashMap<String, Object> review = new HashMap<>();
                        String comment = ((EditText)view.findViewById(R.id.dialog_rating_feedback)).getText().toString();
                        String rate_key = myRef.push().getKey();
                        updateRestaurantStars(resKey, smileRating.getRating());
                        if(!comment.isEmpty()){
                            setRated(orderKey, true);
                            review.put(rate_key, new ReviewItem(smileRating.getRating(), comment, ROOT_UID, user.getPhotoPath(), user.getName()));
                            myRef.updateChildren(review);
                        }
                        else{
                            setRated(orderKey, true);
                            review.put(rate_key, new ReviewItem(smileRating.getRating(), null, ROOT_UID, user.getPhotoPath(), user.getName()));
                            myRef.updateChildren(review);
                        }
                        Toast.makeText(getApplicationContext(), "Thanks for your review!", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You forgot to rate!", Toast.LENGTH_LONG).show();
                    }
                });
                view.findViewById(R.id.dialog_rating_button_negative).setOnClickListener(b->{
                    alertDialog.dismiss();
                });
                alertDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateRestaurantStars (String resKey, int stars) {
        Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO).child(resKey).child("stars");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> star = new HashMap<>();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO + "/" + resKey);
                if(dataSnapshot.exists()){
                    int s = ((Long)dataSnapshot.child("tot_stars").getValue()).intValue();
                    int p = ((Long)dataSnapshot.child("tot_review").getValue()).intValue();
                    star.put("stars", new StarItem(s+stars, p+1, -s-stars));
                    myRef.updateChildren(star);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefuseOrder();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void setRated (String orderKey, boolean bool) {
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference(CUSTOMER_PATH).child(ROOT_UID).child("orders").child(orderKey);
        HashMap <String, Object> rated = new HashMap<>();
        rated.put("rated", bool);
        myRef2.updateChildren(rated);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
