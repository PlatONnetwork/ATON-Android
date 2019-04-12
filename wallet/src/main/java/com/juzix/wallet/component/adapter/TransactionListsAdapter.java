package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;
import com.juzix.wallet.utils.DateUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author matrixelement
 */
public class TransactionListsAdapter extends CommonAdapter<TransactionEntity> {

    private final static String TAG = TransactionListsAdapter.class.getSimpleName();

    private String walletAddress;

    private Context mContext;

    public TransactionListsAdapter(int layoutId, List<TransactionEntity> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, TransactionEntity item, int position) {
        this.mContext = context;
        convert(context, viewHolder, item);
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {
        super.updateItemView(context, position, viewHolder);
        convert(context, viewHolder, mDatas.get(position));
    }

    private void convert(Context context, ViewHolder viewHolder, TransactionEntity item) {
        if (item instanceof IndividualTransactionEntity) {
            IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
            IndividualTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
            boolean isReceiver = entity.isReceiver(walletAddress);
            viewHolder.setVisible(R.id.v_new_msg, false);
            viewHolder.setText(R.id.tv_transaction_status, entity.isReceiver(walletAddress) ? context.getString(R.string.receive) : context.getString(R.string.send));
            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
            viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
            viewHolder.setText(R.id.tv_transaction_status_desc, entity.isCompleted() ? context.getString(R.string.success) : context.getString(R.string.pending));
            viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
            viewHolder.setImageResource(R.id.iv_transaction_status, entity.isReceiver(walletAddress) ? R.drawable.icon_receive_transaction : R.drawable.icon_send_transation);
        } else if (item instanceof VoteTransactionEntity) {
            VoteTransactionEntity entity = (VoteTransactionEntity) item;
            VoteTransactionEntity.TransactionStatus transactionStatus = entity.getTransactionStatus();
            boolean isReceiver = entity.isReceiver(walletAddress);
            viewHolder.setVisible(R.id.v_new_msg, false);
            viewHolder.setText(R.id.tv_transaction_status, context.getString(R.string.vote));
            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(entity.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
            viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(entity.getValue()))));
            viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, 12, 12));
            viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
            viewHolder.setImageResource(R.id.iv_transaction_status, R.drawable.icon_valid_ticket);
        } else if (item instanceof SharedTransactionEntity) {
            SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) item;
            SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType());
            SharedTransactionEntity.TransactionStatus transactionStatus = item.getTransactionStatus();
            viewHolder.setVisible(R.id.v_new_msg, !sharedTransactionEntity.isRead());
            viewHolder.setText(R.id.tv_transaction_status, context.getString(transactionType.getTransactionTypeDesc(sharedTransactionEntity.getToAddress(), walletAddress)));
            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(item.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
            viewHolder.setText(R.id.tv_transaction_amount, context.getString(R.string.amount_with_unit, String.format("-%s", NumberParserUtils.getPrettyBalance(sharedTransactionEntity.getValue()))));
            viewHolder.setText(R.id.tv_transaction_status_desc, transactionStatus.getStatusDesc(context, sharedTransactionEntity.getApprovalCount(), sharedTransactionEntity.getRequiredSignNumber()));
            viewHolder.setTextColor(R.id.tv_transaction_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
            viewHolder.setImageResource(R.id.iv_transaction_status, transactionType == SharedTransactionEntity.TransactionType.CREATE_JOINT_WALLET ? R.drawable.icon_create_shared_wallet_transaction : transactionType == SharedTransactionEntity.TransactionType.SEND_TRANSACTION ? R.drawable.icon_send_transation : R.drawable.icon_execute_shared_wallet_transaction);
        }

    }

    public void updateItem(ListView listView, String uuid, boolean hasUnread) {
        int position = getPositionByUUID(uuid);
        if (position != -1) {
            TransactionEntity transactionEntity = mDatas.get(position);
            if (transactionEntity instanceof SharedTransactionEntity) {
                ((SharedTransactionEntity) transactionEntity).setRead(!hasUnread);
                updateItem(mContext, listView, transactionEntity);
            }
        }
    }

    public void addItem(TransactionEntity transactionEntity) {
        if (mDatas != null && transactionEntity.isRelevantWalletAddress(walletAddress)) {
            mDatas.add(transactionEntity);
            Collections.sort(mDatas);
            notifyDataChanged(mDatas);
        }
    }

    private int getPositionByUUID(String uuid) {
        if (mDatas != null && !mDatas.isEmpty()) {
            for (int i = 0; i < mDatas.size(); ) {
                TransactionEntity transactionEntity = mDatas.get(i);
                if (uuid.equals(transactionEntity.getUuid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void notifyDataChanged(List<TransactionEntity> mDatas, String walletAddress) {
        this.mDatas = mDatas;
        this.walletAddress = walletAddress;
        notifyDataSetChanged();
    }

}
