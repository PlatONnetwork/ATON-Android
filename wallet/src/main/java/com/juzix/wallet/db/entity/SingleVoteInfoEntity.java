package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.TicketEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class SingleVoteInfoEntity extends RealmObject implements Cloneable {

    @PrimaryKey
    private String uuid;
    /**
     * 交易hash
     */
    private String hash;
    /**
     * 交易Id
     */
    private String transactionId;
    /**
     * 候选人Id（节点Id）
     */
    private String candidateId;

    /**
     * 节点名称
     */
    private String candidateName;
    /**
     * 区域
     */
    private String host;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 发生交易的钱包名称
     */
    private String walletName;

    /**
     * 发生交易的钱包地址
     */
    private String walletAddress;

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 交易金额
     */
    private double value;

    /**
     * 票数
     */
    private long ticketNumber;

    /**
     * 票价
     */
    private String ticketPrice;

    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 最新交易区块
     */
    private long latestBlockNumber;
    /**
     * 手续费
     */
    private double energonPrice;

    /**
     * 状态
     */
    private int status;

    /**
     * 总票数
     */
    private RealmList<TicketInfoEntity> tickets;

    public SingleVoteInfoEntity() {

    }

    private SingleVoteInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setHash(builder.hash);
        setTransactionId(builder.transactionId);
        setCandidateId(builder.candidateId);
        setCandidateName(builder.candidateName);
        setHost(builder.host);
        setContractAddress(builder.contractAddress);
        setWalletName(builder.walletName);
        setWalletAddress(builder.walletAddress);
        setCreateTime(builder.createTime);
        setValue(builder.value);
        setTicketNumber(builder.ticketNumber);
        setTicketPrice(builder.ticketPrice);
        setBlockNumber(builder.blockNumber);
        setLatestBlockNumber(builder.latestBlockNumber);
        setEnergonPrice(builder.energonPrice);
        setStatus(builder.status);
        setTicketInfoEntityArrayList((ArrayList<TicketInfoEntity>) builder.tickets);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = Numeric.cleanHexPrefix(contractAddress);
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = Numeric.cleanHexPrefix(walletAddress);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public long getLatestBlockNumber() {
        return latestBlockNumber;
    }

    public void setLatestBlockNumber(long latestBlockNumber) {
        this.latestBlockNumber = latestBlockNumber;
    }

    public double getEnergonPrice() {
        return energonPrice;
    }

    public void setEnergonPrice(double energonPrice) {
        this.energonPrice = energonPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RealmList<TicketInfoEntity> getTickets() {
        return tickets;
    }

    public void setTickets(RealmList<TicketInfoEntity> tickets) {
        this.tickets = tickets;
    }

    public void setTicketInfoEntityArrayList(List<TicketInfoEntity> ticketInfoEntityList) {
        if (ticketInfoEntityList == null) {
            return;
        }
        this.tickets = new RealmList<>();
        for (TicketInfoEntity entity : ticketInfoEntityList) {
            this.tickets.add(entity);
        }
    }

    public ArrayList<TicketInfoEntity> getTicketInfoEntityArrayList() {
        ArrayList<TicketInfoEntity> ticketInfoEntities = new ArrayList<>();
        if (this.tickets == null) {
            return ticketInfoEntities;
        }
        for (TicketInfoEntity infoEntity : this.tickets) {
            ticketInfoEntities.add(infoEntity);
        }
        return ticketInfoEntities;
    }

    public double getVoteStaked() {

        double voteStaked = 0D;

        if (tickets != null && !tickets.isEmpty()) {

            for (TicketInfoEntity ticketEntity : tickets) {
                if (ticketEntity.getState() == TicketEntity.NORMAL) {
                    voteStaked += BigDecimalUtil.div(ticketEntity.getDeposit(), "1E18");
                }
            }
        }

        return voteStaked;
    }

    @Override
    public SingleVoteInfoEntity clone() {
        SingleVoteInfoEntity singleVoteInfoEntity = null;
        try {
            singleVoteInfoEntity = (SingleVoteInfoEntity) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return singleVoteInfoEntity;
    }

    public static final class Builder {
        private String uuid;
        private String hash;
        private String transactionId;
        private String candidateId;
        private String candidateName;
        private String host;
        private String contractAddress;
        private String walletName;
        private String walletAddress;
        private long createTime;
        private double value;
        private long ticketNumber;
        private String ticketPrice;
        private long blockNumber;
        private long latestBlockNumber;
        private double energonPrice;
        private int status;
        private List<TicketInfoEntity> tickets;

        public Builder() {
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder candidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder candidateName(String candidateName) {
            this.candidateName = candidateName;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder contractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
            return this;
        }

        public Builder walletName(String walletName) {
            this.walletName = walletName;
            return this;
        }

        public Builder walletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
            return this;
        }

        public Builder createTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public Builder ticketNumber(long ticketNumber) {
            this.ticketNumber = ticketNumber;
            return this;
        }

        public Builder ticketPrice(String ticketPrice) {
            this.ticketPrice = ticketPrice;
            return this;
        }

        public Builder blockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder latestBlockNumber(long latestBlockNumber) {
            this.latestBlockNumber = latestBlockNumber;
            return this;
        }

        public Builder energonPrice(double energonPrice) {
            this.energonPrice = energonPrice;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder tickets(List<TicketInfoEntity> tickets) {
            this.tickets = tickets;
            return this;
        }

        public SingleVoteInfoEntity build() {
            return new SingleVoteInfoEntity(this);
        }
    }

    public SingleVoteEntity buildSingleVoteEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this), SingleVoteEntity.class);
    }

    public VoteTransactionEntity buildVoteTransactionEntity() {
        return new VoteTransactionEntity.Builder(getUuid(), getCreateTime(), getWalletName())
                .hash(getHash())
                .fromAddress(getWalletAddress())
                .toAddress(getContractAddress())
                .value(getValue())
                .blockNumber(getBlockNumber())
                .latestBlockNumber(getLatestBlockNumber())
                .energonPrice(getEnergonPrice())
                .status(getStatus())
                .build();
    }
}
