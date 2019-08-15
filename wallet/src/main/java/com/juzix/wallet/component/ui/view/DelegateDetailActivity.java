package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.DelegateDetailAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.presenter.DelegateDetailPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
//    public boolean isLoadMore = false;
//    public String beginSequence = "-1";//加载更多需要传入的值
//    private List<DelegateDetail> list = new ArrayList<>();
    private DelegateDetailAdapter mDetailAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String walletAddress;

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

        mPresenter.loadDelegateDetailData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                isLoadMore = false;
                mPresenter.loadDelegateDetailData();
            }
        });

//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                isLoadMore = true;
//                mPresenter.loadDelegateDetailData(beginSequence);
//
//            }
//        });

        mDetailAdapter.setmOnDelegateClickListener(new DelegateDetailAdapter.OnDelegateClickListener() {
            @Override
            public void onDelegateClick(String nodeAddress, String nodeName, String nodeIcon) {
                //操作委托
                DelegateActivity.actionStart(getContext(), nodeAddress, nodeName, nodeIcon, 0);
            }

            @Override
            public void onWithDrawClick(String nodeAddress, String nodeName, String nodeIcon, String blockNum) {
                //跳转赎回委托页面
                WithDrawActivity.actionStart(getContext(), nodeAddress, nodeName, nodeIcon, blockNum, walletAddress);
            }

            @Override
            public void onMoveOutClick(DelegateDetail detail) {
                //操作移除列表
                mPresenter.MoveOut(detail);
            }

            @Override
            public void onLinkClick(String webSiteUrl) {
                //todo 操作链接跳转(暂时没链接)
            }
        });

    }

    private void initView() {
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
                DelegateTipsDialog.createWithTitleAndContentDialog(string(R.string.locked_delegate), string(R.string.locked_delegate_des), string(R.string.unlocked_delegate),
                        string(R.string.unlocked_delegate_des), string(R.string.released_delegate), string(R.string.released_delegate_des))
                        .show(getSupportFragmentManager(), "delegateTips");
            }
        });

        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setEnableRefresh(true);
//        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(false);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的

    }


    @Override
    public String getWalletNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_WALLET_NAME);
    }

    @Override
    public String getWalletAddressFromIntent() {
        walletAddress = getIntent().getStringExtra(Constants.Extra.EXTRA_WALLET_ADDRESS);
        return walletAddress;
    }

    @Override
    public String getWalletIconFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_WALLET_ICON);
    }

    @Override
    public void showWalletInfo(String walletAddress, String walletName, String walletIcon) {
        circleImageView.setImageResource(RUtils.drawable(walletIcon));
        tv_wallet_name.setText(walletName);
        tv_wallet_address.setText(AddressFormatUtil.formatAddress(walletAddress));
    }

    @Override
    public void showDelegateDetailData(List<DelegateDetail> detailList) {
//        if (detailList.size() > 0) {
//            beginSequence = detailList.get(detailList.size() - 1).getSequence();
//        }
//
//        if (isLoadMore) {
//            list.addAll(detailList);
//        } else {
//            list.clear();
//            list.addAll(detailList);
//        }

        mDetailAdapter.notifyDataChanged(detailList);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();

    }

    @Override
    public void showDelegateDetailFailed() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    public static void actionStart(Context context, String walletAddress, String walletName, String walletIcon) {
        Intent intent = new Intent(context, DelegateDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_ADDRESS, walletAddress);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_NAME, walletName);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_ICON, walletIcon);
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
    public void onUpdateTransactionEvent(Event.UpdateDelegateDetailEvent event) {
        //刷新页面
        mPresenter.loadDelegateDetailData();
    }

}
