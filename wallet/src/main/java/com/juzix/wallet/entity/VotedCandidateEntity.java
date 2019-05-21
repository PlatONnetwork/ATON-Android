package com.juzix.wallet.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.DateUtil;

public class VotedCandidateEntity implements Parcelable, Comparable<VotedCandidateEntity> {
    private static final long EXPIRE_BLOCKNUMBER = 1536000000;
    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String name;
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
     * 有效票数
     */
    private String validNum;
    /**
     * 总票数
     */
    private String totalTicketNum;
    /**
     * 投票锁定,单位Energon
     */
    private String locked;
    /**
     * 投票解除锁定
     */
    private String unLocked;
    /**
     * 投票收益,单位Energon
     */
    private String earnings;
    /**
     * 最新投票时间，单位-毫秒
     */
    private String transactionTime;
    /**
     * 当时的购票价格，单位Energon
     */
    private String deposit;
    /**
     * 投票人钱包地址
     */
    private String owner;

    /**
     * 实际过期时间
     */
    private String deadLine;
    /**
     * 排列序号：由区块号和交易索引拼接而成
     */
    private int sequence;

    /**
     * 当时的购票价格
     */
    private String price;

    /**
     * 投票人钱包地址(接口为准)
     */
    private String walletAddress;

    /**
     * 是否有效：0-无效，1-有效
     */
    private String isValid;


    public VotedCandidateEntity() {
    }

    protected VotedCandidateEntity(Parcel in) {
        nodeId = in.readString();
        name = in.readString();
        countryCode = in.readString();
        countryEnName = in.readString();
        countryCnName = in.readString();
        countrySpellName = in.readString();
        validNum = in.readString();
        totalTicketNum = in.readString();
        locked = in.readString();
        unLocked = in.readString();
        earnings = in.readString();
        transactionTime = in.readString();
        deposit = in.readString();
        owner = in.readString();
        deadLine = in.readString();
        sequence = in.readInt();
        price = in.readString();
        walletAddress = in.readString();
        isValid = in.readString();
    }

    public static final Creator<VotedCandidateEntity> CREATOR = new Creator<VotedCandidateEntity>() {
        @Override
        public VotedCandidateEntity createFromParcel(Parcel in) {
            return new VotedCandidateEntity(in);
        }

        @Override
        public VotedCandidateEntity[] newArray(int size) {
            return new VotedCandidateEntity[size];
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

    public String getValidNum() {
        return validNum;
    }

    public void setValidNum(String validNum) {
        this.validNum = validNum;
    }

    public String getTotalTicketNum() {
        return totalTicketNum;
    }

    public void setTotalTicketNum(String totalTicketNum) {
        this.totalTicketNum = totalTicketNum;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getUnLocked() {
        return unLocked;
    }

    public void setUnLocked(String unLocked) {
        this.unLocked = unLocked;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(name);
        dest.writeString(countryCode);
        dest.writeString(countryEnName);
        dest.writeString(countryCnName);
        dest.writeString(countrySpellName);
        dest.writeString(validNum);
        dest.writeString(totalTicketNum);
        dest.writeString(locked);
        dest.writeString(unLocked);
        dest.writeString(earnings);
        dest.writeString(transactionTime);
        dest.writeString(deposit);
        dest.writeString(owner);
        dest.writeString(deadLine);
        dest.writeInt(sequence);
        dest.writeString(price);
        dest.writeString(walletAddress);
        dest.writeString(isValid);

    }

    @Override
    public int compareTo(VotedCandidateEntity o) {
        //按时间排序
//        return Long.compare(DateUtil.parse(o.transactionTime,DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND),DateUtil.parse(transactionTime,DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
        return Long.compare(NumberParserUtils.parseLong(o.transactionTime), NumberParserUtils.parseLong(transactionTime));

    }
}
