package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.entity.VerifyNode;

import java.util.List;

public class VerifyNodeDiffCallback extends BaseDiffCallback<VerifyNode> {

    public final static String KEY_RANKING = "key_ranking";

    public final static String KEY_NAME = "key_name";

    public final static String KEY_DEPOSIT = "key_deposit";

    public final static String KEY_URL = "key_url";

    public final static String KEY_RATEPA = "key_ratePA";

    public final static String KEY_NODESTATUS = "key_nodeStatus";

    public final static String KEY_ISINIT = "key_isInit";

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

        if (!oldVerifyNode.getName().equals(newVerifyNode.getName())) {
            return false;
        }

        if (!oldVerifyNode.getDeposit().equals(newVerifyNode.getName())) {
            return false;
        }

        if (oldVerifyNode.getUrl() != null) {
            if (!oldVerifyNode.getUrl().equals(newVerifyNode.getUrl())) {
                return false;
            }
        } else {
            if (newVerifyNode.getUrl() != null) {
                return false;
            }
        }

        if (!oldVerifyNode.getRatePA().equals(newVerifyNode.getRatePA())) {
            return false;
        }

        if (!oldVerifyNode.getNodeStatus().equals(newVerifyNode.getNodeStatus())) {
            return false;
        }

        if (oldVerifyNode.isInit() != newVerifyNode.isInit()) {
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

        if (!oldVerifyNode.getDeposit().equals(newVerifyNode.getName())) {
            bundle.putString(KEY_DEPOSIT, newVerifyNode.getDeposit());
        }

        if (oldVerifyNode.getUrl() != null) {
            if (!oldVerifyNode.getUrl().equals(newVerifyNode.getUrl())) {
                bundle.putString(KEY_URL, newVerifyNode.getUrl());
            }
        } else {
            if (newVerifyNode.getUrl() != null) {
                bundle.putString(KEY_URL, newVerifyNode.getUrl());
            }
        }

        if (!oldVerifyNode.getRatePA().equals(newVerifyNode.getRatePA())) {
            bundle.putString(KEY_RATEPA, newVerifyNode.getRatePA());
        }

        if (!oldVerifyNode.getNodeStatus().equals(newVerifyNode.getNodeStatus())) {
            bundle.putString(KEY_NODESTATUS, newVerifyNode.getNodeStatus());
        }

        if (oldVerifyNode.isInit() != newVerifyNode.isInit()) {
            bundle.putBoolean(KEY_ISINIT, newVerifyNode.isInit());
        }

        return bundle;
    }
}
