package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.util.LogUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.RecycleViewProxyAdapter;
import com.juzix.wallet.component.adapter.TabAdapter;
import com.juzix.wallet.component.adapter.WalletHorizontalRecycleViewAdapter;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.BaseViewPageFragment;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.AssetsContract;
import com.juzix.wallet.component.ui.dialog.AssetsMoreDialogFragment;

import com.juzix.wallet.component.ui.dialog.CommonGuideDialogFragment;

import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;

import com.juzix.wallet.component.ui.presenter.AssetsPresenter;
import com.juzix.wallet.component.widget.CustomImageSpan;
import com.juzix.wallet.component.widget.ShadowContainer;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.component.widget.table.AssetsTabLayout;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.QrCodeType;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.netlistener.NetStateChangeObserver;
import com.juzix.wallet.netlistener.NetStateChangeReceiver;
import com.juzix.wallet.netlistener.NetworkType;
import com.juzix.wallet.netlistener.NetworkUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import com.juzix.wallet.utils.LanguageUtil;

import com.juzix.wallet.utils.QrCodeParser;

import com.juzix.wallet.utils.StringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.http.HEAD;

/**
 * @author matrixelement
 */
public class AssetsFragment extends MVPBaseFragment<AssetsPresenter> implements AssetsContract.View, NetStateChangeObserver {

    public static final int TAB1 = 0;
    public static final int TAB2 = 1;
    public static final int TAB3 = 2;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.v_tab_line)
    View vTabLine;
    @BindView(R.id.ll_assets_title)
    LinearLayout llAssetsTitle;
    @BindView(R.id.tv_total_assets_title)
    TextView tvTotalAssetsUnit;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_total_assets_amount)
    TextView tvTotalAssetsAmount;
    @BindView(R.id.stb_bar)
    AssetsTabLayout stbBar;
    @BindView(R.id.rv_wallet)
    RecyclerView rvWallet;
    @BindView(R.id.rl_wallet_detail)
    RelativeLayout rlWalletDetail;
    @BindView(R.id.iv_wallet_avatar)
    ImageView ivWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_amount)
    TextView tvWalletAmount;
    @BindView(R.id.tv_backup)
    TextView tvBackup;
    @BindView(R.id.vp_content)
    ViewPagerSlide vpContent;
    @BindView(R.id.layout_empty)
    ConstraintLayout layoutEmpty;
    @BindString(R.string.transactions)
    String transaction;
    @BindString(R.string.action_send_transation)
    String send;
    @BindString(R.string.action_receive_transation)
    String receive;
    @BindView(R.id.sc_import_wallet)
    ShadowContainer scImportWallet;
    @BindView(R.id.sc_create_wallet)
    ShadowContainer scCreateWallet;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;
    @BindView(R.id.tv_restricted_amount)
    TextView tvRestrictedAmount;

    private WalletHorizontalRecycleViewAdapter mWalletAdapter;
    private Unbinder unbinder;
    private TabAdapter mTabAdapter;

    @Override
    protected AssetsPresenter createPresenter() {
        return new AssetsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        Log.debug("AssetsFragment111","===============" + "走这里");
        initIndicator();
        mPresenter.fetchWalletList();
        mPresenter.fetchWalletsBalance();
        //如果切回来是在交易列表页面，则重新轮询交易列表，因为轮询绑定了父Fragment的stop事件
        if (vpContent.getCurrentItem() == TAB1) {
            BaseViewPageFragment viewPageFragment = (BaseViewPageFragment) mTabAdapter.getItem(TAB1);
            if (viewPageFragment != null) {
                viewPageFragment.onPageStart();
            }
        }
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_assets, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        NetStateChangeReceiver.registerReceiver(getContext());
        initViews();
        return rootView;
    }

    private void initViews() {
        initRefreshLayout();
        initHeader();
//        initIndicator();
        initTab();
        showAssets(AppSettings.getInstance().getShowAssetsFlag());
        showContent(true);
        tvTotalAssetsAmount.setText(StringUtil.formatBalance(BigDecimalUtil.div("0", "1E18"))); //设置总计默认值为0
        initGuide();
    }

    private void initGuide() {
        boolean isShowRecord = AppSettings.getInstance().getRecordBoolean();
        boolean isEnglish = Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage()) == true ? false : true;
        if (!isShowRecord) {
            CommonGuideDialogFragment.newInstance(CommonGuideDialogFragment.RECORD, isEnglish).setKnowListener(new CommonGuideDialogFragment.knowListener() {
                @Override
                public void know() {
                    AppSettings.getInstance().setRecordBoolean(true);
                }
            }).show(getChildFragmentManager(), "transactionRecord");
        }

    }

    private void initIndicator() {
        List<BaseFragment> fragments = getFragments(null);
        stbBar.setCustomTabView(new AssetsTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });

        int indicatorThickness = (int) getResources().getDimension(R.dimen.assetsCollapsIndicatorThickness);
        int indicatorCornerRadius = indicatorThickness / 2;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) stbBar.getLayoutParams();
        params.topMargin = 0;
        stbBar.setIndicatorCornerRadius(indicatorCornerRadius);
        stbBar.setIndicatorThickness(indicatorThickness);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                boolean collapsed = Math.abs(i) >= appBarLayout.getTotalScrollRange();
                vTabLine.setVisibility(collapsed ? View.GONE : View.VISIBLE);
                appBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), collapsed ? R.color.color_ffffff : R.color.color_f9fbff));
            }
        });
        vpContent.setOffscreenPageLimit(fragments.size());
        mTabAdapter = new TabAdapter(getChildFragmentManager(), getTitles(), fragments);
        vpContent.setAdapter(mTabAdapter);
        vpContent.setSlide(true);
        stbBar.setViewPager(vpContent);
    }

    private void initRefreshLayout() {

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchWalletList();
                mPresenter.fetchWalletsBalance();
            }
        });
    }

    private void saveStateToArguments(){
        Bundle saveState = saveState();
        LogUtils.e("saveStateToArguments");
        if (saveState != null) {
            Bundle bundle = getArguments(); // 获取之前初始化时候的bundle
            bundle.putBundle(Constants.Extra.EXTRA_BUNDLE, saveState);//将bundle传入数据
        }
    }

    private void reStoreStateFromArguments(){
        Bundle bundle = getArguments().getBundle(Constants.Extra.EXTRA_BUNDLE);
        LogUtils.e("reStoreStateFromArguments");
        if (bundle != null){
            List<Wallet> walletList = bundle.getParcelableArrayList(Constants.Extra.EXTRA_WALLET_LIST);
            LogUtils.e("reStoreStateFromArguments"+walletList.size());
            showContent(walletList == null || walletList.isEmpty());
        }
    }

    private Bundle saveState(){
        Bundle outState = new Bundle();
        List<Wallet> walletList = mPresenter.getRecycleViewDataSource();
        if (walletList != null && walletList.size() > 0) {
            outState.putParcelableArrayList(Constants.Extra.EXTRA_WALLET_LIST, (ArrayList<? extends Parcelable>) walletList);
        }
        return outState;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reStoreStateFromArguments();
    }

    public void fetchWalletsBalance() {
        mPresenter.fetchWalletsBalance();
    }

    public void initTab() {

        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        if ((NetworkUtil.getNetWorkType(getContext()) == NetworkType.NETWORK_NO)) { //没有网络，调起相机界面
                            new RxPermissions(currentActivity())
                                    .request(Manifest.permission.CAMERA)
                                    .subscribe(new CustomObserver<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) {
                                            if (success) {
                                                ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_TAB_QR_CODE);
                                            }
                                        }
                                    });
                        }
                        Log.debug("OnPageChangeListener", "我选中了" + TAB2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        stbBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB1);
                        break;
                    case 1:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB2);
                        break;
                    case 2:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB3);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        NetStateChangeReceiver.registerObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        NetStateChangeReceiver.unRegisterObserver(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MainActivity.REQ_ASSETS_TAB_QR_CODE:
                String result = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);

                @QrCodeType int qrCodeType = QrCodeParser.parseQrCode(result);

                if (qrCodeType == QrCodeType.NONE){
                    showLongToast(currentActivity().string(R.string.unrecognized));
                    return;
                }

                if (qrCodeType == QrCodeType.TRANSACTION_AUTHORIZATION){
                    TransactionAuthorizationDetailActivity.actionStart(currentActivity(), JSONUtil.parseObject(result, TransactionAuthorizationData.class));
                    return;
                }

                if (qrCodeType == QrCodeType.TRANSACTION_SIGNATURE){
                    TransactionSignatureDialogFragment.newInstance(JSONUtil.parseObject(result, TransactionSignatureData.class)).show(getActivity().getSupportFragmentManager(),TransactionSignatureDialogFragment.TAG);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_ADDRESS){
                    if (vpContent.getVisibility() == View.VISIBLE) {
                        vpContent.setCurrentItem(1);
                        ((TabAdapter) vpContent.getAdapter()).getItem(1).onActivityResult(MainActivity.REQ_ASSETS_ADDRESS_QR_CODE, resultCode, data);
                    } else {
                        AddNewAddressActivity.actionStartWithAddress(getContext(), result);
                    }
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_KEYSTORE){
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_KEYSTORE, result);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_MNEMONIC){
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_MNEMONIC, result);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_PRIVATEKEY){
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_PRIVATEKEY, result);
                    return;
                }
                break;
            case MainActivity.REQ_ASSETS_ADDRESS_QR_CODE:
            case MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK:
                if (vpContent.getVisibility() == View.VISIBLE) {
                    ((TabAdapter) vpContent.getAdapter()).getItem(1).onActivityResult(requestCode, resultCode, data);
                }
                break;
            case Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE:
                getActivity().getSupportFragmentManager().findFragmentByTag(TransactionSignatureDialogFragment.TAG).onActivityResult(Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE, resultCode, data);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_scan, R.id.tv_total_assets_title, R.id.tv_backup, R.id.iv_add, R.id.tv_restricted_amount, R.id.rl_wallet_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan://扫一扫功能
                new RxPermissions(currentActivity())
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new CustomObserver<Boolean>() {
                            @Override
                            public void accept(Boolean success) {
                                if (success) {
                                    ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_TAB_QR_CODE);
                                }
                            }
                        });
                break;
            case R.id.iv_add:
                AssetsMoreDialogFragment.newInstance().setOnAssetMoreClickListener(new AssetsMoreDialogFragment.OnAssetMoreClickListener() {
                    @Override
                    public void onCreateWalletClick() {
                        CreateWalletActivity.actionStart(getContext());
                    }

                    @Override
                    public void onImportWalletClick() {
                        ImportWalletActivity.actionStart(getContext());
                    }
                }).show(getChildFragmentManager(), "showAssetsMore");
                break;
            case R.id.tv_total_assets_title:
                AppSettings.getInstance().setShowAssetsFlag(!AppSettings.getInstance().getShowAssetsFlag());
                showAssets(AppSettings.getInstance().getShowAssetsFlag());
                break;
            case R.id.tv_backup:
                mPresenter.backupWallet();
                break;
            case R.id.tv_restricted_amount:
                showLongToast(R.string.restricted_amount_tips);
                break;
            case R.id.rl_wallet_detail:
                ManageIndividualWalletActivity.actionStart(currentActivity(), mWalletAdapter.getSelectedWallet());
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletListOrderChangedEvent(Event.WalletListOrderChangedEvent event) {
        mWalletAdapter.notify();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
        NetStateChangeReceiver.unRegisterReceiver(getContext());
    }

    private String makeFragmentName(long id) {
        return "android:switcher:" + vpContent.getId() + ":" + id;
    }

    private void initHeader(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWallet.setLayoutManager(linearLayoutManager);
        mWalletAdapter = new WalletHorizontalRecycleViewAdapter(getContext(), mPresenter.getRecycleViewDataSource());
        mWalletAdapter.setOnItemClickListener(new WalletHorizontalRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onContentViewClick(Wallet walletEntity) {
                mPresenter.clickRecycleViewItem(walletEntity);
            }
        });
        RecycleViewProxyAdapter proxyAdapter = new RecycleViewProxyAdapter(mWalletAdapter);
        rvWallet.setAdapter(proxyAdapter);
        proxyAdapter.addFooterView(getCreateWalletView());
        proxyAdapter.addFooterView(getImportWalletView());
    }

    private View getCreateWalletView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list_footer, null);
        ((TextView) view.findViewById(R.id.tv_name)).setText(R.string.createIndividualWallet);
        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(R.drawable.icon_assets_create);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateWalletActivity.actionStart(getContext());
            }
        });
        return view;
    }

    private View getImportWalletView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list_footer, null);
        ((TextView) view.findViewById(R.id.tv_name)).setText(R.string.importIndividualWallet);
        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(R.drawable.icon_assets_import);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImportWalletActivity.actionStart(getContext());
            }
        });
        return view;
    }

    private void showAssets(boolean visible) {
        tvTotalAssetsUnit.setCompoundDrawablesWithIntrinsicBounds(0, 0, visible ? R.drawable.icon_open_eyes : R.drawable.icon_close_eyes, 0);
        tvTotalAssetsAmount.setTransformationMethod(visible ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(string(R.string.transactions));
        titleList.add(string(R.string.action_send_transation));
        titleList.add(string(R.string.action_receive_transation));
        return titleList;
    }

    private ArrayList<Integer> getCollapsIcons() {
        ArrayList<Integer> titleList = new ArrayList<>();
        titleList.add(R.drawable.assets_tab_transactions_icon);
        titleList.add(R.drawable.assets_tab_send_icon);
        titleList.add(R.drawable.assets_tab_receive_icon);
        return titleList;
    }

    private List<BaseFragment> getFragments(Wallet walletEntity){
        List<BaseFragment> list = new ArrayList<>();
        list.add(getFragment(TAB1, walletEntity));
        list.add(getFragment(TAB2, walletEntity));
        list.add(getFragment(TAB3, walletEntity));
        return list;
    }

    private BaseFragment getFragment(int tab, Wallet walletEntity) {
        BaseFragment fragment = null;
        switch (tab) {
            case TAB1:
                fragment = new TransactionsFragment();
                break;
            case TAB2:
                fragment = new SendTransactionFragment();
                break;
            case TAB3:
                fragment = new ReceiveTransactionFragment();
                break;
            default:
                break;
        }
        if (walletEntity != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.Extra.EXTRA_WALLET, walletEntity);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void showTotalBalance(String totalBalance) {//显示总资产
        tvTotalAssetsAmount.setText(StringUtil.formatBalance(BigDecimalUtil.div(totalBalance, "1E18")));
    }

    @Override
    public void showFreeBalance(String balance) {//当前钱包的资产

        tvWalletAmount.setText(string(R.string.amount_with_unit, StringUtil.formatBalance(BigDecimalUtil.div(balance, "1E18"))));

        if (vpContent.getCurrentItem() == TAB2) {
            SendTransactionFragment sendTransactionFragment = (SendTransactionFragment) mTabAdapter.getItem(TAB2);
            if (sendTransactionFragment != null) {
                sendTransactionFragment.updateWalletBalance(balance);
            }
        }
    }

    @Override
    public void showLockBalance(String balance) { //当前选中钱包的锁仓金额
        tvRestrictedAmount.setVisibility(BigDecimalUtil.isBiggerThanZero(balance) ? View.VISIBLE : View.GONE);
        tvRestrictedAmount.setText(getRestrictedAmount(string(R.string.restricted_amount_with_unit, StringUtil.formatBalance(BigDecimalUtil.div(balance, "1E18")))));
    }

    @Override
    public void showWalletList(Wallet walletEntity) {
        mWalletAdapter.notifyDataSetChanged(walletEntity);
    }

    @Override
    public void showWalletInfo(Wallet wallet) {
        tvBackup.setVisibility(wallet.isNeedBackup() ? View.VISIBLE : View.GONE);
        int resId = RUtils.drawable(wallet.getExportAvatar());
        if (resId < 0) {
            resId = R.drawable.icon_export_avatar_15;
        }
        ivWalletAvatar.setImageResource(resId);
        tvWalletName.setText(wallet.getName());
        showFreeBalance(wallet.getFreeBalance());
    }

    @Override
    public void showContent(boolean isEmpty) {
        rlWalletDetail.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        stbBar.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        vpContent.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        scCreateWallet.setVisibility(View.GONE);
        scImportWallet.setVisibility(View.GONE);
        AppBarLayout.LayoutParams layoutParams = ((AppBarLayout.LayoutParams) llAssetsTitle.getLayoutParams());
        layoutParams.setScrollFlags(isEmpty ? 0 : AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        mWalletAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCurrentItem(int index) {
        vpContent.setCurrentItem(index, true);
    }

    @Override
    public void setArgument(Wallet entity) {
        List<BaseFragment> fragments = ((TabAdapter) vpContent.getAdapter()).getFragments();
        for (BaseFragment fragment : fragments) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.Extra.EXTRA_WALLET, entity);
            fragment.setArguments(bundle);
        }
    }

    @Override
    public void finishRefresh() {
        layoutRefresh.finishRefresh();
    }

    private SpannableString getRestrictedAmount(String text) {
        SpannableString spannableString = new SpannableString(text);
        CustomImageSpan imageSpan = new CustomImageSpan(getActivity(), R.drawable.icon_restricted_amount);
        int index = TextUtils.indexOf(text, "(");
        if (index != -1) {
            spannableString.setSpan(imageSpan, index + 1, index + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private View getTableView(int position, ViewGroup container) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item1, container, false);
        ImageView ivIcon = contentView.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(getCollapsIcons().get(position));
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text2));
        return contentView;
    }

    @Override
    public void onNetDisconnected() {
        initIndicator();
        android.util.Log.d("AssetsFragment", "网络断开连接");
//        mPresenter.fetchWalletsBalance();
        mPresenter.fetchWalletList();
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        initIndicator();
//        Log.debug("AssetsFragment", "网络连接" + networkType.name());
//        mPresenter.fetchWalletList();
//        mPresenter.fetchWalletsBalance();

    }
}
