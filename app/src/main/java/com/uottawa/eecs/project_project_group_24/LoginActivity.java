package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //adien - below every variables are just for UI/UX item IDs.
    private EditText editLoginEmail; //aiden - email typing slot in login page.
    private EditText editLoginPassword; //aiden - password typing slot in login page.
    private Button btnLogin; // aiden - login button Id.
    private TextView loginMessage; // aiden - console ID. if you typing wrong typing in login page, red message will come out
    private ProgressBar loginProgress; // aiden - loading bar. while login take few times, it will shows up.

    private FirebaseFirestore db;

    private FirebaseManager fbManager;
    String email;

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

        btnLogin.setOnClickListener(v -> attemptLogin()); //aiden - when login button is clicked, start the attemptLogin() method
    }

    private void attemptLogin() { //aiden - login method.
        loginMessage.setText("");
        email = editLoginEmail.getText().toString().trim(); //aiden - when user typing the email in the box, that will be stored in local variable here.
        String password = editLoginPassword.getText().toString().trim(); //aiden - when user typing the password in the box, that will be stored in local variable here.

        //aiden VALIDATION SESSION
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //aiden - typing error case.
            editLoginEmail.setError("Enter a valid email");
            return;
        }
        if (password.isEmpty()) { //aiden - typing error case2.
            editLoginPassword.setError("Enter your password");
            return;
        }
        

        loginProgress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        fbManager = FirebaseManager.getInstance(); //aiden - get the data from firebase.

        fbManager.loginUser(email, password, this); //aiden - send the instant variable to fb EMAIL, PASSWORD, and itself as object
        if (fbManager.getAdmin() == true) {
            Intent i = new Intent(LoginActivity.this, AdminHomeActivity.class);
            startActivity(i);
        } else if (fbManager.getLoggedIn() == true) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(i);
        }

    }

    public void loginCallback() {
        if (fbManager.getAdmin() == true) {
            Intent i = new Intent(LoginActivity.this, AdminHomeActivity.class);
            startActivity(i);
        } else if (fbManager.getLoggedIn() == true) {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            FirebaseFirestore tmp = fbManager.getDb();
//            CollectionReference dbref;
            db.collection("student").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()){
                            i.putExtra("role","student");
                            startActivity(i);
                        } else{

                        }
                    }
                }
            });
            db.collection("user").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()){

                            i.putExtra("role","user");
                            startActivity(i);
                        } else{

                        }
                    }
                }
            });
//            Log.d("CONSOLE","Not user");

            db.collection("tutor").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()){
                            i.putExtra("role","tutor");
                            startActivity(i);
                        } else{

                        }
                    }
                }
            });


//            startActivity(i);
        } else {
            // bad credentials!!!
        }
        finish();
    }

    // what's this!?!?!?! -> adien: guess that will bonus mark contents. after user reject, the mail will send to user's email.
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

        if (role.equalsIgnoreCase("ADMIN")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, AdminRequestListFragment.newPending()) //aiden fragmentContainer is textbox id in UI
                    .commit();
        } else {
            Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
            i.putExtra("role", role);
            startActivity(i);
        }
    }

    private String safeString(String s) {
        return (s == null) ? "" : s;
    }
}
