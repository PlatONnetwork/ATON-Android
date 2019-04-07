package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.entity.SharedWalletOwnerInfoEntity;
import com.juzix.wallet.utils.JSONUtil;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.juzix.wallet.entity.TransactionResult.Status.OPERATION_APPROVAL;
import static com.juzix.wallet.entity.TransactionResult.Status.OPERATION_REVOKE;
import static com.juzix.wallet.entity.TransactionResult.Status.OPERATION_UNDETERMINED;

/**
 * @author matrixelement
 */
public class SharedTransactionEntity extends TransactionEntity implements Cloneable, Parcelable {

    /**
     * 交易ID
     */
    private String transactionId;
    /**
     * 合约地址
     */
    private String contractAddress;
    private boolean pending;
    private boolean executed;
    private String transactionResult;
    /**
     * 所需签名数
     */
    private int requiredSignNumber;
    /**
     * 是否已读
     */
    private boolean read;

    private List<OwnerEntity> ownerEntityList;
    /**
     * 与共享钱包关联的钱包地址
     */
    private String ownerWalletAddress;
    /**
     * 交易类型，包括，创建联名钱包、执行联名钱包、发送交易
     */
    private int transactionType;

    public SharedTransactionEntity() {
    }

    private SharedTransactionEntity(Builder builder) {
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
        setTransactionResult(builder.transactionResult);
        setPending(builder.pending);
        setExecuted(builder.executed);
        setRequiredSignNumber(builder.requiredSignNumber);
        setRead(builder.read);
        setOwnerEntityList(builder.ownerEntityList);
        setOwnerWalletAddress(builder.ownerWalletAddress);
        setTransactionType(builder.transactionType);
    }

    protected SharedTransactionEntity(Parcel in) {
        uuid = in.readString();
        hash = in.readString();
        fromAddress = in.readString();
        toAddress = in.readString();
        createTime = in.readLong();
        value = in.readDouble();
        blockNumber = in.readLong();
        latestBlockNumber = in.readLong();
        walletName = in.readString();
        energonPrice = in.readDouble();
        memo = in.readString();
        transactionId = in.readString();
        contractAddress = in.readString();
        transactionResult = in.readString();
        pending = in.readByte() != 0;
        executed = in.readByte() != 0;
        requiredSignNumber = in.readInt();
        read = in.readByte() != 0;
        ownerEntityList = in.readArrayList(OwnerEntity.class.getClassLoader());
        ownerWalletAddress = in.readString();
        transactionType = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(hash);
        dest.writeString(fromAddress);
        dest.writeString(toAddress);
        dest.writeLong(createTime);
        dest.writeDouble(value);
        dest.writeLong(blockNumber);
        dest.writeLong(latestBlockNumber);
        dest.writeString(walletName);
        dest.writeDouble(energonPrice);
        dest.writeString(memo);
        dest.writeString(transactionId);
        dest.writeString(contractAddress);
        dest.writeString(transactionResult);
        dest.writeByte((byte) (pending ? 1 : 0));
        dest.writeByte((byte) (executed ? 1 : 0));
        dest.writeInt(requiredSignNumber);
        dest.writeByte((byte) (read ? 1 : 0));
        dest.writeList(ownerEntityList);
        dest.writeString(ownerWalletAddress);
        dest.writeInt(transactionType);
    }

    public static final Creator<SharedTransactionEntity> CREATOR = new Creator<SharedTransactionEntity>() {
        @Override
        public SharedTransactionEntity createFromParcel(Parcel in) {
            return new SharedTransactionEntity(in);
        }

        @Override
        public SharedTransactionEntity[] newArray(int size) {
            return new SharedTransactionEntity[size];
        }
    };

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public boolean isOwner() {
        if (ownerEntityList == null || ownerEntityList.isEmpty()) {
            return false;
        }
        for (OwnerEntity ownerEntity : ownerEntityList) {
            if (contractAddress.contains(ownerEntity.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public void setContractAddress(String contractAddress) {
        if (contractAddress.toLowerCase().startsWith("0x")) {
            this.contractAddress = contractAddress;
        } else {
            this.contractAddress = "0x" + contractAddress;
        }
    }

    public String getTransactionResult() {
        return transactionResult;
    }

    public void setTransactionResult(String transactionResult) {
        this.transactionResult = transactionResult;
    }

    public void setRequiredSignNumber(int requiredSignNumber) {
        this.requiredSignNumber = requiredSignNumber;
    }

    public int getRequiredSignNumber() {
        return requiredSignNumber;
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

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public void setOwnerEntityList(List<OwnerEntity> ownerEntityList) {
        this.ownerEntityList = ownerEntityList;
    }

    public List<OwnerEntity> getOwnerEntityList() {
        return ownerEntityList;
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

    public boolean transfered() {
        if (getConfirms() >= requiredSignNumber || getRevokes() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 已经确认数，包括同意和拒绝
     *
     * @return
     */
    public long getConfirms() {

        return Flowable.fromCallable(new Callable<List<TransactionResult>>() {
            @Override
            public List<TransactionResult> call() throws Exception {
                return getTransactionResultList();
            }
        }).filter(new Predicate<List<TransactionResult>>() {
            @Override
            public boolean test(List<TransactionResult> transactionResults) throws Exception {
                return !transactionResults.isEmpty();
            }
        }).flatMap(new Function<List<TransactionResult>, Publisher<TransactionResult>>() {
            @Override
            public Publisher<TransactionResult> apply(List<TransactionResult> transactionResults) throws Exception {
                return Flowable.fromIterable(transactionResults);
            }
        })
                .map(new Function<TransactionResult, TransactionResult.Status>() {
                    @Override
                    public TransactionResult.Status apply(TransactionResult transactionResult) throws Exception {
                        return transactionResult.getStatus();
                    }
                }).filter(new Predicate<TransactionResult.Status>() {
                    @Override
                    public boolean test(TransactionResult.Status status) throws Exception {
                        return status == OPERATION_APPROVAL || status == TransactionResult.Status.OPERATION_REVOKE;
                    }
                }).count().onErrorReturnItem(0L).blockingGet();
    }

    /**
     * 获取拒绝数
     *
     * @return
     */
    public long getRevokes() {
        return Flowable
                .fromIterable(getTransactionResultList())
                .map(new Function<TransactionResult, TransactionResult.Status>() {
                    @Override
                    public TransactionResult.Status apply(TransactionResult transactionResult) throws Exception {
                        return transactionResult.getStatus();
                    }
                }).filter(new Predicate<TransactionResult.Status>() {
                    @Override
                    public boolean test(TransactionResult.Status status) throws Exception {
                        return status == TransactionResult.Status.OPERATION_REVOKE;
                    }
                }).count().onErrorReturnItem(0L).blockingGet();
    }

    public int getRequired() {
        return TextUtils.isEmpty(hash) ? requiredSignNumber : 12;
    }

    public List<TransactionResult> getTransactionResultList() {
        return JSONUtil.parseArray(transactionResult, TransactionResult.class);
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
        private String transactionResult;
        private boolean pending;
        private boolean executed;
        private int requiredSignNumber;
        private boolean read;
        private List<OwnerEntity> ownerEntityList;
        private String ownerWalletAddress;
        private int transactionType;

        public Builder(String uuid, long createTime, String walletName) {
            this.uuid = uuid;
            this.createTime = createTime;
            this.walletName = walletName;
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

        public Builder value(double val) {
            value = val;
            return this;
        }

        public Builder blockNumber(long va1) {
            blockNumber = va1;
            return this;
        }

        public Builder latestBlockNumber(long va1) {
            latestBlockNumber = va1;
            return this;
        }

        public Builder energonPrice(double va1) {
            energonPrice = va1;
            return this;
        }

        public Builder memo(String va1) {
            memo = va1;
            return this;
        }

        public Builder transactionId(String val) {
            transactionId = val;
            return this;
        }

        public Builder contractAddress(String va1) {
            contractAddress = va1;
            return this;
        }

        public Builder transactionResult(String va1) {
            transactionResult = va1;
            return this;
        }

        public Builder pending(boolean va1) {
            pending = va1;
            return this;
        }

        public Builder executed(boolean va1) {
            executed = va1;
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

        public Builder ownerEntityList(List<OwnerEntity> val) {
            this.ownerEntityList = val;
            return this;
        }

        public Builder ownerWalletAddress(String val) {
            this.ownerWalletAddress = val;
            return this;
        }

        public Builder transactionType(int val) {
            this.transactionType = val;
            return this;
        }

        public SharedTransactionEntity build() {
            return new SharedTransactionEntity(this);
        }
    }

    @Override
    public SharedTransactionEntity clone() {
        SharedTransactionEntity transactionEntity = null;
        try {
            transactionEntity = (SharedTransactionEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return transactionEntity;
    }

    @Override
    public TransactionStatus getTransactionStatus() {

        if (TextUtils.isEmpty(hash) && executed) {
            return TransactionStatus.SUCCEED;
        }

        if (transactionResult == null || transactionResult.isEmpty()) {
            return TransactionStatus.CREATE_JOINT_WALLET;
        }

        long approvalCount = getApprovalCount();
        long undeterminedCount = getUndeterminedCount();

        //同意数达到要求签名数
        if (approvalCount >= requiredSignNumber) {
            return TransactionStatus.SUCCEED;
        }

        if (undeterminedCount + approvalCount < requiredSignNumber) {
            return TransactionStatus.FAILED;
        }
        return TransactionStatus.SIGNING;
    }

    public long getApprovalCount() {
        return Flowable.fromCallable(new Callable<List<TransactionResult>>() {
            @Override
            public List<TransactionResult> call() throws Exception {
                return getTransactionResultList();
            }
        }).filter(new Predicate<List<TransactionResult>>() {
            @Override
            public boolean test(List<TransactionResult> transactionResults) throws Exception {
                return !transactionResults.isEmpty();
            }
        }).flatMap(new Function<List<TransactionResult>, Publisher<TransactionResult>>() {
            @Override
            public Publisher<TransactionResult> apply(List<TransactionResult> transactionResults) throws Exception {
                return Flowable.fromIterable(transactionResults);
            }
        })
                .map(new Function<TransactionResult, Boolean>() {
                    @Override
                    public Boolean apply(TransactionResult transactionResult) throws Exception {
                        return transactionResult.getStatus() == OPERATION_APPROVAL;
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).count().onErrorReturnItem(0L).blockingGet();
    }

    public long getRevokeCount() {
        return Flowable.fromCallable(new Callable<List<TransactionResult>>() {
            @Override
            public List<TransactionResult> call() throws Exception {
                return getTransactionResultList();
            }
        }).filter(new Predicate<List<TransactionResult>>() {
            @Override
            public boolean test(List<TransactionResult> transactionResults) throws Exception {
                return !transactionResults.isEmpty();
            }
        }).flatMap(new Function<List<TransactionResult>, Publisher<TransactionResult>>() {
            @Override
            public Publisher<TransactionResult> apply(List<TransactionResult> transactionResults) throws Exception {
                return Flowable.fromIterable(transactionResults);
            }
        })
                .map(new Function<TransactionResult, Boolean>() {
                    @Override
                    public Boolean apply(TransactionResult transactionResult) throws Exception {
                        return transactionResult.getStatus() == OPERATION_REVOKE;
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).count().onErrorReturnItem(0L).blockingGet();
    }

    public long getUndeterminedCount() {
        return Flowable.fromCallable(new Callable<List<TransactionResult>>() {
            @Override
            public List<TransactionResult> call() throws Exception {
                return getTransactionResultList();
            }
        }).filter(new Predicate<List<TransactionResult>>() {
            @Override
            public boolean test(List<TransactionResult> transactionResults) throws Exception {
                return !transactionResults.isEmpty();
            }
        }).flatMap(new Function<List<TransactionResult>, Publisher<TransactionResult>>() {
            @Override
            public Publisher<TransactionResult> apply(List<TransactionResult> transactionResults) throws Exception {
                return Flowable.fromIterable(transactionResults);
            }
        })
                .map(new Function<TransactionResult, Boolean>() {
                    @Override
                    public Boolean apply(TransactionResult transactionResult) throws Exception {
                        return transactionResult.getStatus() == OPERATION_UNDETERMINED;
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).count().onErrorReturnItem(0L).blockingGet();
    }

    public List<SharedWalletOwnerInfoEntity> buildSharedWalletOwnerInfoEntityList() {
        List<SharedWalletOwnerInfoEntity> sharedWalletOwnerInfoEntityList = new ArrayList<>();
        if (ownerEntityList != null && !ownerEntityList.isEmpty()) {
            for (OwnerEntity ownerEntity : ownerEntityList) {
                SharedWalletOwnerInfoEntity sharedWalletOwnerInfoEntity = new SharedWalletOwnerInfoEntity();
                sharedWalletOwnerInfoEntity.setUuid(ownerEntity.getUuid());
                sharedWalletOwnerInfoEntity.setAddress(ownerEntity.getAddress());
                sharedWalletOwnerInfoEntity.setName(ownerEntity.getName());
                sharedWalletOwnerInfoEntityList.add(sharedWalletOwnerInfoEntity);
            }
        }

        return sharedWalletOwnerInfoEntityList;
    }

    public SharedTransactionInfoEntity buildSharedTransactionInfoEntity() {
        return new SharedTransactionInfoEntity.Builder()
                .uuid(getUuid())
                .createTime(getCreateTime())
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
                .transactionResult(getTransactionResult())
                .requiredSignNumber(getRequiredSignNumber())
                .blockNumber(getBlockNumber())
                .latestBlockNumber(getLatestBlockNumber())
                .read(isRead())
                .ownerWalletAddress(getOwnerWalletAddress())
                .transactionType(getTransactionType())
                .sharedWalletOwnerInfoEntityList(buildSharedWalletOwnerInfoEntityList())
                .walletName(getWalletName())
                .build();
    }

    public enum TransactionType {

        CREATE_JOINT_WALLET(0) {
            @Override
            public int getTransactionTypeDesc(String transactionToAddress, String queryAddress) {
                return R.string.create_joint_wallet;
            }
        },

        EXECUTED_CONTRACT(1) {
            @Override
            public int getTransactionTypeDesc(String transactionToAddress, String queryAddress) {
                return R.string.joint_wallet_execution;
            }
        },

        SEND_TRANSACTION(2) {
            @Override
            public int getTransactionTypeDesc(String transactionToAddress, String queryAddress) {
                return !TextUtils.isEmpty(transactionToAddress) && transactionToAddress.equals(queryAddress) ? R.string.receive : R.string.send;
            }
        };

        private int typeValue;

        public int getValue() {
            return typeValue;
        }

        TransactionType(int type) {
            typeValue = type;
        }

        private final static Map<Integer, TransactionType> map = new HashMap<>();

        static {
            for (TransactionType type : TransactionType.values()) {
                map.put(type.typeValue, type);
            }
        }

        public static TransactionType getTransactionType(int typeValue) {
            return map.get(typeValue);
        }

        public abstract int getTransactionTypeDesc(String transactionToAddress, String queryAddress);
    }

    @Override
    public String toString() {
        return "SharedTransactionEntity{" +
                "transactionId='" + transactionId + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", pending=" + pending +
                ", executed=" + executed +
                ", transactionResult=" + transactionResult +
                ", requiredSignNumber=" + requiredSignNumber +
                ", read=" + read +
                ", ownerEntityList=" + ownerEntityList +
                ", ownerWalletAddress='" + ownerWalletAddress + '\'' +
                ", transactionType=" + transactionType +
                '}';
    }
}
