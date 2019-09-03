package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzhen.framework.util.LogUtils;
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

import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
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
    public void onPageStart() {
        if (mPresenter != null) {
            LogUtils.e("onPageStart");
            mPresenter.loadNew(TransactionsPresenter.DIRECTION_NEW);
        }
    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mTransactionAdapter = new TransactionAdapter(getContext(), null, R.layout.item_transaction_record);
        listTransaction.addItemDecoration(new CommonVerticalItemDecoration(getContext(), R.drawable.bg_transation_list_divider));
        listTransaction.setLayoutManager(linearLayoutManager);
        //解决数据加载完成后, 没有停留在顶部的问题
        listTransaction.setFocusable(false);
        listTransaction.setAdapter(mTransactionAdapter);
        mTransactionAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Transaction transaction = mTransactionAdapter.getDatas().get(position);
                TransactionDetailActivity.actionStart(getActivity(), transaction, WalletManager.getInstance().getSelectedWalletAddress(),"","");
            }
        });
    }

    public void loadMoreTransaction() {
        mPresenter.loadMore();
    }

    @Override
    public void notifyItemRangeInserted(List<Transaction> transactionList, String queryAddress, int positionStart, int itemCount) {
        emptyView.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyItemRangeInserted(transactionList, Arrays.asList(queryAddress), positionStart, itemCount);
        listTransaction.scrollToPosition(positionStart == 0 ? positionStart : transactionList.size() - 1);
    }

    @Override
    public void notifyItemChanged(List<Transaction> transactionList, String queryAddress, int position) {
        emptyView.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyItemChanged(transactionList, Arrays.asList(queryAddress), position);
    }

    @Override
    public void notifyDataSetChanged(List<Transaction> transactionList, String queryAddress) {
        emptyView.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyDataSetChanged(transactionList, Arrays.asList(queryAddress));
    }

    @Override
    public void finishLoadMore() {
        ((AssetsFragment) getParentFragment()).finishLoadMore();
    }

    @Override
    public List<Transaction> getTransactionList() {
        return mTransactionAdapter.getDatas();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
