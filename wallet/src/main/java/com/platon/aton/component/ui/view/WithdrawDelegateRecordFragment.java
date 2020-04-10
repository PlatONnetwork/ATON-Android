package com.platon.aton.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.DelegateRecordAdapter;
import com.platon.aton.component.ui.contract.DelegateRecordContract;
import com.platon.aton.component.ui.presenter.DelegateRecordPresenter;
import com.platon.aton.component.widget.CustomRefreshFooter;
import com.platon.aton.component.widget.CustomRefreshHeader;
import com.platon.aton.entity.Transaction;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ziv
 * date On 2020-04-10
 */
public class WithdrawDelegateRecordFragment extends BaseLazyFragment<DelegateRecordContract.View, DelegateRecordPresenter> implements DelegateRecordContract.View {

    private Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rlv_list)
    ListView rlv_list;
    @BindView(R.id.layout_no_record)
    LinearLayout ll_no_data;
    private DelegateRecordAdapter mAdapter;
    //加载更多需要传入的值
    public long beginSequence = 0;
    private List<Transaction> list = new ArrayList<>();
    private boolean isLoadMore = false;

    @Override
    public DelegateRecordPresenter createPresenter() {
        return new DelegateRecordPresenter();
    }

    @Override
    public DelegateRecordContract.View createView() {
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_delegate_record;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        initView();
    }

    @Override
    public void onFragmentFirst() {
        super.onFragmentFirst();
        refreshLayout.autoRefresh();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        refreshLayout.autoRefresh();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        showLoadingDialog();
        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的
        mAdapter = new DelegateRecordAdapter(R.layout.item_delegate_record_list, null);
        rlv_list.setAdapter(mAdapter);
        rlv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction transactionRecord = mAdapter.getItem(position);
                TransactionDetailActivity.actionStart(getContext(), transactionRecord, Arrays.asList(transactionRecord.getFrom()));
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isLoadMore = false;
                getPresenter().loadDelegateRecordData(Constants.VoteConstants.NEWEST_DATA, Constants.VoteConstants.REFRESH_DIRECTION, Constants.DelegateRecordType.REDEEM);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isLoadMore = true;
                getPresenter().loadDelegateRecordData(beginSequence, Constants.VoteConstants.REQUEST_DIRECTION, Constants.DelegateRecordType.REDEEM);
            }
        });

    }

    @Override
    public void showDelegateRecordData(List<Transaction> recordList) {
        if (recordList.size() > 0) {
            beginSequence = recordList.get(recordList.size() - 1).getSequence();
        }
        if (isLoadMore) {
            list.addAll(recordList);
        } else {
            if (null != recordList && recordList.size() == 0) {
                ll_no_data.setVisibility(View.VISIBLE);
            } else {
                ll_no_data.setVisibility(View.GONE);
            }
            list.clear();
            list.addAll(recordList);
        }

        mAdapter.notifyDataChanged(list);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        dismissLoadingDialogImmediately();
    }


    @Override
    public void showDelegateRecordFailed() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        dismissLoadingDialogImmediately();
        ll_no_data.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            isLoadMore = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
