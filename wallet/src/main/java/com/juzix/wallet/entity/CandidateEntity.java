package com.juzix.wallet.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.utils.LanguageUtil;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class CandidateEntity implements Parcelable {

    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 质押排名
     */
    private int ranking;
    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 国家区域信息
     */
    @JSONField(deserialize = false, serialize = false)
    private CountryEntity countryEntity;
    /**
     * 质押金(单位:Energon)
     */
    private String deposit;
    /**
     * 投票激励:小数
     */
    private String reward;

    /**
     * 默认构造函数fastJson自动解析
     */
    public CandidateEntity() {
    }

    protected CandidateEntity(Parcel in) {
        nodeId = in.readString();
        name = in.readString();
        ranking = in.readInt();
        countryCode = in.readString();
        deposit = in.readString();
        reward = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(name);
        dest.writeInt(ranking);
        dest.writeString(countryCode);
        dest.writeString(deposit);
        dest.writeString(reward);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CandidateEntity> CREATOR = new Creator<CandidateEntity>() {
        @Override
        public CandidateEntity createFromParcel(Parcel in) {
            return new CandidateEntity(in);
        }

        @Override
        public CandidateEntity[] newArray(int size) {
            return new CandidateEntity[size];
        }
    };

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public CountryEntity getCountryEntity() {
        return countryEntity;
    }

    public void setCountryEntity(CountryEntity countryEntity) {
        this.countryEntity = countryEntity;
    }

    public String getCountryName(Context context) {
        if (countryEntity == null) {
            return null;
        }
        if (Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(context).getLanguage())) {
            return countryEntity.getZhName();
        } else {
            return countryEntity.getEnName();
        }
    }
}
