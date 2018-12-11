package com.mobilegradingsystem.mobilegradingsystem;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
    public static  String studentProfile = "studentProfile";

    public static void uPrint(String text){
        System.out.println(text);
    }
    public static boolean confirmPassword(EditText newPassword, EditText confirmPassword){
        boolean isConfirm = false;
        try {
            if (newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                isConfirm = true;
            }
        }catch (NullPointerException e){

        }
        return  isConfirm;
    }

    public static void message(String message,Context context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


}

