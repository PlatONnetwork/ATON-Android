package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.VoteDetailListAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.component.ui.presenter.VoteDetailPresenter;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.entity.VotedCandidate;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
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
    @BindView(R.id.list_vote_detail)
    ListView listVoteDetail;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private Unbinder unbinder;
    private VoteDetailListAdapter mVoteDetailListAdapter;
    public int beginSequence = 0;//加载更多需要传入的值
    private List<VotedCandidate> list = new ArrayList<>();
    private boolean isLoadMore = false;

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
        mPresenter.loadVoteDetailData(Constants.VoteConstants.NEWEST_DATA);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //上拉刷新
                isLoadMore = false;
                mPresenter.loadVoteDetailData(Constants.VoteConstants.NEWEST_DATA);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //加载更多
                isLoadMore = true;
                mPresenter.loadVoteDetailData(beginSequence);
            }
        });


    }

    private void initViews() {
        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(this));
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(this));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的

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
    public String getCandidateIdFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_ID);
    }

    @Override
    public String getCandidateNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_NAME);
    }

    @Override
    public void getVoteDetailListDataSuccess(List<VotedCandidate> entityList) {
        if (entityList.size() > 0) {
            beginSequence = entityList.get(entityList.size() - 1).getSequence();
        }
        if (isLoadMore) {
            list.addAll(entityList);
        } else {
            list.clear();
            list.addAll(entityList);
        }
        mVoteDetailListAdapter.notifyDataChanged(list);
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void getVoteDetailListDataFailed() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void showNodeInfo(String nodeName, String nodeId) {
        tvNodeId.setText(nodeId);
        tvNodeName.setText(nodeName);
    }

    public static void actionStart(Context context, String nodeId, String nodeName) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_ID, nodeId);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_NAME, nodeName);
        context.startActivity(intent);
    }
}
