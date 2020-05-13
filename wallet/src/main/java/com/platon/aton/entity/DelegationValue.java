package com.platon.aton.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.NumberParserUtils;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author ziv
 */
public class DelegationValue {


    @JSONField(name = "deleList")
    private List<WithDrawBalance> withDrawBalanceList;

    /**
     * 最新最小委托数量
     */
    private String minDelegation;
    /**
     * 地址对应的自由账户余额  单位von
     */
    private String free;
    /**
     * 地址对应的锁仓账户余额  单位von
     */
    private String lock;
    /**
     * 该地址对应的nonce值
     */
    private String nonce;

    public DelegationValue() {
    }

    public List<WithDrawBalance> getWithDrawBalanceList() {
        return withDrawBalanceList;
    }

    public void setWithDrawBalanceList(List<WithDrawBalance> withDrawBalanceList) {
        this.withDrawBalanceList = withDrawBalanceList;
    }

    public String getMinDelegation() {
        return minDelegation;
    }

    public void setMinDelegation(String minDelegation) {
        this.minDelegation = minDelegation;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public double getDelegatedSumAmount() {
        if (withDrawBalanceList == null || withDrawBalanceList.isEmpty()) {
            return 0D;
        }

        return Flowable
                .fromIterable(withDrawBalanceList)
                .map(new Function<WithDrawBalance, Double>() {
                    @Override
                    public Double apply(WithDrawBalance withDrawBalance) throws Exception {
                        return NumberParserUtils.parseDouble(withDrawBalance.getShowDelegated());
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
                        return BigDecimalUtil.add(aDouble, aDouble2);
                    }
                })
                .blockingGet();

    }

    public double getReleasedSumAmount() {
        if (withDrawBalanceList == null || withDrawBalanceList.isEmpty()) {
            return 0D;
        }

        return Flowable
                .fromIterable(withDrawBalanceList)
                .map(new Function<WithDrawBalance, Double>() {
                    @Override
                    public Double apply(WithDrawBalance withDrawBalance) throws Exception {
                        return NumberParserUtils.parseDouble(withDrawBalance.getShowReleased());
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
                        return BigDecimalUtil.add(aDouble, aDouble2);
                    }
                })
                .blockingGet();
    }

    /**
     * 获取默认展示的项，优先展示待赎回待
     *
     * @return
     */
    public WithDrawBalance getDefaultShowWithDrawBalance() {

        if (withDrawBalanceList == null || withDrawBalanceList.isEmpty()) {
            return null;
        }

        return Flowable.fromIterable(withDrawBalanceList)
                .filter(new Predicate<WithDrawBalance>() {
                    @Override
                    public boolean test(WithDrawBalance withDrawBalance) throws Exception {
                        return !withDrawBalance.isDelegated();
                    }
                })
                .defaultIfEmpty(withDrawBalanceList.get(0))
                .blockingFirst();

    }

}
