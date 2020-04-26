package us.xingkong.study.ui.activity.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class AppPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    @Override
    public int getCount() {
        return fragments.length;
    }

    AppPagerAdapter(@NonNull FragmentManager fm, int behavior, Fragment[] fragments) {
        super(fm, behavior);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }
}
