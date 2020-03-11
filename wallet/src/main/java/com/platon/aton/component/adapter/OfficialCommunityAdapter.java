package com.platon.aton.component.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.base.RecycleHolder;
import com.platon.aton.component.adapter.base.RecyclerAdapter;
import com.platon.aton.component.ui.view.CommonHybridActivity;
import com.platon.aton.entity.OfficialCommunity;
import com.platon.aton.entity.OfficialCommunityItem;
import com.platon.aton.entity.OfficialCommunityType;
import com.platon.aton.entity.WebType;
import com.platon.aton.utils.CommonUtil;

import java.util.List;

public class OfficialCommunityAdapter extends RecyclerAdapter<OfficialCommunity> {


    public OfficialCommunityAdapter(Context mContext, List<OfficialCommunity> mDatas, int mLayoutId) {
        super(mContext, mDatas, mLayoutId);
    }

    @Override
    public void convert(RecycleHolder holder, OfficialCommunity data, int position) {
        if (data.getOfficialCommunityImageRes() != -1) {
            holder.setImageResource(R.id.iv_community, data.getOfficialCommunityImageRes());
        }
        holder.setText(R.id.tv_community, data.getName());

        LinearLayout officialCommunityLayout = holder.findView(R.id.layout_official_community);
        List<OfficialCommunityItem> itemList = data.getOfficialCommunityItemList();
        if (itemList != null && !itemList.isEmpty()) {
            officialCommunityLayout.removeAllViews();
            for (OfficialCommunityItem item : itemList) {
                officialCommunityLayout.addView(buildOfficialCommunityItemView(item, data.getOfficialCommunityType()));
            }
        }

    }

    static class ViewHolder extends RecycleHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private View buildOfficialCommunityItemView(OfficialCommunityItem item, @OfficialCommunityType int officialCommunityType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_official_community_detail, null);
        TextView communityUrlTv = view.findViewById(R.id.tv_community_url);
        ImageView jumpIv = view.findViewById(R.id.iv_jump);
        ImageView copyIv = view.findViewById(R.id.iv_copy);
        ImageView qrCodeIv = view.findViewById(R.id.iv_qr_code);
        if (officialCommunityType == OfficialCommunityType.WECHAT) {
            jumpIv.setImageResource(R.drawable.icon_scan_qr_code);
            qrCodeIv.setImageResource(item.getQrCodeImageRes());
        } else {
            jumpIv.setImageResource(R.drawable.icon_jump);
        }

        jumpIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (officialCommunityType == OfficialCommunityType.WECHAT) {
                    qrCodeIv.setVisibility(qrCodeIv.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                } else {
                    CommonHybridActivity.actionStart(mContext, item.getWebPortalUrl(), WebType.WEB_TYPE_OFFICIAL_COMMUNITY);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (officialCommunityType == OfficialCommunityType.WECHAT) {
                    qrCodeIv.setVisibility(qrCodeIv.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });
        communityUrlTv.setText(item.getWebPortalUrl());
        copyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.copyTextToClipboard(mContext, item.getWebPortalUrl());
            }
        });
        return view;
    }
}
