package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class CountryEntity implements Nullable{

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

    public CountryEntity() {
    }

    public static NullCountryEntity getNullInstance() {
        return new NullCountryEntity();
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
}
