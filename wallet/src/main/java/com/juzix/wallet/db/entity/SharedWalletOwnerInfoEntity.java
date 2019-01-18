package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class SharedWalletOwnerInfoEntity extends RealmObject {

    @PrimaryKey
    private String uuid;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 钱包名称
     */
    private String name;

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
}
