package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.App;
import com.juzix.wallet.db.entity.RegionInfoEntity;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.LanguageUtil;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class RegionEntity implements Parcelable {

    @JSONField(name = "query")
    private String ip;
    private String countryCode;
    private long updateTime;
    private String countryEn;
    private String countryZh;
    private String countryPinyin;

    public RegionEntity() {
        super();
    }

    private RegionEntity(Builder builder) {
        setIp(builder.ip);
        setCountryCode(builder.countryCode);
        setUpdateTime(builder.updateTime);
        setCountryEn(builder.countryEn);
        setCountryZh(builder.countryZh);
        setCountryPinyin(builder.countryPinyin);
    }

    protected RegionEntity(Parcel in) {
        ip = in.readString();
        countryCode = in.readString();
        updateTime = in.readLong();
        countryEn = in.readString();
        countryZh = in.readString();
        countryPinyin = in.readString();
    }

    public static final Creator<RegionEntity> CREATOR = new Creator<RegionEntity>() {
        @Override
        public RegionEntity createFromParcel(Parcel in) {
            return new RegionEntity(in);
        }

        @Override
        public RegionEntity[] newArray(int size) {
            return new RegionEntity[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ip);
        dest.writeString(countryCode);
        dest.writeLong(updateTime);
        dest.writeString(countryEn);
        dest.writeString(countryZh);
        dest.writeString(countryPinyin);
    }


    public static final class Builder {
        private String uuid;
        private String ip;
        private String countryCode;
        private long updateTime;
        private String countryEn;
        private String countryZh;
        private String countryPinyin;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder ip(String val) {
            ip = val;
            return this;
        }

        public Builder countryCode(String val) {
            countryCode = val;
            return this;
        }

        public Builder updateTime(long val) {
            updateTime = val;
            return this;
        }

        public Builder countryEn(String val) {
            countryEn = val;
            return this;
        }

        public Builder countryZh(String val) {
            countryZh = val;
            return this;
        }

        public Builder countryPinyin(String val) {
            countryPinyin = val;
            return this;
        }

        public RegionEntity build() {
            return new RegionEntity(this);
        }
    }

    /**
     * 获取国家名称
     *
     * @return
     */
    public String getCountry() {

        Locale locale = LanguageUtil.getLocale(App.getContext());

        return Locale.CHINESE.getLanguage().equals(locale.getLanguage()) ? countryZh : countryEn;
    }

    public RegionInfoEntity buildRegionInfoEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), RegionInfoEntity.class);
    }

    @Override
    public String toString() {
        return "RegionEntity{" +
                "ip='" + ip + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", updateTime=" + updateTime +
                ", countryEn='" + countryEn + '\'' +
                ", countryZh='" + countryZh + '\'' +
                ", countryPinyin='" + countryPinyin + '\'' +
                '}';
    }
}
