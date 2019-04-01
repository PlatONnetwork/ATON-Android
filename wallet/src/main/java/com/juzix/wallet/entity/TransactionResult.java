package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionResult extends AddressEntity implements Parcelable {

    private Status status = Status.OPERATION_UNDETERMINED;

    public TransactionResult(String uuid, String name, String address, Status status) {
        setUuid(uuid);
        setName(name);
        setAddress(address);
        setStatus(status);
    }

    protected TransactionResult(Parcel in) {
        setUuid(in.readString());
        setAddress(in.readString());
        setName(in.readString());
        setStatus(in.readParcelable(Status.class.getClassLoader()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUuid());
        dest.writeString(getAddress());
        dest.writeString(getName());
        dest.writeParcelable(getStatus(), flags);
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status implements Parcelable {

        OPERATION_UNDETERMINED, OPERATION_APPROVAL, OPERATION_REVOKE, OPERATION_SIGNING;

        public static final Creator<Status> CREATOR = new Creator<Status>() {
            @Override
            public Status createFromParcel(Parcel in) {
                return Status.values()[in.readInt()];
            }

            @Override
            public Status[] newArray(int size) {
                return new Status[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }
    }

}
