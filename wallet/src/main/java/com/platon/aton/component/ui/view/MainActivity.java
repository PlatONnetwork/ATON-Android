package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.SidebarWalletListAdapter;
import com.platon.aton.component.adapter.base.CommonSidebarItemDecoration2;
import com.platon.aton.component.ui.contract.MainContract;
import com.platon.aton.component.ui.presenter.MainPresenter;
import com.platon.aton.component.widget.FragmentTabHost;
import com.platon.aton.entity.MainTab;
import com.platon.aton.entity.MainTabTag;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.ActivityManager;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.AndroidUtil;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class MainActivity extends BaseActivity<MainContract.View, MainPresenter>  implements MainContract.View {

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.layout_drawer)
    DrawerLayout layoutDrawer;
    @BindView(R.id.btn_all)
    RadioButton btnAll;
    @BindView(R.id.btn_hd)
    RadioButton btnHd;
    @BindView(R.id.btn_ordinary)
    RadioButton btnOrdinary;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.layout_tab)
    LinearLayout layoutTab;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_hide)
    TextView ivHide;
    @BindView(R.id.layout_search)
    ConstraintLayout layoutSearch;
    @BindView(R.id.list_wallet)
    RecyclerView listWallet;
    @BindView(R.id.tv_no_wallet)
    TextView tvNoWallet;
    @BindView(R.id.layout_no_wallet)
    RelativeLayout layoutNoWallet;


    public static final int REQ_ASSETS_TAB_QR_CODE = 0x101;
    public static final int REQ_ASSETS_ADDRESS_QR_CODE = 0x102;
    public static final int REQ_ASSETS_SELECT_ADDRESS_BOOK = 0x103;

    private Unbinder unbinder;
    private int mCurIndex = MainTab.TAB_ASSETS;
    private FragmentManager fragmentManager;
    private SidebarWalletListAdapter mSidebarWalletListAdapter;
    private CommonSidebarItemDecoration2 itemDecoration;
    private @WalletTypeSearch int walletTypeSearch = WalletTypeSearch.WALLET_ALL;

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
        getPresenter().checkVersion();
        getPresenter().loadData(WalletTypeSearch.WALLET_ALL,etSearch.getText().toString().trim());
        initViews();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenRightSidebarEvent(Event.OpenRightSidebarEvent event) {
        Wallet wallet = event.wallet;
        int toWalletTypeSearch = event.walletTypeSearch;

        if(toWalletTypeSearch == WalletTypeSearch.WALLET_ALL){

            btnAll.setChecked(true);
            layoutDrawer.openDrawer(GravityCompat.END);
            getPresenter().loadData(WalletTypeSearch.WALLET_ALL,etSearch.getText().toString().trim());
        }else if(toWalletTypeSearch == WalletTypeSearch.HD_WALLET){

            btnHd.setChecked(true);
            layoutDrawer.openDrawer(GravityCompat.END);
            getPresenter().loadData(toWalletTypeSearch,etSearch.getText().toString().trim());
        }else{
            getPresenter().loadData(walletTypeSearch,etSearch.getText().toString().trim());
        }
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

        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_ASSETS).setIndicator(getIndicatorView(R.drawable.bg_nav_property, R.string.nav_property)), AssetsFragment.class, new Bundle());
        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_DELEGATE).setIndicator(getIndicatorView(R.drawable.bg_nav_delegate, R.string.nav_delegate)), DelegateFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(MainTabTag.TAG_ME).setIndicator(getIndicatorView(R.drawable.bg_nav_me, R.string.nav_me)), MeFragment.class, null);
        tabhost.setCurrentTab(mCurIndex);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                viewStatusBar.setVisibility(TextUtils.equals(tabId, MainTabTag.TAG_ASSETS) ? View.VISIBLE : View.GONE);
            }
        });


        //加载RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listWallet.setLayoutManager(linearLayoutManager);
        mSidebarWalletListAdapter = new SidebarWalletListAdapter(getPresenter().getDataSource(),getContext());
        itemDecoration = new CommonSidebarItemDecoration2(getContext(),getPresenter().getDataSource(),2);
        listWallet.setAdapter(mSidebarWalletListAdapter);
        listWallet.addItemDecoration(itemDecoration);
        mSidebarWalletListAdapter.setOnSelectClickListener(new SidebarWalletListAdapter.OnSelectClickListener(){

            @Override
            public void onItemClick(int position) {

               Wallet selectedWallet = getPresenter().getDataSource().get(position);
               getPresenter().updateSelectedWalletnotifyData(selectedWallet);
               //关闭侧滑栏
               layoutDrawer.closeDrawer(GravityCompat.END);
            }
        });


        //搜索
        RxView
                .clicks(ivSearch)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        layoutSearch.setVisibility(View.VISIBLE);
                    }
                });

        RxView
                .clicks(ivHide)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        layoutSearch.setVisibility(View.GONE);
                    }
                });

        //选择钱包tab切换
        RxRadioGroup
                .checkedChanges(radioGroup)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer tabId) throws Exception {
                        tabStateChangedLoadData(getTabById(tabId));
                    }
                });

        RxTextView
                .editorActionEvents(etSearch, new Predicate<TextViewEditorActionEvent>() {
                    @Override
                    public boolean test(TextViewEditorActionEvent textViewEditorActionEvent) throws Exception {
                        return textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH;
                    }
                })
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<TextViewEditorActionEvent>() {
                    @Override
                    public void accept(TextViewEditorActionEvent textViewEditorActionEvent) {
                        String searchStr =  etSearch.getText().toString().trim();
                        getPresenter().loadData(walletTypeSearch,(TextUtils.isEmpty(searchStr) ? "NULL" : searchStr));
                    }
                });

    }


    public void tabStateChangedLoadData(@WalletTypeSearch int walletTypeSearch){

        this.walletTypeSearch = walletTypeSearch;
        getPresenter().loadData(walletTypeSearch,etSearch.getText().toString().trim());
    }

    int getTabById(int id) {
        switch (id) {
            case R.id.btn_all:
                return WalletTypeSearch.WALLET_ALL;
            case R.id.btn_hd:
                return WalletTypeSearch.HD_WALLET;
            default:
                return WalletTypeSearch.ORDINARY_WALLET;
        }
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

    @Override
    public void notifyDataSetChanged() {
        if(mSidebarWalletListAdapter != null){
            itemDecoration.setDataSource(getPresenter().getDataSource());
            mSidebarWalletListAdapter.notifyDataSetChanged();

        }
    }



    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }
}
