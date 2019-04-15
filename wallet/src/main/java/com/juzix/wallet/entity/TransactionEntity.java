package com.juzix.wallet.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.juzix.wallet.R;

/**
 * @author matrixelement
 */
public abstract class TransactionEntity implements Comparable<TransactionEntity> {

    protected String uuid;
    /**
     * 交易hash
     */
    protected String hash;
    /**
     * 交易发送方地址
     */
    protected String fromAddress;
    /**
     * 交易接收方地址
     */
    protected String toAddress;
    /**
     * 交易创建时间
     */
    protected long createTime;
    /**
     * 交易金额
     */
    protected double value;
    /**
     * 当前交易区块
     */
    protected long blockNumber;
    /**
     * 最新交易区块
     */
    protected long latestBlockNumber;
    /**
     * 发生交易的钱包名称
     */
    protected String walletName;
    /**
     * 手续费
     */
    protected double energonPrice;
    /**
     * 转账备注
     */
    protected String memo;

    protected String nodeAddress;

    public TransactionEntity() {
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
        if (fromAddress.toLowerCase().startsWith("0x")) {
            this.fromAddress = fromAddress;
        } else {
            this.fromAddress = "0x" + fromAddress;
        }
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {

        if (TextUtils.isEmpty(toAddress)) {
            return;
        }
        if (toAddress.toLowerCase().startsWith("0x")) {
            this.toAddress = toAddress;
        } else {
            this.toAddress = "0x" + toAddress;
        }
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

    public long getSignedBlockNumber() {
        return latestBlockNumber - blockNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public boolean isReceiver(String walletAddress) {
        return !TextUtils.isEmpty(toAddress) && toAddress.equals(walletAddress);
    }

    @Override
    public int compareTo(@NonNull TransactionEntity o) {
        return Long.compare(o.createTime, createTime);
    }

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(uuid) ? 0 : uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TransactionEntity) {
            TransactionEntity transactionEntity = (TransactionEntity) obj;
            return transactionEntity.getUuid() != null && transactionEntity.getUuid().equals(uuid);
        }

        return super.equals(obj);
    }

    public boolean isRelevantWalletAddress(String walletAddress){
        return fromAddress.equals(walletAddress) || toAddress.equals(walletAddress);
    }

    public enum TransactionStatus {

        PENDING {
            @Override
            public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
                return context.getString(R.string.pending);
            }

            @Override
            public int getStatusDescTextColor() {
                return R.color.color_105cfe;
            }

            @Override
            public int getStatusDrawable() {
                return R.drawable.icon_pending;
            }
        }, SIGNING {
            @Override
            public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
                return String.format("%s(%d/%d)", context.getString(R.string.signing), signedBlockNumber, requiredSignNumber);
            }

            @Override
            public int getStatusDescTextColor() {
                return R.color.color_105cfe;
            }

            @Override
            public int getStatusDrawable() {
                return R.drawable.icon_pending;
            }
        }, SUCCEED {
            @Override
            public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
                return context.getString(R.string.success);
            }

            @Override
            public int getStatusDescTextColor() {
                return R.color.color_19a20e;
            }

            @Override
            public int getStatusDrawable() {
                return R.drawable.icon_successed;
            }
        }, FAILED {
            @Override
            public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
                return context.getString(R.string.failed);
            }

            @Override
            public int getStatusDescTextColor() {
                return R.color.color_f5302c;
            }

            @Override
            public int getStatusDrawable() {
                return R.drawable.icon_failed;
            }
        }, CREATE_JOINT_WALLET {
            @Override
            public String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber) {
                return context.getString(R.string.success);
            }

            @Override
            public int getStatusDescTextColor() {
                return R.color.color_19a20e;
            }

            @Override
            public int getStatusDrawable() {
                return R.drawable.icon_successed;
            }
        };

        public abstract String getStatusDesc(Context context, long signedBlockNumber, long requiredSignNumber);

        public abstract int getStatusDescTextColor();

        public abstract int getStatusDrawable();
    }

    public abstract TransactionStatus getTransactionStatus();
}

