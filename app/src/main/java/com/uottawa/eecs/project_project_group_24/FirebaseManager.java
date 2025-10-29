package com.uottawa.eecs.project_project_group_24;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

//this class deals with the interactions with the database
public final class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth; //aiden - object that get from local typing and will be used to compared with fb datas.
    private FirebaseFirestore db; //firebase database itself

    private Boolean admin = false;
    private Boolean loggedIn = false;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    //needs to be implemented - not need til D2
    public void onSuccess() {}
    public void onSuccess(User u) {}



    //needs to be implemented
    public void onFailure(String errorMessage){ //adien - print to logcat that is failed case.
        Log.e("Register", "Auth failed: " + errorMessage);
    }

    //when sending new registration to the database
    public void addRegistrationRequest(RegistrationRequest registrationRequest){
                        db.collection("student")
                                .document(registrationRequest.getId())
                                .set(toMap(registrationRequest))
                                .addOnSuccessListener(aVoid -> this.onSuccess())
                                .addOnFailureListener(e -> this.onFailure(e.getMessage()));

    }

    //this is called when the administrator approves or rejects a request, this will update the status to firebase
    //takes id to identity the request, but needs email and role to update information in student and tutor collections
    public void updateRegistrationStatus(String id, String email,String role,String newStatus) {
        db.collection("registrationRequests")
                .document(id) // the document ID, e.g. johndoe67@gmail.com
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> this.onSuccess())
                .addOnFailureListener(e -> this.onFailure(e.getMessage()));

        if(role.equalsIgnoreCase("student")){
            db.collection("student")
                    .document(email) // the document ID, e.g. johndoe67@gmail.com
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> this.onSuccess())
                    .addOnFailureListener(e -> this.onFailure(e.getMessage()));
        }

        else if(role.equalsIgnoreCase("tutor")){
            db.collection("tutor")
                    .document(email) // the document ID, e.g. johndoe67@gmail.com
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> this.onSuccess())
                    .addOnFailureListener(e -> this.onFailure(e.getMessage()));
        }

    }
    public void registerUser(User user,String password)
    {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("user")
                                .document(user.getEmail())
                                .set(toMap(user))
                                .addOnSuccessListener(aVoid -> this.onSuccess())
                                .addOnFailureListener(e -> this.onFailure(e.getMessage()));
                    } else {
                        this.onFailure(task.getException().getMessage());
                    }
                });
    }

    //call this method when creating a new Student, takes in a student and adds it to the database
    public void registerStudent(Student student, String password) {

        auth.createUserWithEmailAndPassword(student.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("student")
                                .document(student.getEmail())
                                .set(toMap(student))
                                .addOnSuccessListener(aVoid -> this.onSuccess())
                                .addOnFailureListener(e -> this.onFailure(e.getMessage()));
                    } else {
                        this.onFailure(task.getException().getMessage());
                    }
                });
    }

    //call this method when creating a new Tutor, takes in a student and adds it to the database

    public void registerTutor(Tutor tutor, String password) {
        auth.createUserWithEmailAndPassword(tutor.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("tutor")
                                .document(tutor.getEmail())
                                .set(toMap(tutor))
                                .addOnSuccessListener(aVoid -> this.onSuccess())
                                .addOnFailureListener(e -> this.onFailure(e.getMessage()));
                    } else {
                        this.onFailure(task.getException().getMessage());
                    }
                });
    }

   //call this method when creating a new Course, takes in a student and adds it to the database
    public void addCourse(Course course) {
        long courseId = course.id;

        db.collection("courses")
                .document(String.valueOf(courseId))
                .set(toMap(course))
                .addOnSuccessListener(aVoid -> this.onSuccess())
                .addOnFailureListener(e -> this.onFailure(
                        "Failed to create new course: " + e.getMessage()));
    }

    //takes the registration request and converts it to a map
    private Map<String, Object> toMap(RegistrationRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", request.getRole());
        map.put("firstName", request.getFirstName());
        map.put("lastName", request.getLastName());
        map.put("email", request.getEmail());
        map.put("phone", request.getPhone());
        map.put("status", request.getStatus());
        if(request.getRole().equalsIgnoreCase("TUTOR")){
            map.put("highestDegree", request.getHighestDegree());
            map.put("coursesOffered", request.getCoursesOffered());
        }

        else if(request.getRole().equalsIgnoreCase("STUDENT")){
            map.put("programOfStudy", request.getProgramOfStudy());
        }

        return map;
    }

    //converts Tutor to a map to add to database
    private Map<String, Object> toMap(Tutor tutor) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", tutor.getFirstName());
        map.put("lastName", tutor.getLastName());
        map.put("degree", tutor.getDegree());
        map.put("courses", tutor.courses);
        map.put("phoneNumber", tutor.getPhoneNumber());
        if(tutor.getStatus()== User.requestStatus.PendingTutor){
            map.put("status", "PENDING");
        }

        else if(tutor.getStatus()== User.requestStatus.AcceptedTutor){
            map.put("status", "ACCEPTED");
        }

        else if(tutor.getStatus()== User.requestStatus.RejectedTutor){
            map.put("status", "REJECTED");
        }
        return map;
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("email",user.getEmail());
//        map.put("requested_register",user.get)

        if(user.getStatus()== User.requestStatus.PendingTutor){
            map.put("status", "PENDING");
        }

        else if(user.getStatus()== User.requestStatus.AcceptedTutor){
            map.put("status", "ACCEPTED");
        }

        else if(user.getStatus()== User.requestStatus.RejectedTutor){
            map.put("status", "REJECTED");
        }
        return map;
    }
    //converts Course to a map to add to database
    private Map<String, Object> toMap(Course course) {
        Map<String, Object> map = new HashMap<>();

        map.put("tutor", course.tutor);
        map.put("slots", course.slots);

        return map;
    }

    //converts Student to a map to add to database
    private Map<String, Object> toMap(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", student.getFirstName());
        map.put("lastName", student.getLastName());
        map.put("program", student.getProgram());
        if(student.getStatus()== User.requestStatus.PendingStudent){
            map.put("status", "PENDING");
        }

        else if(student.getStatus()== User.requestStatus.AcceptedStudent){
            map.put("status", "ACCEPTED");
        }

        else if(student.getStatus()== User.requestStatus.RejectedStudent){
            map.put("status", "REJECTED");
        }
        return map;
    }

    //logs in tutor by validating email and password
    public void loginTutor(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {

                        fetchTutorProfile(email);
                    } else {

                        this.onFailure(authTask.getException().getMessage());
                    }
                });
    }

//fetches tutor from database given email
    private void fetchTutorProfile(String email) {

        db.collection("tutor")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Tutor tutor = convertToTutorObject(documentSnapshot.getData());

                        this.onSuccess(tutor);

                    } else {
                        this.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> this.onFailure(
                        "Failed to login tutor: " + e.getMessage()));

    }

    //logs in student by validating email and password
    public void loginStudent(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        fetchStudentProfile(email);
                    } else {
                        this.onFailure(authTask.getException().getMessage());
                    }
                });
    }

    public Boolean loginUser(String email, String password, LoginActivity activity) {
        Map<String, Object> userData = new HashMap<>();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        loggedIn = true;
                        Log.w("FIREBASE", "Login Success");
                    } else {
                        this.onFailure(authTask.getException().getMessage());
                    }
                    activity.loginCallback();
                });
        return admin;
    }

    //fetches student from database given email
    private void fetchStudentProfile(String email) {
        db.collection("student")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Student student = convertToStudentObject(documentSnapshot.getData());
                        this.onSuccess(student);
                    } else {
                        this.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> this.onFailure(
                        "Database fetch failed: " + e.getMessage()));
    }

    private RegistrationRequest convertToRequestObject(Map<String, Object> data) {
        String role = (String) data.get("role");
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String id = (String) data.get("id");
        String email = (String) data.get("email");
        String phone = (String) data.get("phone");
        String status = (String) data.get("status");
        RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, RegistrationRequest.Role.valueOf(role), RegistrationRequest.Status.valueOf(status));
        request.setId(id);
        request.setPhone(phone);
        if(role.equalsIgnoreCase("TUTOR")){
            String highestDegree = (String) data.get("highestDegree");
            String[] cO = (String[]) data.get("coursesOffered");
            List<String> coursesOffered = new ArrayList<>();
            for(int i = 0; i<cO.length; i++){
                coursesOffered.add(cO[i]);
            }

            request.setCoursesOffered(coursesOffered);
            request.setHighestDegree(highestDegree);
        }

        else if(role.equalsIgnoreCase("student")){
            String programOfStudy = (String) data.get("programOfStudy");
            request.setProgramOfStudy(programOfStudy);
        }



        return request;
    }


    //takes map from database and converts it to a student object
    private Student convertToStudentObject(Map<String, Object> data) {

        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("id");
        String program = (String) data.get("program");
        long phoneNumber = (long) data.get("phoneNumber");
        String status = (String) data.get("status");
        boolean newuser = false;

        if(status.equalsIgnoreCase("PENDING")){
            newuser = true;
        }
        Student student = new Student(email, null);
        student.setProgram(program);
        student.phoneNumber = phoneNumber;
        student.firstName = firstName;
        student.lastName = lastName;

        if(status.equalsIgnoreCase("PENDING")){
            student.setStatus(User.requestStatus.PendingStudent);
        }

        else if(status.equalsIgnoreCase("ACCEPTED")){
            student.setStatus(User.requestStatus.AcceptedStudent);
        }

        else if(status.equalsIgnoreCase("REJECTED")){
            student.setStatus(User.requestStatus.RejectedStudent);
        }
        return student;
    }

    //takes map from database and converts it to a tutor object
    private Tutor convertToTutorObject(Map<String, Object> data) {
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("id");
        String degree = (String) data.get("degree");
        long phoneNumber = (long) data.get("phoneNumber");
        String status = (String) data.get("status");

        @SuppressWarnings("unchecked")
        ArrayList<Course> coursesList =(ArrayList<Course>) data.get("courses");

        Course[] courses = coursesList.toArray(new Course[0]);
        boolean newuser = false;
        if(status.equalsIgnoreCase("PENDING")){
            newuser = true;
        }
        Tutor tutor = new Tutor(email, null, newuser);

        tutor.firstName = firstName;
        tutor.phoneNumber = phoneNumber;
        tutor.lastName = lastName;
        tutor.courses = courses;
        tutor.setDegree(degree);
        if(status.equalsIgnoreCase("PENDING")){
            tutor.setStatus(User.requestStatus.PendingTutor);
        }

        else if(status.equalsIgnoreCase("ACCEPTED")){
            tutor.setStatus(User.requestStatus.AcceptedTutor);
        }

        else if(status.equalsIgnoreCase("REJECTED")){
            tutor.setStatus(User.requestStatus.RejectedTutor);
        }
        return tutor;
    }
    public Map<String,Object> getUserData(String email, String collectionName)
    {
        Map<String,Object> tmp = null;
        Task<DocumentSnapshot> tmp3 = db.collection(collectionName)
                .document(email)
                .get();
        DocumentSnapshot tmp2=tmp3.getResult();
        if(tmp2!=null) tmp = tmp2.getData();
        return tmp;
    }

    public void isAdmin(String email) {
        db.collection("admin").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()){
                        admin = true;
                    } else{
                        admin = false;
                    }
                }
            }
        });

    }

    public Map<String, Object>  findUser(String email){
        boolean found = false;
        Map<String, Object> userData = null;
        String[] collections = {"user", "student", "tutor", "admin"};
        for(int i = 0; i < collections.length; i++){
            Map<String, Object> temp = getUserData(email, collections[i]);
            if(temp != null){

            }
        }

    return null;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void logout() {
        auth.signOut();
        loggedIn = false;
        admin = false;
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
