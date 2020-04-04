package com.example.customer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.common.Restaurateur;
import com.example.customer.Adapters.SectionsPageAdapter;
import com.example.customer.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.common.Shared.RESTAURATEUR_INFO;

public class TabApp extends AppCompatActivity {

    //Info restaurant
    private Restaurateur item;
    private String key;

    //Handle switch of tabs
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private long stars;
    private long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_app);
        //Functions for menu
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RatingBar r = findViewById(R.id.ratingbar);

        getIncomingIntent();

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager_id);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_id);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new OrderingFragment(), "Order");
        adapter.addFragment(new RatingFragment(), "Rewiew");
        viewPager.setAdapter(adapter);
    }

    private void getIncomingIntent() {
        item = (Restaurateur) getIntent().getSerializableExtra("res_item");
        key = getIntent().getStringExtra("key");

        Query query = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO).child(key).child("stars");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RatingBar r = findViewById(R.id.ratingbar);
                TextView number = (TextView) findViewById(R.id.number_rating);
                if(dataSnapshot.exists()) {
                    float s = ((Long)dataSnapshot.child("tot_stars").getValue()).floatValue();
                    float p = ((Long)dataSnapshot.child("tot_review").getValue()).floatValue();
                    if(p!=0){
                        r.setRating(s/p);
                        number.setText(String.format("%.2f", s/p));
                    }
                    else{
                        r.setRating(0);
                        number.setVisibility(View.GONE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setFields(item.getName(), item.getAddr(), item.getPhone(), item.getCuisine(), item.getMail(), item.getOpeningTime(), item.getPhotoUri());
    }

    private void setFields (String name, String addr, String cell, String description, String email, String opening, String img){
        TextView mname = findViewById(R.id.rest_info_name);
        TextView maddr = findViewById(R.id.rest_info_addr);
        TextView mcell = findViewById(R.id.rest_info_cell);
        TextView memail = findViewById(R.id.rest_info_mail);

        ImageView mimg = findViewById(R.id.imageView);

        mname.setText(name);
        maddr.setText(addr);
        mcell.setText(cell);
        memail.setText(email);

        if(!img.equals("null")) {
            Glide.with(getApplicationContext())
                    .load(img)
                    .into(mimg);
        }
    }

    public Restaurateur getItem() {
        return item;
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==1){
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
