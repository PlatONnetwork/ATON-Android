package com.platon.wallet.entity;

import android.support.annotation.DrawableRes;

public class OfficialCommunityItem {

    private String webPortalUrl;

    private @DrawableRes
    int qrCodeImageRes;

    public OfficialCommunityItem(String webPortalUrl, int qrCodeImageRes) {
        this.webPortalUrl = webPortalUrl;
        this.qrCodeImageRes = qrCodeImageRes;
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

    public int getQrCodeImageRes() {
        return qrCodeImageRes;
    }

    public void setQrCodeImageRes(int qrCodeImageRes) {
        this.qrCodeImageRes = qrCodeImageRes;
    }
}
