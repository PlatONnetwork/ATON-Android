package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.engine.WalletManager;
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
        String walletAddress = WalletManager.getInstance().getSelectedWalletAddress();
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

    /**
     * 如果当前是合约地址，则不需要将创建合约和部署合约的加到当前列表中
     *
     * @param transactionEntity
     */
    public void addItem(TransactionEntity transactionEntity) {
        if (mDatas != null && isNeedUpdateItem(transactionEntity)) {
            mDatas.add(transactionEntity);
            Collections.sort(mDatas);
            notifyDataChanged(mDatas);
        }
    }

    /**
     * 是否需要更新item
     * 1.普通钱包
     * 2.联名钱包交易，如果是非交易类型，则只显示在联名钱包创建者交易列表里
     *
     * @param transactionEntity
     * @return
     */
    private boolean isNeedUpdateItem(TransactionEntity transactionEntity) {
        String walletAddress = WalletManager.getInstance().getSelectedWalletAddress();
        //地址是否与交易有关
        if (!transactionEntity.isRelevantWalletAddress(walletAddress)) {
            return false;
        }
        if (transactionEntity instanceof SharedTransactionEntity) {
            SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) transactionEntity;
            SharedTransactionEntity.TransactionType transactionType = SharedTransactionEntity.TransactionType.getTransactionType(sharedTransactionEntity.getTransactionType());
            //创建联名钱包+执行联名钱包只在创建者交易记录里展示
            return transactionType == SharedTransactionEntity.TransactionType.SEND_TRANSACTION || sharedTransactionEntity.getFromAddress().equals(walletAddress);
        }
        if (transactionEntity instanceof VoteTransactionEntity) {
            return ((VoteTransactionEntity) transactionEntity).isVoter(walletAddress);
        }
        return true;
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

    public void notifyDataChanged(List<TransactionEntity> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

}
