package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.TransactionListsAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.component.ui.presenter.TransactionsPresenter;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionsFragment extends MVPBaseFragment<TransactionsPresenter> implements TransactionsContract.View {

    @BindView(R.id.list_transaction)
    ListView  listTransaction;
    @BindView(R.id.layout_no_data)
    View      emptyView;

    private Unbinder                unbinder;
    private TransactionListsAdapter transactionListAdapter;

    @Override
    protected TransactionsPresenter createPresenter() {
        return new TransactionsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.fetchWalletDetail();
    }

    @Nullable
    @Override
    public View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_transactions, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        mPresenter.updateWalletEntity();
        setAdapter();
        mPresenter.fetchWalletDetail();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }


    private void setAdapter() {
        transactionListAdapter = new TransactionListsAdapter(R.layout.item_transaction_list, null, mPresenter.getWalletType());
        listTransaction.setAdapter(transactionListAdapter);
        listTransaction.setEmptyView(emptyView);
        listTransaction.setFocusable(false);
    }

    @OnItemClick({R.id.list_transaction})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TransactionEntity transactionEntity = (TransactionEntity) parent.getAdapter().getItem(position);
        mPresenter.enterTransactionDetailActivity(transactionEntity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSharedWalletTransactionEvent(Event.UpdateSharedWalletTransactionEvent event) {
        mPresenter.fetchWalletTransactionList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        mPresenter.updateWalletEntity();
        setAdapter();
        mPresenter.fetchWalletDetail();
    }

    @Override
    public WalletEntity getWalletFromIntent() {
        return getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void notifyTransactionListChanged(List<TransactionEntity> transactionEntityList, String walletAddress) {
        transactionListAdapter.notifyDataChanged(transactionEntityList, walletAddress);
    }

    @Override
    public void notifyTransactionChanged(SharedTransactionEntity transactionEntity, String walletAddress) {
        transactionListAdapter.notifyDataChanged(transactionEntity, walletAddress);
    }
}
