package com.platon.aton.component.ui.view;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.component.adapter.TransactionDiffCallback;
import com.platon.aton.component.adapter.TransactionListAdapter;
import com.platon.aton.component.ui.contract.TransactionsContract;
import com.platon.aton.component.ui.presenter.TransactionsPresenter;
import com.platon.aton.component.widget.CommonVerticalItemDecoration;
import com.platon.aton.component.widget.WrapContentLinearLayoutManager;
import com.platon.aton.entity.Transaction;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.base.BaseLazyFragment;

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
public class TransactionsFragment extends BaseLazyFragment<TransactionsContract.View, TransactionsPresenter> implements TransactionsContract.View {

    @BindView(R.id.list_transaction)
    RecyclerView listTransaction;
    @BindView(R.id.layout_no_data)
    View emptyView;

    private Unbinder unbinder;
    private TransactionListAdapter mTransactionListAdapter;

    @Override
    public TransactionsPresenter createPresenter() {
        return new TransactionsPresenter();
    }

    @Override
    public TransactionsContract.View createView() {
        return this;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        if (getPresenter() != null) {
            getPresenter().loadNew(TransactionsPresenter.DIRECTION_NEW);
        }
    }

    @Override
    public void onFragmentFirst() {
        super.onFragmentFirst();
        if (getPresenter() != null) {
            getPresenter().loadNew(TransactionsPresenter.DIRECTION_NEW);
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
        getPresenter().addNewTransaction(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTransactionEvent(Event.DeleteTransactionEvent event) {
        getPresenter().deleteTransaction(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        getPresenter().loadLatestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        //获取最新
        getPresenter().loadLatestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSumAccountBalanceChanged(Event.SumAccountBalanceChanged event) {
        getPresenter().loadNew(TransactionsPresenter.DIRECTION_NEW);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public int getLayoutId() {
        return 0;
    }
}
