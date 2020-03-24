package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.component.adapter.DelegateDetailAdapter;
import com.platon.aton.component.adapter.DelegateItemInfoDiffCallback;
import com.platon.aton.component.ui.contract.DelegateDetailContract;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.ui.presenter.DelegateDetailPresenter;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.CustomRefreshHeader;
import com.platon.aton.config.AppSettings;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegateInfo;
import com.platon.aton.entity.GuideType;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 委托详情activity
 */
public class DelegateDetailActivity extends MVPBaseActivity<DelegateDetailPresenter> implements DelegateDetailContract.View {

    @BindView(R.id.civ_wallet_avatar)
    CircleImageView civWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_avaliable_balance_amount)
    TextView tvAvaliableBalanceAmount;
    @BindView(R.id.tv_total_delegated_amount)
    TextView tvTotalDelegatedAmount;
    @BindView(R.id.rlv_list)
    RecyclerView rlvList;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private Unbinder unbinder;
    private DelegateDetailAdapter mDetailAdapter;

    @Override
    protected DelegateDetailPresenter createPresenter() {
        return new DelegateDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate_detail);
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }

    private void initView() {

        mDetailAdapter = new DelegateDetailAdapter();
        rlvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rlvList.setAdapter(mDetailAdapter);

        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadDelegateDetailData();
            }
        });

        initGuide();
    }

    private void initGuide() {
        if (!AppSettings.getInstance().getDelegateDetailBoolean()) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE_NODE_DETAIL).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    AppSettings.getInstance().setDelegateDetailBoolean(true);
                }
            }).show(getSupportFragmentManager(), "showGuideDialogFragment");
        }
    }


    @Override
    public void showWalletInfo(DelegateInfo delegateInfo) {
        civWalletAvatar.setImageResource(RUtils.drawable(delegateInfo.getWalletIcon()));
        tvWalletName.setText(delegateInfo.getWalletName());
        tvWalletAddress.setText(AddressFormatUtil.formatAddress(delegateInfo.getWalletAddress()));
    }

    @Override
    public void showDelegateDetailData(List<DelegateItemInfo> oldDelegateItemInfoList, List<DelegateItemInfo> newDelegateItemInfoList) {
        refreshLayout.finishRefresh();
        llNoData.setVisibility(newDelegateItemInfoList == null || newDelegateItemInfoList.isEmpty() ? View.VISIBLE : View.GONE);
        mDetailAdapter.setDatas(newDelegateItemInfoList);
        if (newDelegateItemInfoList != null && !newDelegateItemInfoList.isEmpty()) {
            DelegateItemInfoDiffCallback diffCallback = new DelegateItemInfoDiffCallback(oldDelegateItemInfoList, newDelegateItemInfoList);
            DiffUtil.calculateDiff(diffCallback, true).dispatchUpdatesTo(mDetailAdapter);
        } else {
            mDetailAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showWalletDelegatedInfo(String availableDelegationBalance, String totalDelegatedAmount) {
        tvAvaliableBalanceAmount.setText(AmountUtil.formatAmountText(availableDelegationBalance));
        tvTotalDelegatedAmount.setText(AmountUtil.formatAmountText(totalDelegatedAmount));
    }

    //是否可以进入委托页面进行委托
    @Override
    public void showIsCanDelegate(String nodeAddress, String nodeName, String nodeIcon, String walletAddress, boolean isCanDelegate) {
//        if (!isCanDelegate) {//表示不能委托
//            ToastUtil.showLongToast(getContext(), R.string.tips_no_wallet);
//        } else {
//            DelegateNodeDetail delegateDetail = new DelegateNodeDetail();
//            delegateDetail.setNodeId(nodeAddress);
//            delegateDetail.setNodeName(nodeName);
//            delegateDetail.setUrl(nodeIcon);
//            delegateDetail.setWalletAddress(walletAddress);
//            DelegateActivity.actionStart(getContext(), delegateDetail);
//        }
    }

    @Override
    public DelegateInfo getDelegateInfoFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_DELEGATE_INFO);
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    /**
     * event事件，刷新的操作
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDelegateDetailPageEvent(Event.UpdateDelegateDetailEvent event) {
        //刷新页面
        mPresenter.loadDelegateDetailData();
    }

    //赎回成功，返回，刷新页面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPageEvent(Event.UpdateRefreshPageEvent event) {
        mPresenter.loadDelegateDetailData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, DelegateInfo delegateInfo) {
        Intent intent = new Intent(context, DelegateDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_INFO, delegateInfo);
        context.startActivity(intent);
    }
}
