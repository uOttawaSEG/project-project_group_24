package com.uottawa.eecs.project_project_group_24;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class StudentRegisterActivity extends AppCompatActivity {

    private TextInputLayout tutorFirstNameLayout, tutorLastNameLayout, tutorEmailLayout,
            tutorPasswordLayout, tutorPhoneLayout;
    private TextInputEditText programEditText;
            //tutorFirstName, tutorLastName, tutorEmail, tutorPassword, tutorPhone;
    //    private Spinner spinnerDegree;
    //private CheckBox checkCourse1, checkCourse2, checkCourse3;
    private Button btnStudentRegister;
    String email,password, firstName, lastName, phone, program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        password = getIntent().getStringExtra("password");
        email = getIntent().getStringExtra("email");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        phone = getIntent().getStringExtra("phone");

//        tutorFirstNameLayout = findViewById(R.id.tutorFirstNameLayout);
//        tutorLastNameLayout = findViewById(R.id.tutorLastNameLayout);
//        tutorEmailLayout = findViewById(R.id.tutorEmailLayout);
//        tutorPasswordLayout = findViewById(R.id.tutorPasswordLayout);
//        tutorPhoneLayout = findViewById(R.id.tutorPhoneLayout);

//        tutorFirstName = findViewById(R.id.tutorFirstName);
//        tutorLastName = findViewById(R.id.tutorLastName);
//        tutorEmail = findViewById(R.id.tutorEmail);
//        tutorPassword = findViewById(R.id.tutorPassword);
//        tutorPhone = findViewById(R.id.tutorPhone);

//        spinnerDegree = findViewById(R.id.spinnerDegree);
//        checkCourse1 = findViewById(R.id.checkCourse1);
//        checkCourse2 = findViewById(R.id.checkCourse2);
//        checkCourse3 = findViewById(R.id.checkCourse3);
        btnStudentRegister = findViewById(R.id.btnRegisterAsStudent);

        programEditText = findViewById(R.id.programEditText);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_item,
//                new String[]{"Select degree", "Bachelor", "Master", "PhD"});
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDegree.setAdapter(adapter);


        btnStudentRegister.setOnClickListener(v -> {
            if (validateStudent()) {

                Toast.makeText(this, "Student registration successful!", Toast.LENGTH_SHORT).show();


                Intent i = new Intent(StudentRegisterActivity.this, WelcomeActivity.class);
                i.putExtra("role", "Student");
                i.putExtra("state","waiting");
                startActivity(i);
                finish();
            }
        });
    }

    private boolean validateStudent() {
        boolean ok = true;
        clearErrors();

//        String firstName = tutorFirstName.getText().toString().trim();
//        String lastName = tutorLastName.getText().toString().trim();
//        String email = tutorEmail.getText().toString().trim();
//        String password = tutorPassword.getText().toString().trim();
//        String phone = tutorPhone.getText().toString().trim();
//        String degree = spinnerDegree.getSelectedItem().toString();
            program = programEditText.getText().toString();

//        if (firstName.isEmpty()) {
//            tutorFirstNameLayout.setError("First name required");
//            ok = false;
//        }
//        if (lastName.isEmpty()) {
//            tutorLastNameLayout.setError("Last name required");
//            ok = false;
//        }
//        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            tutorEmailLayout.setError("Invalid email");
//            ok = false;
//        }
//        if (password.length() < 6) {
//            tutorPasswordLayout.setError("Password must be at least 6 characters");
//            ok = false;
//        }
//        if (!phone.matches("\\d{10}")) {
//            tutorPhoneLayout.setError("Phone must be 10 digits");
//            ok = false;
//        }
//        if (degree.equals("Select degree")) {
//            Toast.makeText(this, "Please select a degree", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!checkCourse1.isChecked() && !checkCourse2.isChecked() && !checkCourse3.isChecked()) {
//            Toast.makeText(this, "Please select at least one course", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        String status = "PENDING";
        Student user = new Student(email, password);
        user.setPhoneNumber(Long.parseLong(phone));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setProgram(program);
        RegistrationRequest request = new RegistrationRequest(user.getFirstName(), user.getLastName(), email, RegistrationRequest.Role.STUDENT, RegistrationRequest.Status.valueOf(status));

        // FirebaseManager.getInstance().registerTutor(user, password);
        request.setId(email);
        request.setPhone(String.valueOf(user.getPhoneNumber()));
        request.setFirstName(user.getFirstName());
        request.setLastName(user.getLastName());
        request.setProgramOfStudy(program);
        request.setRole("STUDENT");
        FirebaseManager.getInstance().addRegistrationRequest(request);
        Log.d("OTA_TUTORREG",String.valueOf(request==null));
        Administrator.receiveRequest(request);
//        request.setHighestDegree(degree);
//        FirebaseManager.getInstance().addRegistrationRequest(request);
        return ok;
    }



    private void clearErrors() {
//        tutorFirstNameLayout.setError(null);
//        tutorLastNameLayout.setError(null);
//        tutorEmailLayout.setError(null);
//        tutorPasswordLayout.setError(null);
//        tutorPhoneLayout.setError(null);
    }
}