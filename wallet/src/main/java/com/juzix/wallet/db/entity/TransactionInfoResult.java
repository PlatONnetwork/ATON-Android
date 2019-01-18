package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TransactionInfoResult extends RealmObject {
    @PrimaryKey
    private String uuid;
    /**
     * /钱包地址
     */
    private String address;
    /**
     * 钱包名称
     */
    private String name;
    private int operation;

    public TransactionInfoResult() {
    }

    private TransactionInfoResult(Builder builder) {
        setUuid(builder.uuid);
        setName(builder.name);
        setAddress(builder.address);
        setOperation(builder.operation);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "TransactionInfoResult{" +
                "address='" + address + '\'' +
                ", candidateName='" + name + '\'' +
                ", operation=" + operation +
                '}';
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

        public TransactionInfoResult build() {
            return new TransactionInfoResult(this);
        }
    }
}
