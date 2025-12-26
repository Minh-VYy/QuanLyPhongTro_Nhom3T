package com.example.QuanLyPhongTro_App.ui.tenant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BookingPagerAdapter extends FragmentStateAdapter {

    public BookingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position=0: upcoming, position=1: past(history)
        return BookingListFragment.newInstance(position == 0 ? "upcoming" : "past");
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
