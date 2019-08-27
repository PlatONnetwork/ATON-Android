package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.component.ui.presenter.TransactionDetailPresenter;
import com.juzix.wallet.db.sqlite.AddressDao;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.platon.BaseResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionDetailActivity extends MVPBaseActivity<TransactionDetailPresenter> implements IndividualTransactionDetailContract.View {

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
    @BindView(R.id.iv_failed)
    ImageView ivFailed;
    @BindView(R.id.iv_succeed)
    ImageView ivSucceed;
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

    private Unbinder unbinder;

    @Override
    protected TransactionDetailPresenter createPresenter() {
        return new TransactionDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transation_detail);
        EventPublisher.getInstance().register(this);
        unbinder = ButterKnife.bind(this);
        mPresenter.loadData();
        mPresenter.getDelegateResult();
        mPresenter.getWithDrawResult();
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
    public Transaction getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public String getAddressFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    /**
     * 获取委托的交易hash
     * @return
     */
    @Override
    public String getDelegateHash() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_DELEGATE_TRANSACTION_HASH);
    }

    /**
     * 获取赎回交易hash
     * @return
     */
    @Override
    public String getWithDrawHash() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_WITHDRAW_TRANSACTION_HASH);
    }

    @Override
    public void setTransactionDetailInfo(Transaction transaction, String queryAddress, String walletName) {

        TransactionStatus transactionStatus = transaction.getTxReceiptStatus();

        showTransactionStatus(transactionStatus);

        tvAmount.setVisibility(transactionStatus == TransactionStatus.SUCCESSED ? View.VISIBLE : View.GONE);
        tvAmount.setText(String.format("%s%s", transaction.isSender() ? "-" : "+", transaction.getShowValue()));
        tvAmount.setTextColor(transaction.isSender() ? ContextCompat.getColor(this, R.color.color_ff3b3b) : ContextCompat.getColor(this, R.color.color_19a20e));
        tvCopyFromName.setText(walletName);
        tvFromAddress.setText(transaction.getFrom());
        tvToAddress.setText(transaction.getTo());

        viewTransactionDetailInfo.setData(transaction);

    }

    @Override
    public void showDelegateResponse(BaseResponse response) {
        if (null != response) {
            if (response.isStatusOk()) {
                //更新UI
                TransactionStatus transactionStatus = TransactionStatus.SUCCESSED;
                showTransactionStatus(transactionStatus);
            }
        }


    }

    @Override
    public void showWithDrawResponse(BaseResponse response) {
        if(null != response && response.isStatusOk()){
              //更新UI
            TransactionStatus status =TransactionStatus.SUCCESSED;
            showTransactionStatus(status);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateIndividualWalletTransactionEvent(Event.UpdateTransactionEvent event) {
        mPresenter.updateTransactionDetailInfo(event.transaction);
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

    private String getSenderName(String prefixAddress) {
        String walletName = WalletDao.getWalletNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(walletName)) {
            return walletName;
        }
        String remark = AddressDao.getAddressNameByAddress(prefixAddress);
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        }
        return AddressFormatUtil.formatAddress(prefixAddress);
    }

    private int getSenderAvatar(String prefixAddress) {
        String avatar = WalletDao.getWalletAvatarByAddress(prefixAddress);
        if (!TextUtils.isEmpty(avatar) && RUtils.drawable(avatar) != -1) {
            return RUtils.drawable(avatar);
        }
        return R.drawable.avatar_1;
    }

//    private String getReceiverName() {
//
//    }
//
//    private String getReceiverAvatar() {
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }

    public static void actionStart(Context context, Transaction transaction, String queryAddress, String platonSendTransaction, String withdrawTransaction) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transaction);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, queryAddress);
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_TRANSACTION_HASH, platonSendTransaction);
        intent.putExtra(Constants.Extra.EXTRA_WITHDRAW_TRANSACTION_HASH, withdrawTransaction);
        context.startActivity(intent);
    }
}
