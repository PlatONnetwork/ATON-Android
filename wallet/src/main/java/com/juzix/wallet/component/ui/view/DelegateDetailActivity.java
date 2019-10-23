package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.DelegateDetailAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.component.ui.dialog.BaseDialogFragment;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.dialog.CommonGuideDialogFragment;
import com.juzix.wallet.component.ui.presenter.DelegateDetailPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.GuideType;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.ToastUtil;
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

    private Unbinder unbinder;

    @BindView(R.id.commonTitleBar)
    CommonTitleBar titleBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_wallet_icon)
    CircleImageView circleImageView;
    @BindView(R.id.tv_wallet_name)
    TextView tv_wallet_name;
    @BindView(R.id.tv_wallet_address)
    TextView tv_wallet_address;
    @BindView(R.id.rlv_list)
    RecyclerView rlv_list;

    private DelegateDetailAdapter mDetailAdapter;
    private LinearLayoutManager linearLayoutManager;

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
        mPresenter.loadDelegateDetailData();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadDelegateDetailData();
            }
        });

        mDetailAdapter.setOnDelegateClickListener(new DelegateDetailAdapter.OnDelegateClickListener() {

            @Override
            public void onDelegateClick(DelegateDetail delegateDetail) {
                DelegateActivity.actionStart(getContext(), delegateDetail);
            }

            @Override
            public void onWithDrawClick(DelegateDetail delegateDetail) {
                WithDrawActivity.actionStart(getContext(), delegateDetail);
            }

            @Override
            public void onLinkClick(String webSiteUrl) {
                CommonHybridActivity.actionStart(getContext(), webSiteUrl, WebType.WEB_TYPE_NODE_DETAIL);
            }
        });

    }

    private void initView() {
        showLoadingDialog();
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlv_list.setLayoutManager(linearLayoutManager);
        mDetailAdapter = new DelegateDetailAdapter();
        rlv_list.setAdapter(mDetailAdapter);


        titleBar.setRightDrawable(R.drawable.icon_tips);
        titleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出tips
//               new DelegateTipsDialog().show(getSupportFragmentManager(),"delegateTips");
                DelegateTipsDialog.createWithTitleAndContentDialog(string(R.string.detail_wait_undelegate), string(R.string.detail_tips_content),
                        "", "", "", "")
                        .show(getSupportFragmentManager(), "delegateTips");
            }
        });

        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setEnableRefresh(true);
//        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(false);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的
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
        if (delegateInfo != null) {
            circleImageView.setImageResource(RUtils.drawable(delegateInfo.getWalletIcon()));
            tv_wallet_name.setText(delegateInfo.getWalletName());
            tv_wallet_address.setText(AddressFormatUtil.formatAddress(delegateInfo.getWalletAddress()));
        }
    }

    @Override
    public void showDelegateDetailData(List<DelegateDetail> detailList) {
        mDetailAdapter.notifyDataChanged(detailList);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        dismissLoadingDialogImmediately();

    }

    @Override
    public void showDelegateDetailFailed() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        dismissLoadingDialogImmediately();
    }

    //是否可以进入委托页面进行委托
    @Override
    public void showIsCanDelegate(String nodeAddress, String nodeName, String nodeIcon, String walletAddress, boolean isCanDelegate) {
        if (!isCanDelegate) {//表示不能委托
            ToastUtil.showLongToast(getContext(), R.string.tips_no_wallet);
        } else {
            DelegateDetail delegateDetail = new DelegateDetail();
            delegateDetail.setNodeId(nodeAddress);
            delegateDetail.setNodeName(nodeName);
            delegateDetail.setUrl(nodeIcon);
            delegateDetail.setWalletAddress(walletAddress);
            DelegateActivity.actionStart(getContext(), delegateDetail);
        }
    }

    @Override
    public DelegateInfo getDelegateInfoFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_DELEGATE_INFO);
    }

    public static void actionStart(Context context, DelegateInfo delegateInfo) {
        Intent intent = new Intent(context, DelegateDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_INFO, delegateInfo);
        context.startActivity(intent);
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
}
