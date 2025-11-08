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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AdminRequestListFragment
 *
 * Usage：
 *   - AdminRequestListFragment.newPending()   // (status == "PENDING")
 *   - AdminRequestListFragment.newRejected()  // (status == "REJECTED")
 *
 * D2 domain：
 *   - Admin can see Pending request、Approve or Reject
 *   - Admin can let someone in Rejected list go back to Approve
 *   - after Approve will not go back to Rejected。
 */
public class AdminRequestListFragment extends Fragment
        implements RegistrationRequestAdapter.OnActionListener {

    // ==== Constants / args ====
    private static final String ARG_MODE = "mode";

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

    // ==== UI refs ====
    private TextView header;
    private TextView emptyView;
    private ProgressBar progress;
    private RecyclerView recycler;

    // ==== Data / adapter ====
    private RegistrationRequestAdapter adapter;
    private RegistrationRequestAdapter.Mode mode;

    // ==== Firestore ====
    private FirebaseFirestore db;
    private ListenerRegistration listenerReg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inf.inflate(R.layout.fragment_admin_request_list, container, false);

        header = v.findViewById(R.id.header);
        emptyView = v.findViewById(R.id.emptyView);
        progress = v.findViewById(R.id.progress);
        recycler = v.findViewById(R.id.recycler);

        // get Fragment mode (PENDING or REJECTED)
        mode = RegistrationRequestAdapter.Mode.valueOf(
                requireArguments().getString(ARG_MODE));

        header.setText(
                mode == RegistrationRequestAdapter.Mode.PENDING
                        ? "Pending Registration Requests"
                        : "Rejected Registration Requests"
        );

        // RecyclerView setup
        adapter = new RegistrationRequestAdapter(mode, this);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(
                new DividerItemDecoration(getContext(), RecyclerView.VERTICAL)
        );
        recycler.setAdapter(adapter);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        // get data from DB
        startListeningForData();


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // stop Firestore listen，avoid memory leak
        if (listenerReg != null) {
            listenerReg.remove();
            listenerReg = null;
        }
    }

    /**
     * listen from Firestore's registrationRequests/{doc} relay on status
     * mode = PENDING   → status == "PENDING"
     * mode = REJECTED  → status == "REJECTED"
     *
     * D2 ：
     * - Pending list：waiting for Admin do Approve or Reject。
     * - Rejected list：people in Rejected list, Admin can still Approve。
     */
    private void startListeningForData() {
        progress.setVisibility(View.VISIBLE);

        String wantedStatus = (mode == RegistrationRequestAdapter.Mode.PENDING)
                ? "PENDING"
                : "REJECTED";

        // if there is a listening, remove it first
        if (listenerReg != null) {
            listenerReg.remove();
            listenerReg = null;
        }

        listenerReg = db.collection("registrationRequests")
                .whereEqualTo("status", wantedStatus)
                .addSnapshotListener((snap, err) -> {
                    progress.setVisibility(View.GONE);

                    if (err != null) {
                        Toast.makeText(
                                getContext(),
                                "DB error: " + err.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (snap == null || snap.isEmpty()) {
                        adapter.submit(new ArrayList<>());
                        emptyView.setVisibility(View.VISIBLE);
                        return;
                    }

                    List<RegistrationRequest> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        RegistrationRequest r = doc.toObject(RegistrationRequest.class);
                        // keep Firestore docId, after we need to use on approve/reject
                        r.setId(doc.getId());
                        list.add(r);
                    }

                    adapter.submit(list);
                    emptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    // =========================================================================
    // Adapter callback: Approve
    // =========================================================================
    @Override
    public void onApprove(RegistrationRequest req, int position) {
        // - in Pending list click Approve → status: "APPROVED"
        // - in Rejected list click Approve → status: "APPROVED"
        //
        //  D2 ：
        //   * Admin can approve Pending
        //   * Admin can make Rejected become Approved
        //   * If Approved happened, then Rejected will not allow(UI will not show Reject button)

        updateStatus(req, "APPROVED", "Approved: " + req.getFullName());
    }

    // =========================================================================
    // Adapter callback: Reject
    // =========================================================================
    @Override
    public void onReject(RegistrationRequest req, int position) {
        // only Pending list would show Reject button
        // Then -> status = "REJECTED"
        // After that it would disappear in Pending list, and then show in Rejected list

        updateStatus(req, "REJECTED", "Rejected: " + req.getFullName());
    }

    @Override
    public void onItemClick(RegistrationRequest req, int position) {
        // this can pop up BottomSheet to shows more detail (phone number、Program、Degree、Courses)
        Toast.makeText(getContext(),
                "Open detail for " + req.getFullName(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Firestore update function
     * @param req  which request
     * @param newStatus "APPROVED" / "REJECTED"
     * @param successMsg after success toast the word will show
     */
    private void updateStatus(RegistrationRequest req, String newStatus, String successMsg) {
        if (db == null) return;
        progress.setVisibility(View.VISIBLE);

        db.collection("registrationRequests")
                .document(req.getId())
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), successMsg, Toast.LENGTH_SHORT).show();
                    // here do not need remove adapter by ourself
                    // because snapshotListener will do that -> startListeningForData()'s listenerReg
                    // will make a new form adapter.submit(...)
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Error updating status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
        db.collection("user")
                .document(req.getId())
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), successMsg, Toast.LENGTH_SHORT).show();
                    // here do not need remove adapter by ourself
                    // because snapshotListener will do that -> startListeningForData()'s listenerReg
                    // will make a new form adapter.submit(...)
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Error updating status: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
        FirebaseManager dbmanage = FirebaseManager.getInstance();
        if(newStatus.equalsIgnoreCase("Approved"))
        {
            if(req.getRole().equalsIgnoreCase("tutor")) dbmanage.moveCollections("user","tutor",req.getEmail());
            if(req.getRole().equalsIgnoreCase("student")) dbmanage.moveCollections("user","student",req.getEmail());
        }
    }
}
