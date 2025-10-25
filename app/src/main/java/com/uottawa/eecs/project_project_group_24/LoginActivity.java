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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText editLoginEmail;
    private EditText editLoginPassword;
    private Button btnLogin;
    private TextView loginMessage;
    private ProgressBar loginProgress;

    private FirebaseFirestore db;

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
        String email = editLoginEmail.getText().toString().trim();
        String password = editLoginPassword.getText().toString().trim();

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

        db.collection("registrationRequests")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(this::handleLoginQueryResult)
                .addOnFailureListener(e -> {
                    loginProgress.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    loginMessage.setText("Error connecting to server: " + e.getMessage());
                });
    }

    private void handleLoginQueryResult(QuerySnapshot snap) {
        loginProgress.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        if (snap == null || snap.isEmpty()) {
            loginMessage.setText("No account found. Please register first.");
            return;
        }

        DocumentSnapshot doc = snap.getDocuments().get(0);

        String status = safeString(doc.getString("status")); // APPROVED / REJECTED / PENDING
        String role   = safeString(doc.getString("role"));   // STUDENT / TUTOR

        if (status.equalsIgnoreCase("APPROVED")) {
            // APPROVED: (WelcomeActivity)
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", role.equalsIgnoreCase("TUTOR") ? "Tutor" : "Student");
            startActivity(i);

        } else if (status.equalsIgnoreCase("REJECTED")) {
            // REJECTED: show reject message + fake phone number
            loginMessage.setText(
                    "Your registration was rejected.\n" +
                            "Please contact 613-123-4567 for assistance."
            );

        } else {
            // PENDING: show message
            loginMessage.setText(
                    "Your registration is still pending approval.\n" +
                            "Please try again later."
            );
        }
    }

    private String safeString(String s) {
        return (s == null) ? "" : s;
    }
}
