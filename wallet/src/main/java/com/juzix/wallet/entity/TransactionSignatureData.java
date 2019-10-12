package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.utils.JSONUtil;

import java.util.List;

public class TransactionSignatureData implements Parcelable {

    @JSONField(name = "qrCodeData")
    private List<String> signedDatas;

    private String from;

    private String chainId;

    /**
     * @see org.web3j.platon.FunctionType
     */
    private int functionType;

    private long timestamp;

    public TransactionSignatureData() {
    }

    public TransactionSignatureData(List<String> signedDatas, String from, String chainId,long timeStamp,int functionType) {
        this.signedDatas = signedDatas;
        this.from = from;
        this.chainId = chainId;
        this.timestamp = timeStamp;
        this.functionType = functionType;
    }

    protected TransactionSignatureData(Parcel in) {
        signedDatas = in.createStringArrayList();
        from = in.readString();
        chainId = in.readString();
        timestamp = in.readLong();
        functionType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(signedDatas);
        dest.writeString(from);
        dest.writeString(chainId);
        dest.writeLong(timestamp);
        dest.writeInt(functionType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionSignatureData> CREATOR = new Creator<TransactionSignatureData>() {
        @Override
        public TransactionSignatureData createFromParcel(Parcel in) {
            return new TransactionSignatureData(in);
        }

        @Override
        public TransactionSignatureData[] newArray(int size) {
            return new TransactionSignatureData[size];
        }
    };

    public List<String> getSignedDatas() {
        return signedDatas;
    }

    public void setSignedDatas(List<String> signedDatas) {
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

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public @QrCodeType
    int getQrCodeType() {
        return QrCodeType.TRANSACTION_SIGNATURE;
    }

    public String toJSONString() {
        return JSONUtil.toJSONString(this);
    }
}
