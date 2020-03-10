package com.platon.wallet.component.widget.table;

import android.support.v4.app.FragmentManager;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class PagerItemAdapter extends FragmentPagerItemAdapter {
    public PagerItemAdapter(FragmentManager fm, FragmentPagerItems pages) {
        super(fm, pages);
    }
}
