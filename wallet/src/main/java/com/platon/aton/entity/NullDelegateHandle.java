package com.platon.aton.entity;

import com.platon.aton.engine.AppConfigManager;

import java.math.BigInteger;

class NullDelegateHandle extends DelegateHandle {

    private NullDelegateHandle() {
    }

    public static NullDelegateHandle getInstance() {
        return new NullDelegateHandle();
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getFree() {
        return BigInteger.ZERO.toString(10);
    }

    @Override
    public String getLock() {
        return BigInteger.ZERO.toString(10);
    }

    @Override
    public String getMinDelegation() {
        return AppConfigManager.getInstance().getMinDelegation();
    }
}
