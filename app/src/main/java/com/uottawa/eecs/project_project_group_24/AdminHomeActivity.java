package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {

    private Button btnShowRejected, btnShowPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnShowRejected = findViewById(R.id.btnShowRejected);
        btnShowPending = findViewById(R.id.btnShowPending);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AdminRequestListFragment.newPending())
                .commit();

        btnShowRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, AdminRequestListFragment.newRejected())
                        .commit();
            }
        });

        btnShowPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, AdminRequestListFragment.newPending())
                        .commit();
            }
        });
        Button logoff = findViewById(R.id.btnLogoff2);
        logoff.setOnClickListener(v -> {
            FirebaseManager fbmanager = FirebaseManager.getInstance();
            fbmanager.logout();
            Intent i = new Intent(AdminHomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
