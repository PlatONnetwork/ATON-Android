package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class DelegationValue {


    @JSONField(name = "deleList")
    private List<WithDrawBalance> withDrawBalanceList;

    /**
     * 最新最小委托数量
     */
    private String minDelegation;

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
