package com.juzix.wallet.component.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.entity.ClaimReward;
import com.juzix.wallet.entity.ClaimRewardRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClaimRewardRecordDiffCallback extends BaseDiffCallback<ClaimRewardRecord> {

    public final static String KEY_CLAIM_REWARD_ADDRESS = "key_claim_reward_address";
    public final static String KEY_CLAIM_REWARD_AMOUNT = "key_claim_reward_amount";
    public final static String KEY_CLAIM_REWARD_LIST = "key_claim_reward";

    public ClaimRewardRecordDiffCallback(List<ClaimRewardRecord> oldList, List<ClaimRewardRecord> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return mOldList.get(i).getTimestamp() == mNewList.get(i).getTimestamp();
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {

        ClaimRewardRecord oldClaimRewardRecord = mOldList.get(i);
        ClaimRewardRecord newClaimRewardRecord = mNewList.get(i);

        if (!TextUtils.equals(oldClaimRewardRecord.getAddress(), newClaimRewardRecord.getAddress())) {
            return false;
        }

        if (!TextUtils.equals(oldClaimRewardRecord.getTotalReward(), newClaimRewardRecord.getTotalReward())) {
            return false;
        }

        List<ClaimReward> oldClaimRewardList = oldClaimRewardRecord.getClaimRewardList();
        List<ClaimReward> newClaimRewardList = newClaimRewardRecord.getClaimRewardList();

        if (oldClaimRewardList == null) {
            return newClaimRewardList == null;
        } else {
            if (newClaimRewardList == null) {
                return false;
            }
            Collections.sort(oldClaimRewardList, new ClaimRewardComparator());
            Collections.sort(newClaimRewardList, new ClaimRewardComparator());

            return oldClaimRewardList.size() == newClaimRewardList.size() && oldClaimRewardList.equals(newClaimRewardList);
        }

    }

    @Nullable
    @Override
    public Bundle getChangePayload(int oldItemPosition, int newItemPosition) {

        ClaimRewardRecord oldClaimRewardRecord = mOldList.get(oldItemPosition);
        ClaimRewardRecord newClaimRewardRecord = mNewList.get(newItemPosition);

        Bundle bundle = new Bundle();

        if (!TextUtils.equals(oldClaimRewardRecord.getAddress(), newClaimRewardRecord.getAddress())) {
            bundle.putString(KEY_CLAIM_REWARD_ADDRESS, newClaimRewardRecord.getAddress());
        }

        if (!TextUtils.equals(oldClaimRewardRecord.getTotalReward(), newClaimRewardRecord.getTotalReward())) {
            bundle.putString(KEY_CLAIM_REWARD_AMOUNT, newClaimRewardRecord.getTotalReward());
        }

        List<ClaimReward> oldClaimRewardList = oldClaimRewardRecord.getClaimRewardList();
        List<ClaimReward> newClaimRewardList = newClaimRewardRecord.getClaimRewardList();

        if (oldClaimRewardList == null) {
            if (newClaimRewardList != null) {
                bundle.putParcelableArrayList(KEY_CLAIM_REWARD_LIST, (ArrayList<? extends Parcelable>) newClaimRewardList);
            }
        } else {
            if (newClaimRewardList == null) {
                bundle.putParcelableArrayList(KEY_CLAIM_REWARD_LIST, (ArrayList<? extends Parcelable>) newClaimRewardList);
            }
            Collections.sort(oldClaimRewardList, new ClaimRewardComparator());
            Collections.sort(newClaimRewardList, new ClaimRewardComparator());

            if (oldClaimRewardList.size() != newClaimRewardList.size() || !oldClaimRewardList.equals(newClaimRewardList)) {
                bundle.putParcelableArrayList(KEY_CLAIM_REWARD_LIST, (ArrayList<? extends Parcelable>) newClaimRewardList);
            }
        }

        return bundle.isEmpty() ? null : bundle;
    }

    static class ClaimRewardComparator implements Comparator<ClaimReward> {

        @Override
        public int compare(ClaimReward o1, ClaimReward o2) {
            return o1.getNodeName().compareTo(o2.getNodeName());
        }
    }
}
