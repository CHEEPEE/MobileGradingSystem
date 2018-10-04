package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.AnnouncementTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.DashboardClassTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.ViewStudentsTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ViewPagerAdapter;

public class ClssProfileTeacherBotNav extends AppCompatActivity {

    private TextView mTextMessage;
    ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView navigation;
    ViewPagerAdapter adapter;
    String businessKey;
    Context context;
    Dialog dialog;
    String classKey;
    ViewStudentsTeacherFragement viewStudentsTeacherFragement;
    AnnouncementTeacherFragement announcementTeacherFragement;
    DashboardClassTeacherFragement dashboardClassTeacherFragement;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clss_profile_teacher_bot_nav);
        context = ClssProfileTeacherBotNav.this;
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        classKey = getIntent().getExtras().getString("classKey");


        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);

                }

                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewStudentsTeacherFragement = new ViewStudentsTeacherFragement();
        announcementTeacherFragement = new AnnouncementTeacherFragement();
        dashboardClassTeacherFragement = new DashboardClassTeacherFragement();
        adapter.addFragment(dashboardClassTeacherFragement);
        adapter.addFragment(announcementTeacherFragement);
        adapter.addFragment(viewStudentsTeacherFragement);

        viewPager.setAdapter(adapter);
    }
    public String getClassKey() {
         return classKey;
    }
}
