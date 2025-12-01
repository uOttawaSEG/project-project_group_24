package com.uottawa.eecs.project_project_group_24;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RateSessionDialog extends DialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private static final String ARG_TUTOR_ID = "tutor_id";
    private static final String ARG_TUTOR_NAME = "tutor_name";

    // Listener interface to send data back to the Fragment
    public interface RateSessionListener {
        void onRatingSubmitted(String sessionId, String tutorId, float rating);
    }

    private RateSessionListener listener;

    public static RateSessionDialog newInstance(String sessionId, String tutorId, String tutorName) {
        RateSessionDialog dialog = new RateSessionDialog();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        args.putString(ARG_TUTOR_ID, tutorId);
        args.putString(ARG_TUTOR_NAME, tutorName);
        dialog.setArguments(args);
        return dialog;
    }

    public void setRateSessionListener(RateSessionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Assume R.layout.dialog_rate_session is your XML layout
        View view = inflater.inflate(R.layout.dialog_rate_session, container, false);

        // Find views based on your XML IDs
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Button btnSave = view.findViewById(R.id.btnSave2);
        TextView textViewTitle = view.findViewById(R.id.idid); // Assuming this ID exists

        String tutorName = getArguments().getString(ARG_TUTOR_NAME, "this tutor");

        if (textViewTitle != null) {
             textViewTitle.setText("Rate " + tutorName);
         }

        btnSave.setOnClickListener(v -> {
            float ratingValue = ratingBar.getRating();
            submitRating(ratingValue);
        });

        return view;
    }

    private void submitRating(float ratingValue) {
        String sessionId = getArguments().getString(ARG_SESSION_ID);
        String tutorId = getArguments().getString(ARG_TUTOR_ID);

        if (ratingValue > 0 && sessionId != null && tutorId != null) {
            // Send the rating and IDs back to the hosting Fragment
            if (listener != null) {
                listener.onRatingSubmitted(sessionId, tutorId, ratingValue);
            }
            dismiss(); // Close the dialog
        } else {
            Toast.makeText(getContext(), "Please select a rating.", Toast.LENGTH_SHORT).show();
        }
    }
}