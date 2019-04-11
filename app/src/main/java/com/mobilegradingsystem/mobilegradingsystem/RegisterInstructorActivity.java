package com.mobilegradingsystem.mobilegradingsystem;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherProfileProfileObjectModel;

public class RegisterInstructorActivity extends AppCompatActivity {

     TeacherProfileProfileObjectModel teacherObjectModel = new TeacherProfileProfileObjectModel();
     Context context;
     EditText teacherId,email,teacherName,password;
     TextView login;
     CheckBox isShowPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_instructor);
        context = this;
        teacherObjectModel.setContext(RegisterInstructorActivity.this,RegisterInstructorActivity.this);
        email = (EditText) findViewById(R.id.email);
        teacherId =(EditText) findViewById(R.id.instructorID);
        teacherName =(EditText) findViewById(R.id.UserName);
        password =(EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.login);
        isShowPassword = (CheckBox) findViewById(R.id.isShowPassword);
        isShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                password.setTransformationMethod(b? HideReturnsTransformationMethod.getInstance(): PasswordTransformationMethod.getInstance());
            }
        });
        
        userInputs();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teacherObjectModel.validate(new TeacherProfileProfileObjectModel.MyCallback() {
                    @Override
                    public void onSuccess() {
                        Utils.message("Valid",RegisterInstructorActivity.this);
                        teacherObjectModel.registerTeacher();
                    }

                    @Override
                    public void onError(String err) {

                        if(!teacherObjectModel.teacherIDValid()){
                           teacherId.setError("Invalid Id");
                        }
                        else if(!teacherObjectModel.isEmailValid()){
                           email.setError("Invalid Email Address");
                        }
                        else if(!teacherObjectModel.isPasswordValid()){
                           password.setError("Password Empty/too short");
                        }else {
                            Utils.message("Fill up All fields",RegisterInstructorActivity.this);
                        }
                    }
                });
            }
        });

    }

    private void userInputs(){
        getuserInput(email, new OnChangeText() {
            @Override
            public void change(String text) {
                teacherObjectModel.setEmail(text);
            }

            @Override
            public void onError(String err) {

            }
        });

        getuserInput(teacherId, new OnChangeText() {
            @Override
            public void change(String text) {
                teacherObjectModel.setTeacherId(text);
            }

            @Override
            public void onError(String err) {

            }
        });

        getuserInput(teacherName, new OnChangeText() {
            @Override
            public void change(String text) {
                teacherObjectModel.setTeacherName(text);
            }

            @Override
            public void onError(String err) {

            }
        });

        getuserInput(password, new OnChangeText() {
            @Override
            public void change(String text) {
                teacherObjectModel.setPassword(text);
            }

            @Override
            public void onError(String err) {

            }
        });


    }




    private interface OnChangeText{

            void change(String text);

            void onError(String err);

    }
    private void getuserInput(EditText editText, final OnChangeText onChangeText ){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onChangeText.change(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
