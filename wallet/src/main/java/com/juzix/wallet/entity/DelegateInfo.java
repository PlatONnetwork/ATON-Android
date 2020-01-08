package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DelegateInfo implements Parcelable {
    /**
     * 钱包名称
     */
    private String walletName;

    /**
     * 钱包地址
     */
    private String walletAddress;

    /**
     * 钱包头像
     */

    private String walletIcon;

    /**
     * 累计的奖励  单位von   1LAT(ETH)=1000000000000000000von(wei)
     */
    private String cumulativeReward;

    /**
     * 待领取的奖励，单位von
     */
    private String withdrawReward;

    /**
     * 总委托  单位von   1LAT(ETH)=1000000000000000000von(wei)
     */
    private String delegated;


    public DelegateInfo() {

    }

    protected DelegateInfo(Parcel in) {
        walletName = in.readString();
        walletAddress = in.readString();
        walletIcon = in.readString();
        cumulativeReward =in.readString();
        withdrawReward = in.readString();
        delegated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(walletName);
        dest.writeString(walletAddress);
        dest.writeString(walletIcon);
        dest.writeString(cumulativeReward);
        dest.writeString(withdrawReward);
        dest.writeString(delegated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DelegateInfo> CREATOR = new Creator<DelegateInfo>() {
        @Override
        public DelegateInfo createFromParcel(Parcel in) {
            return new DelegateInfo(in);
        }

        @Override
        public DelegateInfo[] newArray(int size) {
            return new DelegateInfo[size];
        }
    };

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getDelegated() {
        return delegated;
    }

    public void setDelegated(String delegated) {
        this.delegated = delegated;
    }

    public String getWalletIcon() {
        return walletIcon;
    }

    public void setWalletIcon(String walletIcon) {
        this.walletIcon = walletIcon;
    }

    public String getCumulativeReward() {
        return cumulativeReward;
    }

    public void setCumulativeReward(String cumulativeReward) {
        this.cumulativeReward = cumulativeReward;
    }

    public String getWithdrawReward() {
        return withdrawReward;
    }

    public void setWithdrawReward(String withdrawReward) {
        this.withdrawReward = withdrawReward;
    }
}
