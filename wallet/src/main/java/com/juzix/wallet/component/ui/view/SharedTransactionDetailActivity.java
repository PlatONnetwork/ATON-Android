package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Barrier;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.SharedTransactionDetailContract;
import com.juzix.wallet.component.ui.presenter.SharedTransactionDetailPresenter;
import com.juzix.wallet.component.widget.ListViewForScrollView;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.DensityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SharedTransactionDetailActivity extends MVPBaseActivity<SharedTransactionDetailPresenter> implements SharedTransactionDetailContract.View {

    @BindView(R.id.list_transaction_result)
    ListViewForScrollView listView;
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
    @BindView(R.id.layout_transaction_result)
    LinearLayout layoutTransactionResult;
    @BindView(R.id.tv_transaction_status)
    TextView tvTransactionStatus;
    @BindView(R.id.tv_member_title)
    TextView tvMemberTitle;

    private CommonAdapter<TransactionResult> mAdapter;
    private Unbinder unbinder;

    @Override
    protected SharedTransactionDetailPresenter createPresenter() {
        return new SharedTransactionDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_transation_detail);
        unbinder = ButterKnife.bind(this);
        initView();
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
    public SharedTransactionEntity getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public String getAddressFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    @Override
    public void setTransactionDetailInfo(SharedTransactionEntity transactionEntity, String queryAddress) {

        SharedTransactionEntity.TransactionStatus status = transactionEntity.getTransactionStatus();

        tvTransactionStatus.setText(TextUtils.isEmpty(transactionEntity.getHash()) ?
                status.getStatusDesc(this, transactionEntity.getConfirms(), transactionEntity.getRequiredSignNumber()) :
                status.getStatusDesc(this, transactionEntity.getSignedBlockNumber(), 12));

        tvTransactionStatus.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, status.getStatusDrawable()), null, null);
        tvTransactionStatus.setCompoundDrawablePadding(DensityUtil.dp2px(this, 10));

        tvCopyFromName.setText(transactionEntity.getWalletName());
        tvFromAddress.setText(transactionEntity.getFromAddress());
        tvToAddress.setText(transactionEntity.getToAddress());

        SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(transactionEntity.getTransactionType());
        tvTransactionType.setText(transactionType.getTransactionTypeDesc(transactionEntity.getToAddress(), queryAddress));
        tvTransactionTime.setText(DateUtil.format(transactionEntity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));

        boolean isSendTransaction = transactionType == SharedTransactionEntity.TransactionType.SEND_TRANSACTION;

        String transactionAmount = isSendTransaction ? string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getValue())) : "-";
        tvTransactionAmount.setText(transactionAmount);

        String energonPrice = isSendTransaction ? "-" : string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(transactionEntity.getEnergonPrice()));
        tvTransactionEnergon.setText(energonPrice);

        tvTransactionWalletName.setText(transactionEntity.getWalletName());
        tvMemberTitle.setText(string(R.string.executeContractConfirm) + "(" + transactionEntity.getConfirms() + "/" + transactionEntity.getRequiredSignNumber() + ")");
    }

    @Override
    public void showTransactionResult(List<TransactionResult> transactionResultList) {
        layoutTransactionResult.setVisibility(transactionResultList == null || transactionResultList.isEmpty() ? View.GONE : View.VISIBLE);
        mAdapter.notifyDataChanged(transactionResultList);
    }

    private void initView() {
        mAdapter = new CommonAdapter<TransactionResult>(R.layout.item_shared_transaction_detail_member, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, TransactionResult item, int position) {
                viewHolder.setText(R.id.tv_name, item.getName());
                viewHolder.setText(R.id.tv_address, item.getPrefixAddress());
                switch (item.getStatus()) {
                    case OPERATION_APPROVAL:
                        viewHolder.setImageResource(R.id.iv_hook, R.drawable.icon_hook_s);
                        break;
                    case OPERATION_REVOKE:
                        viewHolder.setImageResource(R.id.iv_hook, R.drawable.icon_fork_s);
                        break;
                    case OPERATION_UNDETERMINED:
                        viewHolder.setImageResource(R.id.iv_hook, R.drawable.icon_fork_s);
                        break;
                }
                viewHolder.setOnClickListener(R.id.iv_copy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.copyTextToClipboard(SharedTransactionDetailActivity.this, item.getAddress());
                    }
                });
            }
        };
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, SharedTransactionEntity transactionEntity, String queryAddress) {
        Intent intent = new Intent(context, SharedTransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transactionEntity);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, queryAddress);
        context.startActivity(intent);
    }
}
