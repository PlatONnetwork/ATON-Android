package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.NumberParserUtils;

public class AccountBalance implements Parcelable {
    /**
     * 钱包地址
     */
    private String addr;

    /**
     * 自由账户余额  单位von
     */
    private String free;


    /**
     * 锁仓账户余额  单位von
     */
    private String lock;

    public AccountBalance(String addr, String free, String lock) {
        this.addr = addr;
        this.free = free;
        this.lock = lock;
    }

    public AccountBalance() {
    }

    protected AccountBalance(Parcel in) {
        addr = in.readString();
        free = in.readString();
        lock = in.readString();
    }

    public static final Creator<AccountBalance> CREATOR = new Creator<AccountBalance>() {
        @Override
        public AccountBalance createFromParcel(Parcel in) {
            return new AccountBalance(in);
        }

        @Override
        public AccountBalance[] newArray(int size) {
            return new AccountBalance[size];
        }
    };

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
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

    public String getShowFreeBalace() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(free, "1E18"));
    }

    public String getShowLockBalance() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(lock, "1E18"));
    }

    /**
     * 获取钱包地址
     *
     * @return
     */
    public String getPrefixAddress() {
        try {
            if (TextUtils.isEmpty(addr)) {
                return "";
            }
            if (addr.toLowerCase().startsWith("0x")) {
                return addr;
            }
            return "0x" + addr;
        } catch (Exception exp) {
            exp.printStackTrace();
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addr);
        dest.writeString(free);
        dest.writeString(lock);
    }
}
