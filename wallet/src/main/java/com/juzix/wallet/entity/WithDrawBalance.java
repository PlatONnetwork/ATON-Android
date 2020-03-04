package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.BigIntegerUtil;
import com.juzix.wallet.utils.NumberParserUtils;

import org.web3j.tx.gas.GasProvider;

import java.math.BigInteger;

public class WithDrawBalance implements Parcelable {

    /**
     * 待赎回  单位von
     */
    private String released;

    /**
     * 块高
     */
    private String stakingBlockNum;

    /**
     * 已委托  单位von
     */
    private String delegated;

    private String gasLimit;

    private String gasPrice;

    public WithDrawBalance() {

    }

    protected WithDrawBalance(Parcel in) {
        released = in.readString();
        stakingBlockNum = in.readString();
        delegated = in.readString();
        gasLimit = in.readString();
        gasPrice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(released);
        dest.writeString(stakingBlockNum);
        dest.writeString(delegated);
        dest.writeString(gasLimit);
        dest.writeString(gasPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WithDrawBalance> CREATOR = new Creator<WithDrawBalance>() {
        @Override
        public WithDrawBalance createFromParcel(Parcel in) {
            return new WithDrawBalance(in);
        }

        @Override
        public WithDrawBalance[] newArray(int size) {
            return new WithDrawBalance[size];
        }
    };

    public String getReleased() {
        return released;
    }

    public String getShowReleased() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(released, "1E18"));
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getStakingBlockNum() {
        return stakingBlockNum;
    }

    public void setStakingBlockNum(String stakingBlockNum) {
        this.stakingBlockNum = stakingBlockNum;
    }

    public String getDelegated() {
        return delegated;
    }

    public String getShowDelegated() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(delegated, "1E18"));
    }

    public void setDelegated(String delegated) {
        this.delegated = delegated;
    }

    public boolean isDelegated() {
        return BigDecimalUtil.isBiggerThanZero(delegated);
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public int hashCode() {
        int result = TextUtils.isEmpty(released) ? 0 : released.hashCode();
        result = 31 * result + (TextUtils.isEmpty(stakingBlockNum) ? 0 : stakingBlockNum.hashCode());
        result = 31 * result + (TextUtils.isEmpty(delegated) ? 0 : delegated.hashCode());
        result = 31 * result + (TextUtils.isEmpty(gasLimit) ? 0 : gasLimit.hashCode());
        result = 31 * result + (TextUtils.isEmpty(gasPrice) ? 0 : gasPrice.hashCode());
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj.getClass() == getClass()) {
            WithDrawBalance withDrawBalance = (WithDrawBalance) obj;
            return TextUtils.equals(withDrawBalance.delegated, delegated)
                    && TextUtils.equals(withDrawBalance.released, released)
                    && TextUtils.equals(withDrawBalance.stakingBlockNum, stakingBlockNum)
                    && TextUtils.equals(withDrawBalance.gasLimit, gasLimit)
                    && TextUtils.equals(withDrawBalance.gasPrice, gasPrice);
        }
        return super.equals(obj);
    }

    public org.web3j.tx.gas.GasProvider getGasProvider() {
        
        return new GasProvider() {
            @Override
            public BigInteger getGasPrice() {
                return BigIntegerUtil.toBigInteger(gasPrice);
            }

            @Override
            public BigInteger getGasLimit() {
                return BigIntegerUtil.toBigInteger(gasLimit);
            }
        };
    }
}
