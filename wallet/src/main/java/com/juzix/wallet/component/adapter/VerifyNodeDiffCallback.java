package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.entity.VerifyNode;

import java.util.List;

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
     * 节点委托数量
     */
    public final static String KEY_DEPOSIT = "key_deposit";
    /**
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
    public final static String KEY_NODESTATUS = "key_nodeStatus";

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


        if (!oldVerifyNode.getDelegatedRatePA().equals(newVerifyNode.getDelegatedRatePA())) {
            return false;
        }

        if (!oldVerifyNode.getNodeStatus().equals(newVerifyNode.getNodeStatus())) {
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

        if (!oldVerifyNode.getDelegateSum().equals(newVerifyNode.getName())) {
            bundle.putString(KEY_DEPOSIT, newVerifyNode.getDelegateSum());
        }

        if (!TextUtils.equals(oldVerifyNode.getDelegate(), newVerifyNode.getDelegate())) {
            bundle.putString(KEY_DELEGATOR_NUMBER, newVerifyNode.getDelegate());
        }

        if (!TextUtils.equals(oldVerifyNode.getUrl(), newVerifyNode.getUrl())) {
            bundle.putString(KEY_DELEGATOR_NUMBER, newVerifyNode.getDelegate());
        }


        if (!oldVerifyNode.getDelegatedRatePA().equals(newVerifyNode.getDelegatedRatePA())) {
            bundle.putString(KEY_RATEPA, newVerifyNode.getDelegatedRatePA());
        }

        if (!oldVerifyNode.getNodeStatus().equals(newVerifyNode.getNodeStatus())) {
            bundle.putString(KEY_NODESTATUS, newVerifyNode.getNodeStatus());
        }


        return bundle;
    }
}
