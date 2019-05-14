package com.juzix.wallet.component.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class VoteListAdapter extends RecyclerView.Adapter<VoteListAdapter.ViewHolder> {

    private final static String TAG = VoteListAdapter.class.getSimpleName();

    private Context mContext;
    private List<CandidateEntity> mCandidateEntityList;
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CandidateEntity candidateEntity = mCandidateEntityList.get(position);
        if (candidateEntity != null) {
            CandidateExtraEntity extraEntity = candidateEntity.getCandidateExtraEntity();
            if (extraEntity != null) {
                holder.tvNodeName.setText(extraEntity.getNodeName());
            }
            RegionEntity regionEntity = candidateEntity.getRegionEntity();

            if (regionEntity == null || TextUtils.isEmpty(regionEntity.getCountry())) {
                holder.tvLocation.setText(String.format("(%s)", mContext.getString(R.string.unknownRegion)));
            } else {
                holder.tvLocation.setText(String.format("(%s)", regionEntity.getCountry()));
            }

            holder.tvRewardRatio.setText(mContext.getString(R.string.reward_radio_with_colon_and_value, String.format("%s%%", NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateEntity.getFee(), 100D), 0))));

            holder.tvStaked.setText(mContext.getString(R.string.staked_with_colon, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateEntity.getDeposit(), "1E18"), 0)));

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
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //更新局部控件
            CandidateEntity candidateEntity = mCandidateEntityList.get(position);
            if (candidateEntity != null) {
                CandidateExtraEntity extraEntity = candidateEntity.getCandidateExtraEntity();
                if (extraEntity != null) {
                    holder.tvNodeName.setText(extraEntity.getNodeName());
                }
                RegionEntity regionEntity = candidateEntity.getRegionEntity();
                if (regionEntity == null || TextUtils.isEmpty(regionEntity.getCountry())) {
                    holder.tvLocation.setText(String.format("(%s)", mContext.getString(R.string.unknownRegion)));
                } else {
                    holder.tvLocation.setText(String.format("(%s)", regionEntity.getCountry()));
                }
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

    public void notifyDataChanged(List<CandidateEntity> candidateEntityList) {
        this.mCandidateEntityList = candidateEntityList;
        notifyDataSetChanged();
    }

    @SuppressLint("CheckResult")
    public void updateRegionInfo(RegionEntity regionEntity) {
        Flowable
                .range(0, mCandidateEntityList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer position) throws Exception {
                        return regionEntity.getIp().equals(mCandidateEntityList.get(position).getHost());
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        CandidateEntity candidateEntity = mCandidateEntityList.get(position);
                        candidateEntity.setRegionEntity(regionEntity);
                        notifyItemChanged(position, "updateRegionInfo");
                    }
                });
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

        void onItemClick(CandidateEntity candidateEntity);
    }

    public interface OnVoteTicketClickListener {

        void onVoteTicketClick(CandidateEntity candidateEntity);
    }

}
