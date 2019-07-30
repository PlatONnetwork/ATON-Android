package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DelegateAddressEntity extends RealmObject {
    @PrimaryKey
    private long id;

    private String address;


    public DelegateAddressEntity() {

    }

    public DelegateAddressEntity(Builder builder) {
        setId(builder.id);
        setAddress(builder.address);

    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static final class Builder {
        private long id;
        private String address;

        public Builder() {

        }

        public Builder id(long index) {
            id = index;
            return this;
        }

        public Builder address(String address) {
            address = address;
            return this;
        }


        public DelegateAddressEntity build() {
            return new DelegateAddressEntity(this);
        }
    }

}
