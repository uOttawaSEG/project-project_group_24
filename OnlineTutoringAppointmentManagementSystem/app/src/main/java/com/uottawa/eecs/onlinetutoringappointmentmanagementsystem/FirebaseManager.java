package com.uottawa.eecs.onlinetutoringappointmentmanagementsystem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FirebaseManager implements FirebaseCallBack{
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

    public void registerStudent(Student student,FirebaseCallback callback) {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("student")
                                .document(user.getEmail())
                                .set(toMap(user))
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void registerTutor(Student student,FirebaseCallback callback) {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("tutor")
                                .document(user.getEmail())
                                .set(toMap(user))
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    //
    public boolean loginUser(String email, String password, FirebaseCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                        return true;
                    } else {
                        callback.onFailure(task.getException().getMessage());
                        return false
                    }
                });
    }

    private Map<String, Object> toMap(Tutor tutor) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", tutor.getFirstName());
        map.put("lastName", tutor.getLastName());
        map.put("email", tutor.getEmail());

        map.put("courses", tutor.getCourses());


    }

    private Map<String, Object> toMap(Stuent student) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("email", user.getEmail());

        return map;
    }

    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
