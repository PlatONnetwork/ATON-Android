package com.platon.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.platon.framework.util.NumberParserUtils;
import com.platon.wallet.R;
import com.platon.wallet.db.entity.TransactionEntity;
import com.platon.wallet.db.entity.TransactionRecordEntity;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.utils.BigDecimalUtil;
import com.platon.wallet.utils.DateUtil;
import com.platon.wallet.utils.JSONUtil;

import org.web3j.abi.datatypes.generated.Uint32;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Transaction implements Comparable<Transaction>, Parcelable, Cloneable {

    /**
     * 交易hash
     */
    private String hash;
    /**
     * 当前交易所在快高
     */
    private long blockNumber;
    /**
     * 当前交易的链id
     */
    private String chainId;
    /**
     * 交易实际花费值(手续费)，单位：wei
     * “21168000000000”
     */
    private String actualTxCost;
    /**
     * 交易发送方
     */
    private String from;
    /**
     * 交易接收方
     */
    private String to;
    /**
     * 交易序列号
     */
    private long sequence;
    /**
     * 交易状态2 pending 1 成功 0 失败
     */
    private int txReceiptStatus;
    /**
     * 0: 转账
     * 1: 合约发布(合约创建)
     * 2: 合约调用(合约执行)
     * 3: 其他收入
     * 4: 其他支出
     * 5: MPC交易
     * 1000: 发起质押(创建验证人)
     * 1001: 修改质押信息(编辑验证人)
     * 1002: 增持质押(增加自有质押)
     * 1003: 撤销质押(退出验证人)
     * 1004: 发起委托(委托)
     * 1005: 减持/撤销委托(赎回委托)
     * 2000: 提交文本提案(创建提案)
     * 2001: 提交升级提案(创建提案)
     * 2002: 提交参数提案(创建提案)
     * 2003: 给提案投票(提案投票)
     * 2004: 版本声明
     * 3000: 举报多签(举报验证人)
     * 4000: 创建锁仓计划(创建锁仓)
     */
    private String txType;
    /**
     * 交易金额
     */
    private String value;
    /**
     * 发送者钱包名称
     */
    private String senderWalletName;
    /**
     * {json}交易详细信息
     */
    private String txInfo;
    /**
     * 提交时间（单位：毫秒）
     */
    private long timestamp;
    /**
     * to类型
     * contract —— 合约
     * address —— 地址
     */
    private String toType;
    /**
     * Sent发送/Receive接收
     */
    private String direction;
    /**
     * 节点名称/委托给/验证人
     * //txType = 1004,1005,1000,1001,1002,1003,3000,2000,2001,2002,2003,2004,nodeName不为空
     * 详细描述：txType =  2000,2001,2002,2003(验证人)
     * 详细描述：txType =  1004,1005(委托给，同时也是节点名称)
     */
    private String nodeName;
    /**
     * txType =  1004,1005,1000,1001,1002,1003,3000,2004,nodeId不为空
     */
    private String nodeId;
    /**
     * txType =  4000,lockAddress不为空
     */
    private String lockAddress;
    /**
     * 举报类型
     */
    private String reportType;
    /**
     * 版本
     */
    private String version;
    /**
     * 提案id(截取最后一个破折号)
     */
    private String url;
    /**
     * PIP编号   eip-100(EIP-由前端拼接)
     */
    private String piDID;
    /**
     * 提案类型
     */
    private String proposalType;
    /**
     * 提案id
     */
    private String proposalId;
    /**
     * 投票
     */
    private String vote;


    //=======================下面是新加的三个字段=================================
    /**
     * //赎回状态， 1： 退回中   2：退回成功     赎回失败查看交易txReceiptStatus
     */
    private String redeemStatus;

    /**
     * 钱包头像
     */

    private String walletIcon;

    /**
     * 钱包名称
     */

    private String walletName;

    /**
     * "unDelegation":"10000",       //赎回金额 txType = 1005(赎回数量)
     */

    private String unDelegation;
    /**
     * 质押金额 txType = 1003(退回数量)
     */
    private String stakingValue;
    /**
     * 领取数量 单位von   1LAT(ETH)=1000000000000000000von(wei)
     */
    private String totalReward;


    public Transaction() {
    }

    protected Transaction(Parcel in) {
        hash = in.readString();
        blockNumber = in.readLong();
        chainId = in.readString();
        actualTxCost = in.readString();
        from = in.readString();
        to = in.readString();
        sequence = in.readLong();
        txReceiptStatus = in.readInt();
        txType = in.readString();
        value = in.readString();
        senderWalletName = in.readString();
        txInfo = in.readString();
        timestamp = in.readLong();
        toType = in.readString();
        direction = in.readString();
        nodeName = in.readString();
        nodeId = in.readString();
        lockAddress = in.readString();
        reportType = in.readString();
        version = in.readString();
        url = in.readString();
        piDID = in.readString();
        proposalType = in.readString();
        proposalId = in.readString();
        vote = in.readString();
        redeemStatus = in.readString();
        walletIcon = in.readString();
        walletName = in.readString();
        unDelegation = in.readString();
        stakingValue = in.readString();
        totalReward = in.readString();
    }

    public Transaction(Builder builder) {
        this.hash = builder.hash;
        this.blockNumber = builder.blockNumber;
        this.chainId = builder.chainId;
        this.actualTxCost = builder.actualTxCost;
        this.from = builder.from;
        this.to = builder.to;
        this.sequence = builder.sequence;
        this.txReceiptStatus = builder.txReceiptStatus;
        this.txType = builder.txType;
        this.value = builder.value;
        this.senderWalletName = builder.senderWalletName;
        this.txInfo = builder.txInfo;
        this.timestamp = builder.timestamp;
        this.toType = builder.toType;
        this.direction = builder.direction;
        this.nodeName = builder.nodeName;
        this.nodeId = builder.nodeId;
        this.lockAddress = builder.lockAddress;
        this.reportType = builder.reportType;
        this.version = builder.version;
        this.url = builder.url;
        this.piDID = builder.piDID;
        this.proposalType = builder.proposalType;
        this.proposalId = builder.proposalId;
        this.vote = builder.vote;
        this.redeemStatus = builder.redeemStatus;
        this.walletIcon = builder.walletIcon;
        this.walletName = builder.walletName;
        this.unDelegation = builder.unDelegation;
        this.stakingValue = builder.stakingValue;
        this.totalReward = builder.totalReward;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeLong(blockNumber);
        dest.writeString(chainId);
        dest.writeString(actualTxCost);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(sequence);
        dest.writeInt(txReceiptStatus);
        dest.writeString(txType);
        dest.writeString(value);
        dest.writeString(senderWalletName);
        dest.writeString(txInfo);
        dest.writeLong(timestamp);
        dest.writeString(toType);
        dest.writeString(direction);
        dest.writeString(nodeName);
        dest.writeString(nodeId);
        dest.writeString(lockAddress);
        dest.writeString(reportType);
        dest.writeString(version);
        dest.writeString(url);
        dest.writeString(piDID);
        dest.writeString(proposalType);
        dest.writeString(proposalId);
        dest.writeString(vote);
        dest.writeString(redeemStatus);
        dest.writeString(walletIcon);
        dest.writeString(walletName);
        dest.writeString(unDelegation);
        dest.writeString(stakingValue);
        dest.writeString(totalReward);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    /**
     * 是否是发送者
     *
     * @return
     */
    public boolean isSender(String queryAddress) {
        TransactionType transactionType = getTxType();
        switch (transactionType) {
            case TRANSFER:
                return !TextUtils.isEmpty(queryAddress) && queryAddress.equalsIgnoreCase(from);
            case UNDELEGATE:
            case EXIT_VALIDATOR:
                return false;
            default:
                return true;
        }
    }

    /**
     * 是否是发送者
     *
     * @return
     */
    public boolean isSender(List<String> queryAddressList) {
        return isContainIgnoreCase(queryAddressList, from);
    }

    /**
     * 是否是接收者
     *
     * @param queryAddressList
     * @return
     */
    public boolean isReceiver(List<String> queryAddressList) {
        TransactionType transactionType = getTxType();
        switch (transactionType) {
            case TRANSFER:
                return isContainIgnoreCase(queryAddressList, to);
            case UNDELEGATE:
            case EXIT_VALIDATOR:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是转账
     *
     * @param queryAddressList
     * @return
     */
    public boolean isTransfer(List<String> queryAddressList) {

        return getTxType() == TransactionType.TRANSFER && isSender(queryAddressList) && isReceiver(queryAddressList);
    }

    private boolean isContainIgnoreCase(List<String> queryAddressList, String address) {

        if (TextUtils.isEmpty(address) || queryAddressList == null || queryAddressList.isEmpty()) {
            return false;
        }

        return Flowable
                .fromIterable(queryAddressList)
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        return address.equalsIgnoreCase(s);
                    }
                })
                .takeUntil(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .defaultIfEmpty(false)
                .onErrorReturnItem(false)
                .blockingSingle();

    }

    public @TransferType
    int getTransferType(List<String> queryAddressList) {
        if (isTransfer(queryAddressList)) {
            return TransferType.TRANSFER;
        } else if (isSender(queryAddressList)) {
            return TransferType.SEND;
        } else {
            return TransferType.RECEIVE;
        }
    }

    /**
     * 获取转账描述
     *
     * @param queryAddressList
     * @return
     */
    public int getTransferDescRes(List<String> queryAddressList) {
        if (isSender(queryAddressList)) {
            if (isReceiver(queryAddressList)) {
                return R.string.transfer;
            } else {
                return R.string.sent;
            }
        } else {
            return R.string.received;
        }
    }

    /**
     * 获取转账描述
     *
     * @param transferType
     * @return
     */
    public int getTransferDescRes(@TransferType int transferType) {
        switch (transferType) {
            case TransferType.TRANSFER:
                return R.string.transfer;
            case TransferType.RECEIVE:
                return R.string.received;
            default:
                return R.string.sent;
        }
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getRedeemStatus() {
        return redeemStatus;
    }

    public void setRedeemStatus(String redeemStatus) {
        this.redeemStatus = redeemStatus;
    }

    public String getWalletIcon() {
        return walletIcon;
    }

    public void setWalletIcon(String walletIcon) {
        this.walletIcon = walletIcon;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getShowCreateTime() {
        return DateUtil.format(timestamp, DateUtil.DATETIME_FORMAT_PATTERN);
    }

    public String getActualTxCost() {
        return actualTxCost;
    }

    public String getShowActualTxCost() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(actualTxCost, "1E18"));
    }

    public void setActualTxCost(String actualTxCost) {
        this.actualTxCost = actualTxCost;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getUnDelegation() {
        return unDelegation;
    }

    public void setUnDelegation(String unDelegation) {
        this.unDelegation = unDelegation;
    }

    public TransactionStatus getTxReceiptStatus() {
        return TransactionStatus.getTransactionStatusByIndex(txReceiptStatus);
    }

    public void setTxReceiptStatus(int txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public TransactionType getTxType() {
        return TransactionType.getTxTypeByValue(NumberParserUtils.parseInt(txType));
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getValue() {
        return value;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    public String getShowValue() {
        switch (getTxType()) {
            case TRANSFER:
            case DELEGATE:
            case CREATE_VALIDATOR:
            case EDIT_VALIDATOR:
            case INCREASE_STAKING:
                return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, "1E18"));
            case EXIT_VALIDATOR:
                return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(stakingValue, "1E18"));
            case UNDELEGATE:
                return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(unDelegation, "1E18"));
            default:
                return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, "1E18"));

        }
    }

    public String getShowTotalReward() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(totalReward, "1E18"));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSenderWalletName() {
        return senderWalletName;
    }

    public void setSenderWalletName(String senderWalletName) {
        this.senderWalletName = senderWalletName;
    }

    public String getTxInfo() {
        return txInfo;
    }

    public TransactionExtra getTransactionExtra() {
        return JSONUtil.parseObject(txInfo, TransactionExtra.class);
    }

    public void setTxInfo(String txInfo) {
        this.txInfo = txInfo;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLockAddress() {
        return lockAddress;
    }

    public void setLockAddress(String lockAddress) {
        this.lockAddress = lockAddress;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFormatVersion() {

        if (TextUtils.isEmpty(version)) {
            return "--";
        }

        Uint32 uint32 = new Uint32(new BigInteger(version));

        return String.format("V%s", TextUtils.join(".", byteArrayToList(uint32.getValue().toByteArray())));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProposalType() {
        return proposalType;
    }

    public void setProposalType(String proposalType) {
        this.proposalType = proposalType;
    }

    public String getPiDID() {
        return piDID;
    }

    public void setPiDID(String piDID) {
        this.piDID = piDID;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getStakingValue() {
        return stakingValue;
    }

    public void setStakingValue(String stakingValue) {
        this.stakingValue = stakingValue;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(hash) ? 0 : hash.hashCode();
    }

    public @StringRes
    int getProposalTypeDescRes() {
        int proposalType = NumberParserUtils.parseInt(getProposalType());
        if (proposalType == ProposalType.UPGRADE_PROPOSAL) {
            return R.string.upgrade_proposal;
        } else if (proposalType == ProposalType.TEXT_PROPOSAL) {
            return R.string.text_proposal;
        } else if (proposalType == ProposalType.PARAMETER_PROPOSAL) {
            return R.string.parameter_proposal;
        } else if (proposalType == ProposalType.CANCEL_PROPOSAL) {
            return R.string.cancel_proposal;
        } else {
            return -1;
        }
    }


    public @StringRes
    int getVoteOptionTypeDescRes() {
        int voteOptionType = NumberParserUtils.parseInt(vote);
        if (voteOptionType == VoteOptionType.VOTE_YES) {
            return R.string.vote_yes;
        } else if (voteOptionType == VoteOptionType.VOTE_NO) {
            return R.string.vote_no;
        } else if (voteOptionType == VoteOptionType.VOTE_ABSTAIN) {
            return R.string.vote_abstain;
        } else {
            return -1;
        }
    }

    public @StringRes
    int getReportTypeDescRes() {
        return R.string.double_signing;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Transaction) {
            Transaction transaction = (Transaction) obj;
            return !TextUtils.isEmpty(hash) && hash.equals(transaction.getHash());
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(Transaction o) {
        if (o.sequence == 0 || sequence == 0) {
            return Long.compare(o.timestamp, timestamp);
        }
        return Long.compare(o.sequence, sequence);
    }

    @Override
    public Transaction clone() {
        Transaction transaction = null;
        try {
            transaction = (Transaction) super.clone();
        } catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
        }
        return transaction;
    }

    public TransactionEntity toTransactionEntity() {
        return new TransactionEntity.Builder(hash, senderWalletName, from, to, timestamp)
                .setTxType(txType)
                .setTxInfo(txInfo)
                .setBlockNumber(blockNumber)
                .setChainId(chainId)
                .setTxReceiptStatus(txReceiptStatus)
                .setActualTxCost(actualTxCost)
                .setValue(NumberParserUtils.parseDouble(value))
                .setNodeId(nodeId)
                .setNodeName(nodeName)
                .setTotalReward(totalReward)
                .setUnDelegation(unDelegation)
                .build();
    }


    public static final class Builder {
        private String hash;
        private long blockNumber;
        private String chainId;
        private long createTime;
        private String actualTxCost;
        private String from;
        private String to;
        private long sequence;
        private int txReceiptStatus;
        private String txType;
        private String value;
        private String senderWalletName;
        private String txInfo;
        private long timestamp;
        private String toType;
        private String direction;
        private String nodeName;
        private String nodeId;
        private String lockAddress;
        private String reportType;
        private String version;
        private String url;
        private String piDID;
        private String proposalType;
        private String proposalId;
        private String vote;
        private String redeemStatus;
        private String walletIcon;
        private String walletName;
        private String unDelegation;
        private String stakingValue;
        private String totalReward;

        public Builder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder blockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder chainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder createTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder actualTxCost(String actualTxCost) {
            this.actualTxCost = actualTxCost;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder sequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder txReceiptStatus(int txReceiptStatus) {
            this.txReceiptStatus = txReceiptStatus;
            return this;
        }

        public Builder txType(String txType) {
            this.txType = txType;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder senderWalletName(String senderWalletName) {
            this.senderWalletName = senderWalletName;
            return this;
        }

        public Builder txInfo(String txInfo) {
            this.txInfo = txInfo;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder toType(String toType) {
            this.toType = toType;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder lockAddress(String lockAddress) {
            this.lockAddress = lockAddress;
            return this;
        }

        public Builder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder proposalType(String proposalType) {
            this.proposalType = proposalType;
            return this;
        }

        public Builder piDID(String piDID) {
            this.piDID = piDID;
            return this;
        }

        public Builder voteProposalType(String proposalId) {
            this.proposalId = proposalId;
            return this;
        }

        public Builder vote(String vote) {
            this.vote = vote;
            return this;
        }

        public Builder redeemStatus(String redeemStatus) {
            this.redeemStatus = redeemStatus;
            return this;
        }

        public Builder walletIcon(String walletIcon) {
            this.walletIcon = walletIcon;
            return this;
        }

        public Builder walletName(String walletName) {
            this.walletName = walletName;
            return this;
        }

        public Builder unDelegation(String unDelegation) {
            this.unDelegation = unDelegation;
            return this;
        }

        public Builder stakingValue(String stakingValue) {
            this.stakingValue = stakingValue;
            return this;
        }

        public Builder totalReward(String totalReward) {
            this.totalReward = totalReward;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "hash='" + hash + '\'' +
                ", blockNumber=" + blockNumber +
                ", chainId='" + chainId + '\'' +
                ", actualTxCost='" + actualTxCost + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", sequence=" + sequence +
                ", txReceiptStatus=" + txReceiptStatus +
                ", txType='" + txType + '\'' +
                ", value='" + value + '\'' +
                ", senderWalletName='" + senderWalletName + '\'' +
                ", txInfo='" + txInfo + '\'' +
                ", timestamp=" + timestamp +
                ", toType='" + toType + '\'' +
                ", direction='" + direction + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", lockAddress='" + lockAddress + '\'' +
                ", reportType='" + reportType + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", piDID='" + piDID + '\'' +
                ", proposalType='" + proposalType + '\'' +
                ", proposalId='" + proposalId + '\'' +
                ", vote='" + vote + '\'' +
                ", redeemStatus='" + redeemStatus + '\'' +
                ", walletIcon='" + walletIcon + '\'' +
                ", walletName='" + walletName + '\'' +
                ", unDelegation='" + unDelegation + '\'' +
                ", stakingValue='" + stakingValue + '\'' +
                ", totalReward='" + totalReward + '\'' +
                '}';
    }

    private List<String> byteArrayToList(byte[] bytes) {

        List<String> list = new ArrayList<>();

        int size = bytes.length;
        if (size == 2) {
            list.add("0");
        }
        for (byte b : bytes) {
            list.add(String.valueOf(b));
        }
        return list;
    }

    public TransactionRecordEntity buildTransactionRecordEntity() {
        return new TransactionRecordEntity(System.currentTimeMillis(), from, to, value, NodeManager.getInstance().getChainId());
    }
}
