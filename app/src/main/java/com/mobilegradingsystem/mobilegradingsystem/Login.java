package com.mobilegradingsystem.mobilegradingsystem;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentRegistration;
import com.mobilegradingsystem.mobilegradingsystem.teacher.TeacherProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.TeacherRegistration;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;

import javax.annotation.Nullable;

public class Login extends AppCompatActivity {
    TextView getStarted;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView getstarted;
    EditText userSchoolId,password;
    TextView login,forgotPassword;
    FirebaseFirestore firestore;
    Dialog inputEmailDialog;
    ConstraintLayout laodingIndicator;
    CheckBox passwordVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        userSchoolId = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.login);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        laodingIndicator = (ConstraintLayout) findViewById(R.id.loading_layout);
        passwordVisibility = (CheckBox) findViewById(R.id.passwordVisibility);


        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Logging In");
                login.setClickable(false);
                firestore.collection("users").whereEqualTo("userSchoolId", userSchoolId.getText().toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots.getDocuments().size() == 0){
                            login.setText("Login");
                            login.setClickable(true);
                            Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                            authWithEmail(userProfileObjectModel.getEmail(),password.getText().toString());
                        }
                    }
                });
            }
        });

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                        signIn();
                        loading(false);


                    }
                },
                5000
        );
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPasswordModal();
            }
        });
        passwordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showPassword(isChecked);
                Utils.message("isShow password? "+isChecked,Login.this);
            }
        });
    }

    void loading(Boolean load){
        try {
            findViewById(R.id.loading_layout).setVisibility(load?View.VISIBLE:View.INVISIBLE);
        }catch (AndroidRuntimeException e){

        }
    }

    private void authWithEmail(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            signIn();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Utils.message(task.getException().toString(),Login.this);
                            login.setText("Login");
                            login.setClickable(true);

                        }

                        // ...
                    }
                });
    }


    private void signIn() {

        FirebaseUser user = mAuth.getCurrentUser();
        boolean isUser = false;

        try {
            isUser = !user.getUid().toString().equals("");
        }catch (NullPointerException e){
            System.out.println("isUser:" +isUser);
        }
//        laodingIndicator.setVisibility(View.INVISIBLE);
        if (isUser) {

            Log.d(TAG, "signInWithEmail:success");
            FirebaseFirestore.getInstance()
                    .collection("users").document(mAuth.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot.getData() != null) {
//                                        if user type == student
                                if (documentSnapshot.getData().get("userType").toString().equals("student")) {
                                    FirebaseFirestore.getInstance()
                                            .collection("studentProfile")
                                            .document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                                                   check if user is registered
                                            if (documentSnapshot.getData() != null) {
                                                if (documentSnapshot.get("accountStatus").equals("pending")) {
                                                    Intent i = new Intent(Login.this, IfAccountIsPending.class);
                                                    startActivity(i);
                                                    finish();
                                                } else if (documentSnapshot.get("accountStatus").equals("block")) {
                                                    Intent i = new Intent(Login.this, IfAccountIsBlock.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Intent i = new Intent(Login.this, StudentProfile.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            } else {
//                                                        the user has not been registered
                                                Intent i = new Intent(Login.this, StudentRegistration.class);
                                                i.putExtra("isUpdate", false);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });
                                } else {
//                                            else user is a teacher
                                    FirebaseFirestore.getInstance()
                                            .collection("teacherProfile")
                                            .document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            if (documentSnapshot.getData() != null) {
                                                if (documentSnapshot.get("accountStatus").equals("pending")) {
                                                    Intent i = new Intent(Login.this, IfAccountIsPendingTeacher.class);
                                                    startActivity(i);
                                                    finish();
                                                } else if (documentSnapshot.get("accountStatus").equals("block")) {
                                                    Intent i = new Intent(Login.this, IfAccountIsBlockTeacher.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Intent i = new Intent(Login.this, TeacherProfile.class);
                                                    startActivity(i);
                                                    finish();
                                                }

                                            } else {
                                                Intent i = new Intent(Login.this, TeacherRegistration.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });

                                }
                            } else {
//                                selectUserTypeDialog();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                mAuth = FirebaseAuth.getInstance();
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }else {
            loading(false);
        }
    }

    private void forgetPasswordModal(){
        inputEmailDialog = new Dialog(Login.this);
        inputEmailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputEmailDialog.setCancelable(true);
        inputEmailDialog.setContentView(R.layout.dlg_single_input_layout);

        Window window = inputEmailDialog.getWindow();
        inputEmailDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText email = (EditText) inputEmailDialog.findViewById(R.id.inputName);
        TextView confirm = (TextView) inputEmailDialog.findViewById(R.id.confirmButton);
        TextView modalTitle = (TextView) inputEmailDialog.findViewById(R.id.modalTitle);
        modalTitle.setText("Input Email Address");
        email.setHint("Email");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("")){
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Utils.message("Password Reset email has been sent",Login.this);
                                        inputEmailDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
        inputEmailDialog.show();
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            System.out.println(user);
//                            check user type

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            loading(null);
                        }
                        // ...
                    }
                });
    }
    private void showPassword(Boolean show){
       password.setTransformationMethod(show? HideReturnsTransformationMethod.getInstance(): PasswordTransformationMethod.getInstance());

    }
//    private void loading(boolean loading){
//        if (loading){
////            findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
//            getstarted.setVisibility(View.GONE);
//
//        }else {
////            findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
//            getstarted.setVisibility(View.VISIBLE);
//        }
//    }



//    void selectUserTypeDialog(){
//        final Dialog dialog = new Dialog(Login.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog_select_user);
//        Window window = dialog.getWindow();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        dialog.show();
//        dialog.findViewById(R.id.student).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                UserProfileObjectModel userProfileObjectModel
//                        = new UserProfileObjectModel(mAuth.getUid(),
//                        mAuth.getCurrentUser().getPhotoUrl().toString(),
//                        mAuth.getCurrentUser().getDisplayName(),
//                        mAuth.getCurrentUser().getPhoneNumber(),
//                        mAuth.getCurrentUser().getEmail(),"student",null);
//                FirebaseFirestore.getInstance()
//                        .collection("users")
//                        .document(mAuth.getUid()).set(userProfileObjectModel)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//
//                                Intent i = new Intent(Login.this,StudentRegistration.class);
//                                startActivity(i);
//                                dialog.dismiss();
//                                finish();
//                            }
//                        });
//            }
//        });
//        dialog.findViewById(R.id.teacher).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserProfileObjectModel userProfileObjectModel
//                        = new UserProfileObjectModel(mAuth.getUid(),
//                        mAuth.getCurrentUser().getPhotoUrl().toString(),
//                        mAuth.getCurrentUser().getDisplayName(),
//                        mAuth.getCurrentUser().getPhoneNumber(),
//                        mAuth.getCurrentUser().getEmail(),"teacher",null);
//                FirebaseFirestore.getInstance()
//                        .collection("users")
//                        .document(mAuth.getUid()).set(userProfileObjectModel)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//
//                                Intent i = new Intent(Login.this,TeacherRegistration.class);
//                                startActivity(i);
//                                dialog.dismiss();
//                                finish();
//                            }
//                        });
//            }
//        });
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });
//    }


}
