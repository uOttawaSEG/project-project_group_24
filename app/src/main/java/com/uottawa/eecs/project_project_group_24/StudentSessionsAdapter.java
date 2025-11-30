package com.uottawa.eecs.project_project_group_24;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * show sessions
 */
public class StudentSessionsAdapter
        extends RecyclerView.Adapter<StudentSessionsAdapter.SessionViewHolder> {

    public interface OnSessionActionListener {
        void onCancelSession(Session session);
        // If you want to rate it later, you can add `onRateSession(Session s)`.
        void onRateSession(Session s);
    }

    private List<Session> sessions;
    private final OnSessionActionListener listener;

    public StudentSessionsAdapter(List<Session> sessions,
                                  OnSessionActionListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    public void setSessions(List<Session> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_session, parent, false);
        return new SessionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session s = sessions.get(position);

        holder.tvCourseCode.setText(s.courseCode != null ? s.courseCode : "");
        holder.tvTutorName.setText(s.tutorName != null ? s.tutorName : "");

        // time
        if (s.startMillis > 0) {
            Date d = new Date(s.startMillis);
            SimpleDateFormat fmt =
                    new SimpleDateFormat("EEE MMM dd • HH:mm", Locale.getDefault());
            holder.tvDateTime.setText(fmt.format(d));
        } else {
            holder.tvDateTime.setText("");
        }

        if (s.status != null) {
            holder.tvStatus.setText(s.status.name());
        } else {
            holder.tvStatus.setText("");
        }

        // Rate We'll add the features later; let's hide the buttons first.
        holder.btnRate.setVisibility(View.GONE);

        // Cancel Display rules：
        // - PENDING: Available anytime cancel
        // - APPROVED: If there are more than 24 hours left until the start time, you can cancel.
        // - Other statuses: Not displayed
        if (s.status == Session.Status.PENDING ||
                (s.status == Session.Status.APPROVED &&
                        isMoreThan24HoursAway(s.startMillis))) {

            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelSession(s);
                }
            });
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (sessions != null) ? sessions.size() : 0;
    }

    private boolean isMoreThan24HoursAway(long startMillis) {
        if (startMillis <= 0) return false;
        long now = System.currentTimeMillis();
        long diff = startMillis - now;
        long oneDay = 24L * 60L * 60L * 1000L;
        return diff > oneDay;
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvTutorName, tvDateTime, tvStatus;
        Button btnCancel, btnRate;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvTutorName  = itemView.findViewById(R.id.tvTutorName);
            tvDateTime   = itemView.findViewById(R.id.tvDateTime);
            tvStatus     = itemView.findViewById(R.id.tvStatus);
            btnCancel    = itemView.findViewById(R.id.btnCancel);
            btnRate      = itemView.findViewById(R.id.btnRate);
        }
    }
}
