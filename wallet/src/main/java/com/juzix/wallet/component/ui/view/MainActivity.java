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
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.FragmentTabHost;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.SystemManager;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MainActivity extends BaseActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String TAG_PROPERTY = "property";
    private final static String TAG_ME = "me";
    public final static int TAB_PROPERTY = 0;
    public final static int TAB_ME = 1;

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    @BindString(R.string.nav_property)
    String property;
    @BindString(R.string.nav_me)
    String me;

    View indicatorView1;
    View indicatorView2;

    private Unbinder           unbinder;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sInstance = this;
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initViews();
        EventPublisher.getInstance().register(this);
        updateMsgTips(SharedWalletTransactionManager.getInstance().unRead());
    }

    private void initViews() {
        fragmentManager = getSupportFragmentManager();
        tabhost.setup(this, fragmentManager, R.id.realTabContent);
        TabWidget tabWidget = findViewById(android.R.id.tabs);
        tabWidget.setDividerDrawable(null);
        indicatorView1 = getIndicatorView(TAG_PROPERTY, R.drawable.bg_nav_property, property);
        indicatorView2 = getIndicatorView(TAG_ME, R.drawable.bg_nav_me, me);

        tabhost.addTab(tabhost.newTabSpec(TAG_PROPERTY).setIndicator(indicatorView1), PropertyFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_ME).setIndicator(indicatorView2), MeFragment.class, null);

        tabhost.setCurrentTab(mCurIndex);
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurIndex = getCurIndexByTabId(tabId);
            }
        });
    }

    private int getCurIndexByTabId(String tabId) {
        return TAG_PROPERTY.equals(tabId) ? TAB_PROPERTY : TAB_ME;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int subIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_SUB_INDEX, PropertyFragment.TAB_INDIVIDUAL);
        mCurIndex = intent.getIntExtra(Constants.Extra.EXTRA_WALLET_INDEX, TAB_PROPERTY);
        tabhost.setCurrentTab(mCurIndex);

        if (mCurIndex == TAB_PROPERTY && subIndex == PropertyFragment.TAB_SHARED) {
            PropertyFragment.Page page = PropertyFragment.Page.values()[subIndex];
            PropertyFragment fragment = (PropertyFragment) fragmentManager.findFragmentByTag(TAG_PROPERTY);
            fragment.showPageWithUnActive(page);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageTipsEvent(Event.UpdateMessageTipsEvent event) {
        updateMsgTips(event.unRead);
    }

    private void updateMsgTips(boolean unRead){
        indicatorView1.findViewById(R.id.v_new_msg).setVisibility(unRead ? View.VISIBLE : View.GONE);
    }

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
        SystemManager.getInstance().stop();
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
