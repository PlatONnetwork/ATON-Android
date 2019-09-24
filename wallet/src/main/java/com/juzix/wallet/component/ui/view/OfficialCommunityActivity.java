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

        List<OfficialCommunityItem> wxOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem(getString(R.string.group_secretary), R.drawable.icon_platon_alita),
                new OfficialCommunityItem(getString(R.string.official_account), R.drawable.icon_platon_network));
        OfficialCommunity wxOfficialCommunity = new OfficialCommunity(OfficialCommunityType.WECHAT, getResources().getString(R.string.wechat), wxOfficialCommunityItemList);
        officialCommunityList.add(wxOfficialCommunity);

        List<OfficialCommunityItem> twOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://twitter.com/PlatON_Network"));
        OfficialCommunity twOfficialCommunity = new OfficialCommunity(OfficialCommunityType.TWITTER, getResources().getString(R.string.twitter), twOfficialCommunityItemList);
        officialCommunityList.add(twOfficialCommunity);

        List<OfficialCommunityItem> fbOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://facebook.com/PlatONNetwork/"));
        OfficialCommunity fbOfficialCommunity = new OfficialCommunity(OfficialCommunityType.FACEBOOK, getResources().getString(R.string.facebook), fbOfficialCommunityItemList);
        officialCommunityList.add(fbOfficialCommunity);

        List<OfficialCommunityItem> ghOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://github.com/PlatONnetwork"));
        OfficialCommunity ghOfficialCommunity = new OfficialCommunity(OfficialCommunityType.GITHUB, getResources().getString(R.string.github), ghOfficialCommunityItemList);
        officialCommunityList.add(ghOfficialCommunity);

        List<OfficialCommunityItem> rdOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://www.reddit.com/user/PlatON_Network"));
        OfficialCommunity rdOfficialCommunity = new OfficialCommunity(OfficialCommunityType.REDDIT, getResources().getString(R.string.reddit), rdOfficialCommunityItemList);
        officialCommunityList.add(rdOfficialCommunity);

        List<OfficialCommunityItem> meOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://medium.com/@PlatON_Network"));
        OfficialCommunity meOfficialCommunity = new OfficialCommunity(OfficialCommunityType.MEDIUM, getResources().getString(R.string.medium), meOfficialCommunityItemList);
        officialCommunityList.add(meOfficialCommunity);

        List<OfficialCommunityItem> liOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://www.linkedin.com/company/platonnetwork/"));
        OfficialCommunity liOfficialCommunity = new OfficialCommunity(OfficialCommunityType.LINKEDIN, getResources().getString(R.string.linked_in), liOfficialCommunityItemList);
        officialCommunityList.add(liOfficialCommunity);

        List<OfficialCommunityItem> tgOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://t.me/PlatONHK"));
        OfficialCommunity tgOfficialCommunity = new OfficialCommunity(OfficialCommunityType.TELEGRAM, getResources().getString(R.string.telegram), tgOfficialCommunityItemList);
        officialCommunityList.add(tgOfficialCommunity);

        List<OfficialCommunityItem> bhOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://www.bihu.com/people/1215832888"));
        OfficialCommunity bhOfficialCommunity = new OfficialCommunity(OfficialCommunityType.BI_HU, getResources().getString(R.string.bi_hu), bhOfficialCommunityItemList);
        officialCommunityList.add(bhOfficialCommunity);

        List<OfficialCommunityItem> baOfficialCommunityItemList = Arrays.asList(new OfficialCommunityItem("https://www.chainnode.com/forum/267"));
        OfficialCommunity baOfficialCommunity = new OfficialCommunity(OfficialCommunityType.BABIT, getResources().getString(R.string.babit), baOfficialCommunityItemList);
        officialCommunityList.add(baOfficialCommunity);

        return officialCommunityList;
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, OfficialCommunityActivity.class);
        context.startActivity(intent);
    }
}
