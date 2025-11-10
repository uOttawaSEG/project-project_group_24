package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnGeneral;
    private FirebaseFirestore db;
    DatabaseReference user_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user_database = FirebaseDatabase.getInstance().getReference();
        FirebaseManager.getInstance().initializeRequests();

        Button btnLogin = findViewById(R.id.btnLoginMain);
        btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });
//        Button btnAdmin = findViewById(R.id.adminButton);
//        btnAdmin.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, AdminHomeActivity.class);
//            startActivity(i);
//        });


//        btnStudent = findViewById(R.id.btnStudent);
//        btnTutor = findViewById(R.id.btnTutor);
        btnGeneral = findViewById(R.id.btnGeneralRegister);

//        btnStudent.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
//            startActivity(i);
//        });

//        btnTutor.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, TutorRegisterActivity.class);
//            startActivity(i);
//        });
		
//		btnAdmin = findViewById(R.id.btnAdmin);
//        btnAdmin.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
//            i.putExtra("role", "administrator");
//            startActivity(i);
//        });

        btnGeneral.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // firebase


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();

    }

}