package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.platon.aton.utils.JSONUtil;

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

    private String nodeName;

    @JSONField(name = "rn")
    private String claimRewardAmount;

    @JSONField(name = "rk")
    private String remark;

    @JSONField(name = "si")
    private String signedMessage;

    @JSONField(name = "v")
    private int version;

    public TransactionSignatureData() {
    }

    public TransactionSignatureData(List<String> signedDatas, String from, String chainId, int functionType, long timestamp, String nodeName, String claimRewardAmount, String remark, String signedMessage, int version) {
        this.signedDatas = signedDatas;
        this.from = from;
        this.chainId = chainId;
        this.functionType = functionType;
        this.timestamp = timestamp;
        this.nodeName = nodeName;
        this.claimRewardAmount = claimRewardAmount;
        this.remark = remark;
        this.signedMessage = signedMessage;
        this.version = version;
    }

    protected TransactionSignatureData(Parcel in) {
        signedDatas = in.createStringArrayList();
        from = in.readString();
        chainId = in.readString();
        timestamp = in.readLong();
        functionType = in.readInt();
        nodeName = in.readString();
        claimRewardAmount = in.readString();
        remark = in.readString();
        signedMessage = in.readString();
        version = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(signedDatas);
        dest.writeString(from);
        dest.writeString(chainId);
        dest.writeLong(timestamp);
        dest.writeInt(functionType);
        dest.writeString(nodeName);
        dest.writeString(claimRewardAmount);
        dest.writeString(remark);
        dest.writeString(signedMessage);
        dest.writeInt(version);
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

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getClaimRewardAmount() {
        return claimRewardAmount;
    }

    public void setClaimRewardAmount(String claimRewardAmount) {
        this.claimRewardAmount = claimRewardAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSignedMessage() {
        return signedMessage;
    }

    public void setSignedMessage(String signedMessage) {
        this.signedMessage = signedMessage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isVersionNotEmpty() {
        return version > 0;
    }


    public @QrCodeType
    int getQrCodeType() {
        return QrCodeType.TRANSACTION_SIGNATURE;
    }

    public String toJSONString() {
        return JSONUtil.toJSONString(this);
    }


}
