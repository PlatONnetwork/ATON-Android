package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class IndividualTransactionListAdapter extends CommonAdapter<TransactionEntity> {

    private final static String TAG = IndividualTransactionListAdapter.class.getSimpleName();

    private String walletAddress;

    public IndividualTransactionListAdapter(int layoutId, List<TransactionEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, TransactionEntity item, int position) {
        if (item != null) {
            if (item instanceof  IndividualTransactionEntity){
                IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
                IndividualTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
                boolean                                       isReceiver        = entity.isReceiver(walletAddress);
                viewHolder.setText(R.id.tv_transaction_status, entity.isReceiver(walletAddress) ? context.getString(R.string.receive) : context.getString(R.string.send));
                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, entity.getSignedBlockNumber(), 12));
                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
                viewHolder.setImageResource(R.id.iv_transaction_status, entity.isReceiver(walletAddress) ? R.drawable.icon_receive_transaction : R.drawable.icon_send_transation);
            }else if (item instanceof  SharedTransactionEntity){
                SharedTransactionEntity entity = (SharedTransactionEntity) item;
                SharedTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
                boolean                                   isReceiver        = entity.isReceiver(walletAddress);
                boolean isCreateJointWallet = transactionStatus == TransactionEntity.TransactionStatus.CREATE_JOINT_WALLET;
                viewHolder.setText(R.id.tv_transaction_status, isCreateJointWallet ? context.getString(R.string.create_joint_wallet) : context.getString(R.string.joint_wallet_execution));
                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, entity.getConfirms(), entity.getRequiredSignNumber()));
                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
                viewHolder.setImageResource(R.id.iv_transaction_status, isCreateJointWallet ? R.drawable.icon_create_shared_wallet_transaction : R.drawable.icon_execute_shared_wallet_transaction);
            }
        }

    }


    public void notifyDataChanged(List<TransactionEntity> mDatas, String walletAddress) {
        this.mDatas = mDatas;
        this.walletAddress = walletAddress;
        notifyDataSetChanged();
    }
}
