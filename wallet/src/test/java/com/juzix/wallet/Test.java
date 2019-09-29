package com.juzix.wallet;

import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;

import java.math.BigInteger;

public class Test {


    public static void main(String[] args) {

        System.out.println(JSONUtil.toJSONString(new Number(new BigInteger("1E80"))));
    }


    static class Number {

        private BigInteger value;

        public Number() {
        }

        public Number(BigInteger value) {
            this.value = value;
        }

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }
    }
}
