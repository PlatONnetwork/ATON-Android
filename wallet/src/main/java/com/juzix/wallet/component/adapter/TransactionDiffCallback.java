package com.juzix.wallet.component.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.juzix.wallet.entity.Transaction;

import java.util.List;

public class TransactionDiffCallback extends BaseDiffCallback<Transaction> {

    public final static String KEY_TRANSACTION = "key_transaction";

    public TransactionDiffCallback(List<Transaction> oldList, List<Transaction> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return !TextUtils.isEmpty(mOldList.get(oldItemPosition).getHash()) && mOldList.get(oldItemPosition).getHash().equals(mNewList.get(newItemPosition).getHash());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Transaction oldTransaction = mOldList.get(oldItemPosition);
        Transaction newTransaction = mNewList.get(newItemPosition);

        if (oldTransaction.getTxReceiptStatus() != newTransaction.getTxReceiptStatus()) {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Transaction oldTransaction = mOldList.get(oldItemPosition);
        Transaction newTransaction = mNewList.get(newItemPosition);

        Bundle payload = new Bundle();

        if (oldTransaction.getTxReceiptStatus() != newTransaction.getTxReceiptStatus()) {
            payload.putParcelable(KEY_TRANSACTION, newTransaction);
        }

        return payload;
    }
}
