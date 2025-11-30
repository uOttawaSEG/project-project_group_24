package com.uottawa.eecs.project_project_group_24;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchSlotsFragment extends Fragment {

    private static final String ARG_STUDENT_ID   = "ARG_STUDENT_ID";
    private static final String ARG_STUDENT_NAME = "ARG_STUDENT_NAME";

    private EditText editCourse;
    private Button btnSearch;
    private RecyclerView recycler;

    private AvailableSlotsAdapter adapter;
    private final List<AvailabilitySlot> slotList = new ArrayList<>();

    private FirebaseFirestore db;

    private String studentId;
    private String studentName;

    public static SearchSlotsFragment newInstance(String studentId, String studentName) {
        SearchSlotsFragment f = new SearchSlotsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_ID, studentId);
        args.putString(ARG_STUDENT_NAME, studentName);
        f.setArguments(args);
        return f;
    }

    public SearchSlotsFragment() {
        // must be empty
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_slots, container, false);

        editCourse = v.findViewById(R.id.editCourse);
        btnSearch  = v.findViewById(R.id.btnSearch);
        recycler   = v.findViewById(R.id.recycler);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            studentId   = getArguments().getString(ARG_STUDENT_ID);
            studentName = getArguments().getString(ARG_STUDENT_NAME);
        }

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableSlotsAdapter(slotList, this::onBookClicked);
        recycler.setAdapter(adapter);

        btnSearch.setOnClickListener(view -> performSearch());

        return v;
    }

    /**
     * course version search：
     * User-input string = course collection 的 document id
     */
    private void performSearch() {
        String courseCode = editCourse.getText().toString().trim();

        if (TextUtils.isEmpty(courseCode)) {
            Toast.makeText(getContext(),
                    "Please enter course code (e.g. 0, 1, ...)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("course")
                .document(courseCode)
                .get()
                .addOnSuccessListener(doc -> {
                    slotList.clear();

                    if (!doc.exists()) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(),
                                "Course not found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Use course documents to create a "fake" AvailabilitySlot to display
                    AvailabilitySlot slot = new AvailabilitySlot();
                    slot.id = doc.getId();          // doc id in course
                    slot.courseCode = doc.getId();          // use doc id as courseCode
                    // Read the tutor reference from the course file
                    DocumentReference tutorRef = doc.getDocumentReference("tutor");
                    if (tutorRef == null) {
                        slot.tutorId = "Unknown";
                        slot.tutorName = "Unknown";

                        slotList.add(slot);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    tutorRef.get()
                            .addOnSuccessListener(tutorInfo -> {
                                slot.tutorId = tutorRef.getId();

                                if (tutorInfo.exists()) {
                                    String fName = tutorInfo.getString("firstName");
                                    String lName = tutorInfo.getString("lastName");
                                    slot.tutorName = fName + " " + lName;
                                } else {
                                    slot.tutorName = "Unknown Tutor";
                                }
                                // There's no time field,
                                // so I'll just use the current time for now (just for UI display purposes).
                                slot.startMillis = System.currentTimeMillis();
                                slot.durationMin = 30;

                                slotList.add(slot);
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(),
                                        "Failed to load tutor info: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });

                });
    }

    /** When a student presses "Request" to reserve a slot for this course */
    private void onBookClicked(AvailabilitySlot slot) {
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(getContext(),
                    "Student ID not set. Check login flow.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) First, check if this student already has a pending/approved session for the same course.
        db.collection("session")
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("courseCode", slot.courseCode)
                .whereIn("status",
                        Arrays.asList(
                                Session.Status.PENDING.name(),
                                Session.Status.APPROVED.name()
                        ))
                .get()
                .addOnSuccessListener(qs -> {
                    if (!qs.isEmpty()) {
                        // If there are already requests/bookings for the same course,
                        // it will be considered a time conflict.
                        Toast.makeText(getContext(),
                                "You already have a session for this course.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 2) No conflict → Create a new session
                    Session s = new Session();
                    s.tutorId     = slot.tutorId;     // now will be "newTutor@gmail.com"
                    s.studentId   = studentId;        // e.g. "UI@gmail.com"
                    s.studentName = studentName;
                    s.courseCode  = slot.courseCode;  // this is course doc id（e.g. "0"）
                    s.startMillis = slot.startMillis;
                    s.durationMin = slot.durationMin;
                    s.tutorName = slot.tutorName;
                    s.status      = Session.Status.PENDING;

                    db.collection("session")
                            .add(s)
                            .addOnSuccessListener(docRef -> {
                                String generatedId = docRef.getId();
                                docRef.update("id", generatedId);

                                // update course：slots + filledSlots
                                db.collection("course")
                                        .document(slot.courseCode)
                                        .update(
                                                "slots",
                                                com.google.firebase.firestore.FieldValue
                                                        .arrayUnion("/student/" + studentId),
                                                "filledSlots",
                                                com.google.firebase.firestore.FieldValue
                                                        .increment(1)
                                        );

                                Toast.makeText(getContext(),
                                        "Request sent!",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(),
                                        "Failed to request: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to check existing sessions: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

}



