package com.example.customer.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;

public class MainActivity extends AppCompatActivity {
    private String email, password, errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
