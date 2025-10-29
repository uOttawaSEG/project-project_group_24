package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, passwordLayout, phoneLayout, roleLayout;
    private TextInputEditText editFirstName, editLastName, editEmail, editPassword, editPhone, editRole;
    private Spinner spinnerProgram;
    private Button btnRegister;
    User logged_user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // 對應 res/layout/activity_register.xml

        bindViews();
//        setupProgramSpinner();
        setupClickListeners();
    }

    private void bindViews() {
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout  = findViewById(R.id.lastNameLayout);
        emailLayout     = findViewById(R.id.emailLayout);
        passwordLayout  = findViewById(R.id.passwordLayout);
        phoneLayout     = findViewById(R.id.phoneLayout);

        editFirstName   = findViewById(R.id.editFirstName);
        editLastName    = findViewById(R.id.editLastName);
        editEmail       = findViewById(R.id.editEmail);
        editPassword    = findViewById(R.id.editPassword);
        editPhone       = findViewById(R.id.editPhone);

//        spinnerProgram  = findViewById(R.id.spinnerProgram);
        btnRegister     = findViewById(R.id.btnRegister);
    }

//    private void setupProgramSpinner() {
//        ArrayAdapter<CharSequence> adapter;
//        try {
//            adapter = ArrayAdapter.createFromResource(
//                    this, R.array.programs_array, android.R.layout.simple_spinner_item);
//        } catch (Exception ignore) {
//            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
//                    new String[]{"Select your program", "Computer Science", "Software Engineering",
//                            "Information Technology", "Electrical Engineering"});
//        }
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerProgram.setAdapter(adapter);
//        spinnerProgram.setSelection(0);
//    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            if (processForm()) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(RegisterActivity.this, UserHomeActivity.class);
                i.putExtra("email",logged_user.getEmail());
                i.putExtra("role", "User");
                i.putExtra("password",logged_user.getPassword());
                startActivity(i);

                finish();
            }
        });
    }

    private boolean processForm() {

        clearErrors();

        String firstName = getText(editFirstName);
        String lastName  = getText(editLastName);
        String email     = getText(editEmail);
        String password  = getText(editPassword);
        String phone     = getText(editPhone);
//      Object program   = spinnerProgram.getSelectedItem();

        if (firstName.isEmpty()) {
            firstNameLayout.setError("First name required");
            return false;
        }
        if (lastName.isEmpty()) {
            lastNameLayout.setError("Last name required");
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email");
            return false;
        }
        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        }
        if (phone.length()!=10) {
            phoneLayout.setError("Phone must be 10 digits ");
            return false;
        }
//        if (program == null || program.toString().toLowerCase().contains("select")) {
//            Toast.makeText(this, "Please select your program", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        User user = new User(email,password,firstName,lastName,Long.parseLong(phone));
        RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, RegistrationRequest.Role.TUTOR);
        request.setId(email);
        request.setPhone(phone);
        request.setStatus(RegistrationRequest.Status.PENDING);
        FirebaseManager.getInstance().addRegistrationRequest(request);
        logged_user = user;
//        Student student = new Student(email, password, true);
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setPhoneNumber(Long.parseLong(phone));
//        student.setProgram((String)program);

        FirebaseManager.getInstance().registerUser(user, password);
//        RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, RegistrationRequest.Role.STUDENT, RegistrationRequest.Status.PENDING);
//        request.setProgramOfStudy((String)program);
//        request.setId(email);
//        request.setPhone(phone);
//        FirebaseManager.getInstance().addRegistrationRequest(request);
        //student.StudentRegister();
        return true;
    }

    private void clearErrors() {
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        phoneLayout.setError(null);
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
