package com.mobilegradingsystem.mobilegradingsystem;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Helpfull {
    private void equival(){
        String rawScore[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99","100"};
        String equivalent[] = {"65", "65", "65", "65", "65", "66", "66", "66", "66", "66", "67", "67", "67", "67", "67", "68", "68", "68", "68", "68", "69", "69", "69", "69", "69", "70", "70", "70", "70", "70", "70", "71", "71", "71", "71", "71", "71", "72", "72", "72", "72", "72", "72", "73", "73", "73", "73", "73", "73", "74", "75", "75", "76", "76", "77", "77", "78", "78", "79", "79", "80", "80", "81", "81", "82", "82", "83", "83", "84", "84", "85", "85", "85", "86", "86", "86", "87", "87", "87", "88", "88", "88", "89", "89", "89", "90", "90", "90", "91", "91", "91", "92", "92", "92", "93", "93", "93", "94", "94", "94","95"};
        ArrayList<String> raw = new ArrayList<>();
        final ArrayList<String> equi = new ArrayList<>();
        for (String i:rawScore){
            raw.add(i);
        }
        for (String i:equivalent){
            equi.add(i);
        }

        FirebaseFirestore.getInstance().collection("scoreEquivalents").document("4TYJW5v4LBujNmMCNEPs").update("rawScore",raw,"equivalent",equi).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println(equi);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e);
            }
        });
    }
}
