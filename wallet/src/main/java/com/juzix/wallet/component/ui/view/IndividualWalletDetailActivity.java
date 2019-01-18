package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.IndividualTransactionListAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.IndividualWalletDetailContract;
import com.juzix.wallet.component.ui.presenter.IndividualWalletDetailPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class IndividualWalletDetailActivity extends MVPBaseActivity<IndividualWalletDetailPresenter> implements IndividualWalletDetailContract.View {

    @BindView(R.id.tv_total_balance)
    TextView tvTotalBalance;
    @BindView(R.id.iv_copy_wallet_address)
    ImageView ivCopyWalletAddress;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_send_transation)
    TextView tvSendTransation;
    @BindView(R.id.tv_receive_transation)
    TextView tvReceiveTransation;
    @BindView(R.id.tv_call_vote)
    TextView tvCallVote;
    @BindView(R.id.list_transaction)
    ListView listTransaction;
    @BindView(R.id.layout_no_data)
    View emptyView;

    private Unbinder                         unbinder;
    private IndividualTransactionListAdapter transactionListAdapter;
    private CommonTitleBar                   commonTitleBar;

    @Override
    protected IndividualWalletDetailPresenter createPresenter() {
        return new IndividualWalletDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_wallet_detail);
        unbinder = ButterKnife.bind(this);
        initViews();
        EventPublisher.getInstance().register(this);
        mPresenter.fetchWalletDetail();
    }

    private void initViews() {

        commonTitleBar = new CommonTitleBar(this).setLeftDrawable(R.drawable.icon_back_black);
        commonTitleBar.build();

        transactionListAdapter = new IndividualTransactionListAdapter(R.layout.item_transaction_list, null);
        listTransaction.setAdapter(transactionListAdapter);
        listTransaction.setEmptyView(emptyView);
    }

    @OnItemClick({R.id.list_transaction})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.enterTransactionDetailActivity((TransactionEntity) parent.getAdapter().getItem(position));
    }

    @OnClick({R.id.iv_copy_wallet_address, R.id.tv_send_transation, R.id.tv_receive_transation, R.id.tv_call_vote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy_wallet_address:
                CommonUtil.copyTextToClipboard(this, tvWalletAddress.getText().toString());
                break;
            case R.id.tv_send_transation:
                SendIndividualTransationActivity.actionStart(this, getWalletEntityFromIntent());
                break;
            case R.id.tv_receive_transation:
                mPresenter.enterReceiveTransactionActivity();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionSucceedEvent(Event.IndividualTransactionSucceedEvent event) {
        mPresenter.fetchWalletDetail();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateIndividualWalletTransactionEvent(Event.UpdateIndividualWalletTransactionEvent event) {
        mPresenter.fetchWalletDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }


    @Override
    public IndividualWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void updateWalletInfo(IndividualWalletEntity walletEntity) {
        tvWalletAddress.setText(walletEntity.getPrefixAddress());
        tvTotalBalance.setText(string(R.string.wallet_balance, NumberParserUtils.getPrettyDetailBalance(walletEntity.getBalance())));
        commonTitleBar.setMiddleTitle(walletEntity.getName());
    }

    @Override
    public void notifyTransactionListChanged(List<TransactionEntity> transactionEntityList, String walletAddress) {
        transactionListAdapter.notifyDataChanged(transactionEntityList,walletAddress);
    }

    public static void actionStart(Context context, IndividualWalletEntity walletEntity) {
        Intent intent = new Intent(context, IndividualWalletDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
}
