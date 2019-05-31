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
public class Candidate implements Parcelable {

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
    private Country countryEntity;
    /**
     * 质押金(单位:Energon)
     */
    private String deposit;
    /**
     * 投票激励:小数
     */
    private String reward;
    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 机构官网
     */
    private String orgWebsite;
    /**
     * 节点简介
     */
    private String intro;
    /**
     * 节点地址
     */
    private String nodeUrl;
    /**
     * 票价
     */
    private String ticketPrice;
    /**
     * 得票数
     */
    private String ticketCount;
    /**
     * 加入时间
     */
    private long joinTime;
    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 默认构造函数fastJson自动解析
     */
    public Candidate() {
    }

    protected Candidate(Parcel in) {
        nodeId = in.readString();
        name = in.readString();
        ranking = in.readInt();
        countryCode = in.readString();
        deposit = in.readString();
        reward = in.readString();
        ticketPrice = in.readString();
        countryEntity = in.readParcelable(Country.class.getClassLoader());
        orgName = in.readString();
        orgWebsite = in.readString();
        intro = in.readString();
        nodeUrl = in.readString();
        nodeType = in.readString();
        ticketCount = in.readString();
        joinTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(name);
        dest.writeInt(ranking);
        dest.writeString(countryCode);
        dest.writeString(deposit);
        dest.writeString(reward);
        dest.writeString(ticketPrice);
        dest.writeParcelable(countryEntity, flags);
        dest.writeString(orgName);
        dest.writeString(orgWebsite);
        dest.writeString(intro);
        dest.writeString(nodeUrl);
        dest.writeString(nodeType);
        dest.writeString(ticketCount);
        dest.writeLong(joinTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Candidate> CREATOR = new Creator<Candidate>() {
        @Override
        public Candidate createFromParcel(Parcel in) {
            return new Candidate(in);
        }

        @Override
        public Candidate[] newArray(int size) {
            return new Candidate[size];
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgWebsite() {
        return orgWebsite;
    }

    public void setOrgWebsite(String orgWebsite) {
        this.orgWebsite = orgWebsite;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public Country getCountryEntity() {
        return countryEntity;
    }

    public void setCountryEntity(Country countryEntity) {
        this.countryEntity = countryEntity;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public NodeType getNodeType() {
        return NodeType.getNodeTypeByName(nodeType);
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
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
