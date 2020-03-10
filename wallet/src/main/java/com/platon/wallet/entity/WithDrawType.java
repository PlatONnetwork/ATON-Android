package com.platon.wallet.entity;

import com.platon.wallet.component.adapter.WithDrawPopWindowAdapter;

public class WithDrawType implements Comparable<WithDrawType> {
    private String key;
    private double value;

    public WithDrawType(String key, double value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * 当“待赎回委托”不为0时，优先显示待赎回委托。
     *
     * @param withDrawType
     * @return
     */
    @Override
    public int compareTo(WithDrawType withDrawType) {
        if (WithDrawPopWindowAdapter.TAG_RELEASED.equals(key) && WithDrawPopWindowAdapter.TAG_DELEGATED.equals(withDrawType.getKey()) && value > 0) {
            return -1;
        }
        return 1;
    }
}
