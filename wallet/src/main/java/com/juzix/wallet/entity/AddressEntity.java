package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class AddressEntity implements Parcelable {

    private String uuid;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 钱包地址
     */
    private String address;

    /**
     * 钱包头像
     */
    private String avatar;

    public AddressEntity() {
    }

    public AddressEntity(String uuid, String name, String address, String avatar) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.avatar = avatar;
    }

    protected AddressEntity(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        address = in.readString();
        avatar = in.readString();
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
        dest.writeString(avatar);
    }

    public static final Creator<AddressEntity> CREATOR = new Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel in) {
            return new AddressEntity(in);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
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

        if (obj instanceof AddressEntity) {
            AddressEntity entity = (AddressEntity) obj;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPrefixAddress(){
        try {
            if(TextUtils.isEmpty(address)){
                return null;
            }
            if (address.toLowerCase().startsWith("0x")){
                return address;
            }
            return "0x" + address;
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

}
