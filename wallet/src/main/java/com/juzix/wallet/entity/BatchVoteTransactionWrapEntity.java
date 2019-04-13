package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BatchVoteTransactionWrapEntity implements Parcelable {

    /**
     * 投票合并类
     */
    private BatchVoteTransactionEntity batchVoteTransactionEntity;

    /**
     * 投票列表
     */
    private List<BatchVoteTransactionEntity> batchVoteTransactionEntityList;

    public BatchVoteTransactionWrapEntity(BatchVoteTransactionEntity batchVoteTransactionEntity, List<BatchVoteTransactionEntity> batchVoteTransactionEntityList) {
        this.batchVoteTransactionEntity = batchVoteTransactionEntity;
        this.batchVoteTransactionEntityList = batchVoteTransactionEntityList;
    }

    protected BatchVoteTransactionWrapEntity(Parcel in) {
        batchVoteTransactionEntity = in.readParcelable(BatchVoteTransactionEntity.class.getClassLoader());
        batchVoteTransactionEntityList = in.createTypedArrayList(BatchVoteTransactionEntity.CREATOR);
    }

    public static final Creator<BatchVoteTransactionWrapEntity> CREATOR = new Creator<BatchVoteTransactionWrapEntity>() {
        @Override
        public BatchVoteTransactionWrapEntity createFromParcel(Parcel in) {
            return new BatchVoteTransactionWrapEntity(in);
        }

        @Override
        public BatchVoteTransactionWrapEntity[] newArray(int size) {
            return new BatchVoteTransactionWrapEntity[size];
        }
    };

    public BatchVoteTransactionEntity getBatchVoteTransactionEntity() {
        return batchVoteTransactionEntity;
    }

    public List<BatchVoteTransactionEntity> getBatchVoteTransactionEntityList() {
        return batchVoteTransactionEntityList;
    }

    public void setBatchVoteTransactionEntity(BatchVoteTransactionEntity batchVoteTransactionEntity) {
        this.batchVoteTransactionEntity = batchVoteTransactionEntity;
    }

    public void setBatchVoteTransactionEntityList(List<BatchVoteTransactionEntity> batchVoteTransactionEntityList) {
        this.batchVoteTransactionEntityList = batchVoteTransactionEntityList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(batchVoteTransactionEntity, flags);
        dest.writeTypedList(batchVoteTransactionEntityList);
    }
}
