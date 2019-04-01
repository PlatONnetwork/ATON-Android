package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author matrixelement
 */
public class VersionEntity implements Parcelable {
    private String version;
    private String downloadUrl;

    public VersionEntity() {
        super();
    }

    private VersionEntity(Builder builder) {
        setVersion(builder.version);
        setDownloadUrl(builder.downloadUrl);
    }

    protected VersionEntity(Parcel in) {
        version = in.readString();
        downloadUrl = in.readString();
    }

    public static final Creator<VersionEntity> CREATOR = new Creator<VersionEntity>() {
        @Override
        public VersionEntity createFromParcel(Parcel in) {
            return new VersionEntity(in);
        }

        @Override
        public VersionEntity[] newArray(int size) {
            return new VersionEntity[size];
        }
    };

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(downloadUrl);
    }


    public static final class Builder {
        private String version;
        private String downloadUrl;

        public Builder() {
        }

        public Builder version(String val) {
            version = val;
            return this;
        }

        public Builder downloadUrl(String val) {
            downloadUrl = val;
            return this;
        }

        public VersionEntity build() {
            return new VersionEntity(this);
        }
    }
}
