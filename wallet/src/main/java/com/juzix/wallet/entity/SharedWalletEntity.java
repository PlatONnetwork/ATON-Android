package com.juzix.wallet.entity;

import android.os.Parcel;
import android.text.TextUtils;

import com.juzix.wallet.db.entity.OwnerInfoEntity;
import com.juzix.wallet.db.entity.SharedWalletInfoEntity;
import com.juzix.wallet.db.entity.SharedWalletOwnerInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class SharedWalletEntity extends WalletEntity implements Cloneable {

    /**
     * 创建者地址y
     */
    private String creatorAddress;
    /**
     * 共享钱包成员
     */
    private List<OwnerEntity> owner;
    /**
     * 所需签名数
     */
    private int requiredSignNumber;
    /**
     * 创建钱包进度
     */
    private int progress;
    /**
     * 创建钱包完成
     */
    private boolean finished;

    private boolean hasUnreadMessage;

    private String nodeAddress;

    protected SharedWalletEntity(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        address = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        owner = in.readArrayList(OwnerEntity.class.getClassLoader());
        creatorAddress = in.readString();
        requiredSignNumber = in.readInt();
        balance = in.readDouble();
        avatar = in.readString();
        hasUnreadMessage = in.readByte() != 0;
        progress = in.readInt();
        finished = in.readByte() != 0;
        nodeAddress = in.readString();
    }

    private SharedWalletEntity(Builder builder) {
        setUuid(builder.uuid);
        setName(builder.name);
        setAddress(builder.contractAddress);
        setCreatorAddress(builder.creatorAddress);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setOwner(builder.owner);
        setRequiredSignNumber(builder.requiredSignNumber);
        setBalance(builder.balance);
        setAvatar(builder.avatar);
        setHasUnreadMessage(builder.hasUnreadMessage);
        setProgress(builder.progress);
        setFinished(builder.finished);
        setNodeAddress(builder.nodeAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeList(owner);
        dest.writeString(creatorAddress);
        dest.writeInt(requiredSignNumber);
        dest.writeDouble(balance);
        dest.writeString(avatar);
        dest.writeByte((byte) (hasUnreadMessage ? 1 : 0));
        dest.writeInt(progress);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeString(nodeAddress);
    }

    public static final Creator<SharedWalletEntity> CREATOR = new Creator<SharedWalletEntity>() {
        @Override
        public SharedWalletEntity createFromParcel(Parcel in) {
            return new SharedWalletEntity(in);
        }

        @Override
        public SharedWalletEntity[] newArray(int size) {
            return new SharedWalletEntity[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(uuid) ? 0 : uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof SharedWalletEntity) {
            SharedWalletEntity entity = (SharedWalletEntity) obj;
            return !TextUtils.isEmpty(entity.uuid) && entity.uuid.equals(uuid);
        }

        return false;
    }

    @Override
    public SharedWalletEntity clone() {

        SharedWalletEntity walletEntity = null;
        try {
            walletEntity = (SharedWalletEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return walletEntity;
    }

    @Override
    public SharedWalletEntity updateBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public int getRequiredSignNumber() {
        return requiredSignNumber;
    }

    public void setRequiredSignNumber(int requiredSignNumber) {
        this.requiredSignNumber = requiredSignNumber;
    }

    public String getCreatorAddress() {
        return creatorAddress;
    }

    public void setCreatorAddress(String creatorAddress) {
        this.creatorAddress = creatorAddress;
    }

    @Override
    public String getNodeAddress() {
        return nodeAddress;
    }

    @Override
    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getPrefixCreatorAddress() {
        try {
            if (TextUtils.isEmpty(creatorAddress)) {
                return null;
            }
            if (creatorAddress.toLowerCase().startsWith("0x")) {
                return creatorAddress;
            }
            return "0x" + creatorAddress;
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public List<OwnerEntity> getOwner() {
        return owner;
    }

    public void setOwner(List<OwnerEntity> owner) {
        this.owner = owner;
    }

    public boolean isOwner() {
        if (owner == null || owner.isEmpty()) {
            return false;
        }
        for (OwnerEntity ownerEntity : owner) {
            if (getPrefixCreatorAddress().contains(ownerEntity.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

    public boolean isHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public SharedWalletEntity updateProgress(int progress) {
        setProgress(progress);
        return this;
    }

    public SharedWalletEntity updateFinished(boolean finished) {
        setFinished(finished);
        return this;
    }

    public SharedWalletInfoEntity buildWalletInfoEntity() {
        SharedWalletInfoEntity entity = new SharedWalletInfoEntity.Builder()
                .uuid(uuid)
                .name(name)
                .address(address)
                .creatorAddress(creatorAddress)
                .createTime(createTime)
                .updateTime(updateTime)
                .owner(buildAddressInfoEntityArrayList())
                .requiredSignNumber(requiredSignNumber)
                .avatar(avatar)
                .nodeAddress(nodeAddress)
                .build();
        return entity;
    }

    public ArrayList<OwnerInfoEntity> buildAddressInfoEntityArrayList() {
        ArrayList<OwnerInfoEntity> addressInfoEntityArrayList = new ArrayList<>();
        for (OwnerEntity entity : owner) {
            OwnerInfoEntity addressInfoEntity = new OwnerInfoEntity.Builder()
                    .uuid(entity.getUuid())
                    .address(entity.getAddress())
                    .name(entity.getName())
                    .nodeAddress(entity.getNodeAddress())
                    .build();
            addressInfoEntityArrayList.add(addressInfoEntity);
        }
        return addressInfoEntityArrayList;
    }

    public static final class Builder {
        private String uuid;
        private String name;
        private String contractAddress;
        private String creatorAddress;
        private long createTime;
        private long updateTime;
        private List<OwnerEntity> owner;
        private int requiredSignNumber;
        private double balance;
        private String avatar;
        private boolean hasUnreadMessage;
        private int progress;
        private boolean finished;
        private String nodeAddress;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder contractAddress(String val) {
            contractAddress = val;
            return this;
        }

        public Builder creatorAddress(String val) {
            creatorAddress = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder updateTime(long val) {
            updateTime = val;
            return this;
        }

        public Builder owner(List<OwnerEntity> val) {
            owner = val;
            return this;
        }

        public Builder requiredSignNumber(int val) {
            requiredSignNumber = val;
            return this;
        }

        public Builder balance(double val) {
            balance = val;
            return this;
        }

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public Builder hasUnreadMessage(boolean val) {
            hasUnreadMessage = val;
            return this;
        }

        public Builder progress(int val) {
            progress = val;
            return this;
        }

        public Builder finished(boolean val) {
            finished = val;
            return this;
        }

        public Builder nodeAddress(String val){
            nodeAddress = val;
            return this;
        }

        public SharedWalletEntity build() {
            return new SharedWalletEntity(this);
        }
    }

    public List<SharedWalletOwnerInfoEntity> buildSharedWalletOwnerInfoEntityList() {
        List<SharedWalletOwnerInfoEntity> sharedWalletOwnerInfoEntityList = new ArrayList<>();
        if (owner != null && !owner.isEmpty()) {
            for (OwnerEntity ownerEntity : owner) {
                SharedWalletOwnerInfoEntity sharedWalletOwnerInfoEntity = new SharedWalletOwnerInfoEntity();
                sharedWalletOwnerInfoEntity.setUuid(ownerEntity.getUuid());
                sharedWalletOwnerInfoEntity.setAddress(ownerEntity.getAddress());
                sharedWalletOwnerInfoEntity.setName(ownerEntity.getName());
                sharedWalletOwnerInfoEntity.setNodeAddress(ownerEntity.getNodeAddress());
                sharedWalletOwnerInfoEntityList.add(sharedWalletOwnerInfoEntity);
            }
        }

        return sharedWalletOwnerInfoEntityList;
    }

    @Override
    public String toString() {
        return "SharedWalletEntity{" +
                "creatorAddress='" + creatorAddress + '\'' +
                ", owner=" + owner +
                ", requiredSignNumber=" + requiredSignNumber +
                ", progress=" + progress +
                ", finished=" + finished +
                ", hasUnreadMessage=" + hasUnreadMessage +
                ", nodeAddress='" + nodeAddress + '\'' +
                '}';
    }
}
