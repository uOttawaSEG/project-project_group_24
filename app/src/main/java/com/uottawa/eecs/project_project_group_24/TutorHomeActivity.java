package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TutorHomeActivity extends AppCompatActivity {

    public static final String EXTRA_TUTOR_ID = "extra_tutor_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        String tutorId = getIntent().getStringExtra(EXTRA_TUTOR_ID);
        if (tutorId == null || tutorId.isEmpty()) tutorId = "TUTOR_DEMO"; // fallback for test

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        ViewPager2 pager = findViewById(R.id.viewPager);
        TabLayout tabs = findViewById(R.id.tabLayout);

        TutorPagerAdapter adapter = new TutorPagerAdapter(this, tutorId);
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(position == 0 ? "My Availability" : "My Sessions");
            }
        }).attach();
        Button logoff = findViewById(R.id.btnLogoff3);
        logoff.setOnClickListener(v -> {
            FirebaseManager fbmanager = FirebaseManager.getInstance();
            fbmanager.logout();
            Intent i = new Intent(TutorHomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
