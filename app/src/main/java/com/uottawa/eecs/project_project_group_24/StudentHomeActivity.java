package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentHomeActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_ID   = "email";
    public static final String EXTRA_STUDENT_NAME = "firstName";

    private String studentId;
    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        // Retrieve student information uploaded during login
        studentId   = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        studentName = getIntent().getStringExtra(EXTRA_STUDENT_NAME);

        Button btnTabSearch   = findViewById(R.id.btnTabSearch);
        Button btnTabSessions = findViewById(R.id.btnTabSessions);
        Button btnLogout      = findViewById(R.id.btnLogout);

        btnTabSearch.setOnClickListener(v -> showSearchFragment());
        btnTabSessions.setOnClickListener(v -> showSessionsFragment());

        // Logout button: Returns to LoginActivity and clears the back stack.
        btnLogout.setOnClickListener(v -> {
            // If you have a real signOut
            // (such as FirebaseAuth/FirebaseManager), you can call it here.
            // FirebaseAuth.getInstance().signOut();
            // FirebaseManager.getInstance().logout(); (if we have this method)

            Intent intent = new Intent(StudentHomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        if (savedInstanceState == null) {
            showSearchFragment();
        }
    }

    private void showSearchFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.student_fragment_container,
                        SearchSlotsFragment.newInstance(studentId, studentName)
                )
                .commit();
    }

    private void showSessionsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.student_fragment_container,
                        StudentSessionsFragment.newInstance(studentId)
                )
                .commit();
    }
}
