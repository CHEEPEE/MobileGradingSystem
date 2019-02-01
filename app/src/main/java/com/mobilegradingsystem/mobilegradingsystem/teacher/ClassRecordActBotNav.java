package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ClassRecordVersion;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.AnnouncementTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.DashboardClassTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.ViewStudentsTeacherFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassAttendanceFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassCharacterFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassExamFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassGradeFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassParticipationFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassProjectsFragement;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord.ClassQuizzesLongTestFragement;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ViewPagerAdapter;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class ClassRecordActBotNav extends AppCompatActivity {

    SlidingRootNav slidingRootNav;
    ClassParticipationFragement classParticipationFragement;
    ClassProjectsFragement classProjectsFragement;
    ClassQuizzesLongTestFragement classQuizzesLongTestFragement;
    ClassGradeFragement classGradeFragement;
    ClassAttendanceFragement classAttendanceFragement;
    ClassCharacterFragement classCharacterFragement;
    ClassExamFragement classExamFragement;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    String classKey;
    String term;
    ClassRecordVersion classRecordVersion;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_record_act_bot_nav);
        classKey = getIntent().getExtras().getString("key");
        term = getIntent().getExtras().getString("term");
        viewPager = (ViewPager) findViewById(R.id.viewpager) ;
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withDragDistance(200)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withRootViewScale(1f)
                .withRootViewYTranslation(4)
                .withMenuLayout(R.layout.root_nav_class_record)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(true)
                .inject();

        findViewById(R.id.toggleMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }else {
                    slidingRootNav.openMenu();
                }
            }
        });

        FirebaseFirestore.getInstance().collection("classRecordVersion").document("4TYJW5v4LBujNmMCNEPs").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ClassRecordVersion cv = documentSnapshot.toObject(ClassRecordVersion.class);
                classRecordVersion = cv;
                setupViewPager(viewPager);
            }
        });


    }
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        classGradeFragement = new ClassGradeFragement();
        classProjectsFragement = new ClassProjectsFragement();
        classQuizzesLongTestFragement = new ClassQuizzesLongTestFragement();
        classParticipationFragement = new ClassParticipationFragement();
        classAttendanceFragement = new ClassAttendanceFragement();
        classCharacterFragement = new ClassCharacterFragement();
        classExamFragement = new ClassExamFragement();

        adapter.addFragment(classCharacterFragement);
        adapter.addFragment(classAttendanceFragement);
        adapter.addFragment(classParticipationFragement);
        adapter.addFragment(classProjectsFragement);
        adapter.addFragment(classQuizzesLongTestFragement);
        adapter.addFragment(classExamFragement);
        adapter.addFragment(classGradeFragement);

        viewPager.setAdapter(adapter);
    }
    public String getClassKey() {
        return classKey;
    }
    public String getTerm() {
        return term;
    }

    public ClassRecordVersion getClassRecordVersion() {
        return classRecordVersion;
    }
}
