package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.TransactionListsAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.presenter.TransactionsPresenter;
import com.juzix.wallet.entity.Transaction;
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
public class TransactionsFragment extends MVPBaseFragment<TransactionsPresenter> implements TransactionsContract.View {

    private final static String TAG = TransactionsFragment.class.getSimpleName();

    @BindView(R.id.list_transaction)
    ListView listTransaction;
    @BindView(R.id.layout_no_data)
    View emptyView;

    private Unbinder unbinder;
    private TransactionListsAdapter transactionListAdapter;

    @Override
    protected TransactionsPresenter createPresenter() {
        return new TransactionsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_transactions, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        transactionListAdapter = new TransactionListsAdapter(R.layout.item_transaction_list, null);
        listTransaction.setAdapter(transactionListAdapter);
        listTransaction.setEmptyView(emptyView);
        listTransaction.setFocusable(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateIndividualWalletTransactionEvent(Event.UpdateIndividualWalletTransactionEvent event) {
//        if (transactionListAdapter.getList() != null && transactionListAdapter.getList().contains(event.individualTransactionEntity)) {
//            transactionListAdapter.updateItem(currentActivity(), listTransaction, event.individualTransactionEntity);
//        } else {
//            transactionListAdapter.addItem(event.individualTransactionEntity);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateVoteTransactionListEvent(Event.UpdateVoteTransactionListEvent event) {
        if (transactionListAdapter.getList() != null && transactionListAdapter.getList().contains(event.voteTransactionEntity)) {
            transactionListAdapter.updateItem(currentActivity(), listTransaction, event.voteTransactionEntity);
        } else {
            transactionListAdapter.addItem(event.voteTransactionEntity);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionUnreadMessageEvent(Event.UpdateTransactionUnreadMessageEvent event) {
//        transactionListAdapter.updateItem(listTransaction, event.uuid, event.hasUnread);
    }


    @Override
    public void notifyTransactionListChanged(List<Transaction> transactionEntityList) {
        transactionListAdapter.notifyDataChanged(transactionEntityList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }

    @Override
    protected void onFragmentPageStart() {

    }
}
