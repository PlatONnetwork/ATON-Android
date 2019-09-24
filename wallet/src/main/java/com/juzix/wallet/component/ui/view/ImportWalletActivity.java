package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.component.widget.table.PagerItem;
import com.juzix.wallet.component.widget.table.PagerItemAdapter;
import com.juzix.wallet.component.widget.table.PagerItems;
import com.juzix.wallet.component.widget.table.SmartTabLayout;
import com.juzix.wallet.utils.JZWalletUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class ImportWalletActivity extends BaseActivity {
    public static final int TAB1 = 0;
    public static final int TAB2 = 1;
    public static final int TAB3 = 2;
    public static final int REQ_QR_CODE = 101;
    private ViewPagerSlide mVpContent;

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, ImportWalletActivity.class));
    }

    public static void actionStart(Context context, int type, String content) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TYPE, type);
        intent.putExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        initView();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initView() {
        CommonTitleBar commonTitleBar = findViewById(R.id.commonTitleBar);
        commonTitleBar.setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        commonTitleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });
        int indicatorThickness = AndroidUtil.dip2px(getContext(), 2.0f);
        SmartTabLayout stbBar = mRootView.findViewById(R.id.stb_bar);
        stbBar.setIndicatorThickness(indicatorThickness);
        stbBar.setIndicatorCornerRadius(indicatorThickness / 2);
        ArrayList<Class<? extends BaseFragment>> fragments = getFragments();
        Intent intent = getIntent();
        int index = -1;
        if (intent.hasExtra(Constants.Extra.EXTRA_TYPE)) {
            index = intent.getIntExtra(Constants.Extra.EXTRA_TYPE, TAB1);
        }
        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });
        PagerItems pages = new PagerItems(getContext());
        int tabNum = fragments.size();
        for (int i = 0; i < tabNum; i++) {
            if (i == index) {
                pages.add(PagerItem.of(getTitles().get(i), fragments.get(i), intent.getExtras()));
            } else {
                pages.add(PagerItem.of(getTitles().get(i), fragments.get(i), new Bundle()));
            }
        }
        mVpContent = mRootView.findViewById(R.id.vp_content);
        mVpContent.setSlide(true);
        mVpContent.setOffscreenPageLimit(fragments.size());
        mVpContent.setAdapter(new PagerItemAdapter(getSupportFragmentManager(), pages));
        stbBar.setViewPager(mVpContent);
        index = index == -1 ? TAB1 : index;
        setTableView(stbBar.getTabAt(index), index);
        mVpContent.setCurrentItem(index);
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.keystore));
        titleList.add(getString(R.string.mnemonicPhrase));
        titleList.add(getString(R.string.privateKey));
        return titleList;
    }

    private ArrayList<Class<? extends BaseFragment>> getFragments() {
        ArrayList<Class<? extends BaseFragment>> list = new ArrayList<>();
        list.add(ImportKeystoreFragment.class);
        list.add(ImportMnemonicPhraseFragment.class);
        list.add(ImportPrivateKeyFragment.class);
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ImportWalletActivity.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
            if (JZWalletUtil.isValidKeystore(scanResult)) {
                mVpContent.setCurrentItem(0);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(0).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidPrivateKey(scanResult)) {
                mVpContent.setCurrentItem(2);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(2).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidMnemonic(scanResult)) {
                mVpContent.setCurrentItem(1);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(1).onActivityResult(requestCode, resultCode, data);
                return;
            }
            showLongToast(string(R.string.unrecognized));
        }
    }

    private void scanQRCode() {
        new RxPermissions(currentActivity())
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            ScanQRCodeActivity.actionStart(currentActivity(), REQ_QR_CODE);
                        }
                    }
                });
    }

    private View getTableView(int position, ViewGroup container) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item1, container, false);
        setTableView(contentView, position);
        return contentView;
    }

    private void setTableView(View contentView, int position) {
        contentView.findViewById(R.id.iv_icon).setVisibility(View.GONE);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text2));
    }

}
