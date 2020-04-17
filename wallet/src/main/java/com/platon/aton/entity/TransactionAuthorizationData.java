package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.platon.aton.BuildConfig;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.SignCodeUtils;
import com.platon.framework.utils.LogUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.platon.FunctionType;
import org.web3j.utils.JSONUtil;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

public class TransactionAuthorizationData implements Parcelable {

    private static final String UTF_8 = "UTF-8";

    @JSONField(name = "qrCodeData")
    private List<TransactionAuthorizationBaseData> baseDataList;

    protected long timestamp;

    @JSONField(name = "v")
    protected int version;

    public TransactionAuthorizationData() {

    }

    public TransactionAuthorizationData(List<TransactionAuthorizationBaseData> baseDataList, long timestamp, int version) {
        this.baseDataList = baseDataList;
        this.timestamp = timestamp;
        this.version = version;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

        return new TransactionAuthorizationDetail(getSumAmount(), baseData.getPlatOnFunction().getType(), baseData.getFrom(), baseData.getTo(), getSumFee(), baseData.getNodeId(), baseData.getNodeName(), baseData.getRemark());

    }

    public String toJSONString() {
        return JSONUtil.toJSONString(this);
    }

    public TransactionSignatureData toTransactionSignatureData(Credentials credentials) {

        if (baseDataList == null || baseDataList.isEmpty()) {
            return null;
        }

        TransactionAuthorizationBaseData firstBaseData = baseDataList.get(0);

        String claimRewardAmount = null;

        if (firstBaseData.getFunctionType() == FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE) {
            claimRewardAmount = firstBaseData.getAmount();
        }

        List<String> signedMessageList = getSignedMessageList(credentials);
        String signedMessage = signedMessageList.isEmpty() ? null : signedMessageList.get(0);

        return new TransactionSignatureData(signedMessageList, firstBaseData.getFrom(), firstBaseData.getChainId(), firstBaseData.getPlatOnFunction().getType(), timestamp, firstBaseData.getNodeName(), claimRewardAmount, firstBaseData.getRemark(), createSigned(credentials.getEcKeyPair(), signedMessage, firstBaseData.getRemark()), BuildConfig.QRCODE_VERSION_CODE);
    }

    private String createSigned(ECKeyPair ecKeyPair, String signedData, String remark) {
        byte[] signedDataByte = Numeric.hexStringToByteArray(signedData);
        byte[] remarkByte = new byte[0];
        if (!TextUtils.isEmpty(remark)) {
            try {
                remarkByte = remark.getBytes(UTF_8);
            } catch (UnsupportedEncodingException e) {
                LogUtils.e(e.getMessage(),e.fillInStackTrace());
            }
        }
        byte[] message = new byte[signedDataByte.length + remarkByte.length];
        System.arraycopy(signedDataByte, 0, message, 0, signedDataByte.length);
        System.arraycopy(remarkByte, 0, message, signedDataByte.length, remarkByte.length);

        byte[] messageHash = Hash.sha3(message);

        //签名 Sign.signMessage(message, ecKeyPair, true) 和  Sign.signMessage(messageHash, ecKeyPair, false) 等效
        Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);

        byte[] signByte = SignCodeUtils.encode(signatureData);

        //报文中sign数据， signHex等于下面打印的值
        return Numeric.toHexString(signByte);
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

    @JSONField(serialize = false, deserialize = false)
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

    @JSONField(serialize = false, deserialize = false)
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
