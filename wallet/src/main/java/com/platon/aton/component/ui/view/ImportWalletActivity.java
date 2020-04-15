package com.platon.aton.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.ViewPagerSlide;
import com.platon.aton.component.widget.table.PagerItem;
import com.platon.aton.component.widget.table.PagerItemAdapter;
import com.platon.aton.component.widget.table.PagerItems;
import com.platon.aton.component.widget.table.SmartTabLayout;
import com.platon.aton.entity.GuideType;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BaseFragment;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.AndroidUtil;
import com.platon.framework.utils.PreferenceTool;
import com.platon.framework.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

public class ImportWalletActivity extends BaseActivity {

    public static final int REQ_QR_CODE = 101;

    private ViewPagerSlide mVpContent;

    private @TabIndex
    int mTabIndex = TabIndex.IMPORT_KEYSTORE;

    @IntDef({
            TabIndex.IMPORT_KEYSTORE,
            TabIndex.IMPORT_MNEMONIC,
            TabIndex.IMPORT_PRIVATEKEY,
            TabIndex.IMPORT_OBSERVED
    })
    @interface TabIndex {
        int IMPORT_KEYSTORE = 0;
        int IMPORT_MNEMONIC = 1;
        int IMPORT_PRIVATEKEY = 2;
        int IMPORT_OBSERVED = 3;
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, ImportWalletActivity.class));
    }

    public static void actionStart(Context context, @TabIndex int tabIndex, String content) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TAB_INDEX, tabIndex);
        intent.putExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, content);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init() {
        initView();
        initGuide();
    }

    private void initGuide() {

        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_OBSERVED_WALLET, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.IMPORT_WALLET)
                    .setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                        @Override
                        public void onDismiss() {
                            PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_OBSERVED_WALLET, true);
                        }
                    })
                    .show(getSupportFragmentManager(), "showGuideDialogFragment");
        }

    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initView() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mTabIndex = bundle.getInt(Constants.Extra.EXTRA_TAB_INDEX, TabIndex.IMPORT_KEYSTORE);
        }

        CommonTitleBar commonTitleBar = findViewById(R.id.commonTitleBar);
        commonTitleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RxPermissions(currentActivity())
                        .requestEach(Manifest.permission.CAMERA)
                        .subscribe(new CustomObserver<Permission>() {
                            @Override
                            public void accept(Permission permission) {
                                if (permission.granted) {
                                    ScanQRCodeActivity.startActivityForResult(currentActivity(), REQ_QR_CODE);
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    // Denied permission without ask never again
                                } else {
                                    showLongToast("使用该功能需要拍照和SD卡存储权限，请前往系统设置开启权限");
                                }
                            }
                        });
            }
        });
        int indicatorThickness = AndroidUtil.dip2px(getContext(), 2.0f);
        SmartTabLayout stbBar = getContentView().findViewById(R.id.stb_bar);
        stbBar.setIndicatorThickness(indicatorThickness);
        stbBar.setIndicatorCornerRadius((float) indicatorThickness / 2);
        ArrayList<Class<? extends BaseFragment>> fragments = getFragments();

        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });
        PagerItems pages = new PagerItems(getContext());
        int tabNum = fragments.size();
        for (int i = 0; i < tabNum; i++) {
            if (i == mTabIndex) {
                pages.add(PagerItem.of(getTitles().get(i), fragments.get(i), bundle == null ? new Bundle() : bundle));
            } else {
                pages.add(PagerItem.of(getTitles().get(i), fragments.get(i), new Bundle()));
            }
        }
        mVpContent = getContentView().findViewById(R.id.vp_content);
        mVpContent.setSlide(true);
        mVpContent.setOffscreenPageLimit(fragments.size());
        mVpContent.setAdapter(new PagerItemAdapter(getSupportFragmentManager(), pages));
        stbBar.setViewPager(mVpContent);
        setTableView(stbBar.getTabAt(mTabIndex), mTabIndex);
        mVpContent.setCurrentItem(mTabIndex);
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.keystore));
        titleList.add(getString(R.string.mnemonicPhrase));
        titleList.add(getString(R.string.privateKey));
        titleList.add(getString(R.string.observed));
        return titleList;
    }

    private ArrayList<Class<? extends BaseFragment>> getFragments() {
        ArrayList<Class<? extends BaseFragment>> list = new ArrayList<>();
        list.add(ImportKeystoreFragment.class);
        list.add(ImportMnemonicPhraseFragment.class);
        list.add(ImportPrivateKeyFragment.class);
        list.add(ImportObservedFragment.class);
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
            String unzip = GZipUtil.unCompress(scanResult);
            if (TextUtils.isEmpty(unzip) && TextUtils.isEmpty(scanResult)) {
                ToastUtil.showLongToast(getContext(), R.string.unrecognized_content);
                return;
            }
            String newStr = TextUtils.isEmpty(unzip) ? scanResult : unzip;
            if (JZWalletUtil.isValidKeystore(newStr)) {
                mVpContent.setCurrentItem(0);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(TabIndex.IMPORT_KEYSTORE).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidMnemonic(newStr)) {
                mVpContent.setCurrentItem(1);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(TabIndex.IMPORT_MNEMONIC).onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (JZWalletUtil.isValidPrivateKey(newStr)) {
                mVpContent.setCurrentItem(2);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(TabIndex.IMPORT_PRIVATEKEY).onActivityResult(requestCode, resultCode, data);
                return;
            }
            //新增导入观察钱包的判断
            if (JZWalletUtil.isValidAddress(newStr)) {
                mVpContent.setCurrentItem(3);
                ((PagerItemAdapter) mVpContent.getAdapter()).getPage(TabIndex.IMPORT_OBSERVED).onActivityResult(requestCode, resultCode, data);
                return;
            }
            showLongToast(string(R.string.unrecognized));
        }
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
