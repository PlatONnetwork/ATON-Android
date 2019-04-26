package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.BatchVoteSummaryAdapter;
import com.juzix.wallet.component.adapter.BatchVoteTransactionAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.presenter.MyVotePresenter;
import com.juzix.wallet.component.widget.LineGridView;
import com.juzix.wallet.entity.BatchVoteTransactionWrapEntity;
import com.juzix.wallet.entity.VoteSummaryEntity;
import com.juzix.wallet.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class MyVoteActivity extends MVPBaseActivity<MyVotePresenter> implements MyVoteContract.View {

    @BindView(R.id.grid_vote_info)
    LineGridView gridVoteInfo;
    @BindView(R.id.list_vote_info)
    ListView listVoteInfo;
    @BindView(R.id.layout_no_voted)
    LinearLayout layoutNoVoted;

    private Unbinder unbinder;
    private BatchVoteSummaryAdapter mBatchVoteSummaryAdapter;
    private BatchVoteTransactionAdapter mBatchVoteTransactionAdapter;

    @Override
    protected MyVotePresenter createPresenter() {
        return new MyVotePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vote);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadData();
    }

    private void initViews() {

        mBatchVoteSummaryAdapter = new BatchVoteSummaryAdapter(R.layout.item_vote_info, null);
        mBatchVoteTransactionAdapter = new BatchVoteTransactionAdapter(R.layout.item_my_vote_list, null);

        gridVoteInfo.setAdapter(mBatchVoteSummaryAdapter);
        listVoteInfo.setAdapter(mBatchVoteTransactionAdapter);
        listVoteInfo.setEmptyView(layoutNoVoted);

        RxAdapterView.itemClicks(listVoteInfo)
                .compose(bindToLifecycle())
                .compose(new ClickTransformer())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        BatchVoteTransactionWrapEntity batchVoteTransactionEntity = mBatchVoteTransactionAdapter.getItem(position);
                        VoteDetailActivity.actionStart(MyVoteActivity.this, batchVoteTransactionEntity);
                    }
                });

        mBatchVoteTransactionAdapter.setOnItemVoteClickListener(new BatchVoteTransactionAdapter.OnItemVoteClickListener() {
            @Override
            public void onItemVoteClick(String candidateId) {
                mPresenter.voteTicket(candidateId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void showBatchVoteSummary(List<VoteSummaryEntity> voteSummaryEntityList) {
        gridVoteInfo.setVisibility(voteSummaryEntityList.isEmpty() ? View.GONE : View.VISIBLE);
        gridVoteInfo.setNumColumns(isContainValueLengthExceedSpecificallyLength(voteSummaryEntityList) ? 2 : 3);
        mBatchVoteSummaryAdapter.notifyDataChanged(voteSummaryEntityList);
    }

    @Override
    public void showBatchVoteTransactionList(List<BatchVoteTransactionWrapEntity> batchVoteTransactionWrapEntityList) {
        mBatchVoteTransactionAdapter.notifyDataChanged(batchVoteTransactionWrapEntityList);
    }

    /**
     * 是否包含子view value字段的长度超过屏幕的三分之一
     * 如果超过三分之一，则显示两列
     * 如果未超过三分之一，则显示两列
     * 暂不考虑，超过二分之一的情况
     *
     * @param voteSummaryEntityList
     * @return
     */
    private boolean isContainValueLengthExceedSpecificallyLength(List<VoteSummaryEntity> voteSummaryEntityList) {
        if (voteSummaryEntityList == null || voteSummaryEntityList.isEmpty()) {
            return false;
        }
        TextView tvValue = findTextView();
        for (VoteSummaryEntity voteSummaryEntity : voteSummaryEntityList) {
            float textWidth = tvValue.getPaint().measureText(voteSummaryEntity.getVoteSummaryValue());
            if (textWidth >= CommonUtil.getScreenWidth(this) / 3) {
                return true;
            }
        }
        return false;
    }

    private TextView findTextView() {

        return LayoutInflater.from(this).inflate(R.layout.item_vote_info, null).findViewById(R.id.tv_value);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyVoteActivity.class);
        context.startActivity(intent);
    }
}
