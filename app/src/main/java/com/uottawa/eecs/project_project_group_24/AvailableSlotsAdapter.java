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

public class AvailableSlotsAdapter
        extends RecyclerView.Adapter<AvailableSlotsAdapter.SlotViewHolder> {

    public interface OnSlotClickListener {
        void onBookClicked(AvailabilitySlot slot);
    }

    private List<AvailabilitySlot> slots;
    private OnSlotClickListener listener;

    public AvailableSlotsAdapter(List<AvailabilitySlot> slots,
                                 OnSlotClickListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_available_slot, parent, false);
        return new SlotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        AvailabilitySlot s = slots.get(position);

        Date d = new Date(s.startMillis);
        SimpleDateFormat fmt =
                new SimpleDateFormat("EEE MMM dd • HH:mm", Locale.getDefault());

        String info = fmt.format(d)
                + " — Tutor: " + s.tutorName
                + " • " + s.courseCode;

        holder.txtInfo.setText(info);

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) listener.onBookClicked(s);
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfo;
        Button btnBook;

        SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo = itemView.findViewById(R.id.txtInfo);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}
