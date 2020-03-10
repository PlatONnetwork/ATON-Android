package com.platon.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MyDelegate {

    private double delegateTotal;

    @JSONField(name = "list")
    private List<DelegateInfo> delegateInfoList;

    public MyDelegate() {

    }

    public double getDelegateTotal() {
        return delegateTotal;
    }

    public void setDelegateTotal(double delegateTotal) {
        this.delegateTotal = delegateTotal;
    }

    public List<DelegateInfo> getDelegateInfoList() {
        return delegateInfoList;
    }

    public void setDelegateInfoList(List<DelegateInfo> delegateInfoList) {
        this.delegateInfoList = delegateInfoList;
    }

    @Override
    public String toString() {
        return "MyDelegate{" +
                "delegateTotel=" + delegateTotal +
                ", infoList=" + delegateInfoList +
                '}';
    }
}
