package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.presenter.DelegateDetailPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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


    @Override
    protected DelegateDetailPresenter createPresenter() {
        return new DelegateDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate_detail);
        unbinder = ButterKnife.bind(this);
        initView();


    }

    private void initView() {
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
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的


    }

    public static void actionStart(Context context, String walletAddress) {
        Intent intent = new Intent(context, DelegateDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_ADDRESS, walletAddress);
        context.startActivity(intent);
    }
}
