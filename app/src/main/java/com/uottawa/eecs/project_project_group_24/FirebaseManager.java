package com.uottawa.eecs.project_project_group_24;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.Collections;
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

    public void onSuccess(List<Session> session) {}



    //needs to be implemented
    public void onFailure(String errorMessage){ //adien - print to logcat that is failed case.
        Log.e("Register", "Auth failed: " + errorMessage);
    }


    //when sending new registration to the database
    public void addRegistrationRequest(RegistrationRequest registrationRequest){
        if(registrationRequest.getId() == null) {
            onFailure("RegistrationRequest ID is null");
            return;
        }

        db.collection("registrationRequests") // use proper collection
                .document(registrationRequest.getId())
                .set(toMap(registrationRequest))
                .addOnSuccessListener(aVoid -> onSuccess())
                .addOnFailureListener(e -> onFailure(e.getMessage()));
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
        else if(role.equalsIgnoreCase("user")){
            db.collection("user")
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
        else if (user.getStatus() == User.requestStatus.Undecided) {
            map.put("status","UNDECIDED");
        }
        else
        {
            map.put("status",null);
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
    public void fetchTutorProfile(String email) {

        db.collection("tutor")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Tutor tutor = convertToTutorObject(documentSnapshot.getData());

                        this.onSuccess(tutor);

                    } else {
                        this.onSuccess();
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
                    db.collection("admin").
                            document(email).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Log.d("FIREBASE_LOGIN","Admin successfully logged in");
                                    Intent i = new Intent(activity, AdminHomeActivity.class);
//                                    admin = true;
                                    activity.startActivity(i);
                                }
                            });
                    activity.loginCallback();
                });

        return admin;
    }



    public RegistrationRequest convertToRequestObject(Map<String, Object> data) {
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
            if(cO!=null) {
                for (int i = 0; i < cO.length; i++) {
                    coursesOffered.add(cO[i]);
                }
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
    public Student convertToStudentObject(Map<String, Object> data) {

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
    public Tutor convertToTutorObject(Map<String, Object> data) {
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String email = (String) data.get("id");
        String degree = (String) data.get("degree");
        long phoneNumber = (long) data.get("phoneNumber");
        String status = (String) data.get("status");

        @SuppressWarnings("unchecked")
//        ArrayList<Course> coursesList =(ArrayList<Course>) data.get("courses");
//
//        Course[] courses = coursesList.toArray(new Course[0]);
        boolean newuser = false;
        if(status.equalsIgnoreCase("PENDING")){
            newuser = true;
        }
        Tutor tutor = new Tutor(email, null, newuser);

        tutor.firstName = firstName;
        tutor.phoneNumber = phoneNumber;
        tutor.lastName = lastName;
       // tutor.courses = courses;
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
    public void finishUserData(User user)
    {
        db.collection("user").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()){
                        user.setFirstName(task.getResult().get("firstName").toString());
                        user.setLastName(task.getResult().get("lastName").toString());
                        user.setPhoneNumber(Long.parseLong(task.getResult().get("phoneNumber").toString()));
                    } else{

                    }
                }
            }
        });
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

    public void makeQueueWithList(List<DocumentSnapshot> l)
    {

        for(int i =0;i<l.size();i++)
        {
            if(l.get(i) != null)
            {
                RegistrationRequest tmp = convertToRequestObject(l.get(i).getData());
                Administrator.receiveRequest(tmp);
            }
        }
    }
    //@params
    public void moveCollections(String start, String end, String id)
    {
        db.collection(start).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                {
                    Map<String,Object> tmp = task.getResult().getData();
                    if(tmp!=null)
                    {
                        tmp.put("id",id);
                        db.collection(end).add(tmp);
                        db.collection(start).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("OTA_FIREBASE", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("OTA_FIREBASE", "Error deleting document", e);
                        }
                    });

                    }
                }

            }
        });

    }

    public void sendUserInfo(DocumentSnapshot t,Intent i)
    {
        i.putExtra("email",String.valueOf(t.get("email")));
        i.putExtra("password",String.valueOf(t.get("password")));
        i.putExtra("firstName", String.valueOf(t.get("firstName")));
        i.putExtra("lastName", String.valueOf(t.get("lastName")));
        i.putExtra("phone", String.valueOf(String.valueOf(t.get("phoneNumber"))));
    }


    public void login(String email, Intent i, Activity a)
    {
//        Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
        db.collection("student").whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(a, StudentHomeActivity.class);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        i.putExtra("role", "student");
                        sendUserInfo(document, i);
                        a.startActivity(i);
                    }
            }
        }});
        db.collection("tutor").whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(a, TutorHomeActivity.class);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        i.putExtra("role", "tutor");
                        sendUserInfo(document, i);
                        a.startActivity(i);
                    }
                }
            }});

//        db.collection("student").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().exists()){
//                        i.putExtra("role","student");
//                        sendUserInfo(task.getResult(),i);
//                        a.startActivity(i);
//                    } else{
//
//                    }
//                }
//            }
//        });
//        db.collection("tutor").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().exists()){
//                        i.putExtra("role","tutor");
//                        sendUserInfo(task,i);
//                        a.startActivity(i);
//                    } else{
//
//                    }
//                }
//            }
//        });
        db.collection("user").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()){

                        String state = null;
                        if(task.getResult().getData().get("status")!=null) state = task.getResult().getData().get("status").toString();
                        Log.d("OTA_LOGIN",state);
                        if(state!=null&&state.equals("UNDECIDED"))
                        {
                            Intent i = new Intent(a, UserHomeActivity.class);
                            i.putExtra("email",email);
                            i.putExtra("role", "User");
                            sendUserInfo(task.getResult(),i);
                            a.startActivity(i);
                        }
                        else if(state!=null&&state.equalsIgnoreCase("Pending"))
                        {
                            Intent i = new Intent(a, WelcomeActivity.class);
                            i.putExtra("role","user");
                            i.putExtra("state","waiting");
                            sendUserInfo(task.getResult(),i);
                            a.startActivity(i);
                        }
                        else if (state!=null&&state.equalsIgnoreCase("Rejected")) {
                            Intent i = new Intent(a, WelcomeActivity.class);
                            i.putExtra("role","user");
                            i.putExtra("state","rejected");
                            sendUserInfo(task.getResult(),i);
                            a.startActivity(i);
                        }
                        else if(state!=null&&state.equalsIgnoreCase("Approved"))
                        {
                            Intent i = new Intent(a, WelcomeActivity.class);
                            i.putExtra("role","user");
                            i.putExtra("state","approved");
                            sendUserInfo(task.getResult(),i);
                            a.startActivity(i);
                        }
                        else {
                            Intent i = new Intent(a, WelcomeActivity.class);
                            i.putExtra("role","user");
                            sendUserInfo(task.getResult(),i);
                            a.startActivity(i);
                        }

                    } else{

                    }
                }
            }
        });
    }


    public void initializeRequests() {
        db.collection("registrationRequests").
                whereEqualTo("status","PENDING").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("OTA_FIREBASE_REQUESTS", "Listen failed.", e);
                            return;
                        }
                        for(QueryDocumentSnapshot doc:value) {

                            Administrator.getPendingRequests().Enqueue(convertToRequestObject(doc.getData()));
                        }
                        Log.d("OTA_FIREBASE_REQUESTS", "Initialized firebase requests ");
                    }
                });

//        List pending = db.collection("registrationRequests")
//                .whereEqualTo("status","PENDING")
//                .get()
//                .getResult()
//                .getDocuments();
//        List rejected = db.collection("registrationRequests")
//                .whereEqualTo("status","REJECTED")
//                .get().getResult().getDocuments();
//        if(pending!=null)makeQueueWithList(pending);
//        if(rejected!=null)makeQueueWithList(rejected);
//        db.collection("registrationRequests").whereEqualTo("status","PENDING").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if (task.getResult().exists()){
//                        admin = true;
//                    } else{
//                        admin = false;
//                    }
//                }
//            }
//        });
    }

    ////////////////////////////////////////////
    ///DELIVERABLE 3, please do not touch the code above
    ////////////////////////////////////////


    public Map<String, Object> toMap(Session session){
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("studentName", session.getStudentName());
        sessionMap.put("studentId", session.getStudentId());
        sessionMap.put("tutorId", session.tutorId);
        sessionMap.put("courseCode", session.getCourseCode());
        sessionMap.put("id", session.id);
        sessionMap.put("startTime",session.getStartMillis());
        sessionMap.put("startMillis", session.startMillis);

        return sessionMap;
    }

    public void addSession(Session session){
        if(session.id == null) {
            onFailure("Session ID is null");
            return;
        }

        db.collection("session") // use proper collection
                .document(session.id)
                .set(toMap(session))
                .addOnSuccessListener(aVoid -> onSuccess())
                .addOnFailureListener(e -> onFailure(e.getMessage()));
    }

    public void getSessions(String tutorId) {
        db.collection("session")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {

                        List<Session> sessions = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            // Convert using your helper method
                            Session session = convertToSessionObject(doc.getData());
                            sessions.add(session);
                        }

                        this.onSuccess(sessions);

                    } else {
                        this.onSuccess(Collections.emptyList()); // Return empty list instead of null
                    }
                })
                .addOnFailureListener(e ->
                        this.onFailure("Failed to fetch sessions: " + e.getMessage()));
    }


    public Session convertToSessionObject(Map<String, Object> sessionMap){
        Session session = new Session();
        session.setId((String) sessionMap.get("id"));
        session.courseCode = (String) sessionMap.get("courseCode");
        session.studentName = (String) sessionMap.get("studentName");
        session.tutorId = (String) sessionMap.get("tutorId");
        session.studentId = (String) sessionMap.get("studentId");
        session.setStatus((String) sessionMap.get("status"));
        session.startMillis = Long.parseLong((String)sessionMap.get("startTime"));
//        session.time =(Timestamp) sessionMap.get("startTime");//gonna fix it
        return session;
    }


    //fetches student from database given email
    //call this when tutor wants to see informaiton about a student
    public void fetchStudentProfile(String email) {
        db.collection("student")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Student student = convertToStudentObject(documentSnapshot.getData());
                        this.onSuccess(student);
                    } else {
                        this.onSuccess();
                    }
                })
                .addOnFailureListener(e -> this.onFailure(
                        "Database fetch failed: " + e.getMessage()));
    }



}
