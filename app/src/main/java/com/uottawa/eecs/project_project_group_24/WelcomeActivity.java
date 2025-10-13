package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String role = getIntent().getStringExtra("role");
        if (role == null) role = "Unknown";

        TextView text = findViewById(R.id.welcomeText);
        text.setText("Welcome! You are logged in as " + role);

        Button logoff = findViewById(R.id.btnLogoff);
        logoff.setOnClickListener(v -> finish());
    }
}
