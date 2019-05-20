package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.NodeDetailContract;
import com.juzix.wallet.component.ui.presenter.NodeDetailPresenter;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.CandidateDetailEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class NodeDetailActivity extends MVPBaseActivity<NodeDetailPresenter> implements NodeDetailContract.View {

    @BindView(R.id.tv_node_name)
    TextView tvNodeName;
    @BindView(R.id.tv_join_time)
    TextView tvJoinTime;
    @BindView(R.id.tv_staked_ranking)
    TextView tvStakedRanking;
    @BindView(R.id.tv_staked_amount)
    TextView tvStakedAmount;
    @BindView(R.id.tv_tickets)
    TextView tvTickets;
    @BindView(R.id.layout_node_staked_info)
    ConstraintLayout layoutNodeStakedInfo;
    @BindView(R.id.tv_node_url)
    TextView tvNodeUrl;
    @BindView(R.id.tv_node_id)
    TextView tvNodeId;
    @BindView(R.id.tv_reward_radio)
    TextView tvRewardRadio;
    @BindView(R.id.tv_institutional_name)
    TextView tvInstitutionalName;
    @BindView(R.id.tv_institutional_website)
    TextView tvInstitutionalWebsite;
    @BindView(R.id.tv_institutional_introduction)
    TextView tvInstitutionalIntroduction;
    @BindView(R.id.sbtn_vote)
    ShadowButton sbtnVote;
    @BindView(R.id.rtv_rank_desc)
    RoundedTextView rtvRankDesc;
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;

    Unbinder unbinder;

    @Override
    protected NodeDetailPresenter createPresenter() {
        return new NodeDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_detail);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.getNodeDetailInfo();
    }

    private void initViews() {

        RxView
                .clicks(tvInstitutionalWebsite)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        String text = tvInstitutionalWebsite.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            ShareUtil.shareUrl(getContext(), text);
                        }
                    }
                });

        RxView
                .clicks(sbtnVote)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        mPresenter.voteTicket();
                    }
                });
    }

    @Override
    public String getCandidateIdFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_ID);
    }

    @Override
    public void showNodeDetailInfo(CandidateDetailEntity candidateDetailEntity) {

        layoutContent.setVisibility(View.VISIBLE);
        sbtnVote.setVisibility(View.VISIBLE);

        tvNodeName.setText(candidateDetailEntity.getName());
        tvJoinTime.setText(string(R.string.joinTime, DateUtil.format(candidateDetailEntity.getJoinTime(), DateUtil.DATETIME_FORMAT_PATTERN)));
        tvInstitutionalName.setText(candidateDetailEntity.getOrgName());
        tvInstitutionalWebsite.setText(candidateDetailEntity.getOrgWebsite());
        tvInstitutionalIntroduction.setText(candidateDetailEntity.getIntro());
        rtvRankDesc.setText(candidateDetailEntity.getNodeType().getStatusDescRes());
        tvStakedRanking.setText(String.format("%d", candidateDetailEntity.getRanking()));
        tvStakedAmount.setText(NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateDetailEntity.getDeposit(), "1E18"), 4));
        tvTickets.setText(candidateDetailEntity.getTicketCount());
        tvNodeUrl.setText(candidateDetailEntity.getNodeUrl());
        tvNodeId.setText(candidateDetailEntity.getNodeId());
        tvRewardRadio.setText(String.format("%s%%", NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(NumberParserUtils.parseDouble(candidateDetailEntity.getReward()), 100D), 0)));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, String candidateId) {
        Intent intent = new Intent(context, NodeDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_ID, candidateId);
        context.startActivity(intent);
    }
}
