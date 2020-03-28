package com.example.customer.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private boolean dialog_open = false;

    private String name;
    private String surname;
    private String mail;
    private String phone;
    private String currentPhotoPath;
    private String psw;
    private String psw_confirm;
    private String address;

    private String error_msg;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
}
