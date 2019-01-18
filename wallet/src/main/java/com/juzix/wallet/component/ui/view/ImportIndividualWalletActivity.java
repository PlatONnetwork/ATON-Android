package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.QRCodeParser;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;
import java.util.List;

public class ImportIndividualWalletActivity extends BaseActivity implements View.OnClickListener{
    public static final int            TAB1                  = 0;
    public static final int            TAB2                  = 1;
    public static final int            TAB3                  = 2;
    private              ViewPagerSlide mViewPager;
    private              TabAdapter     mTabAdapter;
    public static final int REQ_QR_CODE = 101;

    private final static String    TAG = ImportIndividualWalletActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, ImportIndividualWalletActivity.class));
    }

    public static void actionStart(Context context, int type, String content) {
        Intent intent = new Intent(context, ImportIndividualWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TYPE, type);
        intent.putExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_individual_wallet);
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.importIndividualWallet);
        findViewById(R.id.ll_right).setOnClickListener(this);
        ImageView ivRight = findViewById(R.id.iv_right);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.icon_scan);

        ArrayList<String>          titles    = getTitles();
        ArrayList<MVPBaseFragment> fragments ;
        Intent intent = getIntent();
        int index;
        if (intent.hasExtra(Constants.Extra.EXTRA_TYPE)){
            index = intent.getIntExtra(Constants.Extra.EXTRA_TYPE, TAB1);
            fragments = getFragments(index, intent.getExtras());
        }else {
            index = TAB1;
            fragments = getFragments(-1, null);
        }
        mTabAdapter = new TabAdapter(getSupportFragmentManager(), titles, fragments);
        mViewPager = mRootView.findViewById(R.id.vp_content);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setSlide(true);

        TabLayout tablayout = mRootView.findViewById(R.id.tl_indicator);
        for (String title : titles) {
            tablayout.addTab(tablayout.newTab().setText(title));
        }
        tablayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                hideSoftInput();
                finish();
                break;
            case R.id.ll_right:
                scanQRCode();
                break;
        }
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.keystore));
        titleList.add(getString(R.string.mnemonicPhrase));
        titleList.add(getString(R.string.privateKey));
        return titleList;
    }

    private ArrayList<MVPBaseFragment> getFragments(int type, Bundle bundle) {
        ArrayList<MVPBaseFragment> fragments = new ArrayList<>();
        if (type < 0){
            fragments.add(getFragment(TAB1, null));
            fragments.add(getFragment(TAB2, null));
            fragments.add(getFragment(TAB3, null));
            return fragments;
        }
        switch (type){
            case TAB1:
                fragments.add(getFragment(TAB1, bundle));
                fragments.add(getFragment(TAB2, null));
                fragments.add(getFragment(TAB3, null));
                break;
            case TAB2:
                fragments.add(getFragment(TAB1, null));
                fragments.add(getFragment(TAB2, bundle));
                fragments.add(getFragment(TAB3, null));
                break;
            case TAB3:
                fragments.add(getFragment(TAB1, null));
                fragments.add(getFragment(TAB2, null));
                fragments.add(getFragment(TAB3, bundle));
                break;
        }
        return fragments;
    }

    public MVPBaseFragment getFragment(int tab, Bundle bundle) {
        MVPBaseFragment fragment = null;
        switch (tab) {
            case TAB1:
                fragment = new ImportIndividualKeystoreFragment();
                break;
            case TAB2:
                fragment = new ImportIndividualMnemonicPhraseFragment();
                break;
            case TAB3:
                fragment = new ImportIndividualPrivateKeyFragment();
                break;
        }
        if (bundle != null && !bundle.isEmpty()){
            fragment.setArguments(bundle);
        }
//        Bundle bundle = new Bundle();
//        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ImportIndividualWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(ScanQRCodeActivity.EXTRA_SCAN_QRCODE_DATA);
            if (JZWalletUtil.isValidKeystore(scanResult)){
                mViewPager.setCurrentItem(0);
                mTabAdapter.getItem(0).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidPrivateKey(scanResult)){
                mViewPager.setCurrentItem(2);
                mTabAdapter.getItem(2).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidMnemonic(scanResult)){
                mViewPager.setCurrentItem(1);
                mTabAdapter.getItem(1).onActivityResult(requestCode, resultCode, data);
                return;
            }
            showLongToast(string(R.string.unrecognized));
        }
    }

    private void scanQRCode() {
        final BaseActivity activity = currentActivity();
        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                ScanQRCodeActivity.actionStart(currentActivity(), REQ_QR_CODE);
            }

            @Override
            public void onHasPermission(int what) {
                ScanQRCodeActivity.actionStart(currentActivity(), REQ_QR_CODE);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, Manifest.permission.CAMERA);
    }

    private class TabAdapter extends FragmentStatePagerAdapter {

        private ArrayList<MVPBaseFragment> mFragments;
        private ArrayList<String>       mTitles;

        TabAdapter(FragmentManager fm, ArrayList<String> mTitles, ArrayList<MVPBaseFragment> fragments) {
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

        public ArrayList<MVPBaseFragment> getFragments() {
            return mFragments;
        }

        public ArrayList<String> getTitles() {
            return mTitles;
        }

        @Override
        public MVPBaseFragment getItem(int position) {
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
