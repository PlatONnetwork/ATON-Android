package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.promeg.pinyinhelper.Pinyin;
import com.juzix.wallet.App;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.LanguageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class Region {

    /**
     * 地区ip地址
     */
    @JSONField(name = "query")
    private String ip;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 国家代码
     */
    private String countryCode;

    public Region(String ip, long updateTime, String countryCode) {
        this.ip = ip;
        this.updateTime = updateTime;
        this.countryCode = countryCode;
    }

    public Region() {
        this.updateTime = System.currentTimeMillis();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
