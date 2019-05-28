package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;

import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteTransactionDetailContract;
import com.juzix.wallet.component.ui.presenter.VoteTransactionDetailPresenter;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionExtra;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.VoteTrasactionExtra;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class VoteTransactionDetailActivity extends MVPBaseActivity<VoteTransactionDetailPresenter> implements VoteTransactionDetailContract.View {

    @BindView(R.id.iv_copy_from_address)
    ImageView ivCopyFromAddress;
    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;
    @BindView(R.id.iv_copy_to_address)
    ImageView ivCopyToAddress;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.tv_transaction_type_title)
    TextView tvTransactionTypeTitle;
    @BindView(R.id.tv_transaction_time_title)
    TextView tvTransactionTimeTitle;
    @BindView(R.id.tv_transaction_node_name_title)
    TextView tvTransactionNodeNameTitle;
    @BindView(R.id.tv_transaction_energon_title)
    TextView tvTransactionEnergonTitle;
    @BindView(R.id.tv_transaction_type)
    TextView tvTransactionType;
    @BindView(R.id.tv_transaction_time)
    TextView tvTransactionTime;
    @BindView(R.id.tv_transaction_node_name)
    TextView tvTransactionNodeName;
    @BindView(R.id.tv_transaction_node_id)
    TextView tvTransactionNodeId;
    @BindView(R.id.tv_transaction_votes)
    TextView tvTransactionVotes;
    @BindView(R.id.tv_transaction_ticket_price)
    TextView tvTransactionTicketPrice;
    @BindView(R.id.tv_vote_staked)
    TextView tvVoteStaked;
    @BindView(R.id.tv_transaction_energon)
    TextView tvTransactionEnergon;
    @BindView(R.id.iv_failed)
    ImageView ivFailed;
    @BindView(R.id.iv_succeed)
    ImageView ivSucceed;
    @BindView(R.id.layout_pending)
    RelativeLayout layoutPending;
    @BindView(R.id.tv_transaction_status_desc)
    TextView tvTransactionStatusDesc;
    @BindView(R.id.tv_transaction_node_id_title)
    TextView tvTransactionNodeIdTitle;
    @BindView(R.id.tv_transaction_votes_title)
    TextView tvTransactionVotesTitle;
    @BindView(R.id.tv_transaction_ticket_price_title)
    TextView tvTransactionTicketPriceTitle;
    @BindView(R.id.tv_transaction_deposit_title)
    TextView tvTransactionDepositTitle;
    @BindView(R.id.tv_copy_from_name)
    TextView tvCopyFromName;

    private Unbinder unbinder;
    private Transaction mTransaction;

    @Override
    protected VoteTransactionDetailPresenter createPresenter() {
        return new VoteTransactionDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_vote_detail);
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        mPresenter.loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        mPresenter.updateTransactionDetailInfo(event.transaction);
    }

    @OnClick({R.id.iv_copy_from_address, R.id.iv_copy_to_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy_from_address:
                CommonUtil.copyTextToClipboard(this, tvFromAddress.getText().toString());
                break;
            case R.id.iv_copy_to_address:
                CommonUtil.copyTextToClipboard(this, tvToAddress.getText().toString());
                break;
            default:
                break;
        }
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
    public Transaction getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public String getAddressFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    @Override
    public void setTransactionDetailInfo(Transaction transaction, String queryAddress, String senderWalletName) {
        showTransactionStatus(transaction.getTxReceiptStatus());

        tvFromAddress.setText(transaction.getFrom());
        tvToAddress.setText(transaction.getTo());
        tvTransactionType.setText(transaction.getTxType().getTxTypeDescRes());
        tvTransactionTime.setText(transaction.getShowCreateTime());
        tvTransactionEnergon.setText(string(R.string.amount_with_unit, transaction.getShowActualTxCost()));
        tvCopyFromName.setText(senderWalletName);

        TransactionExtra transactionExtra = transaction.getTransactionExtra();
        if (transactionExtra != null) {
            VoteTrasactionExtra voteTrasactionExtra = JSONUtil.parseObject(transactionExtra.getParameters(), VoteTrasactionExtra.class);
            if (voteTrasactionExtra != null) {
                tvTransactionNodeName.setText(voteTrasactionExtra.getNodeName());
                tvTransactionNodeId.setText(voteTrasactionExtra.getNodeId());
                tvTransactionVotes.setText(voteTrasactionExtra.getVotedNum());
                tvTransactionTicketPrice.setText(string(R.string.amount_with_unit, voteTrasactionExtra.getShowTicketPrice()));
                tvVoteStaked.setText(string(R.string.amount_with_unit, voteTrasactionExtra.getShowVotedStaked()));
            }
        }
    }

    private void showTransactionStatus(TransactionStatus status) {
        switch (status) {
            case PENDING:
                tvTransactionStatusDesc.setText(R.string.pending);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.GONE);
                layoutPending.setVisibility(View.VISIBLE);
                break;
            case SUCCESSED:
                tvTransactionStatusDesc.setText(R.string.success);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.VISIBLE);
                layoutPending.setVisibility(View.GONE);
                break;
            case FAILED:
                tvTransactionStatusDesc.setText(R.string.failed);
                ivFailed.setVisibility(View.VISIBLE);
                ivSucceed.setVisibility(View.GONE);
                layoutPending.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context context, Transaction transaction, String queryAddress) {
        Intent intent = new Intent(context, VoteTransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transaction);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, queryAddress);
        context.startActivity(intent);
    }
}
