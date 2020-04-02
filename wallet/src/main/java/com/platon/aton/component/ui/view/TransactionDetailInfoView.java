package com.platon.aton.component.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.TransferType;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.StringUtil;


public class TransactionDetailInfoView extends LinearLayout {

    public TransactionDetailInfoView(Context context) {
        this(context, null, 0);
    }

    public TransactionDetailInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionDetailInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_BEGINNING);
        setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.divider_transaction_info_item));
    }

    public void setData(Transaction transaction, String senderName, @TransferType int transferType) {

        removeAllViews();

        addView(transaction, senderName, transferType);
    }

    private void addView(Transaction transaction, String senderName, @TransferType int transferType) {

        int transactionTimeDescRes = getTransactionTimeDescRes(transaction.getTxReceiptStatus());

        switch (transaction.getTxType()) {
            case TRANSFER:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTransferDescRes(transferType))));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case CONTRACT_CREATION:
            case CONTRACT_EXECUTION:
            case MPC_TRANSACTION:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case OTHER_INCOME:
            case OTHER_EXPENSES:
                addView(getItemView(getStringWithColon(R.string.type), transferType == TransferType.SEND ? getString(R.string.other_expenses) : getString(R.string.other_income)));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case CREATE_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.version), transaction.getFormatVersion()));
                addView(getItemView(getStringWithColon(R.string.stake_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case EDIT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.stake_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case INCREASE_STAKING:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.stake_increase_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case EXIT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.return_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case DELEGATE:
            case UNDELEGATE:
                addView(getItemView(getStringWithColon(R.string.type), transaction.getTxType() == TransactionType.DELEGATE ? getString(R.string.delegate) : getString(R.string.undelegate)));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(transaction.getTxType() == TransactionType.DELEGATE ? R.string.delegated_to : R.string.undelegated_from), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(transaction.getTxType() == TransactionType.DELEGATE ? getStringWithColon(R.string.delegation_amount) : getStringWithColon(R.string.withdrawal_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                if (BigDecimalUtil.isBiggerThanZero(transaction.getTotalReward())) {
                    addView(getItemView(getStringWithColon(R.string.reward_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowTotalReward()))));
                }
                break;
            case CREATE_TEXT_PROPOSAL:
            case CREATE_UPGRADE_PROPOSAL:
            case CREATE_PARAMETER_PROPOSAL:
            case CANCEL_PROPOSAL:
            case VOTING_PROPOSAL:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.proposal_id), AddressFormatUtil.formatAddress(transaction.getProposalId()), transaction.getProposalId(), true));
                addView(getItemView(getStringWithColon(R.string.pip_number), TextUtils.isEmpty(transaction.getPiDID()) ? "--" : String.format("%s-%s", "PIP", transaction.getPiDID())));
                addView(getItemView(getStringWithColon(R.string.proposal_type), getString(transaction.getProposalTypeDescRes())));
                if (transaction.getTxType() == TransactionType.VOTING_PROPOSAL) {
                    addView(getItemView(getStringWithColon(R.string.vote), getString(transaction.getVoteOptionTypeDescRes())));
                }
                break;
            case DECLARE_VERSION:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), getString(transaction.getNodeName())));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.version), transaction.getFormatVersion()));
                break;
            case REPORT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.reported), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), AddressFormatUtil.formatAddress(transaction.getNodeId()), transaction.getNodeId(), true));
                addView(getItemView(getStringWithColon(R.string.report_type), getString(transaction.getReportTypeDescRes())));
                break;
            case CREATE_RESTRICTING:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.restricted_account), AddressFormatUtil.formatAddress(transaction.getLockAddress()), transaction.getLockAddress(), true));
                addView(getItemView(getStringWithColon(R.string.restricted_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            case CLAIM_REWARDS:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(transactionTimeDescRes), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.claim_wallet), senderName));
                addView(getItemView(getStringWithColon(R.string.reward_amount), getString(R.string.amount_with_unit, StringUtil.formatBalance(transaction.getShowValue()))));
                break;
            default:
                break;
        }

        addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
        if (!TextUtils.isEmpty(transaction.getBlockNumber())) {
            addView(getItemView(getStringWithColon(R.string.msg_confirmation_block), String.valueOf(transaction.getBlockNumber()), transaction.getBlockNumber(), true));
        }
        addView(getItemView(getStringWithColon(R.string.msg_transaction_hash), AddressFormatUtil.formatAddress(transaction.getHash()), transaction.getHash(), true));

    }

    private @StringRes
    int getTransactionTimeDescRes(TransactionStatus transactionStatus) {
        return transactionStatus == TransactionStatus.SUCCESSED ? R.string.msg_timestamp : R.string.submissionTime;
    }


    private View getItemView(String key, String value, String originalValue, boolean isShowCopyImage) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction_detail_info, null);
        TextView keyTv = contentView.findViewById(R.id.tv_key);
        TextView valueTv = contentView.findViewById(R.id.tv_value);
        ImageView iv_copy_hash = contentView.findViewById(R.id.iv_copy_hash);
        iv_copy_hash.setVisibility(isShowCopyImage ? View.VISIBLE : View.GONE);
        iv_copy_hash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.copyTextToClipboard(getContext(), originalValue);
            }
        });
        keyTv.setText(key);
        valueTv.setText(value);
        return contentView;
    }

    private View getItemView(String key, String value) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction_detail_info, null);
        TextView keyTv = contentView.findViewById(R.id.tv_key);
        TextView valueTv = contentView.findViewById(R.id.tv_value);
        ImageView iv_copy_hash = contentView.findViewById(R.id.iv_copy_hash);
        iv_copy_hash.setVisibility(View.GONE);
        keyTv.setText(key);
        valueTv.setText(value);
        return contentView;
    }

    private String getString(@StringRes int id) {
        return id == -1 ? "--" : getResources().getString(id);
    }

    private String getStringWithColon(@StringRes int id) {
        return String.format("%s:", getResources().getString(id));
    }

    private String getString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }

    private String getString(String text) {
        return TextUtils.isEmpty(text) ? "--" : text;
    }
}
