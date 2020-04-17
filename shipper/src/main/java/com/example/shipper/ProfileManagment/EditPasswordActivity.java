package com.example.shipper.ProfileManagment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shipper.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPasswordActivity extends AppCompatActivity {
    private String oldPsw, newPsw, confirmPsw, errMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang cập nhật mật khẩu...");


        findViewById(R.id.text_psw_alert).setVisibility(View.INVISIBLE);
        findViewById(R.id.error_psw).setVisibility(View.INVISIBLE);

        findViewById(R.id.button).setOnClickListener(e -> {
            if(checkFields()){
                findViewById(R.id.text_psw_alert).setVisibility(View.INVISIBLE);
                findViewById(R.id.error_psw).setVisibility(View.INVISIBLE);

                progressDialog.show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();

                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPsw);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        user.updatePassword(newPsw).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                progressDialog.hide();
                                Toast.makeText(EditPasswordActivity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                progressDialog.hide();
                                Toast.makeText(EditPasswordActivity.this, "Đã có lỗi xảy ra. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        progressDialog.hide();
                        Toast.makeText(EditPasswordActivity.this, "Quá trình xác thực đã thất bại. Sai mật khẩu !.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
                Toast.makeText(EditPasswordActivity.this, errMsg, Toast.LENGTH_LONG).show();
        });
    }

    private boolean checkFields(){
        oldPsw = ((EditText)findViewById(R.id.old_psw)).getText().toString();
        newPsw = ((EditText)findViewById(R.id.new_password)).getText().toString();
        confirmPsw = ((EditText)findViewById(R.id.confirm_new_password)).getText().toString();

        if(oldPsw.trim().length() == 0){
            errMsg = "Vui lòng nhập mật khẩu cũ!";
            return false;
        }

        if(newPsw.trim().length() < 6){
            errMsg = "Mật khẩu phải có ít nhất 6 ký tự!";
            return false;
        }

        if(newPsw.trim().length() != confirmPsw.trim().length()){
            errMsg = "Mật khẩu phải giống nhau!";
            findViewById(R.id.text_psw_alert).setVisibility(View.VISIBLE);
            findViewById(R.id.error_psw).setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }
}
