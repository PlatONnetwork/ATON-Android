package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.ClaimRewardRecordAdapter;
import com.juzix.wallet.component.adapter.ClaimRewardRecordDiffCallback;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ClaimRecordContract;
import com.juzix.wallet.component.ui.presenter.ClaimRecordPresenter;
import com.juzix.wallet.component.ui.presenter.Direction;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ClaimRewardRecordActivity extends MVPBaseActivity<ClaimRecordPresenter> implements ClaimRecordContract.View {

    @BindView(R.id.list_claim_record)
    RecyclerView listClaimRecord;
    @BindView(R.id.layout_empty)
    LinearLayout emptyLayout;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private Unbinder unbinder;
    private ClaimRewardRecordAdapter mClaimRewardRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_record);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        mClaimRewardRecordAdapter = new ClaimRewardRecordAdapter();

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getRewardTransactions(Direction.DIRECTION_NEW);
            }
        });

        layoutRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getRewardTransactions(Direction.DIRECTION_OLD);
            }
        });

        listClaimRecord.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listClaimRecord.setAdapter(mClaimRewardRecordAdapter);

        layoutRefresh.autoRefresh();
    }

    @Override
    protected ClaimRecordPresenter createPresenter() {
        return new ClaimRecordPresenter(this);
    }

    @Override
    public void getRewardTransactionsResult(List<ClaimRewardRecord> oldClaimRewardRecordList, List<ClaimRewardRecord> newClaimRewardRecordList) {
        if (newClaimRewardRecordList == null || newClaimRewardRecordList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            listClaimRecord.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            listClaimRecord.setVisibility(View.VISIBLE);

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ClaimRewardRecordDiffCallback(oldClaimRewardRecordList, newClaimRewardRecordList), true);
            mClaimRewardRecordAdapter.setList(newClaimRewardRecordList);
            diffResult.dispatchUpdatesTo(mClaimRewardRecordAdapter);
        }
    }

    @Override
    public void finishLoadMore() {
        layoutRefresh.finishLoadMore();
    }

    @Override
    public void finishRefresh() {
        layoutRefresh.finishRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ClaimRewardRecordActivity.class);
        context.startActivity(intent);
    }
}
