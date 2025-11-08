package com.uottawa.eecs.project_project_group_24;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class SessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onApprove(Session s, int pos);
        void onReject(Session s, int pos);
        void onCancel(Session s, int pos);
        void onClick(Session s, int pos);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM   = 1;

    private final Listener listener;
    // show data： header / item
    private final List<Object> display = new ArrayList<>();

    public SessionAdapter(Listener l) { this.listener = l; }

    public void submit(List<Session> all, long nowMs) {
        display.clear();

        List<Session> upcoming = new ArrayList<>();
        List<Session> past     = new ArrayList<>();

        for (Session s : all) {
            long end = s.startMillis + s.durationMin * 60_000L;
            boolean isPast = end < nowMs || s.status == Session.Status.CANCELLED || s.status == Session.Status.REJECTED || s.status == Session.Status.COMPLETED;
            if (isPast) past.add(s); else upcoming.add(s);
        }

        // arrange by time
        Comparator<Session> byStart = Comparator.comparingLong(a -> a.startMillis);
        Collections.sort(upcoming, byStart);
        Collections.sort(past, byStart);

        if (!upcoming.isEmpty()) {
            display.add("Upcoming Sessions");
            display.addAll(upcoming);
        }
        if (!past.isEmpty()) {
            display.add("Past Sessions");
            display.addAll(past);
        }

        notifyDataSetChanged();
    }

    @Override public int getItemCount() { return display.size(); }

    @Override public int getItemViewType(int position) {
        return (display.get(position) instanceof String) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_header, parent, false);
            return new HeaderVH(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
            return new ItemVH(v);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        if (getItemViewType(pos) == TYPE_HEADER) {
            ((HeaderVH)h).txt.setText((String) display.get(pos));
        } else {
            ((ItemVH)h).bind((Session) display.get(pos), listener);
        }
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView txt;
        HeaderVH(@NonNull View v) { super(v); txt = v.findViewById(R.id.txtHeader); }
    }

    static class ItemVH extends RecyclerView.ViewHolder {
        TextView title, time, status;
        View actions;
        Button btnApprove, btnReject, btnCancel;

        ItemVH(@NonNull View v) {
            super(v);
            title  = v.findViewById(R.id.txtTitle);
            time   = v.findViewById(R.id.txtTime);
            status = v.findViewById(R.id.txtStatus);
            actions= v.findViewById(R.id.actions);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject  = v.findViewById(R.id.btnReject);
            btnCancel  = v.findViewById(R.id.btnCancel);
        }

        void bind(Session s, Listener l) {
            title.setText((s.courseCode == null ? "-" : s.courseCode) + " — " + (s.studentName == null ? "Student" : s.studentName));
            Date start = new Date(s.startMillis);
            Date end   = new Date(s.startMillis + s.durationMin * 60_000L);
            time.setText(DateFormat.format("EEE, MMM d • HH:mm", start) + "–" + DateFormat.format("HH:mm", end));
            status.setText("Status: " + s.status.name());

            // Upcoming only: show button。Past: hide action
            boolean upcoming = !(s.status == Session.Status.CANCELLED || s.status == Session.Status.REJECTED || s.status == Session.Status.COMPLETED)
                    && (s.startMillis + s.durationMin * 60_000L) >= System.currentTimeMillis();
            actions.setVisibility(upcoming ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> l.onClick(s, getBindingAdapterPosition()));
            btnApprove.setOnClickListener(v -> l.onApprove(s, getBindingAdapterPosition()));
            btnReject.setOnClickListener(v -> l.onReject(s, getBindingAdapterPosition()));
            btnCancel.setOnClickListener(v -> l.onCancel(s, getBindingAdapterPosition()));
        }
    }
}

