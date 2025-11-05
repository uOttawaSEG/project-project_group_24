package com.uottawa.eecs.project_project_group_24;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.VH> {

    public interface Listener {
        void onDelete(AvailabilitySlot slot, int position);
        void onClick(AvailabilitySlot slot, int position);
    }

    private final List<AvailabilitySlot> items = new ArrayList<>();
    private final Listener listener;

    public AvailabilityAdapter(Listener l) { this.listener = l; }

    public void submit(List<AvailabilitySlot> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_availability_slot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        AvailabilitySlot s = items.get(pos);
        h.bind(s, listener);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtDate, txtTime, txtCourse;
        Button btnDelete;

        VH(@NonNull View v) {
            super(v);
            txtDate = v.findViewById(R.id.txtDate);
            txtTime = v.findViewById(R.id.txtTime);
            txtCourse = v.findViewById(R.id.txtCourse);
            btnDelete = v.findViewById(R.id.btnDelete);
        }

        void bind(AvailabilitySlot s, Listener l) {
            Date start = new Date(s.startMillis);
            long endMillis = s.startMillis + s.durationMin * 60_000L;
            Date end = new Date(endMillis);

            // date and time（use system area/24h depend on devices）
            String dateStr = DateFormat.format("EEE, MMM d, yyyy", start).toString();
            String timeStr = DateFormat.format("HH:mm", start) + " – " + DateFormat.format("HH:mm", end);

            txtDate.setText(dateStr);
            txtTime.setText(timeStr);
            txtCourse.setText("Course: " + (s.courseCode == null ? "-" : s.courseCode));

            itemView.setOnClickListener(v -> l.onClick(s, getBindingAdapterPosition()));
            btnDelete.setOnClickListener(v -> l.onDelete(s, getBindingAdapterPosition()));
        }
    }
}

