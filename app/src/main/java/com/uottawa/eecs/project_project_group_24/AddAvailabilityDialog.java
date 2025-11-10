package com.uottawa.eecs.project_project_group_24;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddAvailabilityDialog extends BottomSheetDialogFragment {

    public interface Callback {
        void onAdded(); // call callback after add（list rely on listener to refresh）
    }

    private static final String ARG_TUTOR_ID = "tutor_id";

    public static AddAvailabilityDialog newInstance(String tutorId, Callback cb) {
        AddAvailabilityDialog d = new AddAvailabilityDialog();
        Bundle b = new Bundle(); b.putString(ARG_TUTOR_ID, tutorId); d.setArguments(b);
        d.callback = cb;
        return d;
    }

    private Callback callback;
    private String tutorId;

    private Button btnPickDate, btnPickTime, btnSave, btnCancel;
    private Spinner spinnerCourse;
    private TextView txtError;
    private ProgressBar progress;

    private final Calendar picked = Calendar.getInstance(); // user choose [start time]
    private boolean dateChosen = false, timeChosen = false;

    private FirebaseAvailabilityRepository repo;
    private ValueEventListener preloadListener;
    private final List<AvailabilitySlot> existing = new ArrayList<>();

    private static final int SLOT_MIN = 30;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup container, @Nullable Bundle s) {
        View v = inf.inflate(R.layout.dialog_add_availability, container, false);

        btnPickDate = v.findViewById(R.id.btnPickDate);
        btnPickTime = v.findViewById(R.id.btnPickTime);
        spinnerCourse = v.findViewById(R.id.spinnerCourse);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        txtError = v.findViewById(R.id.txtError);
        progress = v.findViewById(R.id.progress);

        tutorId = requireArguments().getString(ARG_TUTOR_ID, "TUTOR_DEMO");
        repo = new FirebaseAvailabilityRepository();

        setupCourseSpinner();
        setupPickers();
        btnCancel.setOnClickListener(vw -> dismiss());
        btnSave.setOnClickListener(vw -> onSave());

        preloadExisting(); // load current slot to check
        return v;
    }

    private void setupCourseSpinner() {
        // We'll use fake data here for now.
        // It will be dynamically loaded later based on the tutor's actual course list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Select course", "ITI1121", "CSI2110", "ELG2138"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);
        spinnerCourse.setSelection(0);
    }

    private void setupPickers() {
        btnPickDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (view, y, m, d) -> {
                        picked.set(Calendar.YEAR, y);
                        picked.set(Calendar.MONTH, m);
                        picked.set(Calendar.DAY_OF_MONTH, d);
                        dateChosen = true;
                        btnPickDate.setText(String.format("%04d-%02d-%02d", y, m+1, d));
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnPickTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute) -> {
                        picked.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        picked.set(Calendar.MINUTE, minute);
                        picked.set(Calendar.SECOND, 0);
                        picked.set(Calendar.MILLISECOND, 0);
                        timeChosen = true;
                        btnPickTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true).show(); // 24 hr
        });
    }

    private void preloadExisting() {
        // Load all tutor slots at once for overlap checking.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("availability").child(tutorId);
        preloadListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                existing.clear();
                for (DataSnapshot s : snap.getChildren()) {
                    AvailabilitySlot slot = s.getValue(AvailabilitySlot.class);
                    if (slot != null) { slot.id = s.getKey(); existing.add(slot); }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { /* Ignore this; it will be verified again when saving later. */ }
        };
        ref.addListenerForSingleValueEvent(preloadListener);
    }

    private void onSave() {
        txtError.setText("");
        if (!dateChosen || !timeChosen) {
            setError("Please pick date and time.");
            return;
        }
        Object courseSel = spinnerCourse.getSelectedItem();
        if (courseSel == null || "Select course".equals(courseSel.toString())) {
            setError("Please select a course.");
            return;
        }

        long startMillis = picked.getTimeInMillis();
        long now = System.currentTimeMillis();
        if (startMillis < now) {
            setError("Selected time is in the past.");
            return;
        }

        // Overlap check: The intersection of any existing interval
        // [s, s+30) and the new interval [start, start+30) must not be non-empty.
        long newStart = startMillis;
        long newEnd = startMillis + SLOT_MIN * 60_000L;

        for (AvailabilitySlot sl : existing) {
            long s = sl.startMillis;
            long e = s + (sl.durationMin > 0 ? sl.durationMin : SLOT_MIN) * 60_000L;
            boolean overlap = Math.max(s, newStart) < Math.min(e, newEnd);
            if (overlap) {
                setError("This slot overlaps with an existing one.");
                return;
            }
        }

        setBusy(true);

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.startMillis = newStart;
        slot.durationMin = SLOT_MIN;
        slot.courseCode = courseSel.toString();





        repo.addSlot(tutorId, slot, new FirebaseAvailabilityRepository.OpCallback() {
            @Override public void onSuccess() {
                setBusy(false);
                Toast.makeText(requireContext(), "Slot added", Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onAdded();
                dismiss();
            }
            @Override public void onError(String message) {
                setBusy(false);
                setError(TextUtils.isEmpty(message) ? "Add failed" : message);
            }
        });
    }

    private void setBusy(boolean b) {
        progress.setVisibility(b ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!b);
        btnCancel.setEnabled(!b);
        btnPickDate.setEnabled(!b);
        btnPickTime.setEnabled(!b);
        spinnerCourse.setEnabled(!b);
    }

    private void setError(String m) { txtError.setText(m); }

    @Override public void onDestroyView() {
        super.onDestroyView();
        // Single read; no need to remove the listener here (using singleValue).
    }
}

