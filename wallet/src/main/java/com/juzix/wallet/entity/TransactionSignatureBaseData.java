package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TransactionSignatureBaseData implements Parcelable {

    private String signedDatas;

    private String from;

    private String chainId;

    public TransactionSignatureBaseData() {
    }

    public TransactionSignatureBaseData(String signedDatas, String from, String chainId) {
        this.signedDatas = signedDatas;
        this.from = from;
        this.chainId = chainId;
    }

    protected TransactionSignatureBaseData(Parcel in) {
        signedDatas = in.readString();
        from = in.readString();
        chainId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(signedDatas);
        dest.writeString(from);
        dest.writeString(chainId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionSignatureBaseData> CREATOR = new Creator<TransactionSignatureBaseData>() {
        @Override
        public TransactionSignatureBaseData createFromParcel(Parcel in) {
            return new TransactionSignatureBaseData(in);
        }

        @Override
        public TransactionSignatureBaseData[] newArray(int size) {
            return new TransactionSignatureBaseData[size];
        }
    };

    public String getSignedDatas() {
        return signedDatas;
    }

    public void setSignedDatas(String signedDatas) {
        this.signedDatas = signedDatas;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }
}
