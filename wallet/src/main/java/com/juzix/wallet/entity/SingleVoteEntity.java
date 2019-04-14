package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matrixelement
 */
public class SingleVoteEntity implements Cloneable, Parcelable {

    public static final Creator<SingleVoteEntity> CREATOR = new Creator<SingleVoteEntity>() {
        @Override
        public SingleVoteEntity createFromParcel(Parcel in) {
            return new SingleVoteEntity(in);
        }

        @Override
        public SingleVoteEntity[] newArray(int size) {
            return new SingleVoteEntity[size];
        }
    };

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;

    private String uuid;
    private String transactionId;
    private String hash;
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
    private List<TicketEntity> tickets;
    private String nodeAddress;

    public SingleVoteEntity() {
    }

    protected SingleVoteEntity(Parcel in) {
        uuid = in.readString();
        transactionId = in.readString();
        hash = in.readString();
        candidateId = in.readString();
        candidateName = in.readString();
        host = in.readString();
        contractAddress = in.readString();
        walletName = in.readString();
        walletAddress = in.readString();
        createTime = in.readLong();
        value = in.readDouble();
        ticketNumber = in.readLong();
        ticketPrice = in.readString();
        blockNumber = in.readLong();
        latestBlockNumber = in.readLong();
        energonPrice = in.readDouble();
        status = in.readInt();
        tickets = in.readArrayList(TicketEntity.class.getClassLoader());
        nodeAddress = in.readString();
    }

    private SingleVoteEntity(Builder builder) {
        setUuid(builder.uuid);
        setTransactionId(builder.transactionId);
        setHash(builder.hash);
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
        setTickets(builder.tickets);
        setNodeAddress(builder.nodeAddress);
    }

    @Override
    public SingleVoteEntity clone() {
        SingleVoteEntity voteEntity = null;
        try {
            voteEntity = (SingleVoteEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return voteEntity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(transactionId);
        dest.writeString(hash);
        dest.writeString(candidateId);
        dest.writeString(candidateName);
        dest.writeString(host);
        dest.writeString(contractAddress);
        dest.writeString(walletName);
        dest.writeString(walletAddress);
        dest.writeLong(createTime);
        dest.writeDouble(value);
        dest.writeLong(ticketNumber);
        dest.writeString(ticketPrice);
        dest.writeLong(blockNumber);
        dest.writeLong(latestBlockNumber);
        dest.writeDouble(energonPrice);
        dest.writeInt(status);
        dest.writeList(tickets);
        dest.writeString(nodeAddress);
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

    public String getPrefixContractAddress() {
        return Numeric.prependHexPrefix(contractAddress);
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

    public String getPrefixWalletAddress() {
        return Numeric.prependHexPrefix(walletAddress);
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

    public List<TicketEntity> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketEntity> tickets) {
        this.tickets = tickets;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public static final class Builder {
        private String uuid;
        private String transactionId;
        private String hash;
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
        private ArrayList<TicketEntity> tickets;
        private String nodeAddress;

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

        public Builder tickets(ArrayList<TicketEntity> tickets) {
            this.tickets = tickets;
            return this;
        }

        public Builder transactionId(String val) {
            this.transactionId = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            this.nodeAddress = val;
            return this;
        }

        public SingleVoteEntity build() {
            return new SingleVoteEntity(this);
        }
    }

    public int getValidVoteNum() {
        int validVoteNum = 0;
        if (tickets != null && !tickets.isEmpty()) {
            for (int i = 0; i < tickets.size(); i++) {
                TicketEntity ticketEntity = tickets.get(i);
                if (ticketEntity.getState() == 1) {
                    validVoteNum++;
                }
            }
        }
        return validVoteNum;
    }

    public int getInvalidVoteNum() {
        int inValidVoteNum = 0;
        if (tickets != null && !tickets.isEmpty()) {
            for (int i = 0; i < tickets.size(); i++) {
                TicketEntity ticketEntity = tickets.get(i);
                if (ticketEntity.getState() != 1) {
                    inValidVoteNum++;
                }
            }

        }
        return inValidVoteNum;
    }

    public double getVoteStaked() {
        return BigDecimalUtil.mul(NumberParserUtils.parseDouble(ticketPrice), NumberParserUtils.parseDouble(getValidVoteNum()));
    }

    public double getVoteUnStaked() {
        return BigDecimalUtil.mul(NumberParserUtils.parseDouble(ticketPrice), NumberParserUtils.parseDouble(getInvalidVoteNum()));
    }

}
