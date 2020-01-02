package com.juzix.wallet.entity;

import android.content.Context;

import com.juzix.wallet.R;
import com.juzix.wallet.engine.AppConfigManager;

import java.math.BigInteger;

/**
 * 委托操作的判断
 */
public class DelegateHandle implements Nullable {
    private boolean canDelegation;
    private String message;
    private String free;
    private String lock;
    private String minDelegation;

    public static DelegateHandle getNullInstance() {
        return NullDelegateHandle.getInstance();
    }

    public DelegateHandle() {

    }


    public boolean isCanDelegation() {
        return canDelegation;
    }

    public void setCanDelegation(boolean canDelegation) {
        this.canDelegation = canDelegation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getMinDelegation() {
        return minDelegation;
    }

    public void setMinDelegation(String minDelegation) {
        this.minDelegation = minDelegation;
    }

    public String getMessageDesc(Context context) {
        return isCanDelegation() ? "" : getMessageDescByMessage(context);
    }

    private String getMessageDescByMessage(Context context) {
        switch (message) {
            case "1":
                return context.getString(R.string.delegate_no_click);
            case "2":
                return context.getString(R.string.the_validator_has_exited_and_cannot_be_delegated);
            case "3":
                return context.getString(R.string.tips_not_delegate);
            case "4":
                return context.getString(R.string.tips_not_balance);
            default:
                return "";
        }
    }

    @Override
    public boolean isNull() {
        return false;
    }
}


