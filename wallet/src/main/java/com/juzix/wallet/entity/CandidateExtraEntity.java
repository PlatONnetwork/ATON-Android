package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CandidateExtraEntity implements Parcelable {

    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点logo
     */
    private String nodePortrait;
    /**
     * 机构简介
     */
    private String nodeDiscription;
    /**
     * 机构名称
     */
    private String nodeDepartment;
    /**
     * 官网
     */
    private String officialWebsite;
    /**
     * 申请时间
     */
    private long time;

    public CandidateExtraEntity() {

    }

    protected CandidateExtraEntity(Parcel in) {
        nodeName = in.readString();
        nodePortrait = in.readString();
        nodeDiscription = in.readString();
        nodeDepartment = in.readString();
        officialWebsite = in.readString();
        time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeName);
        dest.writeString(nodePortrait);
        dest.writeString(nodeDiscription);
        dest.writeString(nodeDepartment);
        dest.writeString(officialWebsite);
        dest.writeLong(time);
    }

    public static final Creator<CandidateExtraEntity> CREATOR = new Creator<CandidateExtraEntity>() {
        @Override
        public CandidateExtraEntity createFromParcel(Parcel in) {
            return new CandidateExtraEntity(in);
        }

        @Override
        public CandidateExtraEntity[] newArray(int size) {
            return new CandidateExtraEntity[size];
        }
    };

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodePortrait() {
        return nodePortrait;
    }

    public void setNodePortrait(String nodePortrait) {
        this.nodePortrait = nodePortrait;
    }

    public String getNodeDiscription() {
        return nodeDiscription;
    }

    public void setNodeDiscription(String nodeDiscription) {
        this.nodeDiscription = nodeDiscription;
    }

    public String getNodeDepartment() {
        return nodeDepartment;
    }

    public void setNodeDepartment(String nodeDepartment) {
        this.nodeDepartment = nodeDepartment;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}