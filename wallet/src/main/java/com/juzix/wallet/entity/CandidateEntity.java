package com.juzix.wallet.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.JSONPDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.utils.LanguageUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    public CandidateEntity() {
    }

    protected CandidateEntity(Parcel in) {
        nodeId = in.readString();
        name = in.readString();
        ranking = in.readInt();
        countryCode = in.readString();
        deposit = in.readString();
        reward = in.readString();
        ticketPrice = in.readString();
        countryEntity = in.readParcelable(countryEntity.getClass().getClassLoader());
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
