package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.VoteDetailListAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.component.ui.presenter.VoteDetailPresenter;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.VoteDetailItemEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class VoteDetailActivity extends MVPBaseActivity<VoteDetailPresenter> implements VoteDetailContract.View {

    @BindView(R.id.tv_node_name)
    TextView tvNodeName;
    @BindView(R.id.tv_node_id)
    TextView tvNodeId;
    @BindView(R.id.layout_node_name)
    ConstraintLayout layoutNodeName;
    @BindView(R.id.list_vote_detail)
    ListView listVoteDetail;

    private Unbinder unbinder;
    private VoteDetailListAdapter mVoteDetailListAdapter;

    @Override
    protected VoteDetailPresenter createPresenter() {
        return new VoteDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadData();
    }

    private void initViews() {
        mVoteDetailListAdapter = new VoteDetailListAdapter(R.layout.item_vote_detail_list, null);
        listVoteDetail.setAdapter(mVoteDetailListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public BatchVoteTransactionEntity getBatchVoteTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_BATCHVOTETRANSACTION);
    }

    @Override
    public void showNodeDetailInfo(BatchVoteTransactionEntity batchVoteTransactionEntity) {
        tvNodeName.setText(batchVoteTransactionEntity.getNodeName());
        tvNodeId.setText(batchVoteTransactionEntity.getFormatCandidateId());
    }

    @Override
    public void notifyDataSetChanged(List<VoteDetailItemEntity> voteDetailItemEntityList) {
        mVoteDetailListAdapter.notifyDataChanged(voteDetailItemEntityList);
    }

    public static void actionStart(Context context, BatchVoteTransactionEntity batchVoteTransactionEntity) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_BATCHVOTETRANSACTION, batchVoteTransactionEntity);
        context.startActivity(intent);
    }
}
