package com.uottawa.eecs.project_project_group_24;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RegistrationRequestAdapter
        extends RecyclerView.Adapter<RegistrationRequestAdapter.VH> {

    public interface OnActionListener {
        void onApprove(RegistrationRequest req, int position);
        void onReject(RegistrationRequest req, int position);
        void onItemClick(RegistrationRequest req, int position);
    }

    public enum Mode { PENDING, REJECTED }

    private final List<RegistrationRequest> items = new ArrayList<>();
    private final OnActionListener listener;
    private final Mode mode;

    public RegistrationRequestAdapter(Mode mode, OnActionListener l) {
        this.mode = mode;
        this.listener = l;
    }

    public void submit(List<RegistrationRequest> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registration_request, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        RegistrationRequest r = items.get(pos);
        h.txtNameRole.setText(r.getFullName() + " — " + (r.getRole() == RegistrationRequest.Role.STUDENT ? "Student" : "Tutor"));
        h.txtEmail.setText(r.getEmail());
        h.txtPhone.setText(r.getPhone() == null ? "" : r.getPhone());

        // show Program or Degree/Courses
        if (r.getRole() == RegistrationRequest.Role.STUDENT) {
            h.txtExtra.setText("Program: " + (r.getProgramOfStudy() == null ? "-" : r.getProgramOfStudy()));
        } else {
            String courses = (r.getCoursesOffered() == null || r.getCoursesOffered().isEmpty())
                    ? "-" : String.join(", ", r.getCoursesOffered());
            h.txtExtra.setText("Degree: " + (r.getHighestDegree() == null ? "-" : r.getHighestDegree())
                    + "  •  Courses: " + courses);
        }

        // Pending: two buttons；Rejected: only leave Approve
        h.btnReject.setVisibility(mode == Mode.PENDING ? View.VISIBLE : View.GONE);
        h.btnApprove.setText(mode == Mode.PENDING ? "Approve" : "Approve (rejected)");

        h.itemView.setOnClickListener(v -> listener.onItemClick(r, h.getBindingAdapterPosition()));
        h.btnApprove.setOnClickListener(v -> listener.onApprove(r, h.getBindingAdapterPosition()));
        h.btnReject.setOnClickListener(v -> listener.onReject(r, h.getBindingAdapterPosition()));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtNameRole, txtEmail, txtPhone, txtExtra;
        Button btnApprove, btnReject;
        VH(@NonNull View v) {
            super(v);
            txtNameRole = v.findViewById(R.id.txtNameRole);
            txtEmail = v.findViewById(R.id.txtEmail);
            txtPhone = v.findViewById(R.id.txtPhone);
            txtExtra = v.findViewById(R.id.txtExtra);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}

