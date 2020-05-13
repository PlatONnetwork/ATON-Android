package com.platon.aton.component.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.platon.framework.base.BaseFragment;
import com.platon.framework.base.BaseLazyFragment;

import java.util.List;

/**
 * @author ziv
 */
public class TabAdapter extends FragmentStatePagerAdapter {

    private List<BaseLazyFragment> mFragments;
    private List<String> mTitles;

    public TabAdapter(FragmentManager fm, List<String> mTitles, List<BaseLazyFragment> fragments) {
        super(fm);
        this.mFragments = fragments;
        this.mTitles = mTitles;
    }


    public List<BaseLazyFragment> getFragments() {
        return mFragments;
    }

    public List<String> getTitles() {
        return mTitles;
    }

    @Override
    public BaseFragment getItem(int position) {
        if (mFragments == null || mFragments.isEmpty() || position >= getCount()) {
            return null;
        }
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position % mTitles.size());
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
    }
}
