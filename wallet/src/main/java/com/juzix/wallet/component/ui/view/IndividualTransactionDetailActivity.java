package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Barrier;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.component.ui.presenter.IndividualTransactionDetailPresenter;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class IndividualTransactionDetailActivity extends MVPBaseActivity<IndividualTransactionDetailPresenter> implements IndividualTransactionDetailContract.View {

    @BindView(R.id.iv_copy_from_address)
    ImageView ivCopyFromAddress;
    @BindView(R.id.tv_copy_from_name)
    TextView tvCopyFromName;
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
    @BindView(R.id.tv_transaction_amount_title)
    TextView tvTransactionAmountTitle;
    @BindView(R.id.tv_transaction_energon_title)
    TextView tvTransactionEnergonTitle;
    @BindView(R.id.tv_transaction_wallet_name_title)
    TextView tvTransactionWalletNameTitle;
    @BindView(R.id.barrier)
    Barrier barrier;
    @BindView(R.id.tv_transaction_type)
    TextView tvTransactionType;
    @BindView(R.id.tv_transaction_time)
    TextView tvTransactionTime;
    @BindView(R.id.tv_transaction_amount)
    TextView tvTransactionAmount;
    @BindView(R.id.tv_transaction_energon)
    TextView tvTransactionEnergon;
    @BindView(R.id.tv_transaction_wallet_name)
    TextView tvTransactionWalletName;
    @BindView(R.id.iv_failed)
    ImageView ivFailed;
    @BindView(R.id.iv_succeed)
    ImageView ivSucceed;
    @BindView(R.id.layout_pending)
    RelativeLayout layoutPending;
    @BindView(R.id.tv_transaction_status_desc)
    TextView tvTransactionStatusDesc;

    private Unbinder unbinder;

    @Override
    protected IndividualTransactionDetailPresenter createPresenter() {
        return new IndividualTransactionDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_transation_detail);
        unbinder = ButterKnife.bind(this);
        mPresenter.fetchTransactionDetail();
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
    }

    public static void actionStart(Context context, IndividualTransactionEntity transactionEntity, String queryAddress) {
        Intent intent = new Intent(context, IndividualTransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transactionEntity);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, queryAddress);
        context.startActivity(intent);
    }

    @Override
    public IndividualTransactionEntity getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public String getAddressFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    @Override
    public void setTransactionDetailInfo(IndividualTransactionEntity transactionEntity, String queryAddress) {

        showTransactionStatus(transactionEntity.getTransactionStatus());

        tvCopyFromName.setText(transactionEntity.getWalletName());
        tvFromAddress.setText(transactionEntity.getFromAddress());
        tvToAddress.setText(transactionEntity.getToAddress());

        tvTransactionType.setText(transactionEntity.isReceiver(queryAddress) ? R.string.receive : R.string.send);
        tvTransactionTime.setText(DateUtil.format(transactionEntity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
        tvTransactionAmount.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getValue())));
        tvTransactionEnergon.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getEnergonPrice())));
        tvTransactionWalletName.setText(transactionEntity.getWalletName());
    }

    private void showTransactionStatus(IndividualTransactionEntity.TransactionStatus status) {
        switch (status) {
            case PENDING:
                tvTransactionStatusDesc.setText(R.string.pending);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.GONE);
                layoutPending.setVisibility(View.VISIBLE);
                break;
            case SUCCEED:
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
}
