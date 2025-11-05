package com.uottawa.eecs.project_project_group_24;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class FirebaseAvailabilityRepository {

    public interface ListListener {
        void onLoaded(List<AvailabilitySlot> list);
        void onError(String message);
    }
    public interface OpCallback {
        void onSuccess();
        void onError(String message);
    }

    private final DatabaseReference root;

    public FirebaseAvailabilityRepository() {
        root = FirebaseDatabase.getInstance().getReference("availability");
    }

    // listen a tutor's all sections
    public ValueEventListener listenSlots(String tutorId, final ListListener l) {
        Query q = root.child(tutorId).orderByChild("startMillis");
        ValueEventListener v = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                List<AvailabilitySlot> out = new ArrayList<>();
                for (DataSnapshot s : snap.getChildren()) {
                    AvailabilitySlot slot = s.getValue(AvailabilitySlot.class);
                    if (slot != null) { slot.id = s.getKey(); out.add(slot); }
                }
                l.onLoaded(out);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                l.onError(e.getMessage());
            }
        };
        q.addValueEventListener(v);
        return v;
    }

    public void deleteSlot(String tutorId, String slotId, final OpCallback cb) {
        root.child(tutorId).child(slotId).removeValue()
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // reserver add after use on dialog box
    public void addSlot(String tutorId, AvailabilitySlot slot, final OpCallback cb) {
        DatabaseReference ref = root.child(tutorId).push();
        slot.tutorId = tutorId;
        ref.setValue(slot)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }
}

