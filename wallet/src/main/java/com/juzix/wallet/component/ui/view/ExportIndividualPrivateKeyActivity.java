package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.widget.ViewPagerSlide;

import java.util.ArrayList;

public class ExportIndividualPrivateKeyActivity extends BaseActivity implements View.OnClickListener{
    private static final int            TAB1                  = 1;
    private static final int            TAB2                  = 2;
    private              ViewPagerSlide mViewPager;
    private              TabAdapter     mTabAdapter;
    private BaseDialog mMnemonicDialog;

    private final static String    TAG = ExportIndividualPrivateKeyActivity.class.getSimpleName();

    public static void actionStart(Context context, String password) {
        Intent intent = new Intent(context, ExportIndividualPrivateKeyActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_PASSWORD, password);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_individual_private_key);
        showPasswordDialog();
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.exportPrivateKey);

        ArrayList<BaseFragment> fragments = getFragments();
        ArrayList<String>       titles    = getTitles();
        mTabAdapter = new TabAdapter(getSupportFragmentManager(), titles, fragments);
        mViewPager = mRootView.findViewById(R.id.vp_content);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setSlide(false);
        TabLayout tablayout = mRootView.findViewById(R.id.tl_indicator);
        for (String title : titles) {
            tablayout.addTab(tablayout.newTab().setText(title));
        }
        tablayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                finish();
                break;
            case R.id.btn_understood:
                dimissPasswordDialog();
        }
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.privateKey));
        titleList.add(getString(R.string.qrCode));
        return titleList;
    }

    private ArrayList<BaseFragment> getFragments() {
        ArrayList<BaseFragment> list = new ArrayList<>();
        list.add(getFragment(TAB1));
        list.add(getFragment(TAB2));
        return list;
    }

    public BaseFragment getFragment(int tab) {
        BaseFragment fragment = null;
        switch (tab) {
            case TAB1:
                fragment = new ExportIndividualPrivateKeyFragment();
                break;
            case TAB2:
                fragment = new ExportIndividualPrivateKeyQRCodeFragment();
                break;
        }
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void showPasswordDialog(){
        dimissPasswordDialog();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_backup_mnemonic_phrase, null);
        ((TextView) view.findViewById(R.id.tv_content)).setText(R.string.backupPrivateKey);
        mMnemonicDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mMnemonicDialog.setContentView(view);
        mMnemonicDialog.show();
        mMnemonicDialog.findViewById(R.id.btn_understood).setOnClickListener(this);
    }

    private void dimissPasswordDialog(){
        if (mMnemonicDialog != null && mMnemonicDialog.isShowing()){
            mMnemonicDialog.dismiss();
            mMnemonicDialog = null;
        }
    }

    private class TabAdapter extends FragmentStatePagerAdapter {

        private ArrayList<BaseFragment> mFragments;
        private ArrayList<String>       mTitles;

        TabAdapter(FragmentManager fm, ArrayList<String> mTitles, ArrayList<BaseFragment> fragments) {
            super(fm);
            this.mFragments = fragments;
            this.mTitles = mTitles;
        }

        public void destroyAll() {
            for (int i = 0; i < mFragments.size(); i++) {
                try {
                    BaseFragment baseFragment = mFragments.get(i);
                    baseFragment.onDestroyView();
                    destroyItem(null, i, mFragments.get(i));
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }

        public ArrayList<BaseFragment> getFragments() {
            return mFragments;
        }

        public ArrayList<String> getTitles() {
            return mTitles;
        }

        @Override
        public BaseFragment getItem(int position) {
            if (mFragments == null || mFragments.isEmpty() || position >= getCount()){
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
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }
    }
}
