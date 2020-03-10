package com.platon.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.platon.wallet.R;
import com.platon.wallet.component.adapter.TransactionDiffCallback;
import com.platon.wallet.component.adapter.TransactionListAdapter;
import com.platon.wallet.component.ui.base.BaseViewPageFragment;
import com.platon.wallet.component.ui.contract.TransactionsContract;
import com.platon.wallet.component.ui.presenter.TransactionsPresenter;
import com.platon.wallet.component.widget.CommonVerticalItemDecoration;
import com.platon.wallet.component.widget.WrapContentLinearLayoutManager;
import com.platon.wallet.entity.Transaction;
import com.platon.wallet.event.Event;
import com.platon.wallet.event.EventPublisher;

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

        WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mTransactionListAdapter = new TransactionListAdapter(getContext());
        listTransaction.addItemDecoration(new CommonVerticalItemDecoration(getContext(), R.drawable.bg_transation_list_divider));
        listTransaction.setLayoutManager(wrapContentLinearLayoutManager);
        //解决数据加载完成后, 没有停留在顶部的问题
        listTransaction.setFocusable(false);
        listTransaction.setAdapter(mTransactionListAdapter);
    }

    @Override
    public void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, String queryAddress, boolean isLoadLatestData) {
        mTransactionListAdapter.setQueryAddressList(Arrays.asList(queryAddress));
        emptyView.setVisibility(newTransactionList == null || newTransactionList.isEmpty() ? View.VISIBLE : View.GONE);
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
    public void onDeleteTransactionEvent(Event.DeleteTransactionEvent event) {
        mPresenter.deleteTransaction(event.transaction);
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
    public void onSumAccountBalanceChanged(Event.SumAccountBalanceChanged event) {
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
