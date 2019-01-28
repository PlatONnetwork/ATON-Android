package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteMainContract;
import com.juzix.wallet.component.ui.presenter.VoteMainPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class VoteMainActivity extends MVPBaseActivity<VoteMainPresenter> implements VoteMainContract.View, View.OnClickListener, TextWatcher {

    private LinearLayout                   mLlSort;
    private TextView                       mTvDefault;
    private TextView                       mTvReward;
    private TextView                       mTvLocation;
    private LinearLayout                   mLlSearch1;
    private LinearLayout                   mLlSearch2;
    private EditText                       mEtSearch;
    private ImageView                      mIvClear;
    private ImageView                      mIvSearch;
    private LinearLayout                   mLayoutEmpty;
    private TextView                       mTvVoteInfo;
    private ProgressBar                    mPbVoteRatio;
    private CommonAdapter<CandidateEntity> mAdapter;

    public static void actionStart(Context context, IndividualWalletEntity walletEntity) {
        Intent intent = new Intent(context, VoteMainActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }

    @Override
    public IndividualWalletEntity getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void setTicketInfo(double voteRatio, long votedTicketCount, String ticketPrice) {
        String voteRate = string(R.string.voteRate) + ": " + NumberParserUtils.getPrettyNumber(voteRatio, 2) + "%";
        String votedNum = string(R.string.votes) + ": " + votedTicketCount;
        String price = string(R.string.ticketPrice) + ": " + string(R.string.amount_with_unit, ticketPrice);
        mTvVoteInfo.setText(voteRate + " | " + votedNum + " | " + price);
//        mTvVoteInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.icon_triangle_up_s), null);
//        mTvVoteInfo.setCompoundDrawablePadding(DensityUtil.dp2px(this, 4));
        mPbVoteRatio.setProgress((int) voteRatio);
    }

    @Override
    public void notifyDataSetChanged(List<CandidateEntity> candidateList) {
        mAdapter.notifyDataChanged(candidateList);
    }

    @Override
    protected VoteMainPresenter createPresenter() {
        return new VoteMainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_main);
        initView();
        mPresenter.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPresenter.search(mEtSearch.getText().toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sort_default:
                mTvDefault.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
                mTvDefault.setTextSize(16);
                mTvReward.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvReward.setTextSize(12);
                mTvLocation.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvLocation.setTextSize(12);
                mPresenter.sort(VoteMainPresenter.SORT_DEFAULT);
                break;
            case R.id.tv_sort_reward:
                mTvReward.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
                mTvReward.setTextSize(16);
                mTvDefault.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvDefault.setTextSize(12);
                mTvLocation.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvLocation.setTextSize(12);
                mPresenter.sort(VoteMainPresenter.SORT_REWARD);
                break;
            case R.id.tv_sort_location:
                mTvLocation.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
                mTvLocation.setTextSize(16);
                mTvDefault.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvDefault.setTextSize(12);
                mTvReward.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                mTvReward.setTextSize(12);
                mPresenter.sort(VoteMainPresenter.SORT_LOCATION);
                break;
            case R.id.iv_clear:
                mEtSearch.setText("");
                mLlSearch1.setVisibility(View.GONE);
                mLlSearch2.setVisibility(View.VISIBLE);
                hideSoftInput();
                break;
            case R.id.iv_search:
                mLlSearch1.setVisibility(View.VISIBLE);
                mLlSearch2.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mEtSearch.removeTextChangedListener(this);
        mPresenter.destroy();
        super.onDestroy();
    }

    private void initView() {
        CommonTitleBar titleBar = findViewById(R.id.commonTitleBar);
        titleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        titleBar.setRightText(string(R.string.myVote), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                MyVoteActivity.actionStart(currentActivity());
            }
        });
        mLlSort = findViewById(R.id.ll_sort);
        mTvDefault = findViewById(R.id.tv_sort_default);
        mTvReward = findViewById(R.id.tv_sort_reward);
        mTvLocation = findViewById(R.id.tv_sort_location);
        mTvDefault.setOnClickListener(this);
        mTvReward.setOnClickListener(this);
        mTvLocation.setOnClickListener(this);
        mLlSearch1 = findViewById(R.id.ll_search1);
        mLlSearch2 = findViewById(R.id.ll_search2);
        mIvClear = findViewById(R.id.iv_clear);
        mIvSearch = findViewById(R.id.iv_search);
        mEtSearch = findViewById(R.id.et_search);
        mLayoutEmpty = findViewById(R.id.layout_empty);
        mTvVoteInfo = findViewById(R.id.tv_vote_info);
        mPbVoteRatio = findViewById(R.id.pb_vote);
        mIvClear.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mEtSearch.addTextChangedListener(this);
        ListView lvVote = findViewById(R.id.lv_vote);
        lvVote.setEmptyView(mLayoutEmpty);
        mAdapter = new CommonAdapter<CandidateEntity>(R.layout.item_vote_main_list, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, CandidateEntity item, int position) {
                CandidateExtraEntity extraEntity = item.getCandidateExtraEntity();
                if (extraEntity != null){
                    viewHolder.setImageResource(R.id.iv_icon, CandidateManager.getInstance().getNodeIcon(extraEntity.getNodePortrait()));
                    viewHolder.setText(R.id.tv_name, extraEntity.getNodeName());
                }
                viewHolder.setText(R.id.tv_location, String.format("(%s)", item.getRegion()));
                String rewardRadio = string(R.string.rewardRadio) + ": " + NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(item.getFee(), 100D), 2) + "%";
                String staked =  string(R.string.staked) + ": " + string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(item.getDeposit(), "1E18"), 3));
                viewHolder.setText(R.id.tv_desc, rewardRadio + " | " + staked);
                viewHolder.setOnClickListener(R.id.tv_vote, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftInput();
                        mPresenter.voteTicket(item);
                    }
                });
            }
        };
        lvVote.setAdapter(mAdapter);
        lvVote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftInput();
                NodeInformationActivity.actionStart(currentActivity(), mAdapter.getItem(position));
            }
        });
    }
}
