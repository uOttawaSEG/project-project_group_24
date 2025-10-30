package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserHomeActivity extends AppCompatActivity {

    Button tutorbtn,studentbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        tutorbtn = findViewById(R.id.btnTutorRegister);
        studentbtn = findViewById(R.id.btnStudentRegister); //aiden - when user indentified as student.

        String password = getIntent().getStringExtra("password");
        String email = getIntent().getStringExtra("email");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String phone = getIntent().getStringExtra("phone");

        tutorbtn.setOnClickListener(v -> {
            Intent i = new Intent(this, TutorRegisterActivity.class);

            i.putExtra("email",email);
            i.putExtra("password",password);
            i.putExtra("phone", phone);
            i.putExtra("lastName", lastName);
            i.putExtra("firstName", firstName);
            startActivity(i);
        });
        studentbtn.setOnClickListener(v -> {
            Intent i = new Intent(this, StudentRegisterActivity.class);

            i.putExtra("email",email);
            i.putExtra("password",password);
            i.putExtra("phone", phone);
            i.putExtra("lastName", lastName);
            i.putExtra("firstName", firstName);
            startActivity(i);
        });
    }
}