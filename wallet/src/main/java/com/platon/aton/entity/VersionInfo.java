package com.platon.aton.entity;

/**
 * @author matrixelement
 */
public class VersionInfo {

    /**
     * 是否需要升级 true 需要  false 不需要
     */
    private boolean isNeed;
    /**
     * 是否强制升级 true 需要  false 不需要
     */
    private boolean isForce;
    /**
     * 最新版本，用于显示
     */
    private String newVersion;
    /**
     * apk下载地址
     */
    private String url;

    public VersionInfo() {
    }

    public boolean isNeed() {
        return isNeed;
    }

    public void setNeed(boolean need) {
        isNeed = need;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
