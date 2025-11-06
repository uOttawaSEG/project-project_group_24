package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MyAvailabilityFragment extends Fragment implements AvailabilityAdapter.Listener {

    private static final String ARG_TUTOR_ID = "tutor_id";

    public static MyAvailabilityFragment newInstance(String tutorId) {
        MyAvailabilityFragment f = new MyAvailabilityFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TUTOR_ID, tutorId);
        f.setArguments(b);
        return f;
    }

    private AvailabilityAdapter adapter;
    private FirebaseAvailabilityRepository repo;
    private ValueEventListener live;
    private String tutorId;

    private ProgressBar progress;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_availability, container, false);

        RecyclerView recycler = v.findViewById(R.id.recycler);
        progress = v.findViewById(R.id.progress);
        emptyView = v.findViewById(R.id.emptyView);
        FloatingActionButton fab = v.findViewById(R.id.fabAdd); // 在這裡取得 FAB

        tutorId = requireArguments().getString(ARG_TUTOR_ID, "TUTOR_DEMO");
        repo = new FirebaseAvailabilityRepository();

        adapter = new AvailabilityAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));
        recycler.setAdapter(adapter);

        //  FAB Click → Open the Add Time Period dialog box
        fab.setOnClickListener(btn ->
                AddAvailabilityDialog.newInstance(tutorId, () -> {
                    // After a successful addition,
                    // the list will be automatically refreshed by Firebase listener,
                    // so you don't need to do anything here.
                }).show(getParentFragmentManager(), "add_availability")
        );

        startListening();
        return v;
    }

    private void startListening() {
        progress.setVisibility(View.VISIBLE);
        live = repo.listenSlots(tutorId, new FirebaseAvailabilityRepository.ListListener() {
            @Override public void onLoaded(List<AvailabilitySlot> list) {
                progress.setVisibility(View.GONE);
                adapter.submit(list);
                emptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Load error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (live != null) {
            FirebaseDatabase.getInstance().getReference("availability")
                    .child(tutorId).removeEventListener(live);
            live = null;
        }
    }

    // === Button callbacks for list items ===
    @Override
    public void onDelete(AvailabilitySlot slot, int position) {
        repo.deleteSlot(tutorId, slot.id, new FirebaseAvailabilityRepository.OpCallback() {
            @Override public void onSuccess() {
                Toast.makeText(requireContext(), "Slot deleted", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String m) {
                Toast.makeText(requireContext(), "Delete failed: " + m, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(AvailabilitySlot slot, int position) {
        // The behavior when clicking on a slot (first provide a hint, then allow editing/details).
        Toast.makeText(requireContext(), "Slot " + slot.courseCode, Toast.LENGTH_SHORT).show();
    }
}
