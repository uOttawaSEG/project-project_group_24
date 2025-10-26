package com.uottawa.eecs.project_project_group_24;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

//this class deals with the interactions with the database
public final class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

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

    //needs to be implemented
    public void onSuccess() {

    }

    public void onSuccess(User u) {

    }

    //needs to be implemented
    public void onFailure(String errorMessage){

    }

    public void addRegistrationRequest(RegistrationRequest registrationRequest){
                        db.collection("student")
                                .document(registrationRequest.getId())
                                .set(toMap(registrationRequest))
                                .addOnSuccessListener(aVoid -> this.onSuccess())
                                .addOnFailureListener(e -> this.onFailure(e.getMessage()));

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
        if(request.getRole().equalsIgnoreCase("tutor")){
            map.put("highestDegree", request.getHighestDegree());
            map.put("coursesOffered", request.getCoursesOffered());
        }

        else if(request.getRole().equalsIgnoreCase("student")){
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
        RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, role, status);
        request.setId(id);
        request.setPhone(phone);
        if(role.equalsIgnoreCase("tutor")){
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
        Student student = new Student(email, null, newuser);
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

}
