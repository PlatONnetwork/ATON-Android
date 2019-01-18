package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestD extends RealmObject {
    private String uuid;
    private String name;
    public TestD() {
    }

    private TestD(Builder builder) {
        setUuid(builder.uuid);
        setName(builder.name);
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


    public static final class Builder {
        private String uuid;
        private String name;

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

        public TestD build() {
            return new TestD(this);
        }
    }
}
