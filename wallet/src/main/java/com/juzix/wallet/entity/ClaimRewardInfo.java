package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ClaimRewardInfo implements Parcelable {

    private String claimRewardAmount;

    private String avaliableBalanceAmount;

    private String feeAmount;

    private String fromWalletName;

    protected ClaimRewardInfo(Parcel in) {
        claimRewardAmount = in.readString();
        avaliableBalanceAmount = in.readString();
        feeAmount = in.readString();
        fromWalletName = in.readString();
    }

    public ClaimRewardInfo(Builder builder) {
        this.claimRewardAmount = builder.claimRewardAmount;
        this.avaliableBalanceAmount = builder.avaliableBalanceAmount;
        this.feeAmount = builder.feeAmount;
        this.fromWalletName = builder.fromWalletName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(claimRewardAmount);
        dest.writeString(avaliableBalanceAmount);
        dest.writeString(feeAmount);
        dest.writeString(fromWalletName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClaimRewardInfo> CREATOR = new Creator<ClaimRewardInfo>() {
        @Override
        public ClaimRewardInfo createFromParcel(Parcel in) {
            return new ClaimRewardInfo(in);
        }

        @Override
        public ClaimRewardInfo[] newArray(int size) {
            return new ClaimRewardInfo[size];
        }
    };

    public String getClaimRewardAmount() {
        return claimRewardAmount;
    }

    public void setClaimRewardAmount(String claimRewardAmount) {
        this.claimRewardAmount = claimRewardAmount;
    }

    public String getAvaliableBalanceAmount() {
        return avaliableBalanceAmount;
    }

    public void setAvaliableBalanceAmount(String avaliableBalanceAmount) {
        this.avaliableBalanceAmount = avaliableBalanceAmount;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getFromWalletName() {
        return fromWalletName;
    }

    public void setFromWalletName(String fromWalletName) {
        this.fromWalletName = fromWalletName;
    }


   public static final class Builder {
        private String claimRewardAmount;
        private String avaliableBalanceAmount;
        private String feeAmount;
        private String fromWalletName;

        public Builder setClaimRewardAmount(String claimRewardAmount) {
            this.claimRewardAmount = claimRewardAmount;
            return this;
        }

        public Builder setAvaliableBalanceAmount(String avaliableBalanceAmount) {
            this.avaliableBalanceAmount = avaliableBalanceAmount;
            return this;
        }

        public Builder setFeeAmount(String feeAmount) {
            this.feeAmount = feeAmount;
            return this;
        }

        public Builder setFromWalletName(String fromWalletName) {
            this.fromWalletName = fromWalletName;
            return this;
        }

        public ClaimRewardInfo build() {
            return new ClaimRewardInfo(this);
        }
    }
}
