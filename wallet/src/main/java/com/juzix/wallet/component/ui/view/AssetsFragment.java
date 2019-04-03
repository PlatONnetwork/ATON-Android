package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.network.NetState;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.RecycleViewProxyAdapter;
import com.juzix.wallet.component.adapter.TabAdapter;
import com.juzix.wallet.component.adapter.WalletHorizontalRecycleViewAdapter;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.AssetsContract;
import com.juzix.wallet.component.ui.presenter.AssetsPresenter;
import com.juzix.wallet.component.widget.ShadowContainer;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.component.widget.table.SmartTabLayout;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AssetsFragment extends MVPBaseFragment<AssetsPresenter> implements AssetsContract.View {
    public static final int TAB1 = 0;
    public static final int TAB2 = 1;
    public static final int TAB3 = 2;

    Unbinder unbinder;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    //    @BindView(R.id.ctl_bar)
//    CollapsingToolbarLayout ctlBar;
    @BindView(R.id.ll_assets_title)
    LinearLayout llAssetsTitle;
    @BindView(R.id.tv_total_assets_unit)
    TextView tvTotalAssetsUnit;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_total_assets_amount)
    TextView tvTotalAssetsAmount;
    @BindView(R.id.stb_bar)
    SmartTabLayout stbBar;
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
    @BindView(R.id.v_tab_line)
    View vTabLine;

    Unbinder unbinder1;
    private WalletHorizontalRecycleViewAdapter mWalletAdapter;
    private Dialog mPopDialog;
    private int mExpandMarginsTop;
    private int mCollapsMarginsTop;
    private int mExpandIndicatorThickness;
    private int mCollapsIndicatorThickness;
    private int mExpandIndicatorCornerRadius;
    private int mCollapsIndicatorCornerRadius;

    @Override
    protected AssetsPresenter createPresenter() {
        return new AssetsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.start();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_assets, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initView();
        initDimen();
        showAssets();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MainActivity.REQ_ASSETS_TAB_QR_CODE:
                Bundle bundle = data.getExtras();
                String result = bundle.getString(ScanQRCodeActivity.EXTRA_SCAN_QRCODE_DATA);
                if (JZWalletUtil.isValidAddress(result)) {
                    if (vpContent.getVisibility() == View.VISIBLE) {
                        vpContent.setCurrentItem(1);
                        ((TabAdapter) vpContent.getAdapter()).getItem(1).onActivityResult(MainActivity.REQ_ASSETS_ADDRESS_QR_CODE, resultCode, data);
                    } else {
                        AddNewAddressActivity.actionStartWithAddress(getContext(), result);
                    }
                    return;
                }
                if (JZWalletUtil.isValidKeystore(result)) {
                    ImportIndividualWalletActivity.actionStart(currentActivity(), 0, result);
                    return;
                }
                if (JZWalletUtil.isValidMnemonic(result)) {
                    ImportIndividualWalletActivity.actionStart(currentActivity(), 1, result);
                    return;
                }
                if (JZWalletUtil.isValidPrivateKey(result)) {
                    ImportIndividualWalletActivity.actionStart(currentActivity(), 2, result);
                    return;
                }
                showLongToast(currentActivity().string(R.string.unrecognized));
                break;
            case MainActivity.REQ_ASSETS_ADDRESS_QR_CODE:
                if (vpContent.getVisibility() == View.VISIBLE) {
                    ((TabAdapter) vpContent.getAdapter()).getItem(1).onActivityResult(requestCode, resultCode, data);
                }
                break;
            case MainActivity.REQ_ASSETS_SELECT_ADDRESS_BOOK:
                if (vpContent.getVisibility() == View.VISIBLE) {
                    ((TabAdapter) vpContent.getAdapter()).getItem(1).onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().register(this);
        unbinder1.unbind();
    }

    @OnClick({R.id.iv_scan, R.id.tv_total_assets_unit, R.id.tv_backup, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                mPresenter.scanQRCode();
                break;
            case R.id.iv_add:
                showPopWindow();
                break;
            case R.id.tv_total_assets_unit:
                AppSettings.getInstance().setShowAssetsFlag(!AppSettings.getInstance().getShowAssetsFlag());
                showAssets();
                break;
            case R.id.tv_backup:
                mPresenter.backupWallet();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        if (event.netState == NetState.CONNECTED) {
            mPresenter.fetchWalletList();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onUpdateSharedWalletTransactionEvent(Event.UpdateSharedWalletTransactionEvent event) {
//        mPresenter.fetchWalletList();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateCreateJointWalletProgressEvent(Event.UpdateCreateJointWalletProgressEvent event) {
        SharedWalletEntity sharedWalletEntity = event.sharedWalletEntity;
        if (sharedWalletEntity != null) {
            if (sharedWalletEntity.getProgress() == 100) {
                SharedWalletManager.getInstance().updateWalletFinished(sharedWalletEntity.getUuid(), true);
                sharedWalletEntity.updateFinished(true);
            }
        }
    }

    private void initDimen() {
        mExpandMarginsTop = (int) getResources().getDimension(R.dimen.assetsMarginsTop);
        mExpandIndicatorThickness = (int) getResources().getDimension(R.dimen.assetsTabLayoutHeight);
        mExpandIndicatorCornerRadius = mExpandIndicatorThickness / 2;
        mCollapsMarginsTop = 0;
        mCollapsIndicatorThickness = (int) getResources().getDimension(R.dimen.assetsCollapsIndicatorThickness);
        mCollapsIndicatorCornerRadius = mCollapsIndicatorThickness / 2;
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWallet.setLayoutManager(linearLayoutManager);
        mWalletAdapter = new WalletHorizontalRecycleViewAdapter(getContext(), mPresenter.getRecycleViewDataSource());
        mWalletAdapter.setOnItemClickListener(new WalletHorizontalRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onContentViewClick(WalletEntity walletEntity) {
                mPresenter.clickRecycleViewItem(walletEntity);
            }
        });
        RecycleViewProxyAdapter proxyAdapter = new RecycleViewProxyAdapter(mWalletAdapter);
        rvWallet.setAdapter(proxyAdapter);
        proxyAdapter.addFooterView(getCreateWalletView());
        proxyAdapter.addFooterView(getImportWalletView());
        showEmptyView(true);
    }

    private View getCreateWalletView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list1_footer, null);
        ((TextView) view.findViewById(R.id.tv_name)).setText(R.string.createIndividualWallet);
        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(R.drawable.icon_assets_create);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createIndividualWallet();
            }
        });
        return view;
    }

    private View getImportWalletView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list1_footer, null);
        ((TextView) view.findViewById(R.id.tv_name)).setText(R.string.importIndividualWallet);
        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(R.drawable.icon_assets_import);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.importIndividualWallet();
            }
        });
        return view;
    }

    private void showAssets() {
        boolean showAssets = AppSettings.getInstance().getShowAssetsFlag();
        tvTotalAssetsUnit.setCompoundDrawablesWithIntrinsicBounds(0, 0, showAssets ? R.drawable.icon_open_eyes : R.drawable.icon_close_eyes, 0);
        tvTotalAssetsAmount.setTransformationMethod(showAssets ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
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
        titleList.add(R.drawable.assets_tab_transactions2_icon);
        titleList.add(R.drawable.assets_tab_send2_icon);
        titleList.add(R.drawable.assets_tab_receive2_icon);
        return titleList;
    }

    private ArrayList<BaseFragment> getFragments(WalletEntity walletEntity) {
        ArrayList<BaseFragment> list = new ArrayList<>();
        list.add(getFragment(TAB1, walletEntity));
        list.add(getFragment(TAB2, walletEntity));
        list.add(getFragment(TAB3, walletEntity));
        return list;
    }

    public BaseFragment getFragment(int tab, WalletEntity walletEntity) {
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
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_WALLET, walletEntity);
        fragment.setArguments(bundle);

        return fragment;
    }

    private void showPopWindow() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_assets_more, null);
        view.findViewById(R.id.ll_create_individual_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createIndividualWallet();
                dimissPopWindow();
            }
        });
        view.findViewById(R.id.ll_create_shared_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createSharedWallet();
                dimissPopWindow();
            }
        });
        view.findViewById(R.id.ll_import_individual_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.importIndividualWallet();
                dimissPopWindow();
            }
        });
        view.findViewById(R.id.ll_import_shared_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addSharedWallet();
                dimissPopWindow();
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!DensityUtil.isTouchInView(view.findViewById(R.id.ll_item), event) && event.getAction() == MotionEvent.ACTION_UP) {
                    dimissPopWindow();
                }
                return true;
            }
        });
        mPopDialog = new AppCompatDialog(getContext(), R.style.Dialog_FullScreen);
        mPopDialog.setContentView(view);
        mPopDialog.show();
        mPopDialog.setCancelable(true);
    }

    private void dimissPopWindow() {
        if (mPopDialog != null && mPopDialog.isShowing()) {
            mPopDialog.dismiss();
        }
    }

    @Override
    public void showTotalBalance(double totalBalance) {
        tvTotalAssetsAmount.setText(NumberParserUtils.getPrettyBalance(totalBalance));
    }

    @Override
    public void showBalance(double balance) {
        tvWalletAmount.setText(NumberParserUtils.getPrettyBalance(balance));
    }

    @Override
    public void showWalletList(WalletEntity walletEntity) {
        mWalletAdapter.setSelectedWallet(walletEntity);
        mWalletAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWalletTab(WalletEntity walletEntity, int tabIndex) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_WALLET, walletEntity);
        ArrayList<BaseFragment> fragments = getFragments(walletEntity);
        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });
        stbBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentActivity().hideSoftInput();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                vTabLine.setVisibility(Math.abs(i) >= appBarLayout.getTotalScrollRange() ? View.GONE : View.VISIBLE);
                if (i == 0) {
                    //EXPANDED
                } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                    //COLLAPSED
                } else {
                }
            }
        });
        vpContent.setOffscreenPageLimit(fragments.size());
        vpContent.setAdapter(new TabAdapter(getChildFragmentManager(), getTitles(), fragments));
        vpContent.setSlide(true);
        stbBar.setViewPager(vpContent);
        setCollapsTableView(stbBar.getTabAt(tabIndex), tabIndex);
        setCollapsIndicator();
        vpContent.setCurrentItem(tabIndex, true);
    }

    @Override
    public void showWalletInfo(WalletEntity walletEntity) {
        tvBackup.setVisibility(mPresenter.needBackup(walletEntity) ? View.VISIBLE : View.GONE);
        ivWalletAvatar.setImageResource(RUtils.drawable("icon_export_" + walletEntity.getAvatar()));
        tvWalletName.setText(walletEntity.getName());
        showBalance(walletEntity.getBalance());
    }

    @Override
    public void showEmptyView(boolean isEmpty) {
        rlWalletDetail.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        stbBar.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        vpContent.setVisibility(!isEmpty ? View.VISIBLE : View.GONE);
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        scCreateWallet.setVisibility(View.GONE);
        scImportWallet.setVisibility(View.GONE);
        mWalletAdapter.notifyDataSetChanged();
        ((AppBarLayout.LayoutParams) llAssetsTitle.getLayoutParams()).setScrollFlags(isEmpty ? 0 : AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
    }

    @Override
    public void showCurrentItem(int index) {
        vpContent.setCurrentItem(index, true);
    }

    @Override
    public int getTabIndex() {
        return vpContent.getCurrentItem();
    }

    private View getTableView(int position, ViewGroup container) {
        View item = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item1, container, false);
        setCollapsTableView(item, position);
        return item;
    }

    private void setCollapsTableView(View contentView, int position) {
        ImageView ivIcon = contentView.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(getCollapsIcons().get(position));
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text2));
    }

    private void setCollapsIndicator() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) stbBar.getLayoutParams();
        params.topMargin = mCollapsMarginsTop;
        stbBar.setIndicatorCornerRadius(mCollapsIndicatorCornerRadius);
        stbBar.setIndicatorThickness(mCollapsIndicatorThickness);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }
}
