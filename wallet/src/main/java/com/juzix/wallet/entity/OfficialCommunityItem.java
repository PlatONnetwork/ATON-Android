package com.juzix.wallet.entity;

public class OfficialCommunityItem {

    private String webPortalUrl;

    private String qrCodeUrl;

    public OfficialCommunityItem(String webPortalUrl, String qrCodeUrl) {
        this.webPortalUrl = webPortalUrl;
        this.qrCodeUrl = qrCodeUrl;
    }

    public OfficialCommunityItem(String webPortalUrl) {
        this.webPortalUrl = webPortalUrl;
    }

    public String getWebPortalUrl() {
        return webPortalUrl;
    }

    public void setWebPortalUrl(String webPortalUrl) {
        this.webPortalUrl = webPortalUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
