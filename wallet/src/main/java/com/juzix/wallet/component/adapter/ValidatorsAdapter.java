package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.base.BaseViewHolder;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.OnItemClickListener;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.StringUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ValidatorsAdapter extends RecyclerView.Adapter<BaseViewHolder<VerifyNode>> {

    private List<VerifyNode> mVerifyNodeList;
    private OnItemClickListener mItemClickListener;

    public void setDatas(List<VerifyNode> verifyNodeList) {
        this.mVerifyNodeList = verifyNodeList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public BaseViewHolder<VerifyNode> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VerifyNodeViewHolder(R.layout.item_validators_list, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<VerifyNode> holder, int position) {
        holder.refreshData(mVerifyNodeList.get(position), position);
        holder.setOnItemClickListener(new BaseViewHolder.OnItemClickListener<VerifyNode>() {
            @Override
            public void onItemClick(VerifyNode verifyNode) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(verifyNode);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<VerifyNode> holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            holder.updateItem((Bundle) payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        if (mVerifyNodeList != null) {
            return mVerifyNodeList.size();
        }
        return 0;
    }
}
