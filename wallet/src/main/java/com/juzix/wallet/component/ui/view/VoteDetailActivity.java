package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.component.ui.presenter.VoteDetailPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.utils.DateUtil;

import java.util.List;

/**
 * @author matrixelement
 */
public class VoteDetailActivity extends MVPBaseActivity<VoteDetailPresenter> implements VoteDetailContract.View, View.OnClickListener {

    private CommonAdapter<VoteDetailContract.Entity> mAdapter;

    public static void actionStart(Context context, String candidateId, String candidateName, String resIcon) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ID, candidateId);
        intent.putExtra(Constants.Extra.EXTRA_NAME, candidateName);
        intent.putExtra(Constants.Extra.EXTRA_PIC, resIcon);
        context.startActivity(intent);
    }

    @Override
    protected VoteDetailPresenter createPresenter() {
        return new VoteDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);
        initView();
        mPresenter.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
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

        ListView lvVote = findViewById(R.id.lv_vote);
        mAdapter = new CommonAdapter<VoteDetailContract.Entity>(R.layout.item_vote_detail_list, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, VoteDetailContract.Entity item, int position) {
                viewHolder.setText(R.id.tv_name, DateUtil.format(item.createTime, DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setText(R.id.tv_item1_desc, item.validVotes + "/" + item.invalidVotes);
                viewHolder.setText(R.id.tv_item2_desc, getString(R.string.amount_with_unit, item.ticketPrice));
                viewHolder.setText(R.id.tv_item3_desc, getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(item.voteStaked) + "/" + NumberParserUtils.parseDoubleToPrettyNumber(item.voteUnstaked)));
                viewHolder.setText(R.id.tv_item4_desc, getString(R.string.amount_with_unit, "-"));
                viewHolder.setText(R.id.tv_item5_desc, item.walletAddress + "(" + item.walletName + ")");
                viewHolder.setText(R.id.tv_item6_desc, DateUtil.format(item.expirTime, DateUtil.DATETIME_FORMAT_PATTERN));
            }
        };
        lvVote.setAdapter(mAdapter);
    }

    @Override
    public String getCandidateIdFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_ID);
    }

    @Override
    public String getCandidateNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_NAME);
    }

    @Override
    public String getCandidateIconFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_PIC);
    }

    @Override
    public void showCandidateInfo(int resIcon, String name, String candidateId) {
        ((ImageView) findViewById(R.id.iv_icon)).setImageResource(resIcon);
        ((TextView) findViewById(R.id.tv_name)).setText(name);
        ((TextView) findViewById(R.id.tv_address)).setText(candidateId);
    }

    public void updateTickets(List<VoteDetailContract.Entity> entityList){
        mAdapter.notifyDataChanged(entityList);
    }
}
