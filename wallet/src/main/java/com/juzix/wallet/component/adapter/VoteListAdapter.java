package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.ClickTransformer;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

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
                    .compose(new ClickTransformer())
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClick(candidateEntity);
                            }
                        }
                    });

            RxView
                    .clicks(holder.tvVote)
                    .compose(new ClickTransformer())
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {
                            if (mOnVoteTicketClickListener != null) {
                                mOnVoteTicketClickListener.onVoteTicketClick(candidateEntity);
                            }
                        }
                    });

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

    public void updateRegionInfo(RegionEntity regionEntity) {
        Log.e(TAG, regionEntity.getIp() + ":" + regionEntity.getCountry() + ":" + regionEntity.getCountryCode());
        int position = getPosition(regionEntity.getIp());
        CandidateEntity candidateEntity = mCandidateEntityList.get(position);
        candidateEntity.setRegionEntity(regionEntity);
        mCandidateEntityList.set(position, candidateEntity);
        notifyItemChanged(position);
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

    private int getPosition(String ip) {

        if (mCandidateEntityList != null && !mCandidateEntityList.isEmpty()) {
            for (int i = 0; i < mCandidateEntityList.size(); i++) {
                CandidateEntity candidateEntity = mCandidateEntityList.get(i);
                if (ip.equals(candidateEntity.getHost())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
