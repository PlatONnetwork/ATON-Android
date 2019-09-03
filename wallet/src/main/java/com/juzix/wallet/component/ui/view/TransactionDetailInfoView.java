package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

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

    public void setData(Transaction transaction,String queryAddress) {

        removeAllViews();

        addView(transaction,queryAddress);
    }

    private void addView(Transaction transaction,String queryAddress) {

        switch (transaction.getTxType()) {
            case TRANSFER:
                addView(getItemView(getStringWithColon(R.string.type), transaction.isSender(queryAddress) ? getString(R.string.send) : getString(R.string.receive)));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case CONTRACT_CREATION:
            case CONTRACT_EXECUTION:
            case MPC_TRANSACTION:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case OTHER_INCOME:
            case OTHER_EXPENSES:
                addView(getItemView(getStringWithColon(R.string.type), transaction.isSender(queryAddress) ? getString(R.string.other_expenses) : getString(R.string.other_income)));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.submissionAmount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case CREATE_VALIDATOR:
            case INCREASE_STAKING:
            case EDIT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.stake_amount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case EXIT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.return_amount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case DELEGATE:
            case UNDELEGATE:
                addView(getItemView(getStringWithColon(R.string.type), transaction.getTxType() == TransactionType.DELEGATE ? getString(R.string.delegate) : getString(R.string.undelegate)));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.delegated_to), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), transaction.getNodeId()));
                addView(getItemView(transaction.getTxType() == TransactionType.DELEGATE ? getStringWithColon(R.string.delegation_amount) : getStringWithColon(R.string.withdrawal_amount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case CREATE_TEXT_PROPOSAL:
            case CREATE_UPGRADE_PROPOSAL:
            case CREATE_PARAMETER_PROPOSAL:
            case VOTING_PROPOSAL:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), transaction.getNodeId()));
                addView(getItemView(getStringWithColon(R.string.proposal_id), transaction.getNodeId()));
                addView(getItemView(getStringWithColon(R.string.proposal_type), transaction.getProposalType()));
                if (transaction.getTxType() == TransactionType.VOTING_PROPOSAL) {
                    addView(getItemView(getStringWithColon(R.string.vote), transaction.getVote()));
                }
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case DECLARE_VERSION:
                addView(getItemView(getStringWithColon(R.string.type), transaction.getTxType() == TransactionType.DELEGATE ? getString(R.string.delegate) : getString(R.string.undelegate)));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.validator), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), transaction.getNodeId()));
                addView(getItemView(getStringWithColon(R.string.version), transaction.getVersion()));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case REPORT_VALIDATOR:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.reported), transaction.getNodeName()));
                addView(getItemView(getStringWithColon(R.string.nodeId), transaction.getNodeId()));
                addView(getItemView(getStringWithColon(R.string.report_type), transaction.getReportType()));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            case CREATE_RESTRICTING:
                addView(getItemView(getStringWithColon(R.string.type), getString(transaction.getTxType().getTxTypeDescRes())));
                addView(getItemView(getStringWithColon(R.string.submissionTime), transaction.getShowCreateTime()));
                addView(getItemView(getStringWithColon(R.string.restricted_account), transaction.getLockAddress()));
                addView(getItemView(getStringWithColon(R.string.restricted_amount), getString(R.string.amount_with_unit, transaction.getShowValue())));
                addView(getItemView(getStringWithColon(R.string.fee), getString(R.string.amount_with_unit, transaction.getShowActualTxCost())));
                break;
            default:
                break;
        }

    }


    private View getItemView(String key, String value) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction_detail_info, null);
        TextView keyTv = contentView.findViewById(R.id.tv_key);
        TextView valueTv = contentView.findViewById(R.id.tv_value);
        keyTv.setText(key);
        valueTv.setText(value);
        return contentView;
    }

    private String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    private String getStringWithColon(@StringRes int id) {
        return String.format("%s:", getResources().getString(id));
    }

    private String getString(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }
}
