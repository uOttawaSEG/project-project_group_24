package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String role = getIntent().getStringExtra("role");
        String status = getIntent().getStringExtra("state");
        TextView text = findViewById(R.id.welcomeText);
        if(status!=null)Log.d("OTA_WELCOME",status);
        if(status!=null&&status.equalsIgnoreCase("waiting"))
        {
            text.setText("Welcome! You are logged in but your application is still pending.");
        }
        else if(status!=null&&status.equalsIgnoreCase("Rejected")){
            text.setText("Welcome! You are logged in but your application has unfortunately been rejected\n\nFor more help please contact our support team at:\nhelp@OTA.ca or\n+1(153)-745-7452.");
        }
        else{
            if (role == null) role = "Unknown";
            text.setText("Welcome! You are logged in as " + role);
        }
        Button logoff = findViewById(R.id.btnLogoff);
        logoff.setOnClickListener(v -> {
            FirebaseManager fbmanager = FirebaseManager.getInstance();
            fbmanager.logout();
            Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}
