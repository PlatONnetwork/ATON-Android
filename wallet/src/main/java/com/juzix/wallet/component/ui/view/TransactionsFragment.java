package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.TransactionDiffCallback;
import com.juzix.wallet.component.adapter.TransactionListAdapter;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.ui.base.BaseViewPageFragment;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.presenter.TransactionsPresenter;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionsFragment extends BaseViewPageFragment<TransactionsPresenter> implements TransactionsContract.View {

    @BindView(R.id.list_transaction)
    RecyclerView listTransaction;
    @BindView(R.id.layout_no_data)
    View emptyView;

    private Unbinder unbinder;
    private TransactionListAdapter mTransactionListAdapter;

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_transactions, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    @Override
    protected TransactionsPresenter createPresenter() {
        return new TransactionsPresenter(this);
    }

    @Override
    public void onPageStart() {
        if (mPresenter != null) {
            mPresenter.loadNew(TransactionsPresenter.DIRECTION_NEW);
        }
    }

    private void initViews() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mTransactionListAdapter = new TransactionListAdapter(getContext());
        listTransaction.addItemDecoration(new CommonVerticalItemDecoration(getContext(), R.drawable.bg_transation_list_divider));
        listTransaction.setLayoutManager(linearLayoutManager);
        //解决数据加载完成后, 没有停留在顶部的问题
        listTransaction.setFocusable(false);
        listTransaction.setAdapter(mTransactionListAdapter);
    }

    @Override
    public void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, String queryAddress, boolean isLoadLatestData) {
        mTransactionListAdapter.setQueryAddressList(Arrays.asList(queryAddress));
        emptyView.setVisibility(newTransactionList.isEmpty() ? View.VISIBLE : View.GONE);
        if (isLoadLatestData) {
            mTransactionListAdapter.notifyDataSetChanged(newTransactionList);
        } else {
            TransactionDiffCallback transactionDiffCallback = new TransactionDiffCallback(oldTransactionList, newTransactionList);
            mTransactionListAdapter.setTransactionList(newTransactionList);
            DiffUtil.calculateDiff(transactionDiffCallback, true).dispatchUpdatesTo(mTransactionListAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        mPresenter.addNewTransaction(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        mPresenter.loadLatestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        //获取最新
        mPresenter.loadLatestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSumAccountBalanceChanged(Event.SumAccountBalanceChanged event){
        mPresenter.loadNew(TransactionsPresenter.DIRECTION_NEW);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
