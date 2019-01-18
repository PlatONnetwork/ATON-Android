package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Barrier;
import android.support.v4.content.ContextCompat;
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
import com.juzix.wallet.utils.DensityUtil;

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
    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;
    @BindView(R.id.layout_from_address)
    RelativeLayout layoutFromAddress;
    @BindView(R.id.iv_copy_to_address)
    ImageView ivCopyToAddress;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.layout_to_address)
    RelativeLayout layoutToAddress;
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
    @BindView(R.id.tv_memo)
    TextView tvMemo;
    @BindView(R.id.iv_copy_transation_hash)
    ImageView ivCopyTransationHash;
    @BindView(R.id.tv_transation_hash)
    TextView tvTransationHash;
    @BindView(R.id.layout_transation_hash)
    RelativeLayout layoutTransationHash;
    @BindView(R.id.tv_transaction_status)
    TextView tvTransactionStatus;
    @BindView(R.id.tv_memo_title)
    TextView tvMemoTitle;

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
        initViews();
        mPresenter.fetchTransactionDetail();
    }

    private void initViews() {
        tvMemo.setVisibility(View.GONE);
        tvMemoTitle.setVisibility(View.GONE);
    }

    @OnClick({R.id.iv_copy_from_address, R.id.iv_copy_to_address, R.id.iv_copy_transation_hash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy_from_address:
                CommonUtil.copyTextToClipboard(this, tvFromAddress.getText().toString());
                break;
            case R.id.iv_copy_to_address:
                CommonUtil.copyTextToClipboard(this, tvToAddress.getText().toString());
                break;
            case R.id.iv_copy_transation_hash:
                CommonUtil.copyTextToClipboard(this, tvTransationHash.getText().toString());
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

        IndividualTransactionEntity.TransactionStatus status = transactionEntity.getTransactionStatus();
        tvTransactionStatus.setText(status.getStatusDesc(this, transactionEntity.getSignedBlockNumber(), 12));
        tvTransactionStatus.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, status.getStatusDrawable()), null, null);
        tvTransactionStatus.setCompoundDrawablePadding(DensityUtil.dp2px(this, 10));

        tvFromAddress.setText(transactionEntity.getFromAddress());
        tvToAddress.setText(transactionEntity.getToAddress());

        tvTransactionType.setText(transactionEntity.isReceiver(queryAddress) ? R.string.receive : R.string.send);
        tvTransactionTime.setText(DateUtil.format(transactionEntity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
        tvTransactionAmount.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getValue())));
        tvTransactionEnergon.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getEnergonPrice())));
        tvTransactionWalletName.setText(transactionEntity.getWalletName());
        tvMemo.setText(transactionEntity.getMemo());
        tvTransationHash.setText(transactionEntity.getHash());
    }
}
