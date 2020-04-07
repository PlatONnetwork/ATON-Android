package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.MainContract;
import com.platon.aton.component.ui.presenter.MainPresenter;
import com.platon.aton.component.widget.FragmentTabHost;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.app.Constants;
import com.platon.framework.base.ActivityManager;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.AndroidUtil;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MainActivity extends BaseActivity<MainContract.View, MainPresenter> implements MainContract.View {


    @IntDef({
            MainTab.TAB_ASSETS,
            MainTab.TAB_DELEGATE,
            MainTab.TAB_ME
    })
    public @interface MainTab {
        /**
         * 钱包页签
         */
        int TAB_ASSETS = 0;
        /**
         * 委托页签
         */
        int TAB_DELEGATE = 1;
        /**
         * 我的页签
         */
        int TAB_ME = 2;
    }

    @StringDef({
            MainTabTag.TAG_ASSETS,
            MainTabTag.TAG_DELEGATE,
            MainTabTag.TAG_ME
    })
    private @interface MainTabTag {

        /**
         * 钱包页签tag
         */
        String TAG_ASSETS = "tag_assets";
        /**
         * 委托页签tag
         */
        String TAG_DELEGATE = "tag_delegate";
        /**
         * 我的页签tag
         */
        String TAG_ME = "tag_me";
    }

    public static final int REQ_ASSETS_TAB_QR_CODE = 0x101;
    public static final int REQ_ASSETS_ADDRESS_QR_CODE = 0x102;
    public static final int REQ_ASSETS_SELECT_ADDRESS_BOOK = 0x103;

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;

    private Unbinder unbinder;
    private int mCurIndex = MainTab.TAB_ASSETS;
    private FragmentManager fragmentManager;

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public MainContract.View createView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initViews();
        getPresenter().checkVersion();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == MainActivity.REQ_ASSETS_TAB_QR_CODE
                || requestCode == MainActivity.REQ_ASSETS_ADDRESS_QR_CODE
                || requestCode == MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK
                || requestCode == Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE) {
            fragmentManager.findFragmentByTag(MainTabTag.TAG_ASSETS).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViews() {
        fragmentManager = getSupportFragmentManager();
        tabhost.setup(this, fragmentManager, R.id.realTabContent);
        TabWidget tabWidget = findViewById(android.R.id.tabs);
        tabWidget.setDividerDrawable(null);

        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_ASSETS).setIndicator(getIndicatorView(R.drawable.bg_nav_property, R.string.nav_property)), AssetsFragment2.class, new Bundle());
        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_DELEGATE).setIndicator(getIndicatorView(R.drawable.bg_nav_delegate, R.string.nav_delegate)), DelegateFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_ME).setIndicator(getIndicatorView(R.drawable.bg_nav_me, R.string.nav_me)), MeFragment.class, null);
        tabhost.setCurrentTab(mCurIndex);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case MainTabTag.TAG_ASSETS:
                        ImmersionBar.with(MainActivity.this).keyboardEnable(false).statusBarDarkFont(true, 0.2f).fitsSystemWindows(true).init();
                        break;
                    case MainTabTag.TAG_DELEGATE:
                    case MainTabTag.TAG_ME:
                        ImmersionBar.with(MainActivity.this).keyboardEnable(false).statusBarDarkFont(true, 0.2f).fitsSystemWindows(true).init();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static final long DOUBLE_TIME = 500;
    private static long lastClickTime = 0;

    @Override
    protected void onResume() {
        super.onResume();
        tabhost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastClickTime > DOUBLE_TIME) {
                    if (tabhost.getCurrentTab() != 1) {
                        tabhost.setCurrentTab(1);
                    }
                } else {
                    //发送一个eventbus
                    EventPublisher.getInstance().sendUpdateDelegateTabEvent();
                    EventPublisher.getInstance().sendUpdateValidatorsTabEvent();

                }
                lastClickTime = currentTimeMillis;
            }
        });

    }

    /**
     * 根据tab tag获取tab index
     *
     * @param tabId
     * @return
     */
    private int getIndexByTabId(String tabId) {
        if (MainTabTag.TAG_ASSETS.equals(tabId)) {
            return MainTab.TAB_ASSETS;
        } else if (MainTabTag.TAG_DELEGATE.equals(tabId)) {
            return MainTab.TAB_DELEGATE;
        }
        return MainTab.TAB_ME;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.hasExtra(Constants.Extra.EXTRA_WALLET_INDEX)) {
            mCurIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_INDEX, MainTab.TAB_ASSETS);
        }
        tabhost.setCurrentTab(mCurIndex);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (AndroidUtil.isOutSizeView(v, ev)) {
                hideSoftInput();
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private View getIndicatorView(int drawableResId, int labelResId) {
        LinearLayout rootView = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_main_tab_indicator, tabhost, false);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(0, rootView.getLayoutParams().height, 1.0f));
        TextView textView = rootView.findViewById(R.id.tv_navigation);
        textView.setText(labelResId);
        ImageView imageView = rootView.findViewById(R.id.iv_navigation);
        imageView.setImageResource(drawableResId);
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
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void restart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, int index) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_INDEX, index);
        context.startActivity(intent);
    }

    @Override
    public void exitApp() {
        ActivityManager.getInstance().finishAll();
        Process.killProcess(Process.myPid());
    }
}
