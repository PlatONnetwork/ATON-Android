package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.service.LoopService;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.MainContract;
import com.juzix.wallet.component.ui.presenter.MainPresenter;
import com.juzix.wallet.component.widget.FragmentTabHost;
import com.juzix.wallet.event.EventPublisher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MainActivity extends MVPBaseActivity<MainPresenter> implements MainContract.View {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String TAG_PROPERTY = "property";
    private final static String TAG_VOTE = "vote";
    private final static String TAG_ME = "me";
    private final static String TAG_ASSET = "asset";
    public final static int TAB_PROPERTY = 0;
    public final static int TAB_VOTE = 1;
    public final static int TAB_ME = 2;
    public static final int REQ_ASSETS_TAB_QR_CODE = 0x101;
    public static final int REQ_ASSETS_ADDRESS_QR_CODE = 0x102;
    public static final int REQ_ASSETS_SELECT_ADDRESS_BOOK = 0x103;

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;

    private Unbinder unbinder;
    private int mCurIndex = TAB_PROPERTY;
    public static MainActivity sInstance;
    private FragmentManager fragmentManager;

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
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        sInstance = this;
        mPresenter.checkVersion();
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initViews();
        EventPublisher.getInstance().register(this);
        LoopService.startLoopService(this);
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

        tabhost.addTab(tabhost.newTabSpec(TAG_PROPERTY).setIndicator(getIndicatorView(R.drawable.bg_nav_property, R.string.nav_property)), AssetsFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_VOTE).setIndicator(getIndicatorView(R.drawable.bg_nav_vote, R.string.nav_vote)), VoteFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_ME).setIndicator(getIndicatorView(R.drawable.bg_nav_me, R.string.nav_me)), MeFragment.class, null);
        tabhost.setCurrentTab(mCurIndex);
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurIndex = getCurIndexByTabId(tabId);
            }
        });
    }

    private int getCurIndexByTabId(String tabId) {
        if (TAG_PROPERTY.equals(tabId)) {
            return TAB_PROPERTY;
        } else if (TAG_VOTE.equals(tabId)) {
            return TAB_VOTE;
        }
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
}
