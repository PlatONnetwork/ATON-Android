package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ClaimReward implements Parcelable {

    /**
     * 节点id
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 奖励
     */
    private String reward;

    public ClaimReward() {
    }

    protected ClaimReward(Parcel in) {
        nodeId = in.readString();
        nodeName = in.readString();
        reward = in.readString();
    }

    public static final Creator<ClaimReward> CREATOR = new Creator<ClaimReward>() {
        @Override
        public ClaimReward createFromParcel(Parcel in) {
            return new ClaimReward(in);
        }

        @Override
        public ClaimReward[] newArray(int size) {
            return new ClaimReward[size];
        }
    };

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    @Override
    public int hashCode() {

        int result = TextUtils.isEmpty(nodeId) ? 0 : nodeId.hashCode();
        result = 31 * result + (TextUtils.isEmpty(nodeName) ? 0 : nodeName.hashCode());
        result = 31 * result + (TextUtils.isEmpty(reward) ? 0 : reward.hashCode());

        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        ClaimReward claimReward = (ClaimReward) obj;

        return TextUtils.equals(nodeId, claimReward.nodeId) && TextUtils.equals(nodeName, claimReward.nodeName) && TextUtils.equals(reward, claimReward.reward);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(nodeName);
        dest.writeString(reward);
    }
}
