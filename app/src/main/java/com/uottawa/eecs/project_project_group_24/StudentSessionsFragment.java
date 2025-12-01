package com.uottawa.eecs.project_project_group_24;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StudentSessionsFragment extends Fragment
        implements StudentSessionsAdapter.OnSessionActionListener,
        RateSessionDialog.RateSessionListener {

    private static final String ARG_STUDENT_ID = "ARG_STUDENT_ID";

    private String studentId;
    private RecyclerView rvSessions;
    private StudentSessionsAdapter adapter;
    private final List<Session> sessionList = new ArrayList<>();

    private FirebaseFirestore db; // Firestore instance

    public StudentSessionsFragment() {
    }

    // --- Helper Method to replicate FirebaseManager logic (Necessary for the rating flow) ---

    /**
     * Helper method: Must replicate logic from FirebaseManager.convertToTutorObject
     * to fetch Tutor data when directly accessing Firestore.
     */
    public Tutor convertToTutorObject(Map<String, Object> data, String email) {
        String firstName = (String) data.get("firstName");
        String lastName = (String) data.get("lastName");
        String degree = (String) data.get("degree");

        Object phoneNumberObj = data.get("phoneNumber");
        long phoneNumber = (phoneNumberObj instanceof Long) ? (Long) phoneNumberObj : 0L;
        String status = (String) data.get("status");

        // Retrieve rating fields
        Object avgRatingObj = data.get("averageRating");
        int averageRating = (avgRatingObj instanceof Long) ? ((Long) avgRatingObj).intValue() : 0;
        Object numOfRatingsObj = data.get("numOfRatings");
        int numOfRatings = (numOfRatingsObj instanceof Long) ? ((Long) numOfRatingsObj).intValue() : 0;


        Tutor tutor = new Tutor(email, null, false);

        tutor.firstName = firstName;
        tutor.phoneNumber = phoneNumber;
        tutor.lastName = lastName;
        tutor.averageRating = averageRating;
        tutor.numOfRatings = numOfRatings;
        tutor.setDegree(degree);

        if(status != null && status.equalsIgnoreCase("PENDING")){
            tutor.setStatus(User.requestStatus.PendingTutor);
        } else if(status != null && status.equalsIgnoreCase("ACCEPTED")){
            tutor.setStatus(User.requestStatus.AcceptedTutor);
        } else if(status != null && status.equalsIgnoreCase("REJECTED")){
            tutor.setStatus(User.requestStatus.RejectedTutor);
        }
        return tutor;
    }


    public static StudentSessionsFragment newInstance(String studentId) {
        StudentSessionsFragment f = new StudentSessionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_ID, studentId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Assuming R.layout.fragment_student_sessions is correct
        View view = inflater.inflate(R.layout.fragment_student_sessions, container, false);

        rvSessions = view.findViewById(R.id.rvStudentSessions);
        rvSessions.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new StudentSessionsAdapter(sessionList, this);
        rvSessions.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            studentId = getArguments().getString(ARG_STUDENT_ID);
        }

        loadSessionsFromFirestore();

        return view;
    }

    private void loadSessionsFromFirestore() {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(),
                    "Student ID not set. Check login flow.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading sessions for student: " + studentId);

        db.collection("session")
                .whereEqualTo("studentId", studentId)
                .orderBy("startMillis", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    sessionList.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                       Session s = doc.toObject(Session.class);
                        if (s == null) continue;

                        s.id = doc.getId();
                        sessionList.add(s);
                    }

                    adapter.setSessions(sessionList);

                    if (sessionList.isEmpty()) {
                        Toast.makeText(getContext(),
                                "You have no sessions yet.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        getContext(),
                        "Failed to load sessions: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show()
                );
    }

    // ===== Cancel session (Existing) =====

    @Override
    public void onCancelSession(Session session) {
        if (session == null || session.id == null || session.id.isEmpty()) {
            Toast.makeText(getContext(),
                    "Cannot cancel this session.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("session")
                .document(session.id)
                .update("status", Session.Status.CANCELLED.name())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Session cancelled.",
                            Toast.LENGTH_SHORT).show();
                    loadSessionsFromFirestore(); // reload list
                })
                .addOnFailureListener(e -> Toast.makeText(
                        getContext(),
                        "Failed to cancel: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show()
                );
    }

    // ===== Rate session (Implementation) =====

    @Override // From StudentSessionsAdapter.OnSessionActionListener
    public void onRateSession(Session s){
        if (s.id == null || s.tutorId == null) {
            Toast.makeText(getContext(), "Session or Tutor ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // This dialog uses the IDs from your session list item
        RateSessionDialog dialog = RateSessionDialog.newInstance(s.id, s.tutorId, s.tutorName);
        dialog.setRateSessionListener(this);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        dialog.show(ft, "RateSessionDialogTag");
    }

    @Override // From RateSessionDialog.RateSessionListener
    public void onRatingSubmitted(String sessionId, String tutorId, float rating) {
        int ratingInt = Math.round(rating); // Convert float (e.g., 4.5) to nearest int (e.g., 5)

        // 1. Update the session document to record the rating
        db.collection("session").document(sessionId)
                .update("studentRating", ratingInt)
                .addOnSuccessListener(aVoid -> {
                    // 2. Session rated, now fetch the Tutor and update average rating
                    updateTutorAverageRating(tutorId, ratingInt);
                })
                .addOnFailureListener(e -> Toast.makeText(
                        getContext(),
                        "Failed to update session rating: " + e.getMessage(),
                        Toast.LENGTH_LONG).show()
                );
    }

    /**
     * Fetches the tutor document directly from Firestore and calls setAverageRating.
     */
    private void updateTutorAverageRating(String tutorId, int ratingInt) {

        // Use the tutorId directly as the document ID, as it is a Firebase Hash ID key.
        db.collection("tutor")
                .document(tutorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // 1. Get the data map
                        Map<String, Object> data = documentSnapshot.getData();

                        // Use the email field from the document for the Tutor object construction
                        String tutorEmail = (String) data.get("email");

                        if (tutorEmail == null) {
                            Toast.makeText(getContext(),
                                    "Tutor document is missing an email field.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // 2. Convert data to Tutor object using local helper
                        Tutor tutor = convertToTutorObject(data, tutorEmail);

                        // 3. Call the tutor's method to calculate and save the new average rating
                        tutor.setAverageRating(ratingInt, new FirebaseManager.OpCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getContext(),
                                        "Tutor average rating updated!",
                                        Toast.LENGTH_LONG).show();
                                loadSessionsFromFirestore(); // Reload list
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(getContext(),
                                        "Failed to update tutor average rating: " + message,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // This indicates the Hash ID passed was not found as a document key.
                        Toast.makeText(getContext(),
                                "Error: Tutor document not found using Hash ID: " + tutorId,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch tutor data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}