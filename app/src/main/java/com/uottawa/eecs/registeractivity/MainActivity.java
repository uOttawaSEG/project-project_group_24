package com.uottawa.eecs.registeractivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStudent, btnTutor;
    //dev_aiden_kang: add arbitrary admin button, should be removed later.
    private Button btnAdmin;

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

        //dev_aiden_kang: add admin button for demo(after D2, you can delete)
        btnAdmin = findViewById(R.id.btnAdmin);
        btnAdmin.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
            i.putExtra("role", "administrator");
            startActivity(i);
        });

    }
}
