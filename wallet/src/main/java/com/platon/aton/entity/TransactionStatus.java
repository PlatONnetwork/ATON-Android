package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.platon.aton.R;

import java.util.HashMap;
import java.util.Map;

public enum TransactionStatus implements Parcelable {

    FAILED {
        @Override
        public int getTransactionStatusDescRes() {
            return R.string.failed;
        }

        @Override
        public int getTransactionStatusDescColorRes() {
            return R.color.color_f5302c;
        }
    }, SUCCESSED {
        @Override
        public int getTransactionStatusDescRes() {
            return R.string.success;
        }

        @Override
        public int getTransactionStatusDescColorRes() {
            return R.color.color_19a201;
        }
    }, PENDING {
        @Override
        public int getTransactionStatusDescRes() {
            return R.string.pending;
        }

        @Override
        public int getTransactionStatusDescColorRes() {
            return R.color.color_105cfe;
        }
    }, TIMEOUT {
        @Override
        public int getTransactionStatusDescRes() {
            return R.string.timeout;
        }

        @Override
        public int getTransactionStatusDescColorRes() {
            return R.color.color_f5302c;
        }
    };

    private static Map<Integer, TransactionStatus> map = new HashMap<>();

    static {
        for (TransactionStatus status : values()) {
            map.put(status.ordinal(), status);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionStatus> CREATOR = new Creator<TransactionStatus>() {
        @Override
        public TransactionStatus createFromParcel(Parcel in) {
            return TransactionStatus.values()[in.readInt()];
        }

        @Override
        public TransactionStatus[] newArray(int size) {
            return new TransactionStatus[size];
        }
    };

    public static TransactionStatus getTransactionStatusByIndex(int index) {
        return map.get(index);
    }

    public abstract int getTransactionStatusDescRes();

    public abstract int getTransactionStatusDescColorRes();
}
