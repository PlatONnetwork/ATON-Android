package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.BigIntegerUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class TransactionAuthorizationData implements Parcelable {

    @JSONField(name = "qrCodeData")
    private List<TransactionAuthorizationBaseData> baseDataList;

    protected long timestamp;

    public TransactionAuthorizationData() {

    }

    public TransactionAuthorizationData(List<TransactionAuthorizationBaseData> baseDataList, long timeStamp) {
        this.baseDataList = baseDataList;
        this.timestamp = timeStamp;
    }

    public List<TransactionAuthorizationBaseData> getBaseDataList() {
        return baseDataList;
    }

    public void setBaseDataList(List<TransactionAuthorizationBaseData> baseDataList) {
        this.baseDataList = baseDataList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    protected TransactionAuthorizationData(Parcel in) {
        baseDataList = in.createTypedArrayList(TransactionAuthorizationBaseData.CREATOR);
        timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(baseDataList);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionAuthorizationData> CREATOR = new Creator<TransactionAuthorizationData>() {
        @Override
        public TransactionAuthorizationData createFromParcel(Parcel in) {
            return new TransactionAuthorizationData(in);
        }

        @Override
        public TransactionAuthorizationData[] newArray(int size) {
            return new TransactionAuthorizationData[size];
        }
    };

    public @QrCodeType
    int getQrCodeType() {
        return QrCodeType.TRANSACTION_AUTHORIZATION;
    }

    @JSONField(serialize = false, deserialize = false)
    public TransactionAuthorizationDetail getTransactionAuthorizationDetail() {
        if (baseDataList == null || baseDataList.isEmpty()) {
            return null;
        }

        TransactionAuthorizationBaseData baseData = baseDataList.get(0);

        return new TransactionAuthorizationDetail(getSumAmount(), baseData.getPlatOnFunction().getType(), baseData.getFrom(), baseData.getTo(), getSumFee(), baseData.getNodeId(), baseData.getNodeName());

    }

    public String toJSONString() {
        return JSONUtil.toJSONString(this);
    }

    public TransactionSignatureData toTransactionSignatureData(Credentials credentials) {

        if (baseDataList == null || baseDataList.isEmpty()) {
            return null;
        }

        TransactionAuthorizationBaseData firstBaseData = baseDataList.get(0);

        return new TransactionSignatureData(getSignedMessageList(credentials), firstBaseData.getFrom(), firstBaseData.getChainId(), timestamp, firstBaseData.getPlatOnFunction().getType());
    }

    private List<String> getSignedMessageList(Credentials credentials) {
        if (baseDataList == null || baseDataList.isEmpty()) {
            return new ArrayList<>();
        }

        long nonce = NumberParserUtils.parseLong(baseDataList.get(0).nonce);
        List<String> signedMessageList = new ArrayList<>();

        for (int i = 0, size = baseDataList.size(); i < size; i++) {
            signedMessageList.add(getSignedMessage(baseDataList.get(i), credentials, String.valueOf(nonce++)));
        }

        return signedMessageList;

    }

    private String getSignedMessage(TransactionAuthorizationBaseData baseData, Credentials credentials, String nonce) {
        BigDecimal transferAmount = baseData.functionType == FunctionType.TRANSFER ? BigDecimalUtil.toBigDecimal(baseData.getAmount()) : BigDecimal.ZERO;
        String encodeData = baseData.functionType == FunctionType.TRANSFER ? "" : baseData.getPlatOnFunction().getEncodeData();
        return TransactionManager.getInstance().signTransaction(credentials, encodeData, baseData.getTo(), transferAmount, BigIntegerUtil.toBigInteger(nonce), BigIntegerUtil.toBigInteger(baseData.getGasPrice()), BigIntegerUtil.toBigInteger(baseData.getGasLimit()));
    }

    private String getSumAmount() {

        if (baseDataList == null || baseDataList.isEmpty()) {
            return null;
        }

        return Flowable
                .fromIterable(baseDataList)
                .map(new Function<TransactionAuthorizationBaseData, String>() {
                    @Override
                    public String apply(TransactionAuthorizationBaseData transactionAuthorizationBaseData) throws Exception {
                        return transactionAuthorizationBaseData.amount;
                    }
                })
                .reduce(new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String amount, String amount2) throws Exception {
                        return BigDecimalUtil.add(amount, amount2).toPlainString();
                    }
                })
                .blockingGet();
    }

    private String getSumFee() {

        if (baseDataList == null || baseDataList.isEmpty()) {
            return null;
        }

        return Flowable
                .fromIterable(baseDataList)
                .map(new Function<TransactionAuthorizationBaseData, String>() {
                    @Override
                    public String apply(TransactionAuthorizationBaseData transactionAuthorizationBaseData) throws Exception {
                        return transactionAuthorizationBaseData.getGasUsed();
                    }
                })
                .reduce(new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String amount, String amoun2) throws Exception {
                        return BigDecimalUtil.add(amount, amoun2).toPlainString();
                    }
                })
                .blockingGet();
    }

}
