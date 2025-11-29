package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    // UI element
    private EditText editLoginEmail;
    private EditText editLoginPassword;
    private Button btnLogin;
    private TextView loginMessage;
    private ProgressBar loginProgress;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseManager fbManager;

    // Temporarily save login information
    String email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        loginMessage = findViewById(R.id.loginMessage);
        loginProgress = findViewById(R.id.loginProgress);

        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        loginMessage.setText("");
        email = editLoginEmail.getText().toString().trim();
        password = editLoginPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editLoginEmail.setError("Enter a valid email");
            return;
        }
        if (password.isEmpty()) {
            editLoginPassword.setError("Enter your password");
            return;
        }

        loginProgress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        fbManager = FirebaseManager.getInstance();
        fbManager.loginUser(email, password, this); // 成功後會呼叫 loginCallback()
    }

    /** After login verification is complete, FirebaseManager will call... */
    public void loginCallback() {
        loginProgress.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        // 1) Admin
        if (fbManager.getAdmin()) {
            Intent i = new Intent(LoginActivity.this, AdminHomeActivity.class);
            startActivity(i);
            finish();
            return;
        }

        // 2) check the regular user log in successfully
        if (!fbManager.getLoggedIn()) {
            Toast.makeText(this,
                    "UserName or Password is Incorrect",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 3) Successfully logged in (not admin)
        // → First check the student collection (using the field id = email).
        db.collection("student")
                .whereEqualTo("id", email)     //  Change to column search
                .limit(1)
                .get()
                .addOnCompleteListener(this::handleStudentQuery);
    }

    /** Query the results of the student collection (QuerySnapshot) */
    private void handleStudentQuery(Task<QuerySnapshot> task) {
        if (task.isSuccessful()
                && task.getResult() != null
                && !task.getResult().isEmpty()) {

            DocumentSnapshot doc = task.getResult().getDocuments().get(0);

            String firstName = safeString(doc.getString("firstName"));
            String lastName  = safeString(doc.getString("lastName"));
            String fullName  = (firstName + " " + lastName).trim();
            String status    = safeString(doc.getString("status"));

            if (!status.isEmpty() && !"APPROVED".equalsIgnoreCase(status)) {
                loginMessage.setText(
                        "Your student registration is " + status + ".\nPlease wait for approval."
                );
                return;
            }

            // Student OK → Enter StudentHomeActivity
            Intent i = new Intent(LoginActivity.this, StudentHomeActivity.class);
            i.putExtra(StudentHomeActivity.EXTRA_STUDENT_ID, email);
            i.putExtra(StudentHomeActivity.EXTRA_STUDENT_NAME, fullName);
            startActivity(i);
            finish();
            return;
        }

        // Cannot find student → Find tutor
        db.collection("tutor")
                .whereEqualTo("id", email)     // 同樣用 id 欄位
                .limit(1)
                .get()
                .addOnCompleteListener(this::handleTutorQuery);
    }

    /** Search results for tutor collection */
    private void handleTutorQuery(Task<QuerySnapshot> task) {
        if (task.isSuccessful()
                && task.getResult() != null
                && !task.getResult().isEmpty()) {

            DocumentSnapshot doc = task.getResult().getDocuments().get(0);

            String firstName = safeString(doc.getString("firstName"));
            String lastName  = safeString(doc.getString("lastName"));
            String fullName  = (firstName + " " + lastName).trim();
            String status    = safeString(doc.getString("status"));

            if (!status.isEmpty() && !"APPROVED".equalsIgnoreCase(status)) {
                loginMessage.setText(
                        "Your tutor registration is " + status + ".\nPlease wait for approval."
                );
                return;
            }

            // Tutor OK → Enter TutorHomeActivity
            Intent i = new Intent(LoginActivity.this, TutorHomeActivity.class);
            i.putExtra("TUTOR_EMAIL", email);
            i.putExtra("TUTOR_NAME", fullName);
            startActivity(i);
            finish();
            return;
        }

        // Not a student or a tutor → Search user collection
        db.collection("user")
                .whereEqualTo("id", email)     // 一樣用 id 欄位，如果 user 裡的欄位名稱不同請改
                .limit(1)
                .get()
                .addOnCompleteListener(this::handleUserQuery);
    }

    /** Results of querying user collection */
    private void handleUserQuery(Task<QuerySnapshot> task) {
        if (!task.isSuccessful()
                || task.getResult() == null
                || task.getResult().isEmpty()) {

            // If none of the three collections can be found,
            // it will be assumed that the account does not exist.
            Toast.makeText(this,
                    "Account info not found. Please register again.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
        String status = safeString(doc.getString("status")); // UNDECIDED / Pending / Rejected / Approved

        if (status.equalsIgnoreCase("UNDECIDED")) {
            Intent i = new Intent(LoginActivity.this, UserHomeActivity.class);
            i.putExtra("email", email);
            i.putExtra("role", "User");
            i.putExtra("password", password);
            startActivity(i);
            finish();
        } else if (status.equalsIgnoreCase("Pending")) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", "user");
            i.putExtra("state", "waiting");
            startActivity(i);
            finish();
        } else if (status.equalsIgnoreCase("Rejected")) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", "user");
            i.putExtra("state", "rejected");
            startActivity(i);
            finish();
        } else if (status.equalsIgnoreCase("Approved")) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", "user");
            i.putExtra("state", "approved");
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", "user");
            startActivity(i);
            finish();
        }
    }

    private String safeString(String s) {
        return (s == null) ? "" : s;
    }
}
