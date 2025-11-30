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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MySessionsFragment extends Fragment implements SessionAdapter.Listener {

    private static final String ARG_TUTOR_ID = "tutor_id";
    private static final String ARG_TUTOR_NAME = "tutorName";

    public static MySessionsFragment newInstance(String tutorId) {
        MySessionsFragment f = new MySessionsFragment();
        Bundle b = new Bundle(); b.putString(ARG_TUTOR_ID, tutorId); f.setArguments(b);
        return f;
    }

    private String tutorId;
    private String tutorName;
    private SessionAdapter adapter;
    private FirebaseSessionsRepository repo;
    private ValueEventListener live;

    private ProgressBar progress;
    private TextView empty;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_sessions, container, false);

        // change to list
        RecyclerView rv = v.findViewById(R.id.recycler);
        progress = v.findViewById(R.id.progress);
        empty = v.findViewById(R.id.emptyView);

        tutorId = requireArguments().getString(ARG_TUTOR_ID, "TUTOR_DEMO");
        tutorName = requireArguments().getString(ARG_TUTOR_NAME, "TUTOR_NAME");
        repo = new FirebaseSessionsRepository();

        adapter = new SessionAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.addItemDecoration(new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL));
        rv.setAdapter(adapter);

        startListening();
        return v;
    }

    private void startListening() {
        progress.setVisibility(View.VISIBLE);
        live = repo.listenForTutor(tutorId, new FirebaseSessionsRepository.ListListener() {
            @Override public void onLoaded(List<Session> list) {
                progress.setVisibility(View.GONE);
                adapter.submit(list, System.currentTimeMillis());
                empty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
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
            FirebaseDatabase.getInstance().getReference("sessions").child(tutorId).removeEventListener(live);
            live = null;
        }
    }

    // === Adapter callbacks ===
    @Override public void onApprove(Session s, int pos) {
        repo.setStatus(tutorId, s.id, Session.Status.APPROVED, opToast("Approved"));
    }
    @Override public void onReject(Session s, int pos) {
        repo.setStatus(tutorId, s.id, Session.Status.REJECTED, opToast("Rejected"));
    }
    @Override public void onCancel(Session s, int pos) {
        repo.cancel(tutorId, s.id, opToast("Canceled"));
    }
    @Override public void onClick(Session s, int pos) {
        Toast.makeText(requireContext(), s.courseCode + " — " + s.studentName, Toast.LENGTH_SHORT).show();
    }

    private FirebaseSessionsRepository.OpCallback opToast(String ok) {
        return new FirebaseSessionsRepository.OpCallback() {
            @Override public void onSuccess() { Toast.makeText(requireContext(), ok, Toast.LENGTH_SHORT).show(); }
            @Override public void onError(String m) { Toast.makeText(requireContext(), "Error: " + m, Toast.LENGTH_SHORT).show(); }
        };
    }
}
