package com.platon.wallet.entity;

class NullCountry extends Country {

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getZhName() {
        return "*未知";
    }

    @Override
    public String getEnName() {
        return "*Unknown";
    }
}
