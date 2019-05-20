package com.juzix.wallet.entity;

class NullCountryEntity extends CountryEntity {

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
