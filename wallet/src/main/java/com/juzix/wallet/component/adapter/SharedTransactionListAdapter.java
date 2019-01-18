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
public class SharedTransactionListAdapter extends CommonAdapter<TransactionEntity> {

    private final static String TAG = SharedTransactionListAdapter.class.getSimpleName();

    private String walletAddress;

    public SharedTransactionListAdapter(int layoutId, List<TransactionEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, TransactionEntity item, int position) {
        if (item != null) {
            if (item instanceof SharedTransactionEntity) {
                SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) item;
                SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType());
                SharedTransactionEntity.TransactionStatus transactionStatus = item.getTransactionStatus();
                viewHolder.setVisible(R.id.v_new_msg, !sharedTransactionEntity.isRead());
                viewHolder.setText(R.id.tv_transaction_status, context.getString(transactionType.getTransactionTypeDesc()));
                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(item.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("-%s", NumberParserUtils.getPrettyBalance(sharedTransactionEntity.getValue()))));
                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, sharedTransactionEntity.getConfirms(), sharedTransactionEntity.getRequiredSignNumber()));
                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
                viewHolder.setImageResource(R.id.iv_transaction_status, transactionType == SharedTransactionEntity.TransactionType.CREATE_JOINT_WALLET ? R.drawable.icon_create_shared_wallet_transaction : transactionType == SharedTransactionEntity.TransactionType.SEND_TRANSACTION ? R.drawable.icon_send_transation : R.drawable.icon_execute_shared_wallet_transaction);
            } else {
                IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
                IndividualTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
                boolean isReceiver = entity.isReceiver(walletAddress);
                viewHolder.setText(R.id.tv_transaction_status, entity.isReceiver(walletAddress) ? context.getString(R.string.receive) : context.getString(R.string.send));
                viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
                viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, entity.getSignedBlockNumber(), 12));
                viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
                viewHolder.setImageResource(R.id.iv_transaction_status, entity.isReceiver(walletAddress) ? R.drawable.icon_receive_transaction : R.drawable.icon_send_transation);
            }
        }
    }


    public void notifyDataChanged(List<TransactionEntity> mDatas, String walletAddress) {
        this.mDatas = mDatas;
        this.walletAddress = walletAddress;
        notifyDataSetChanged();
    }

    public void notifyDataChanged(SharedTransactionEntity entity, String walletAddress) {
        this.mDatas = getList();
        for (TransactionEntity item : this.mDatas) {
            if (item.equals(entity.getUuid())) {
                ((SharedTransactionEntity) item).setRead(true);
            }
        }
        this.notifyDataChanged(mDatas);
        this.walletAddress = walletAddress;
    }
}
