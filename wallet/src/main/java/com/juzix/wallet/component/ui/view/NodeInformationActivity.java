package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.NodeInformationContract;
import com.juzix.wallet.component.ui.presenter.NodeInformationPresenter;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.ShareUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class NodeInformationActivity extends MVPBaseActivity<NodeInformationPresenter> implements NodeInformationContract.View {

    @BindView(R.id.iv_icon)
    ImageView       ivIcon;
    @BindView(R.id.tv_name)
    TextView        tvName;
    @BindView(R.id.tv_location)
    TextView        tvLocation;
    @BindView(R.id.tv_candidate)
    TextView        tvCandidate;
    @BindView(R.id.tv_time)
    TextView        tvTime;
    @BindView(R.id.tv_staked_ranking)
    TextView        tvStakedRanking;
    @BindView(R.id.tv_staked)
    TextView        tvStaked;
    @BindView(R.id.tv_tickets)
    TextView        tvTickets;
    @BindView(R.id.tv_ticket_age)
    TextView        tvTicketAge;
    @BindView(R.id.tv_node_url)
    TextView        tvNodeUrl;
    @BindView(R.id.tv_node_id)
    TextView        tvNodeId;
    @BindView(R.id.tv_reward_radio)
    TextView        tvRewardRadio;
    @BindView(R.id.tv_institutional_name)
    TextView        tvInstitutionalName;
    @BindView(R.id.tv_institutional_website)
    TextView        tvInstitutionalWebsite;
    @BindView(R.id.tv_node_introduction)
    TextView        tvNodeIntroduction;
    @BindView(R.id.rtv_vote)
    RoundedTextView rtvVote;

    private Unbinder unbinder;

    public static void actionStart(Context context, CandidateEntity candidateEntity) {
        Intent intent = new Intent(context, NodeInformationActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE, candidateEntity);
        context.startActivity(intent);
    }

    @Override
    public CandidateEntity getCandidateEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_CANDIDATE);
    }

    @Override
    public void showDetailInfo(CandidateEntity candidateEntity) {
        CandidateExtraEntity extraEntity = candidateEntity.getCandidateExtraEntity();
        if (extraEntity != null){
            ivIcon.setImageResource(CandidateManager.getInstance().getNodeIcon(extraEntity.getNodePortrait()));
            tvName.setText(extraEntity.getNodeName());
            tvTime.setText(string(R.string.joinTime, DateUtil.format(extraEntity.getTime(), DateUtil.DATETIME_FORMAT_PATTERN)));
            tvInstitutionalName.setText(extraEntity.getNodeDepartment());
            tvInstitutionalWebsite.setText(extraEntity.getOfficialWebsite());
            tvNodeIntroduction.setText(extraEntity.getNodeDiscription());
        }
        tvLocation.setText("(" + candidateEntity.getRegion() + ")");
        tvCandidate.setText(candidateEntity.getStatus() == CandidateEntity.STATUS_CANDIDATE ? R.string.candidate : R.string.alternative);
        int stakedRanking = candidateEntity.getStakedRanking();
        tvStakedRanking.setText(String.valueOf(stakedRanking == 0 ? 1 : stakedRanking));
        tvStaked.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateEntity.getDeposit(), "1E18"), 3)));
        tvTickets.setText(String.valueOf(candidateEntity.getVotedNum()));
        tvNodeUrl.setText(candidateEntity.getHost() + ":" + candidateEntity.getPort());
        String candidateId = candidateEntity.getCandidateId();
        if (!candidateId.startsWith("0x")){
            candidateId = "0x" + candidateId;
        }
        tvNodeId.setText(candidateId);
        tvRewardRadio.setText(NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(candidateEntity.getFee(), 100D), 2) + "%");
    }

    @Override
    public void showEpoch(long epoch) {
        tvTicketAge.setText(epoch + " Bs");
    }

    @Override
    protected NodeInformationPresenter createPresenter() {
        return new NodeInformationPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_information);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.start();
    }

    private void initViews() {
        tvInstitutionalWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvInstitutionalWebsite.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    ShareUtil.shareUrl(getContext(), text);
                }
            }
        });
        rtvVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteTicket(getCandidateEntityFromIntent());
            }
        });
    }

    private void voteTicket(CandidateEntity candidateEntity){
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.voteTicketCreateWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0){
            showLongToast(R.string.voteTicketInsufficientBalanceTips);
            return;
        }
        VoteActivity.actionStart(currentActivity(), candidateEntity);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
