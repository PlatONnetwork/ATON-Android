package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.TransactionDetailContract;
import com.platon.aton.component.ui.presenter.TransactionDetailPresenter;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.TransferType;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.RUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionDetailActivity extends BaseActivity<TransactionDetailContract.View, TransactionDetailPresenter> implements TransactionDetailContract.View {

    @BindView(R.id.iv_copy_from_address)
    ImageView ivCopyFromAddress;
    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;
    @BindView(R.id.iv_copy_to_address)
    ImageView ivCopyToAddress;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.iv_failed)
    ImageView ivFailed;
    @BindView(R.id.iv_succeed)
    ImageView ivSucceed;
    @BindView(R.id.iv_timeout)
    ImageView ivTimeout;
    @BindView(R.id.layout_pending)
    RelativeLayout layoutPending;
    @BindView(R.id.tv_transaction_status_desc)
    TextView tvTransactionStatusDesc;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.view_transaction_detai_info)
    TransactionDetailInfoView viewTransactionDetailInfo;
    @BindView(R.id.tv_from)
    TextView tvFrom;
    @BindView(R.id.tv_to)
    TextView tvTo;
    @BindView(R.id.iv_contract_to_tag)
    ImageView ivContractToTag;
    @BindView(R.id.tv_transaction_note)
    TextView tvTransactionNote;

    private Unbinder unbinder;

    @Override
    public TransactionDetailPresenter createPresenter() {
        return new TransactionDetailPresenter();
    }

    @Override
    public TransactionDetailContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        EventPublisher.getInstance().register(this);
        unbinder = ButterKnife.bind(this);
        getPresenter().init();
        getPresenter().loadData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transation_detail;
    }


    @OnClick({R.id.iv_copy_from_address, R.id.tv_from_address, R.id.iv_copy_to_address, R.id.tv_to_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy_from_address:
            case R.id.tv_from_address:
                CommonUtil.copyTextToClipboard(this, tvFromAddress.getText().toString());
                break;
            case R.id.iv_copy_to_address:
            case R.id.tv_to_address:
                CommonUtil.copyTextToClipboard(this, tvToAddress.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public Transaction getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public List<String> getAddressListFromIntent() {
        return getIntent().getStringArrayListExtra(Constants.Extra.EXTRA_ADDRESS_LIST);
    }

    @Override
    public void setTransactionDetailInfo(Transaction transaction, List<String> queryAddressList, String walletName) {

        TransactionStatus transactionStatus = transaction.getTxReceiptStatus();

        TransactionType transactionType = transaction.getTxType();

        showTransactionStatus(transactionStatus);

        boolean isValueZero = !BigDecimalUtil.isBiggerThanZero(transaction.getValue());

        @TransferType int transferType = transaction.getTransferType(queryAddressList);

        if (transferType == TransferType.TRANSFER || isValueZero) {
            tvAmount.setText(AmountUtil.formatAmountText(transaction.getValue()));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_b6bbd0));
        } else if (transferType == TransferType.SEND && transactionType != TransactionType.UNDELEGATE && transactionType != TransactionType.EXIT_VALIDATOR && transactionType != TransactionType.CLAIM_REWARDS) {
            tvAmount.setText(String.format("%s%s", "-", AmountUtil.formatAmountText(transaction.getValue())));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_ff3b3b));
        } else {
            tvAmount.setText(String.format("%s%s", "+", AmountUtil.formatAmountText(transaction.getValue())));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_19a20e));
        }

        tvAmount.setVisibility(transactionStatus == TransactionStatus.SUCCESSED ? View.VISIBLE : View.GONE);
        tvFromAddress.setText(transaction.getFrom());
        tvToAddress.setText(transaction.getTo());
        ivContractToTag.setVisibility(transactionType == TransactionType.TRANSFER ? View.GONE : View.VISIBLE);

        String senderName = getSenderName(transaction.getFrom());

        tvFrom.setText(senderName);
        tvFrom.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, getSenderAvatar(transaction.getFrom())), null, null, null);

        tvTo.setText(getReceiverName(transaction.getTo(), transaction.getNodeName()));
        tvTo.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, getReceiverAvatar(transactionType, transaction.getTo())), null, null, null);

        tvTransactionNote.setVisibility(TextUtils.isEmpty(transaction.getRemark()) ? View.GONE : View.VISIBLE);
        tvTransactionNote.setText(string(R.string.msg_transaction_memo, transaction.getRemark()));

        viewTransactionDetailInfo.setData(transaction, senderName, transferType);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        getPresenter().updateTransactionDetailInfo(event.transaction);
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void showTransactionStatus(TransactionStatus status) {
        switch (status) {
            case PENDING:
                tvTransactionStatusDesc.setText(R.string.pending);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.GONE);
                ivTimeout.setVisibility(View.GONE);
                layoutPending.setVisibility(View.VISIBLE);
                break;
            case SUCCESSED:
                tvTransactionStatusDesc.setText(R.string.success);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.VISIBLE);
                ivTimeout.setVisibility(View.GONE);
                layoutPending.setVisibility(View.GONE);
                break;
            case FAILED:
                tvTransactionStatusDesc.setText(R.string.failed);
                ivFailed.setVisibility(View.VISIBLE);
                ivSucceed.setVisibility(View.GONE);
                ivTimeout.setVisibility(View.GONE);
                layoutPending.setVisibility(View.GONE);
                break;
            case TIMEOUT:
                tvTransactionStatusDesc.setText(R.string.timeout);
                ivFailed.setVisibility(View.GONE);
                ivSucceed.setVisibility(View.GONE);
                ivTimeout.setVisibility(View.VISIBLE);
                layoutPending.setVisibility(View.GONE);
            default:
                break;
        }
    }

    private String getSenderName(String prefixAddress) {
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return walletName;
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        }
        return AddressFormatUtil.formatTransactionAddress(prefixAddress);
    }

    private int getSenderAvatar(String prefixAddress) {
        String avatar = WalletDao.getWalletAvatarByAddress(prefixAddress);
        if (!TextUtils.isEmpty(avatar) && RUtils.drawable(avatar) != -1) {
            return RUtils.drawable(avatar);
        }
        return R.drawable.avatar_1;
    }

    private String getReceiverName(String prefixAddress, String nodeName) {
        if (!TextUtils.isEmpty(nodeName)) {
            return nodeName;
        }
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return walletName;
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        }
        return AddressFormatUtil.formatTransactionAddress(prefixAddress);
    }

    private int getReceiverAvatar(TransactionType txType, String prefixAddress) {
        if (txType == TransactionType.TRANSFER) {
            String avatar = WalletDao.getWalletAvatarByAddress(prefixAddress);
            if (!TextUtils.isEmpty(avatar) && RUtils.drawable(avatar) != -1) {
                return RUtils.drawable(avatar);
            }
            return R.drawable.avatar_1;
        } else {
            return R.drawable.icon_contract;
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

    public static void actionStart(Context context, Transaction transaction, List<String> queryAddress) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transaction);
        intent.putStringArrayListExtra(Constants.Extra.EXTRA_ADDRESS_LIST, new ArrayList<>(queryAddress));
        context.startActivity(intent);
    }
}
