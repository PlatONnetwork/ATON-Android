package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.platon.aton.R;
import com.platon.aton.component.adapter.ClaimRewardRecordAdapter;
import com.platon.aton.component.adapter.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.platon.aton.component.ui.contract.ClaimRecordContract;
import com.platon.aton.component.ui.presenter.ClaimRecordPresenter;
import com.platon.aton.component.ui.presenter.Direction;
import com.platon.aton.entity.ClaimReward;
import com.platon.aton.entity.ClaimRewardRecord;
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

        mClaimRewardRecordAdapter = new ClaimRewardRecordAdapter(this);

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

        mClaimRewardRecordAdapter.setListener(new BaseExpandableRecyclerViewAdapter.ExpandableRecyclerViewOnClickListener<ClaimRewardRecord, ClaimReward>() {
            @Override
            public boolean onGroupLongClicked(ClaimRewardRecord groupItem) {
                return false;
            }

            @Override
            public boolean onInterceptGroupExpandEvent(ClaimRewardRecord groupItem, boolean isExpand) {
                return false;
            }

            @Override
            public void onGroupClicked(ClaimRewardRecord groupItem) {

            }

            @Override
            public void onChildClicked(ClaimRewardRecord groupItem, ClaimReward childItem) {

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
    public void getRewardTransactionsResult(List<ClaimRewardRecord> newClaimRewardRecordList) {

        if (newClaimRewardRecordList == null || newClaimRewardRecordList.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            listClaimRecord.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            listClaimRecord.setVisibility(View.VISIBLE);

            mClaimRewardRecordAdapter.setList(newClaimRewardRecordList);
            mClaimRewardRecordAdapter.notifyDataSetChanged();

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
