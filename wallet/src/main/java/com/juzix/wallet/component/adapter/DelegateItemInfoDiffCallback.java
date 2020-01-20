package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.juzix.wallet.entity.DelegateItemInfo;

import java.util.HashMap;
import java.util.List;

public class DelegateItemInfoDiffCallback extends BaseDiffCallback<DelegateItemInfo> {

    public final static String KEY_NODE_NAME = "key_node_name";
    public final static String KEY_URL = "key_url";
    public final static String KEY_NODE_STATUS = "key_node_status";
    public final static String KEY_CONSENSUS = "key_consensus";
    public final static String KEY_NODE_STATUS_AND_CONSENSUS = "key_node_status_and_consensus";
    public final static String KEY_DELEGATED = "key_delegated";
    public final static String KEY_WITHDRAW_REWARD = "key_withdraw_reward";
    public final static String KEY_RELEASED = "key_released";
    public final static String KEY_INIT = "key_init";
    public final static String KEY_NODE_STATUS_AND_INIT = "key_node_status_and_init";

    public DelegateItemInfoDiffCallback(List oldList, List newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return TextUtils.equals(mOldList.get(oldPosition).getNodeId(), mNewList.get(newPosition).getNodeId());
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        DelegateItemInfo oldDelegateItemInfo = mOldList.get(oldPosition);
        DelegateItemInfo newDelegateItemInfo = mNewList.get(newPosition);

        if (!TextUtils.equals(oldDelegateItemInfo.getNodeName(), newDelegateItemInfo.getNodeName())) {
            return false;
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getUrl(), newDelegateItemInfo.getUrl())) {
            return false;
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getNodeStatus(), newDelegateItemInfo.getNodeStatus())) {
            return false;
        }

        if (oldDelegateItemInfo.isConsensus() != newDelegateItemInfo.isConsensus()) {
            return false;
        }

        if (oldDelegateItemInfo.isInit() != newDelegateItemInfo.isInit()) {
            return false;
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getDelegated(), newDelegateItemInfo.getDelegated())) {
            return false;
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getWithdrawReward(), newDelegateItemInfo.getWithdrawReward())) {
            return false;
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getReleased(), newDelegateItemInfo.getReleased())) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Bundle bundle = new Bundle();

        DelegateItemInfo oldDelegateItemInfo = mOldList.get(oldItemPosition);
        DelegateItemInfo newDelegateItemInfo = mNewList.get(newItemPosition);

        if (!TextUtils.equals(oldDelegateItemInfo.getNodeName(), newDelegateItemInfo.getNodeName())) {
            bundle.putString(KEY_NODE_NAME, newDelegateItemInfo.getNodeName());
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getUrl(), newDelegateItemInfo.getUrl())) {
            bundle.putString(KEY_URL, newDelegateItemInfo.getUrl());
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getNodeStatus(), newDelegateItemInfo.getNodeStatus()) || oldDelegateItemInfo.isConsensus() != newDelegateItemInfo.isConsensus()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(KEY_NODE_STATUS, newDelegateItemInfo.getNodeStatus());
            map.put(KEY_CONSENSUS, newDelegateItemInfo.isConsensus());
            bundle.putSerializable(KEY_NODE_STATUS_AND_CONSENSUS, map);
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getNodeStatus(), newDelegateItemInfo.getNodeStatus()) || oldDelegateItemInfo.isInit() != newDelegateItemInfo.isInit()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(KEY_NODE_STATUS, newDelegateItemInfo.getNodeStatus());
            map.put(KEY_INIT, newDelegateItemInfo.isInit());
            bundle.putSerializable(KEY_NODE_STATUS_AND_INIT, map);
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getDelegated(), newDelegateItemInfo.getDelegated())) {
            bundle.putString(KEY_DELEGATED, newDelegateItemInfo.getDelegated());
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getWithdrawReward(), newDelegateItemInfo.getWithdrawReward())) {
            bundle.putString(KEY_WITHDRAW_REWARD, newDelegateItemInfo.getWithdrawReward());
        }

        if (!TextUtils.equals(oldDelegateItemInfo.getReleased(), newDelegateItemInfo.getReleased())) {
            bundle.putString(KEY_RELEASED, newDelegateItemInfo.getNodeName());
        }

        return bundle.isEmpty() ? null : bundle;
    }
}
