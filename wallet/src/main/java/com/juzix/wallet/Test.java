package com.juzix.wallet;

import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.utils.Numeric;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class Test {

    public static void main(String[] args) {

        System.out.println(JSONUtil.toJSONString(new Number(BigDecimalUtil.toBigDecimal("1E80"))));
    }


    static class Number {

        private BigDecimal value;

        public Number() {
        }

        public Number(BigDecimal value) {
            this.value = value;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }
    }



}
