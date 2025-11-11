package com.uottawa.eecs.project_project_group_24;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class FirebaseSessionsRepository {

    public interface ListListener {
        void onLoaded(List<Session> list);
        void onError(String message);
    }
    public interface OpCallback {
        void onSuccess();
        void onError(String message);
    }

    private final DatabaseReference root;

    public FirebaseSessionsRepository() {
        root = FirebaseDatabase.getInstance().getReference("sessions");
    }

    /** listen a tutor's sessions（mixed status），change by front Upcoming/Past */
    public ValueEventListener listenForTutor(String tutorId, final ListListener l) {
        Query q = root.child(tutorId).orderByChild("startMillis");
        ValueEventListener v = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<Session> out = new ArrayList<>();
                for (DataSnapshot s : snap.getChildren()) {
                    Session sess = s.getValue(Session.class);
                    if (sess != null) { sess.id = s.getKey(); out.add(sess); }
                }
                l.onLoaded(out);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { l.onError(e.getMessage()); }
        };
        q.addValueEventListener(v);
        return v;
    }


    public void setStatus(String tutorId, String sessionId, Session.Status status, final OpCallback cb) {
        root.child(tutorId).child(sessionId).child("status").setValue(status.name())
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    public void cancel(String tutorId, String sessionId, final OpCallback cb) {
        setStatus(tutorId, sessionId, Session.Status.CANCELLED, cb);
    }

    // choose：add/create test data
    public void add(String tutorId, Session s, final OpCallback cb) {
        DatabaseReference ref = root.child(tutorId).push();
        Log.d("OTA_FIREBASESESSIONS",ref.toString());
        s.tutorId = tutorId;
        Log.d("OTA_FIREBASESESSIONS",tutorId.toString());
        ref.setValue(s)
                .addOnSuccessListener(v -> {
                    Log.d("OTA_FIREBASESESSIONS","SUCCESS");
                    cb.onSuccess();
                })
                .addOnFailureListener(e ->
                {
                    Log.d("OTA_FIREBASESESSIONS",String.valueOf(e.getMessage()));
                    cb.onError(e.getMessage());
                });
    }
}

