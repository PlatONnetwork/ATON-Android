package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class DelegateNodeDetail implements Parcelable {

    private String availableDelegationBalance;

    private String delegated;

    @JSONField(name = "item")
    private List<DelegateItemInfo> delegateItemInfoList;

    public DelegateNodeDetail() {
    }

    public DelegateNodeDetail(String availableDelegationBalance, String delegated, List<DelegateItemInfo> delegateItemInfoList) {
        this.availableDelegationBalance = availableDelegationBalance;
        this.delegated = delegated;
        this.delegateItemInfoList = delegateItemInfoList;
    }

    protected DelegateNodeDetail(Parcel in) {
        availableDelegationBalance = in.readString();
        delegated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(availableDelegationBalance);
        dest.writeString(delegated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DelegateNodeDetail> CREATOR = new Creator<DelegateNodeDetail>() {
        @Override
        public DelegateNodeDetail createFromParcel(Parcel in) {
            return new DelegateNodeDetail(in);
        }

        @Override
        public DelegateNodeDetail[] newArray(int size) {
            return new DelegateNodeDetail[size];
        }
    };

    public String getAvailableDelegationBalance() {
        return availableDelegationBalance;
    }

    public void setAvailableDelegationBalance(String availableDelegationBalance) {
        this.availableDelegationBalance = availableDelegationBalance;
    }

    public String getDelegated() {
        return delegated;
    }

    public void setDelegated(String delegated) {
        this.delegated = delegated;
    }

    public List<DelegateItemInfo> getDelegateItemInfoList() {
        return delegateItemInfoList;
    }

    public void setDelegateItemInfoList(List<DelegateItemInfo> delegateItemInfoList) {
        this.delegateItemInfoList = delegateItemInfoList;
    }
}
