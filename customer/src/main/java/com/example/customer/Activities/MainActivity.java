package com.example.customer.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.common.Shared.ROOT_UID;

public class MainActivity extends AppCompatActivity {
    private String email, password, errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!= null){
            ROOT_UID = auth.getUid();

            Intent i = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(i);

            finish();
        }

        findViewById(R.id.sign_up).setOnClickListener(e -> {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivityForResult(i,1);
        });

        findViewById(R.id.sign_in).setOnClickListener(e -> {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Authenticating...");
            if(checkFields()){

                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                ROOT_UID = auth.getUid();
                                Intent fragment = new Intent(this, HomeActivity.class);
                                startActivity(fragment);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this,"Wrong Username or Password", Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else{
                Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });


    }

    public boolean checkFields(){
        email = ((EditText)findViewById(R.id.email)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();

        if(email.trim().length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errMsg = "Invalid Mail";
            return false;
        }

        if(password.trim().length() == 0){
            errMsg = "Fill password";
            return false;
        }

        return true;
    }

}
