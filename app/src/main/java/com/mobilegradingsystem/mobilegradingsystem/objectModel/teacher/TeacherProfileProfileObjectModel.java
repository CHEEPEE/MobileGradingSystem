package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.Login;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.teacher.RegisterStudent;

import java.security.PrivateKey;

/**
 * Created by Keji's Lab on 26/11/2017.
 */


public class TeacherProfileProfileObjectModel {
    private String C_TEACHER_PROFILE = "teacherProfile";
    private String C_USERS = "users";
    private String C_TEMPUSERS = "tempCreateUsers";
    private String userId = "";
    private String teacherName = "";
    private String teacherId = "";
    private String accountStatus = "pending";
    private String email = "";
    private Context context;
    private String password = "";
    private String userType = "teacher";
    private Activity act;


    public TeacherProfileProfileObjectModel() {

    }

    public TeacherProfileProfileObjectModel(
            String userId,
            String teacherId,
            String teacherName,
            String accountStatus,
            String email,String userType


    ) {
        this.userId = userId;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.accountStatus = accountStatus;
        this.email = email;
        this.userType = "teacher";

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setContext(Context context, Activity act) {
        this.context = context;
        this.act = act;
    }

    public interface MyCallback {
        void onSuccess();

        void onError(String err);
    }

    public void testSavethis() {
        FirebaseFirestore.getInstance().collection("TestThis").add(this)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Utils.message("Testing Complete", context);
                    }
                });
    }


    public void validateIfInstructorExist(final MyCallback callback) {
        FirebaseFirestore.getInstance().collection(this.C_USERS).whereEqualTo("userSchoolId",
                this.teacherId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) {
                    callback.onSuccess();
                } else {
                    callback.onError("Teacher ID Already Exist");
                }
            }
        });
    }

    public void registerTeacher() {
        validate(new MyCallback() {
            @Override
            public void onSuccess() {
                validateIfInstructorExist(new MyCallback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("ID Valid");
                        System.out.println("Email: "+email+" Password: "+password);
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        System.out.println("TASK DONE");
                                        if (task.isSuccessful()) {
                                            System.out.println("Success Registration");
                                            // Sign in success, update UI with
                                            // the signed-in user's information
                                            final FirebaseUser user = FirebaseAuth.getInstance()
                                                    .getCurrentUser();
                                            setUserId(user.getUid());
                                            saveToTeacherProfile();
                                            saveToUsers();
                                            addTeacherToTempUsers();
                                            Intent i = new Intent(act,Login.class);
                                            act.startActivity(i);
                                            act.finish();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                    Utils.message("Registration Failed",context);
                                        }
                                        // ...
                                    }

                                });

                    }

                    @Override
                    public void onError(String err) {
                        Utils.message("ID already Exist",context);
                        System.out.println("ID already Exist");
                    }
                });

            }

            @Override
            public void onError(String err) {

            }
        });
    }


    private void addTeacherToTempUsers() {
        getCollection(C_TEMPUSERS).document(this.email).set(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }


    private void saveToUsers() {
        getCollection(C_USERS).document(this.userId).set(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }

    private void saveToTeacherProfile() {

        getCollection(C_TEACHER_PROFILE).document(this.userId).set(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private CollectionReference getCollection(String collectionName) {
        return FirebaseFirestore.getInstance().collection(collectionName);
    }

    public boolean teacherIDValid() {
        boolean value = true;
        if (teacherId.trim().length() == 0 || teacherId.length() <= 4) {
            value = false;
            Utils.message("Invalid ID", context);
        }
        return value;
    }

    public boolean isPasswordValid() {
        boolean value = true;
        if (password.trim().length() == 0 || password.length() <= 7) {
            value = false;

        }
        return value;
    }

    public boolean isEmpty(String value) {
        return value.trim().length() == 0;
    }


    public void validate(MyCallback callback) {
        if (teacherIDValid() && !isEmpty(this.teacherName) &&
                !isEmpty(this.accountStatus) && isEmailValid() && isPasswordValid())
            callback.onSuccess();
        else
            callback.onError("Error");
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEmailValid(){
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(this.email);
            return m.matches();
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }


    public String getUserId() {
        return userId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }
}
