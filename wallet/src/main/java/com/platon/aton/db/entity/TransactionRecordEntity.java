package com.platon.aton.db.entity;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.framework.app.Constants;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TransactionRecordEntity extends RealmObject {

    @PrimaryKey
    private long timeStamp;

    private String from;

    private String to;

    private String value;

    private String chainId;

    public TransactionRecordEntity() {
    }

    public TransactionRecordEntity(long timeStamp, String from, String to, String value, String chainId) {
        this.timeStamp = timeStamp;
        this.from = from;
        this.to = to;
        this.value = value;
        this.chainId = chainId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 31 + (TextUtils.isEmpty(from) ? 0 : from.hashCode());
        hashCode = hashCode * 31 + (TextUtils.isEmpty(to) ? 0 : to.hashCode());
        hashCode = hashCode * 31 + (TextUtils.isEmpty(value) ? 0 : value.hashCode());
        hashCode = hashCode * 31 + (TextUtils.isEmpty(chainId) ? 0 : chainId.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TransactionRecordEntity) {
            TransactionRecordEntity transactionRecordEntity = (TransactionRecordEntity) obj;
            return !TextUtils.isEmpty(from) && from.equals(transactionRecordEntity.getFrom())
                    && !TextUtils.isEmpty(to) && to.equals(transactionRecordEntity.getTo())
                    && !TextUtils.isEmpty(value) && NumberParserUtils.getPrettyNumber(value, 2).equals(NumberParserUtils.getPrettyNumber(transactionRecordEntity.getValue(), 2))
                    && Math.abs(BigDecimalUtil.sub(timeStamp, transactionRecordEntity.getTimeStamp())) < Constants.Common.TRANSACTION_TIMEOUT_WITH_MILLISECOND
                    && !TextUtils.isEmpty(chainId) && chainId.equals(transactionRecordEntity.getChainId());
        }
        return super.equals(obj);
    }
}
