package com.platon.wallet.component.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.platon.wallet.R;
import com.platon.wallet.component.adapter.base.BaseViewHolder;
import com.platon.wallet.component.ui.OnItemClickListener;
import com.platon.wallet.entity.VerifyNode;

import java.util.List;

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
