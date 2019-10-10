package com.juzix.wallet.entity;

/**
 * 委托操作的判断
 */
public class DelegateHandle {
    private boolean canDelegation;
    private String message;
    private String free;
    private String lock;

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
}


