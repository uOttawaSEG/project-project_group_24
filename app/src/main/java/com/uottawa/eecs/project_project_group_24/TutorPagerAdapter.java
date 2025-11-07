package com.uottawa.eecs.project_project_group_24;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TutorPagerAdapter extends FragmentStateAdapter {

    private final String tutorId;

    public TutorPagerAdapter(@NonNull FragmentActivity fa, String tutorId) {
        super(fa);
        this.tutorId = tutorId;
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // You already have a Fragment
            return MyAvailabilityFragment.newInstance(tutorId);
        } else {
            // put a page for seat first, after we can change to real MySessionsFragment
            return MySessionsFragment.newInstance(tutorId);
        }
    }

    @Override public int getItemCount() { return 2; }
}
