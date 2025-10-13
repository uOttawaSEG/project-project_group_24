package com.uottawa.eecs.project_project_group_24;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

//this class deals with the interactions with the database
public class FirebaseManager{
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

  //needs to be implemented
    public void onSuccess(){

    }

    //needs to be implemented
    public void addOnFailure(String errorMessage){

    }

    //call this method when creating a new Student, takes in a student and adds it to the database
    public void registerStudent(Student student, String password, FirebaseCallback callback) {

        auth.createUserWithEmailAndPassword(student.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("student")
                                .document(student.getEmail())
                                .set(toMap(student))
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    //call this method when creating a new Tutor, takes in a student and adds it to the database

    public void registerTutor(Tutor tutor,String password,FirebaseCallback callback) {
        auth.createUserWithEmailAndPassword(tutor.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("tutor")
                                .document(tutor.getEmail())
                                .set(toMap(tutor))
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

   //call this method when creating a new Course, takes in a student and adds it to the database
    public void addCourse(Course course, FirebaseCallback callback) {
        long courseId = course.id;

        db.collection("courses")
                .document(String.valueOf(courseId))
                .set(toMap(course))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        "Failed to create new course: " + e.getMessage()));
    }




    //converts Tutor to a map to add to database
    private Map<String, Object> toMap(Tutor tutor) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", tutor.getFirstName());
        map.put("lastName", tutor.getLastName());
        map.put("degree", tutor.getDegree());
        map.put("courses", tutor.courses);
        map.put("phoneNumber", tutor.getPhoneNumber());
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
        return map;
    }

    //logs in tutor by validating email and password
    public void loginTutor(String email, String password, Callback callback) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {

                        fetchTutorProfile(email, callback);
                    } else {

                        callback.onFailure(authTask.getException().getMessage());
                    }
                });
    }

//fetches tutor from database given email
    private void fetchTutorProfile(String email, Callback callback) {

        db.collection("tutor")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Tutor tutor = convertToTutorObject(documentSnapshot.getData());

                        callback.onSuccess(tutor);

                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(
                        "Failed to login tutor: " + e.getMessage()));

    }

    //logs in student by validating email and password
    public void loginStudent(String email, String password, Callback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        fetchStudentProfile(email, callback);
                    } else {
                        callback.onFailure(authTask.getException().getMessage());
                    }
                });
    }

    //fetches student from database given email
    private void fetchStudentProfile(String email, Callback callback) {
        db.collection("student")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Student student = convertToStudentObject(documentSnapshot.getData());
                        callback.onSuccess(student);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(
                        "Database fetch failed: " + e.getMessage()));
    }

    //takes map from database and converts it to a student object
    private Student convertToStudentObject(Map<String, Object> data) {

        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("id");
        String program = (String) data.get("program");
        long phoneNumber = (long) data.get("phoneNumber");
        Student student = new Student(email, null);
        student.setProgram(program);
        student.phoneNumber = phoneNumber;
        student.firstName = firstName;
        student.lastName = lastName;
        return student;
    }

    //takes map from database and converts it to a tutor object
    private Tutor convertToTutorObject(Map<String, Object> data) {
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("id");
        String degree = (String) data.get("degree");
        long phoneNumber = (long) data.get("phoneNumber");

        @SuppressWarnings("unchecked")
        ArrayList<Course> coursesList =(ArrayList<Course>) data.get("courses");

        Course[] courses = coursesList.toArray(new Course[0]);
        Tutor tutor = new Tutor(email, null);

        tutor.firstName = firstName;
        tutor.phoneNumber = phoneNumber;
        tutor.lastName = lastName;
        tutor.courses = courses;
        tutor.setDegree(degree);

        return tutor;
    }


    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface Callback {
        void onSuccess(User user);
        void onFailure(String error);
    }


}
