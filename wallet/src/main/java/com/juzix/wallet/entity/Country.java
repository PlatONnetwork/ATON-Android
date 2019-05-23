package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

public class Country implements Nullable, Parcelable {

    /**
     * 中文名称
     */
    @JSONField(name = "Name_en")
    private String enName;
    /**
     *
     */
    @JSONField(name = "Alpha3Code")
    private String alpha3Code;
    /**
     * 中文名称
     */
    @JSONField(name = "Name_zh")
    private String zhName;
    /**
     * 中文拼音名称
     */
    @JSONField(name = "Name_zh_pin")
    private String zhPinyinName;
    /**
     * 国家id
     */
    @JSONField(name = "_id")
    private String countryCode;
    /**
     * 手机区号
     */
    @JSONField(name = "TelephoneCode")
    private String telephoneCode;

    public Country() {
    }

    protected Country(Parcel in) {
        enName = in.readString();
        alpha3Code = in.readString();
        zhName = in.readString();
        zhPinyinName = in.readString();
        countryCode = in.readString();
        telephoneCode = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public static NullCountry getNullInstance() {
        return new NullCountry();
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getZhPinyinName() {
        return zhPinyinName;
    }

    public void setZhPinyinName(String zhPinyinName) {
        this.zhPinyinName = zhPinyinName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTelephoneCode() {
        return telephoneCode;
    }

    public void setTelephoneCode(String telephoneCode) {
        this.telephoneCode = telephoneCode;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(enName);
        dest.writeString(alpha3Code);
        dest.writeString(zhName);
        dest.writeString(zhPinyinName);
        dest.writeString(countryCode);
        dest.writeString(telephoneCode);
    }
}
