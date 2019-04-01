package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzhen.framework.util.crypt.Base64Utils;
import com.juzhen.framework.util.crypt.MD5Utils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.service.LoopService;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.widget.FragmentTabHost;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.config.JZDirType;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.VersionManager;
import com.juzix.wallet.entity.VersionEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.DensityUtil;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class MainActivity extends BaseActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String TAG_PROPERTY = "property";
    private final static String TAG_VOTE = "vote";
    private final static String TAG_ME = "me";
    //    private final static String TAG_ASSETS = "assets";
    public final static int TAB_PROPERTY = 0;
    public final static int TAB_VOTE = 1;
    public final static int TAB_ME = 2;
    //    public final static int TAB_ASSETS = 3;
    public static final int REQ_ASSETS_TAB_QR_CODE = 0x101;
    public static final int REQ_ASSETS_ADDRESS_QR_CODE = 0x102;
    public static final int REQ_ASSETS_SELECT_ADDRESS_BOOK = 0x103;

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    @BindString(R.string.nav_property)
    String property;
    @BindString(R.string.nav_vote)
    String vote;
    @BindString(R.string.nav_me)
    String me;

    View indicatorView1;
    View indicatorView2;
    View indicatorView3;
//    View indicatorView4;

    private Unbinder unbinder;
    private int mCurIndex = TAB_PROPERTY;
    public static MainActivity sInstance;
    public FragmentManager fragmentManager;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, int index, int subIndex) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_INDEX, index);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_SUB_INDEX, subIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        sInstance = this;
        checkVersion();
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initViews();
        EventPublisher.getInstance().register(this);
        //启动轮询服务
        LoopService.startLoopService(this);
//        updateMsgTips(SharedWalletTransactionManager.getInstance().unRead());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == MainActivity.REQ_ASSETS_TAB_QR_CODE
                || requestCode == MainActivity.REQ_ASSETS_ADDRESS_QR_CODE
                || requestCode == MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK) {
            fragmentManager.findFragmentByTag(TAG_PROPERTY).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViews() {

        fragmentManager = getSupportFragmentManager();
        tabhost.setup(this, fragmentManager, R.id.realTabContent);
        TabWidget tabWidget = findViewById(android.R.id.tabs);
        tabWidget.setDividerDrawable(null);
        indicatorView1 = getIndicatorView(TAG_PROPERTY, R.drawable.bg_nav_property, property);
        indicatorView2 = getIndicatorView(TAG_VOTE, R.drawable.bg_nav_vote, vote);
        indicatorView3 = getIndicatorView(TAG_ME, R.drawable.bg_nav_me, me);
//        indicatorView4 = getIndicatorView(TAG_ASSETS, R.drawable.bg_nav_property, property);

        tabhost.addTab(tabhost.newTabSpec(TAG_PROPERTY).setIndicator(indicatorView1), AssetsFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_VOTE).setIndicator(indicatorView2), VoteFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_ME).setIndicator(indicatorView3), MeFragment.class, null);
//        tabhost.addTab(tabhost.newTabSpec(TAG_ASSETS).setIndicator(indicatorView4), AssetsFragment.class, null);
        tabhost.setCurrentTab(mCurIndex);
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurIndex = getCurIndexByTabId(tabId);
            }
        });
    }

    private View getStatusBarView() {
        View view = new View(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }

    private int getCurIndexByTabId(String tabId) {
        if (TAG_PROPERTY.equals(tabId)) {
            return TAB_PROPERTY;
        } else if (TAG_VOTE.equals(tabId)) {
            return TAB_VOTE;
        }
//        else if (TAG_ASSETS.equals(tabId)){
//            return TAB_ASSETS;
//        }
        return TAB_ME;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra(Constants.Extra.EXTRA_WALLET_INDEX)) {
            mCurIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_INDEX, TAB_PROPERTY);
        }
        tabhost.setCurrentTab(mCurIndex);
        int subIndex = -1;
        if (intent.hasExtra(Constants.Extra.EXTRA_WALLET_SUB_INDEX)) {
            subIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_SUB_INDEX, AssetsFragment.TAB1);
        }
        if (mCurIndex == TAB_PROPERTY && subIndex == AssetsFragment.TAB1) {
            AssetsFragment fragment = (AssetsFragment) fragmentManager.findFragmentByTag(TAG_PROPERTY);
            fragment.showCurrentItem(subIndex);
        }

//        int subIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_SUB_INDEX, AssetsFragment.TAB1);
//        mCurIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_INDEX, TAB_PROPERTY);
//        tabhost.setCurrentTab(mCurIndex);
//        if (mCurIndex == TAB_PROPERTY && subIndex == AssetsFragment.TAB1) {
//            AssetsFragment fragment = (AssetsFragment) fragmentManager.findFragmentByTag(TAG_PROPERTY);
//            fragment.showCurrentItem(subIndex);
//
////            PropertyFragment.Page page = PropertyFragment.Page.values()[subIndex];
////            PropertyFragment fragment = (PropertyFragment) fragmentManager.findFragmentByTag(TAG_PROPERTY);
////            fragment.showPageWithUnActive(page);
//        }

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageTipsEvent(Event.UpdateMessageTipsEvent event) {
//        updateMsgTips(event.unRead);
//    }
//
//    private void updateMsgTips(boolean unRead){
//        indicatorView1.findViewById(R.id.v_new_msg).setVisibility(unRead ? View.VISIBLE : View.GONE);
//    }

    private View getIndicatorView(String tag, int drawableResId, String labelResId) {

        LinearLayout rootView = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_main_tab_indicator, tabhost, false);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(0, rootView.getLayoutParams().height, 1.0f));

        TextView textView = rootView.findViewById(R.id.tv_navigation);
        textView.setText(labelResId);
        ImageView imageView = rootView.findViewById(R.id.iv_navigation);
        imageView.setImageResource(drawableResId);
//        textView.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, drawableResId), null, null);
//        textView.setCompoundDrawablePadding(DensityUtil.dp2px(this, 4));

        return rootView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        LoopService.quitLoopService(this);
        sInstance = null;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void restart(Context context) {
        ((Activity) context).finish();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void checkVersion() {
        long lastUpdateTime = AppSettings.getInstance().getUpdateVersionTime();
        if (lastUpdateTime != 0 && DateUtil.isToday(lastUpdateTime)) {
            return;
        }
        VersionManager.getInstance().getVersion()
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VersionEntity>() {
                    @Override
                    public void accept(VersionEntity versionEntity) {
                        String newVersion = versionEntity.getVersion().toLowerCase();
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (newVersion.startsWith("v") && !oldVersion.startsWith("v")) {
                            oldVersion = "v" + oldVersion;
                        } else if (!newVersion.startsWith("v") && oldVersion.startsWith("v")) {
                            newVersion = "v" + newVersion;
                        }
                        if (oldVersion.compareTo(newVersion) < 0) {
                            AppSettings.getInstance().setUpdateVersionTime(System.currentTimeMillis());
                            CommonTipsDialogFragment.createDialogWithTitleAndTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                                    string(R.string.version_update),
                                    string(R.string.version_update_tips, newVersion),
                                    string(R.string.update_now), new OnDialogViewClickListener() {
                                        @Override
                                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                            if (fragment != null) {
                                                fragment.dismiss();
                                            }
                                            requestPermission(currentActivity(), 100, new PermissionConfigure.PermissionCallback() {
                                                @Override
                                                public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                                                    download(versionEntity.getDownloadUrl());
                                                }

                                                @Override
                                                public void onHasPermission(int what) {
                                                    download(versionEntity.getDownloadUrl());
                                                }

                                                @Override
                                                public void onFail(int what, @NonNull List<String> deniedPermissions) {

                                                }
                                            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

                                        }
                                    },
                                    string(R.string.not_now), new OnDialogViewClickListener() {
                                        @Override
                                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                            if (fragment != null) {
                                                fragment.dismiss();
                                            }
                                        }
                                    }).show(getSupportFragmentManager(), "showTips");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    private void download(String url) {
        JZAppConfigure.getInstance().getDir(currentActivity(), JZDirType.raw, new JZAppConfigure.DirCallback() {
            @Override
            public void callback(File dir) {
                if (dir == null) {
                    return;
                }
                VersionManager.getInstance().download(url, dir, new String(Base64Utils.encodeToString(MD5Utils.encode(url))) + ".apk");
            }
        });

    }
}
