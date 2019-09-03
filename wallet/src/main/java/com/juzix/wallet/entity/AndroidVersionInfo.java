package com.juzix.wallet.entity;

public class AndroidVersionInfo {

    /**
     * 版本号
     */
    private String version;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 是否强制更新
     */
    private boolean isForce;

    public AndroidVersionInfo() {
    }

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

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }
}
