package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.presenter.MyVotePresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.engine.CandidateManager;

import java.util.List;

/**
 * @author matrixelement
 */
public class MyVoteActivity extends MVPBaseActivity<MyVotePresenter> implements MyVoteContract.View, View.OnClickListener {

    private CommonAdapter<MyVoteContract.Entity> mAdapter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyVoteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected MyVotePresenter createPresenter() {
        return new MyVotePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vote);
        initView();
        mPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.refresh();
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
                finish();
            }
        });
        ListView lvVote = findViewById(R.id.lv_vote);
        mAdapter = new CommonAdapter<MyVoteContract.Entity>(R.layout.item_my_vote_list, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, MyVoteContract.Entity item, int position) {
                viewHolder.setImageResource(R.id.iv_icon, CandidateManager.getInstance().getNodeIcon(item.avatar));
                viewHolder.setText(R.id.tv_name, item.candidateName);
                viewHolder.setText(R.id.tv_location, "(" + item.region + ")");
                //有效/失效
                viewHolder.setText(R.id.tv_item1_desc, item.validVotes + "/" + item.invalidVotes);
                //投票锁定
                viewHolder.setText(R.id.tv_item2_desc, getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(item.voteStaked)));
                //投票收益
                viewHolder.setText(R.id.tv_item3_desc, getString(R.string.amount_with_unit, "-"));
                //投票
                viewHolder.setOnClickListener(R.id.tv_vote, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.enterVoteActivity(item);
                    }
                });
            }
        };
        View emptyView = findViewById(R.id.layout_no_data);
        lvVote.setAdapter(mAdapter);
        lvVote.setEmptyView(emptyView);
        lvVote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.enterVoteDetailActivity(mAdapter.getItem(position));
            }
        });
    }

    @Override
    public void showTicketInfo(double voteStaked, long validVotes, long invalidVotes, double profit) {
        ((TextView) findViewById(R.id.tv_item1_desc)).setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(voteStaked)));
        ((TextView) findViewById(R.id.tv_item2_desc)).setText(validVotes + "/" + invalidVotes);
        ((TextView) findViewById(R.id.tv_item3_desc)).setText(getString(R.string.amount_with_unit, "-"));
    }

    @Override
    public void updateTickets(List<MyVoteContract.Entity> entityList) {
        mAdapter.notifyDataChanged(entityList);
    }
}
