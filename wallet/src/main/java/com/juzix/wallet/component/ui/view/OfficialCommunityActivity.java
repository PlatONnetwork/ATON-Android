package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.OfficialCommunityAdapter;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.entity.OfficialCommunity;
import com.juzix.wallet.entity.OfficialCommunityItem;
import com.juzix.wallet.entity.OfficialCommunityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OfficialCommunityActivity extends BaseActivity {

    @BindView(R.id.list_official_community)
    RecyclerView listOfficialCommunity;

    OfficialCommunityAdapter mOfficialCommunityAdapter;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_community);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        mOfficialCommunityAdapter = new OfficialCommunityAdapter(this, getOfficialCommunityList(), R.layout.item_official_community);

        listOfficialCommunity.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listOfficialCommunity.addItemDecoration(new CommonVerticalItemDecoration(this, R.drawable.divider_official_community));
        listOfficialCommunity.setAdapter(mOfficialCommunityAdapter);
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private List<OfficialCommunity> getOfficialCommunityList() {

        List<OfficialCommunity> officialCommunityList = new ArrayList<>();

        List<OfficialCommunityItem> wxOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("官方微信群秘：CatherinePlatON", "www.baidu.com"),
                new OfficialCommunityItem("官方微信公众号：PlatON_network", "www.baidu.com"));
        OfficialCommunity wxOfficialCommunity = new OfficialCommunity(OfficialCommunityType.WECHAT, getResources().getString(R.string.wechat), wxOfficialCommunityItemList);
        officialCommunityList.add(wxOfficialCommunity);

        List<OfficialCommunityItem> tgOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://t.me/PlatONHK"));
        OfficialCommunity tgOfficialCommunity = new OfficialCommunity(OfficialCommunityType.TELEGRAM, getResources().getString(R.string.telegram), tgOfficialCommunityItemList);
        officialCommunityList.add(tgOfficialCommunity);

        List<OfficialCommunityItem> ghOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://github.com/PlatONnetwork"));
        OfficialCommunity ghOfficialCommunity = new OfficialCommunity(OfficialCommunityType.GITHUB, getResources().getString(R.string.github), ghOfficialCommunityItemList);
        officialCommunityList.add(ghOfficialCommunity);

        List<OfficialCommunityItem> twOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://twitter.com/PlatON_Network"));
        OfficialCommunity twOfficialCommunity = new OfficialCommunity(OfficialCommunityType.TWITTER, getResources().getString(R.string.twitter), twOfficialCommunityItemList);
        officialCommunityList.add(twOfficialCommunity);

        List<OfficialCommunityItem> fbOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://facebook.com/PlatONNetwork/"));
        OfficialCommunity fbOfficialCommunity = new OfficialCommunity(OfficialCommunityType.FACEBOOK, getResources().getString(R.string.facebook), fbOfficialCommunityItemList);
        officialCommunityList.add(fbOfficialCommunity);

        return officialCommunityList;
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, OfficialCommunityActivity.class);
        context.startActivity(intent);
    }
}
