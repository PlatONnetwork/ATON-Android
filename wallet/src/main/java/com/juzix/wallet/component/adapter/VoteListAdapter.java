package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class VoteListAdapter extends RecyclerView.Adapter<VoteListAdapter.ViewHolder> {

    private final static String TAG = VoteListAdapter.class.getSimpleName();

    private Context mContext;
    private List<Candidate> mCandidateEntityList;
    private OnItemClickListener mOnItemClickListener;
    private OnVoteTicketClickListener mOnVoteTicketClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnVoteTicketClickListener(OnVoteTicketClickListener onVoteTicketClickListener) {
        this.mOnVoteTicketClickListener = onVoteTicketClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_vote_list, parent, false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Candidate candidateEntity = mCandidateEntityList.get(position);

        holder.tvNodeName.setText(candidateEntity.getName());

        holder.tvRewardRatio.setText(mContext.getString(R.string.reward_radio_with_colon_and_value, String.format("%s%%", NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(NumberParserUtils.parseDouble(candidateEntity.getReward()), 100D), 0))));

        holder.tvStaked.setText(mContext.getString(R.string.staked_with_colon, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateEntity.getDeposit(), "1E18"), 0)));

        holder.tvLocation.setText(String.format("(%s)", candidateEntity.getCountryName(mContext)));

        RxView
                .clicks(holder.itemView)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(candidateEntity);
                        }
                    }
                });

        RxView
                .clicks(holder.tvVote)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object unit) throws Exception {
                        if (mOnVoteTicketClickListener != null) {
                            mOnVoteTicketClickListener.onVoteTicketClick(candidateEntity);
                        }
                    }
                });

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //更新局部控件
            Candidate candidateEntity = mCandidateEntityList.get(position);
            if (candidateEntity != null) {

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mCandidateEntityList != null) {
            return mCandidateEntityList.size();
        }
        return 0;
    }

    public void notifyDataChanged(List<Candidate> candidateEntityList) {
        this.mCandidateEntityList = candidateEntityList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_node_name)
        TextView tvNodeName;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.tv_reward_ratio)
        TextView tvRewardRatio;
        @BindView(R.id.tv_staked)
        TextView tvStaked;
        @BindView(R.id.tv_vote)
        TextView tvVote;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Candidate candidateEntity);
    }

    public interface OnVoteTicketClickListener {

        void onVoteTicketClick(Candidate candidateEntity);
    }

}
