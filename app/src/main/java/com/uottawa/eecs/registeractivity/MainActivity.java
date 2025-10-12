package com.uottawa.eecs.registeractivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStudent, btnTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStudent = findViewById(R.id.btnStudent);
        btnTutor = findViewById(R.id.btnTutor);

        // 跳到學生註冊
        btnStudent.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // 跳到導師註冊
        btnTutor.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, TutorRegisterActivity.class);
            startActivity(i);
        });
    }
}
