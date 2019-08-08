package com.juzix.wallet.entity;

import com.juzix.wallet.R;

import java.util.List;

public class OfficialCommunity {

    private @OfficialCommunityType
    int officialCommunityType;

    private String name;

    private List<OfficialCommunityItem> officialCommunityItemList;

    public OfficialCommunity(int officialCommunityType, String name, List<OfficialCommunityItem> officialCommunityItemList) {
        this.officialCommunityType = officialCommunityType;
        this.name = name;
        this.officialCommunityItemList = officialCommunityItemList;
    }

    public int getOfficialCommunityType() {
        return officialCommunityType;
    }

    public void setOfficialCommunityType(int officialCommunityType) {
        this.officialCommunityType = officialCommunityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OfficialCommunityItem> getOfficialCommunityItemList() {
        return officialCommunityItemList;
    }

    public void setOfficialCommunityItemList(List<OfficialCommunityItem> officialCommunityItemList) {
        this.officialCommunityItemList = officialCommunityItemList;
    }

    public int getOfficialCommunityImageRes() {
        switch (officialCommunityType) {
            case OfficialCommunityType.WECHAT:
                return R.drawable.icon_wechat_community;
            case OfficialCommunityType.TELEGRAM:
                return R.drawable.icon_telegram_community;
            case OfficialCommunityType.GITHUB:
                return R.drawable.icon_github_community;
            case OfficialCommunityType.TWITTER:
                return R.drawable.icon_twitter_community;
            case OfficialCommunityType.FACEBOOK:
                return R.drawable.icon_facebook_community;
            default:
                return -1;
        }

    }
}
