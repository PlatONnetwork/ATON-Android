package com.juzix.wallet.db.entity;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestEntity extends RealmObject {
    //唯一识别码
    @PrimaryKey
    private String uuid;
    /**
     * 钱包名称
     */
    private String name;

    private RealmList<TestD> list;

    public TestEntity() {
    }

    private TestEntity(Builder builder) {
        setUuid(builder.uuid);
        setName(builder.name);
        setOwnerArrayList(builder.d);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TestD> getOwnerArrayList() {
        ArrayList<TestD> addressInfoEntities = new ArrayList<>();
        if (this.list == null){
            return addressInfoEntities;
        }
        for (TestD infoEntity : this.list){
            addressInfoEntities.add(infoEntity);
        }
        return addressInfoEntities;
    }

    public void setOwnerArrayList(ArrayList<TestD> d) {
        if (d == null){
            return;
        }
        this.list = new RealmList<>();
        for (TestD t : d){
            this.list.add(t);
        }
    }


    public static final class Builder {
        private String uuid;
        private String name;
        private ArrayList<TestD> d;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder d(ArrayList<TestD> val) {
            d = val;
            return this;
        }

        public TestEntity build() {
            return new TestEntity(this);
        }
    }
}
