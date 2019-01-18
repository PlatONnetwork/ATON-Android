package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class TransactionResult extends AddressEntity implements Parcelable {
    public static final int OPERATION_UNDETERMINED = 0;
    public static final int OPERATION_APPROVAL     = 1;
    public static final int OPERATION_REVOKE       = 2;
    public static final int OPERATION_SIGNING      = 3;
    private             int operation;

    private TransactionResult(Builder builder) {
        setUuid(builder.uuid);
        setAddress(builder.address);
        setName(builder.name);
        setOperation(builder.operation);
    }

    protected TransactionResult(Parcel in) {
        setUuid(in.readString());
        setAddress(in.readString());
        setName(in.readString());
        setOperation(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUuid());
        dest.writeString(getAddress());
        dest.writeString(getName());
        dest.writeInt(getOperation());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionResult> CREATOR = new Creator<TransactionResult>() {
        @Override
        public TransactionResult createFromParcel(Parcel in) {
            return new TransactionResult(in);
        }

        @Override
        public TransactionResult[] newArray(int size) {
            return new TransactionResult[size];
        }
    };

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }


    public static final class Builder {
        private String uuid;
        private String address;
        private String name;
        private int    operation;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder operation(int val) {
            operation = val;
            return this;
        }

        public TransactionResult build() {
            return new TransactionResult(this);
        }
    }
}
