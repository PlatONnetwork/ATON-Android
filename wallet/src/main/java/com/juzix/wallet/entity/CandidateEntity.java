package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.R;
import com.juzix.wallet.db.entity.CandidateInfoEntity;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.web3j.utils.Numeric;

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
     * 国家英文名称
     */
    private String countryEnName;
    /**
     * 国家中文名称
     */
    private String countryCnName;
    /**
     * 国家拼音名称，中文环境下，区域进行排序
     */
    private String countrySpellName;
    /**
     * 质押金(单位:Energon)
     */
    private String deposit;
    /**
     * 投票激励:小数
     */
    private double rewardRatio;

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
        countryEnName = in.readString();
        countryCnName = in.readString();
        countrySpellName = in.readString();
        deposit = in.readString();
        rewardRatio = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(name);
        dest.writeInt(ranking);
        dest.writeString(countryCode);
        dest.writeString(countryEnName);
        dest.writeString(countryCnName);
        dest.writeString(countrySpellName);
        dest.writeString(deposit);
        dest.writeDouble(rewardRatio);
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

    public String getCountryEnName() {
        return countryEnName;
    }

    public void setCountryEnName(String countryEnName) {
        this.countryEnName = countryEnName;
    }

    public String getCountryCnName() {
        return countryCnName;
    }

    public void setCountryCnName(String countryCnName) {
        this.countryCnName = countryCnName;
    }

    public String getCountrySpellName() {
        return countrySpellName;
    }

    public void setCountrySpellName(String countrySpellName) {
        this.countrySpellName = countrySpellName;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public double getRewardRatio() {
        return rewardRatio;
    }

    public void setRewardRatio(double rewardRatio) {
        this.rewardRatio = rewardRatio;
    }
}
