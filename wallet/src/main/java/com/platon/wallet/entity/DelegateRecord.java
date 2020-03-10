package com.platon.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class DelegateRecord implements Parcelable, Comparable<DelegateRecord> {
    /**
     * 委托时间，时间戳（秒）
     */
    private String delegateTime;
    /**
     * 节点头像
     */
    private String url;

    /**
     * 委托钱包地址
     */
    private String walletAddress;

    /**
     * 委托节点名称
     */
    private String nodeName;

    /**
     * 委托的节点地址
     */
    private String nodeAddress;


    /**
     * 委托数/赎回数
     */
    private double number;
    /**
     * 排列序号：由区块号和交易索引拼接而成
     */
    private int sequence;
    /**
     * 委交易状态
     * 确认中 —— confirm
     * 委托成功 —— delegateSucc
     * 委托失败 —— delegateFail
     * 赎回中 —— redeem
     * 赎回成功 —— redeemSucc
     * 赎回失败 —— redeemFail
     */
    private String delegateStatus;

    /**
     * 钱包头像
     */

    private String walletIcon;

    /**
     * 钱包名称
     */

    private String walletName;

    public DelegateRecord() {

    }

    public String getDelegateTime() {
        return delegateTime;
    }

    public void setDelegateTime(String delegateTime) {
        this.delegateTime = delegateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDelegateStatus() {
        return delegateStatus;
    }

    public void setDelegateStatus(String delegateStatus) {
        this.delegateStatus = delegateStatus;
    }

    public String getWalletIcon() {
        return walletIcon;
    }

    public void setWalletIcon(String walletIcon) {
        this.walletIcon = walletIcon;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    protected DelegateRecord(Parcel in) {
        delegateTime = in.readString();
        url = in.readString();
        walletAddress = in.readString();
        nodeName = in.readString();
        nodeAddress = in.readString();
        number = in.readDouble();
        sequence = in.readInt();
        delegateStatus = in.readString();
        walletAddress = in.readString();
        walletName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(delegateTime);
        dest.writeString(url);
        dest.writeString(walletAddress);
        dest.writeString(nodeName);
        dest.writeString(nodeAddress);
        dest.writeDouble(number);
        dest.writeInt(sequence);
        dest.writeString(delegateStatus);
        dest.writeString(walletIcon);
        dest.writeString(walletAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DelegateRecord> CREATOR = new Creator<DelegateRecord>() {
        @Override
        public DelegateRecord createFromParcel(Parcel in) {
            return new DelegateRecord(in);
        }

        @Override
        public DelegateRecord[] newArray(int size) {
            return new DelegateRecord[size];
        }
    };

    //排序，暂时没实现
    @Override
    public int compareTo(@NonNull DelegateRecord o) {
        return 0;
    }
}
