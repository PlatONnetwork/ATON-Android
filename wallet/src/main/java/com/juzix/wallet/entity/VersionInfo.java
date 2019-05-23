package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * @author matrixelement
 */
public class VersionInfo implements Parcelable {

    private String version;
    private String downloadUrl;

    public VersionInfo() {
        super();
    }

    private VersionInfo(Builder builder) {
        setVersion(builder.version);
        setDownloadUrl(builder.downloadUrl);
    }

    protected VersionInfo(Parcel in) {
        version = in.readString();
        downloadUrl = in.readString();
    }

    public static final Creator<VersionInfo> CREATOR = new Creator<VersionInfo>() {
        @Override
        public VersionInfo createFromParcel(Parcel in) {
            return new VersionInfo(in);
        }

        @Override
        public VersionInfo[] newArray(int size) {
            return new VersionInfo[size];
        }
    };

    public String getVersion() {
        return version;
    }

    public String getVersionWithoutPrefix(){
        if (!TextUtils.isEmpty(version) && version.startsWith("v")){
            return version.substring(1);
        }
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

        public VersionInfo build() {
            return new VersionInfo(this);
        }
    }
}
