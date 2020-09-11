package com.platon.aton.component.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletSelectedIndex;

import java.util.List;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class WalletListDiffCallback extends BaseDiffCallback<Wallet> {

    /**
     * 钱包名称
     */
    public final static String KEY_WALLET_NAME = "wallet_name";
    /**
     * 钱包是否被选中
     */
    public final static String KEY_WALLET_SELECTED = "wallet_selected";

    public WalletListDiffCallback(List<Wallet> oldList, List<Wallet> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {

        Wallet oldWallet = mOldList.get(oldPosition);
        Wallet newWallet = mNewList.get(newPosition);

        if (!TextUtils.equals(oldWallet.getUuid(), newWallet.getUuid())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {

        Wallet oldWallet = mOldList.get(oldPosition);
        Wallet newWallet = mNewList.get(newPosition);

        if (!TextUtils.equals(oldWallet.getName(), newWallet.getName())) {
            return false;
        }

        if (oldWallet.getSelectedIndex() != newWallet.getSelectedIndex()) {
            return false;
        }

       /* if (oldWallet.isBackedUpPrompt() != newWallet.isBackedUpPrompt()) {
            return false;
        }*/

        return true;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Bundle bundle = new Bundle();

        Wallet oldWallet = mOldList.get(oldItemPosition);
        Wallet newWallet = mNewList.get(newItemPosition);

        if (!TextUtils.equals(oldWallet.getName(), newWallet.getName())) {
            bundle.putString(KEY_WALLET_NAME, newWallet.getName());
        }

        if (oldWallet.getSelectedIndex() != newWallet.getSelectedIndex()) {
            if(newWallet.getSelectedIndex() == WalletSelectedIndex.SELECTED){
                bundle.putBoolean(KEY_WALLET_SELECTED, true);
            }else{
                bundle.putBoolean(KEY_WALLET_SELECTED, false);
            }

        }

        return bundle;

    }
}
