package com.example.restaurants.Startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurants.ManagerActivities.FragmentManager;
import com.example.restaurants.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.SIGNUP;

public class MainActivity extends AppCompatActivity {
    private String email, password, errMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);


        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Đang xác thực...");

            findViewById(R.id.sign_up).setOnClickListener(e -> {
                Intent login = new Intent(this, SignUpActivity.class);
                startActivityForResult(login, SIGNUP);
            });

            findViewById(R.id.login).setOnClickListener(h -> {
                if(checkFields()){
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if(task.isSuccessful()) {
                                    ROOT_UID = auth.getUid();

                                    progressDialog.dismiss();

                                    Intent fragment = new Intent(this, FragmentManager.class);
                                    startActivity(fragment);
                                    finish();
                                }
                                else {
                                    //Log.w("LOGIN", "signInWithCredential:failure", task.getException());
                                    progressDialog.dismiss();
                                    Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again.", Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Snackbar.make(findViewById(R.id.email), "Authentication Failed. Try again.", Snackbar.LENGTH_SHORT).show();
                            });
                }
                else{
                    Snackbar.make(findViewById(R.id.email), errMsg, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        else{
            ROOT_UID = auth.getUid();

            Intent fragment = new Intent(this, FragmentManager.class);
            startActivity(fragment);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkFields(){
        email = ((EditText)findViewById(R.id.email)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();

        if(email.trim().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errMsg = "Nhập email lại";
            return false;
        }

        if(password.trim().length() == 0){
            errMsg = "Nhập mật khẩu";
            return false;
        }

        return true;
    }
}
