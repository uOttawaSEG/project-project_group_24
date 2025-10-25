package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AdminRequestListFragment.newPending())
                .commit();
    }

    public void showRejected() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AdminRequestListFragment.newRejected())
                .addToBackStack(null)
                .commit();
    }
}

