package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class SharedTransactionInfoEntity extends RealmObject {

    @PrimaryKey
    private String uuid;
    /**
     * 交易hash
     */
    private String hash;
    /**
     * 交易发送方地址
     */
    private String fromAddress;
    /**
     * 交易接收方地址
     */
    private String toAddress;
    /**
     * 交易创建时间
     */
    private long createTime;
    /**
     * 交易金额
     */
    private double value;
    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 最新交易区块
     */
    private long latestBlockNumber;
    /**
     * 发生交易的钱包名称
     */
    private String walletName;
    /**
     * 手续费
     */
    private double energonPrice;
    /**
     * 转账备注
     */
    private String memo;

    /**
     * 交易ID
     */
    private String transactionId;
    /**
     * 合约地址
     */
    private String contractAddress;
    /**
     * 执行中
     */
    private boolean pending;
    /**
     * 执行完毕
     */
    private boolean executed;
    /**
     * 状态列表
     */
    private RealmList<TransactionInfoResult> transactionResult;
    /**
     * 所需签名数
     */
    private int requiredSignNumber;
    /**
     * 是否已读
     */
    private boolean read;
    /**
     * 联名交易owner
     */
    private RealmList<SharedWalletOwnerInfoEntity> sharedWalletOwnerInfoEntityRealmList = new RealmList<>();
    /**
     * 钱包创建者地址
     */
    private String ownerWalletAddress;
    /**
     * 交易类型
     */
    private int transactionType;

    public SharedTransactionInfoEntity() {

    }

    private SharedTransactionInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setHash(builder.hash);
        setFromAddress(builder.fromAddress);
        setToAddress(builder.toAddress);
        setCreateTime(builder.createTime);
        setValue(builder.value);
        setBlockNumber(builder.blockNumber);
        setLatestBlockNumber(builder.latestBlockNumber);
        setWalletName(builder.walletName);
        setEnergonPrice(builder.energonPrice);
        setMemo(builder.memo);
        setTransactionId(builder.transactionId);
        setContractAddress(builder.contractAddress);
        setPending(builder.pending);
        setExecuted(builder.executed);
        setTransactionResultArrayList(builder.transactionResult);
        setRequiredSignNumber(builder.requiredSignNumber);
        setRead(builder.read);
        setSharedWalletOwnerInfoEntityRealmList(builder.sharedWalletOwnerInfoEntityList);
        setOwnerWalletAddress(builder.ownerWalletAddress);
        setTransactionType(builder.transactionType);
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
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

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public double getEnergonPrice() {
        return energonPrice;
    }

    public void setEnergonPrice(double energonPrice) {
        this.energonPrice = energonPrice;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public RealmList<TransactionInfoResult> getTransactionResult() {
        return transactionResult;
    }

    public void setTransactionResult(RealmList<TransactionInfoResult> transactionResult) {
        this.transactionResult = transactionResult;
    }

    public RealmList<SharedWalletOwnerInfoEntity> getSharedWalletOwnerInfoEntityRealmList() {
        return sharedWalletOwnerInfoEntityRealmList;
    }

    public void setSharedWalletOwnerInfoEntityRealmList(List<SharedWalletOwnerInfoEntity> sharedWalletOwnerInfoEntityList) {

        if (sharedWalletOwnerInfoEntityList == null || sharedWalletOwnerInfoEntityList.isEmpty()) {
            return;
        }

        for (SharedWalletOwnerInfoEntity sharedWalletOwnerInfoEntity : sharedWalletOwnerInfoEntityList) {
            this.sharedWalletOwnerInfoEntityRealmList.add(sharedWalletOwnerInfoEntity);
        }
    }

    public String getOwnerWalletAddress() {
        return ownerWalletAddress;
    }

    public void setOwnerWalletAddress(String ownerWalletAddress) {
        this.ownerWalletAddress = ownerWalletAddress;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public List<OwnerEntity> buildOwnerEntityList() {
        List<OwnerEntity> ownerEntityList = new ArrayList<>();
        if (sharedWalletOwnerInfoEntityRealmList != null && !sharedWalletOwnerInfoEntityRealmList.isEmpty()) {
            for (SharedWalletOwnerInfoEntity sharedWalletOwnerInfoEntity : sharedWalletOwnerInfoEntityRealmList) {
                ownerEntityList.add(new OwnerEntity(sharedWalletOwnerInfoEntity.getUuid(), sharedWalletOwnerInfoEntity.getName(), sharedWalletOwnerInfoEntity.getAddress()));
            }
        }

        return ownerEntityList;
    }

    public ArrayList<TransactionInfoResult> getTransactionResultArrayList() {
        ArrayList<TransactionInfoResult> transactionInfoResults = new ArrayList<>();
        if (this.transactionId == null) {
            return transactionInfoResults;
        }
        for (TransactionInfoResult infoEntity : this.transactionResult) {
            transactionInfoResults.add(infoEntity);
        }
        return transactionInfoResults;
    }

    public void setTransactionResultArrayList(ArrayList<TransactionInfoResult> transactionInfoResultList) {
        if (transactionInfoResultList == null) {
            return;
        }
        this.transactionResult = new RealmList<>();
        for (TransactionInfoResult resultEntity : transactionInfoResultList) {
            this.transactionResult.add(resultEntity);
        }
    }

    public ArrayList<TransactionResult> buildTransactionResult() {
        ArrayList<TransactionResult> transactionResults = new ArrayList<>();
        for (TransactionInfoResult result : transactionResult) {
            transactionResults.add(new TransactionResult.Builder()
                    .name(result.getName())
                    .address(result.getAddress())
                    .operation(result.getOperation())
                    .build());
        }
        return transactionResults;
    }

    public int getRequiredSignNumber() {
        return requiredSignNumber;
    }

    public void setRequiredSignNumber(int requiredSignNumber) {
        this.requiredSignNumber = requiredSignNumber;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }


    public static final class Builder {
        private String uuid;
        private String hash;
        private String fromAddress;
        private String toAddress;
        private long createTime;
        private double value;
        private long blockNumber;
        private long latestBlockNumber;
        private String walletName;
        private double energonPrice;
        private String memo;
        private String transactionId;
        private String contractAddress;
        private boolean pending;
        private boolean executed;
        private ArrayList<TransactionInfoResult> transactionResult;
        private int requiredSignNumber;
        private boolean read;
        private List<SharedWalletOwnerInfoEntity> sharedWalletOwnerInfoEntityList;
        private String ownerWalletAddress;
        private int transactionType;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder hash(String val) {
            hash = val;
            return this;
        }

        public Builder fromAddress(String val) {
            fromAddress = val;
            return this;
        }

        public Builder toAddress(String val) {
            toAddress = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder value(double val) {
            value = val;
            return this;
        }

        public Builder blockNumber(long val) {
            blockNumber = val;
            return this;
        }

        public Builder latestBlockNumber(long val) {
            latestBlockNumber = val;
            return this;
        }

        public Builder walletName(String val) {
            walletName = val;
            return this;
        }

        public Builder energonPrice(double val) {
            energonPrice = val;
            return this;
        }

        public Builder memo(String val) {
            memo = val;
            return this;
        }

        public Builder transactionId(String val) {
            transactionId = val;
            return this;
        }

        public Builder contractAddress(String val) {
            contractAddress = val;
            return this;
        }

        public Builder pending(boolean val) {
            pending = val;
            return this;
        }

        public Builder executed(boolean val) {
            executed = val;
            return this;
        }

        public Builder transactionResult(ArrayList<TransactionInfoResult> val) {
            transactionResult = val;
            return this;
        }

        public Builder requiredSignNumber(int val) {
            requiredSignNumber = val;
            return this;
        }

        public Builder read(boolean val) {
            read = val;
            return this;
        }

        public Builder sharedWalletOwnerInfoEntityList(List<SharedWalletOwnerInfoEntity> val) {
            sharedWalletOwnerInfoEntityList = val;
            return this;
        }

        public Builder ownerWalletAddress(String val) {
            ownerWalletAddress = val;
            return this;
        }

        public Builder transactionType(int val) {
            transactionType = val;
            return this;
        }

        public SharedTransactionInfoEntity build() {
            return new SharedTransactionInfoEntity(this);
        }
    }

    public SharedTransactionEntity buildSharedTransactionEntity() {
        return new SharedTransactionEntity.Builder(getUuid(), getCreateTime(), getWalletName())
                .hash(getHash())
                .contractAddress(getContractAddress())
                .fromAddress(getFromAddress())
                .toAddress(getToAddress())
                .value(getValue())
                .memo(getMemo())
                .energonPrice(getEnergonPrice())
                .pending(isPending())
                .executed(isExecuted())
                .transactionId(getTransactionId())
                .transactionResult(buildTransactionResult())
                .requiredSignNumber(getRequiredSignNumber())
                .blockNumber(getBlockNumber())
                .latestBlockNumber(getLatestBlockNumber())
                .read(isRead())
                .ownerEntityList(buildOwnerEntityList())
                .ownerWalletAddress(getOwnerWalletAddress())
                .transactionType(transactionType)
                .build();
    }
}
