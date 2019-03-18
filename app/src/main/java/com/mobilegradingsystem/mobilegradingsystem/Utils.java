package com.mobilegradingsystem.mobilegradingsystem;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

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
    public static double getEquivalentGrade(double finalGrade){
        Integer[] gradeScore = {95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55};
        double[] equivalentScore = {
                1.0,
                1.1,
                1.2,
                1.3,
                1.4,
                1.5,
                1.6,
                1.7,
                1.8,
                1.9,
                2.0,
                2.1,
                2.2,
                2.3,
                2.4,
                2.5,
                2.6,
                2.7,
                2.8,
                2.9,
                3.0,
                3.1,
                3.2,
                3.3,
                3.4,
                3.5,
                3.6,
                3.7,
                3.8,
                3.9,
                4.0,
                4.1,
                4.2,
                4.3,
                4.4,
                4.5,
                4.6,
                4.7,
                4.8,
                4.9,
                5.0};


        return equivalentScore[Arrays.asList(gradeScore).indexOf((int)finalGrade)];
    }

    public  static int getRating(int score){
        Integer[] rawScore = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
        int[] rating =  {65,
                65,
                65,
                65,
                65,
                66,
                66,
                66,
                66,
                66,
                67,
                67,
                67,
                67,
                67,
                68,
                68,
                68,
                68,
                68,
                69,
                69,
                69,
                69,
                69,
                70,
                70,
                70,
                70,
                70,
                70,
                71,
                71,
                71,
                71,
                71,
                71,
                72,
                72,
                72,
                72,
                72,
                72,
                73,
                73,
                73,
                73,
                73,
                73,
                74,
                75,
                75,
                76,
                76,
                77,
                77,
                78,
                78,
                79,
                79,
                80,
                80,
                81,
                81,
                82,
                82,
                83,
                83,
                84,
                84,
                85,
                85,
                85,
                86,
                86,
                86,
                87,
                87,
                87,
                88,
                88,
                88,
                89,
                89,
                89,
                90,
                90,
                90,
                91,
                91,
                91,
                92,
                92,
                92,
                93,
                93,
                93,
                94,
                94,
                94,
                95};

        return rating[Arrays.asList(rawScore).indexOf(score)];

    }


}

