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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminRequestListFragment extends Fragment
        implements RegistrationRequestAdapter.OnActionListener {

    private static final String ARG_MODE = "mode";
    private RegistrationRequestAdapter adapter;
    private TextView emptyView, header;
    private ProgressBar progress;

    public static AdminRequestListFragment newPending() {
        return newInstance(RegistrationRequestAdapter.Mode.PENDING);
    }
    public static AdminRequestListFragment newRejected() {
        return newInstance(RegistrationRequestAdapter.Mode.REJECTED);
    }
    public static AdminRequestListFragment newInstance(RegistrationRequestAdapter.Mode mode) {
        AdminRequestListFragment f = new AdminRequestListFragment();
        Bundle b = new Bundle();
        b.putString(ARG_MODE, mode.name());
        f.setArguments(b);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inf.inflate(R.layout.fragment_admin_request_list, c, false);
        RecyclerView recycler = v.findViewById(R.id.recycler);
        emptyView = v.findViewById(R.id.emptyView);
        header = v.findViewById(R.id.header);
        progress = v.findViewById(R.id.progress);

        RegistrationRequestAdapter.Mode mode =
                RegistrationRequestAdapter.Mode.valueOf(requireArguments().getString(ARG_MODE));

        header.setText(mode == RegistrationRequestAdapter.Mode.PENDING ?
                "Pending Registration Requests" : "Rejected Registration Requests");

        adapter = new RegistrationRequestAdapter(mode, this);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));
        recycler.setAdapter(adapter);

        loadData(mode);
        return v;
    }

    private void loadData(RegistrationRequestAdapter.Mode mode) {
        progress.setVisibility(View.VISIBLE);

        // TODO: becoming read from DB/Firebase. This is a demo.
        recyclerPost(() -> {
            List<RegistrationRequest> data = mockData(mode);
            adapter.submit(data);
            progress.setVisibility(View.GONE);
            emptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private List<RegistrationRequest> mockData(RegistrationRequestAdapter.Mode mode) {
        if (mode == RegistrationRequestAdapter.Mode.PENDING) {
            RegistrationRequest s = new RegistrationRequest();
            s.id = "S1"; s.role = RegistrationRequest.Role.STUDENT;
            s.firstName = "Ken"; s.lastName = "Shan";
            s.email = "ken@uottawa.ca"; s.phone = "6130000000";
            s.programOfStudy = "Software Engineering";

            RegistrationRequest t = new RegistrationRequest();
            t.id = "T1"; t.role = RegistrationRequest.Role.TUTOR;
            t.firstName = "Alex"; t.lastName = "Lee";
            t.email = "alex@uottawa.ca"; t.phone = "6131111111";
            t.highestDegree = "Master";
            t.coursesOffered = Arrays.asList("ITI1121", "CSI2110");

            return new ArrayList<>(Arrays.asList(s, t));
        } else {
            RegistrationRequest r = new RegistrationRequest();
            r.id = "R1"; r.role = RegistrationRequest.Role.STUDENT;
            r.firstName = "Mina"; r.lastName = "Wu";
            r.email = "mina@uottawa.ca"; r.phone = "6132222222";
            r.programOfStudy = "Computer Science";
            r.status = RegistrationRequest.Status.REJECTED;
            return new ArrayList<>(Arrays.asList(r));
        }
    }

    private void recyclerPost(Runnable r) {
        // switch to UI execute
        if (getView() != null) getView().post(r);
    }

    // === Adapter callbacks ===
    @Override public void onApprove(RegistrationRequest req, int pos) {
        Toast.makeText(getContext(), "Approved: " + req.fullName(), Toast.LENGTH_SHORT).show();
        // TODO: call background update state；remove from list after success
        removeAt(pos);
    }
    @Override public void onReject(RegistrationRequest req, int pos) {
        Toast.makeText(getContext(), "Rejected: " + req.fullName(), Toast.LENGTH_SHORT).show();
        // TODO: call background update state；remove from list after success（ Rejected page can see）
        removeAt(pos);
    }
    @Override public void onItemClick(RegistrationRequest req, int pos) {
        // TODO: to detail page or BottomSheet
        Toast.makeText(getContext(), "Open: " + req.fullName(), Toast.LENGTH_SHORT).show();
    }

    private void removeAt(int pos) {
        // reload
        RegistrationRequestAdapter.Mode mode =
                RegistrationRequestAdapter.Mode.valueOf(requireArguments().getString(ARG_MODE));
        loadData(mode);
    }
}

