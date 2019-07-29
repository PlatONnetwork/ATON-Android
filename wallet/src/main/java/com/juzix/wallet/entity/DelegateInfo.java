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
     * 委托金额
     */
    private double delegate;

    /**
     * 赎回中
     */
    private double redeem;


    /**
     * 钱包余额
     */
    private double balance;

    /**
     * 钱包头像
     */

    private String walletIcon;


    public DelegateInfo() {

    }


    protected DelegateInfo(Parcel in) {
        walletName = in.readString();
        walletAddress = in.readString();
        delegate = in.readDouble();
        redeem = in.readDouble();
        balance = in.readDouble();
        walletIcon = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(walletName);
        dest.writeString(walletAddress);
        dest.writeDouble(delegate);
        dest.writeDouble(redeem);
        dest.writeDouble(balance);
        dest.writeString(walletIcon);
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

    public double getDelegate() {
        return delegate;
    }

    public void setDelegate(double delegate) {
        this.delegate = delegate;
    }

    public double getRedeem() {
        return redeem;
    }

    public void setRedeem(double redeem) {
        this.redeem = redeem;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getWalletIcon() {
        return walletIcon;
    }

    public void setWalletIcon(String walletIcon) {
        this.walletIcon = walletIcon;
    }
}
