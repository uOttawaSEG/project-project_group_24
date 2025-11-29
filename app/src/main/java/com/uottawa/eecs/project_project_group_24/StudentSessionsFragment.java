package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all sessions currently logged in for the student
 */
public class StudentSessionsFragment extends Fragment
        implements StudentSessionsAdapter.OnSessionActionListener {

    private static final String ARG_STUDENT_ID = "ARG_STUDENT_ID";

    private String studentId;
    private RecyclerView rvSessions;
    private StudentSessionsAdapter adapter;
    private final List<Session> sessionList = new ArrayList<>();

    private FirebaseFirestore db;

    public StudentSessionsFragment() {
        // must be empty
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

        db.collection("session")
                .whereEqualTo("studentId", studentId)
                .orderBy("startMillis", Query.Direction.DESCENDING) // 最近的在上面
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
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to load sessions: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ==== Cancel logic ====

    @Override
    public void onCancelSession(Session session) {
        if (session == null || session.id == null || session.id.isEmpty()) {
            Toast.makeText(getContext(),
                    "Cannot cancel this session.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // PENDING can be cancelled at any time;
        // 24-hour checks for APPROVED are displayed on the adapter control.
        db.collection("session")
                .document(session.id)
                .update("status", Session.Status.CANCELLED.name())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Session cancelled.",
                            Toast.LENGTH_SHORT).show();
                    loadSessionsFromFirestore(); // 重新載入列表
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to cancel: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
