package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.DelegateRecordAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.DelegateRecordContract;
import com.juzix.wallet.component.ui.presenter.DelegateRecordPresenter;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.entity.Transaction;
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
 * 所有委托记录（委托和赎回委托）
 */

public class AllDelegateRecordFragment extends MVPBaseFragment<DelegateRecordPresenter> implements DelegateRecordContract.View {
    private Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rlv_list)
    ListView rlv_list;
    @BindView(R.id.layout_no_record)
    LinearLayout ll_no_data;
    private DelegateRecordAdapter mAdapter;
    public long beginSequence = 0;//加载更多需要传入的值
    private List<Transaction> list = new ArrayList<>();
    private boolean isLoadMore = false;

    @Override
    protected DelegateRecordPresenter createPresenter() {
        return new DelegateRecordPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
//        mPresenter.loadDelegateRecordData(-1, Constants.VoteConstants.REFRESH_DIRECTION, Constants.DelegateRecordType.All);
        refreshLayout.autoRefresh();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
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

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isLoadMore = false;
                mPresenter.loadDelegateRecordData(Constants.VoteConstants.NEWEST_DATA, Constants.VoteConstants.REFRESH_DIRECTION, Constants.DelegateRecordType.All);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isLoadMore = true;
                mPresenter.loadDelegateRecordData(beginSequence, Constants.VoteConstants.REQUEST_DIRECTION, Constants.DelegateRecordType.All);
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
}
