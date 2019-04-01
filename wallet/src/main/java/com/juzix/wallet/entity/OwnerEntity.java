package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class OwnerEntity implements Parcelable, Cloneable {

    private String uuid;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 钱包地址
     */
    private String address;

    public OwnerEntity() {

    }

    public OwnerEntity(String uuid, String name, String address) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }

    protected OwnerEntity(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        address = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(name);
        dest.writeString(address);
    }

    public static final Creator<OwnerEntity> CREATOR = new Creator<OwnerEntity>() {
        @Override
        public OwnerEntity createFromParcel(Parcel in) {
            return new OwnerEntity(in);
        }

        @Override
        public OwnerEntity[] newArray(int size) {
            return new OwnerEntity[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(address) ? 0 : address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof OwnerEntity) {
            OwnerEntity entity = (OwnerEntity) obj;
            return !TextUtils.isEmpty(address) && address.equals(entity.getAddress());
        }

        return super.equals(obj);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrefixAddress() {
        try {
            if (TextUtils.isEmpty(address)) {
                return null;
            }
            if (address.toLowerCase().startsWith("0x")) {
                return address;
            }
            return "0x" + address;
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    @Override
    protected OwnerEntity clone() {
        OwnerEntity ownerEntity = null;
        try {
            ownerEntity = (OwnerEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return ownerEntity;
    }
}
