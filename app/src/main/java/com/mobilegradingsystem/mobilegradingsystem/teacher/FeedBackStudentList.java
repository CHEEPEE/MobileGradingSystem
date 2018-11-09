package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobilegradingsystem.mobilegradingsystem.R;

public class FeedBackStudentList extends AppCompatActivity {
    String classCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_student_list);
        classCode = getIntent().getExtras().getString("classCode");
    }
}
