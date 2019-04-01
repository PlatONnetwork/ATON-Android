package com.juzix.wallet.db.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.utils.JSONUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RegionInfoEntity extends RealmObject {

    @PrimaryKey
    @JSONField(name = "query")
    private String ip;
    private String countryCode;
    private long   updateTime;
    private String countryEn;
    private String countryZh;
    private String countryPinyin;

    public RegionInfoEntity() {

    }

    private RegionInfoEntity(Builder builder) {
        setIp(builder.ip);
        setCountryCode(builder.countryCode);
        setUpdateTime(builder.updateTime);
        setCountryEn(builder.countryEn);
        setCountryZh(builder.countryZh);
        setCountryPinyin(builder.countryPinyin);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public String getCountryZh() {
        return countryZh;
    }

    public void setCountryZh(String countryZh) {
        this.countryZh = countryZh;
    }

    public String getCountryPinyin() {
        return countryPinyin;
    }

    public void setCountryPinyin(String countryPinyin) {
        this.countryPinyin = countryPinyin;
    }

    public static final class Builder {
        private String uuid;
        private String ip;
        private String countryCode;
        private long   updateTime;
        private String countryEn;
        private String countryZh;
        private String countryPinyin;

        public Builder() {
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public Builder updateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder countryEn(String countryEn) {
            this.countryEn = countryEn;
            return this;
        }

        public Builder countryZh(String countryZh) {
            this.countryZh = countryZh;
            return this;
        }

        public Builder countryPinyin(String countryPinyin) {
            this.countryPinyin = countryPinyin;
            return this;
        }

        public RegionInfoEntity build() {
            return new RegionInfoEntity(this);
        }
    }

    public RegionEntity toRegionEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), RegionEntity.class);
    }
}
