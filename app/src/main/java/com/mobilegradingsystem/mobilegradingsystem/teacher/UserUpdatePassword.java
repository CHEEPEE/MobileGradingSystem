package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;

public class UserUpdatePassword extends AppCompatActivity {
    EditText oldPassword,newPassword,confirmPassword;
    Button updateProfile;
    String TAG = "TeacherProfile";
    boolean isPasswordShown = false;
    boolean isNewPasswordShown = false;
    boolean isConfirmPasswordShown = false;
    ImageView showOldPassword,showNewPassword,showConfirmPassword;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_password);
        context = this;
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        showOldPassword = (ImageView) findViewById(R.id.showOldPassword);
        showNewPassword = (ImageView) findViewById(R.id.showNewPassword);
        showConfirmPassword = (ImageView) findViewById(R.id.showConfirmPassword);

        showOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword(oldPassword,!isPasswordShown,(ImageView) v);
                isPasswordShown = !isPasswordShown;
            }
        });
        showNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword(newPassword,!isNewPasswordShown,(ImageView) v);
                isNewPasswordShown = !isNewPasswordShown;
            }
        });
        showConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword(confirmPassword,!isConfirmPasswordShown,(ImageView) v);
                isConfirmPasswordShown = !isConfirmPasswordShown;
            }
        });
        updateProfile = (Button) findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (Utils.confirmPassword(newPassword,confirmPassword)){
                 reAuthUser(oldPassword.getText().toString(),newPassword.getText().toString());
              }else {
                  Utils.message("Password not match",context);
              }
            }
        });
    }

    private void showPassword(EditText password,boolean show,ImageView view){
        view.setImageDrawable(!show? getResources().getDrawable(R.drawable.ic_remove_red_eye_black_24dp):getResources().getDrawable(R.drawable.ic_do_not_disturb_black_24dp));
        password.setTransformationMethod(show? HideReturnsTransformationMethod.getInstance(): PasswordTransformationMethod.getInstance());
    }
    private void reAuthUser(String oldPassword,final String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        if (  task.isSuccessful()){
                            updatePassword(newPassword);
                        }else {
                            Utils.message("Password Incorrect",context);
                        }
                    }
                });
    }


    private void updatePassword(String password){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        // Prompt the user to re-provide their sign-in credentials

        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.message("Password Updated",context);
                            finish();
                        }else {
                            Utils.message(task.toString(),context);
                        }
                    }
                });
    }
}
