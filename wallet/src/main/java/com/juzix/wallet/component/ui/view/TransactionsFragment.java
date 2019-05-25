package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.TransactionAdapter;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.ui.base.BaseViewPageFragment;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.presenter.TransactionsPresenter;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private TransactionAdapter mTransactionAdapter;

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
    protected void onPageStart() {
        mPresenter.autoRefresh();
    }

    private void initViews() {
        mTransactionAdapter = new TransactionAdapter(getContext(), null, R.layout.item_transaction_record);
        listTransaction.addItemDecoration(new CommonVerticalItemDecoration(getContext(), R.drawable.bg_transation_list_divider));
        listTransaction.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //解决数据加载完成后, 没有停留在顶部的问题
        listTransaction.setFocusable(false);
        listTransaction.setAdapter(mTransactionAdapter);
        mTransactionAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Transaction transaction = mTransactionAdapter.getDatas().get(position);
                if (transaction.getTxType() == TransactionType.TRANSFER) {
                    TransactionDetailActivity.actionStart(getActivity(), transaction, WalletManager.getInstance().getSelectedWalletAddress());
                } else if (transaction.getTxType() == TransactionType.VOTETICKET) {
                    VoteTransactionDetailActivity.actionStart(getActivity(), transaction);
                }
            }
        });
    }

    @Override
    public void notifyItemRangeInserted(List<Transaction> transactionList,String queryAddress, int positionStart, int itemCount) {
        emptyView.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyItemRangeInserted(transactionList,queryAddress,positionStart, itemCount);
        listTransaction.scrollToPosition(positionStart == 0 ? positionStart : transactionList.size() - 1);
    }

    @Override
    public void notifyItemChanged(List<Transaction> transactionList,String queryAddress, int position) {
        mTransactionAdapter.notifyItemChanged(transactionList,queryAddress, position);
    }

    @Override
    public void finishLoadMore() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        mPresenter.addNewTransaction(event.transaction);
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
