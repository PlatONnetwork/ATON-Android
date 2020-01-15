package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.entity.VerifyNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyNodeDiffCallback extends BaseDiffCallback<VerifyNode> {

    /**
     * 节点排名
     */
    public final static String KEY_RANKING = "key_ranking";
    /**
     * 节点名称
     */
    public final static String KEY_NAME = "key_name";
    /**
     * 节点委托数量以及节点委托者数
     */
    public final static String KEY_DEPOSIT_DELEGATOR_NUMBER = "key_deposit_delegator_number";
    /**
     * 节点委托数量
     */
    public final static String KEY_DEPOSIT = "key_deposit";
    /**
     * /**
     * 节点委托者数
     */
    public final static String KEY_DELEGATOR_NUMBER = "key_delegator_number";

    /**
     * 节点头像
     */
    public final static String KEY_URL = "key_url";
    /**
     * 节点收益率
     */
    public final static String KEY_RATEPA = "key_ratePA";
    /**
     * 节点状态
     */
    public final static String KEY_NODE_STATUS_DESC = "key_node_status_desc";

    public VerifyNodeDiffCallback(List<VerifyNode> oldList, List<VerifyNode> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getNodeId().equals(mNewList.get(newItemPosition).getNodeId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        VerifyNode oldVerifyNode = mOldList.get(oldItemPosition);
        VerifyNode newVerifyNode = mNewList.get(newItemPosition);

        if (oldVerifyNode.getRanking() != newVerifyNode.getRanking()) {
            return false;
        }

        if (!TextUtils.equals(oldVerifyNode.getName(), newVerifyNode.getName())) {
            return false;
        }

        if (!oldVerifyNode.getDelegateSum().equals(newVerifyNode.getDelegateSum())) {
            return false;
        }

        if (!TextUtils.equals(oldVerifyNode.getDelegate(), newVerifyNode.getDelegate())) {
            return false;
        }

        if (!TextUtils.equals(oldVerifyNode.getUrl(), newVerifyNode.getUrl())) {
            return false;
        }

        if (oldVerifyNode.getNodeStatusDescRes() != newVerifyNode.getNodeStatusDescRes()) {
            return false;
        }

        if (!TextUtils.equals(oldVerifyNode.getShowDelegatedRatePA(), newVerifyNode.getShowDelegatedRatePA())) {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        VerifyNode oldVerifyNode = mOldList.get(oldItemPosition);
        VerifyNode newVerifyNode = mNewList.get(newItemPosition);

        Bundle bundle = new Bundle();

        if (oldVerifyNode.getRanking() != newVerifyNode.getRanking()) {
            bundle.putInt(KEY_RANKING, newVerifyNode.getRanking());
        }

        if (!oldVerifyNode.getName().equals(newVerifyNode.getName())) {
            bundle.putString(KEY_NAME, newVerifyNode.getName());
        }

        if (!oldVerifyNode.getDelegateSum().equals(newVerifyNode.getDelegateSum()) || !TextUtils.equals(oldVerifyNode.getDelegate(), newVerifyNode.getDelegate())) {
            HashMap<String, String> map = new HashMap<>();
            map.put(KEY_DELEGATOR_NUMBER, newVerifyNode.getDelegate());
            map.put(KEY_DEPOSIT, newVerifyNode.getDelegateSum());
            bundle.putSerializable(KEY_DEPOSIT_DELEGATOR_NUMBER, map);
        }

        if (!TextUtils.equals(oldVerifyNode.getUrl(), newVerifyNode.getUrl())) {
            bundle.putString(KEY_URL, newVerifyNode.getUrl());
        }


        if (oldVerifyNode.getNodeStatusDescRes() != newVerifyNode.getNodeStatusDescRes()) {
            bundle.putInt(KEY_NODE_STATUS_DESC, newVerifyNode.getNodeStatusDescRes());
        }

        if (!TextUtils.equals(oldVerifyNode.getShowDelegatedRatePA(), newVerifyNode.getShowDelegatedRatePA())) {
            bundle.putString(KEY_RATEPA, newVerifyNode.getShowDelegatedRatePA());
        }

        return bundle.isEmpty() ? null : bundle;
    }
}
