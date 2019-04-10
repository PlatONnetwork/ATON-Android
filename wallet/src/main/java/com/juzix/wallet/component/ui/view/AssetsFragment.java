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
    private WalletHorizontalRecycleViewAdapter mWalletAdapter;
    private Dialog mPopDialog;

    @Override
    protected AssetsPresenter createPresenter() {
        return new AssetsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.fetchWalletList();
        mPresenter.start();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_assets, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initHeader();
        initTab();
        showAssets(AppSettings.getInstance().getShowAssetsFlag());
        showEmptyView(true);
        mPresenter.init();
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
        EventPublisher.getInstance().unRegister(this);
    }

    @OnClick({R.id.iv_scan, R.id.tv_total_assets_title, R.id.tv_backup, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                mPresenter.scanQRCode();
                break;
            case R.id.iv_add:
                showPopWindow();
                break;
            case R.id.tv_total_assets_title:
                AppSettings.getInstance().setShowAssetsFlag(!AppSettings.getInstance().getShowAssetsFlag());
                showAssets(AppSettings.getInstance().getShowAssetsFlag());
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
            mPresenter.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateCreateJointWalletProgressEvent(Event.UpdateCreateJointWalletProgressEvent event) {
        mPresenter.updateCreateJointWallet(event.sharedWalletEntity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSharedWalletUnreadMessageEvent(Event.UpdateSharedWalletUnreadMessageEvent event) {
        mPresenter.updateUnreadMessage(event.contractAddress, event.hasUnreadMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateWalletListEvent(Event.UpdateWalletListEvent event) {
        mPresenter.fetchWalletList();
        mPresenter.start();
    }

    private void initHeader() {
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
    }

    public void initTab() {
        ArrayList<BaseFragment> fragments = getFragments(null);
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
                switch (position){
                    case 0:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB1);
                        break;
                    case 1:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB2);
                        break;
                    case 2:
                        EventPublisher.getInstance().sendUpdateAssetsTabEvent(TAB3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
        vpContent.setAdapter(new TabAdapter(getChildFragmentManager(), getTitles(), fragments));
        vpContent.setSlide(true);
        stbBar.setViewPager(vpContent);
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

    private BaseFragment getFragment(int tab, WalletEntity walletEntity) {
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
        if (walletEntity != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.Extra.EXTRA_WALLET, walletEntity);
            fragment.setArguments(bundle);
        }
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
        tvTotalAssetsAmount.setText(totalBalance > 0 ? NumberParserUtils.getPrettyBalance(totalBalance) : "0.00");
    }

    @Override
    public void showBalance(double balance) {
        tvWalletAmount.setText(balance > 0 ? NumberParserUtils.getPrettyBalance(balance) : "0.00");
    }

    @Override
    public void showWalletList(WalletEntity walletEntity) {
        mWalletAdapter.setSelectedWallet(walletEntity);
        mWalletAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWalletInfo(WalletEntity walletEntity) {
        tvBackup.setVisibility(mPresenter.needBackup(walletEntity) ? View.VISIBLE : View.GONE);
        int resId = RUtils.drawable(walletEntity.getExportAvatar());
        if (resId < 0){
            resId = R.drawable.icon_export_avatar_15;
        }
        ivWalletAvatar.setImageResource(resId);
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
        ((AppBarLayout.LayoutParams) llAssetsTitle.getLayoutParams()).setScrollFlags(isEmpty ? 0 : AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
    }

    @Override
    public void showCurrentItem(int index) {
        vpContent.setCurrentItem(index, true);
    }

    @Override
    public void setArgument(WalletEntity entity) {
        ArrayList<BaseFragment> fragments = ((TabAdapter) vpContent.getAdapter()).getFragments();
        for (BaseFragment fragment : fragments) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.Extra.EXTRA_WALLET, entity);
            fragment.setArguments(bundle);
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        mWalletAdapter.notifyItemChanged(position);
    }

    @Override
    public void notifyAllChanged() {
        mWalletAdapter.notifyDataSetChanged();
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
}
