package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.ValidatorsAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.component.ui.presenter.ValidatorsPresenter;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.entity.VerifyNodeEntity;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 验证节点页面
 */

public class ValidatorsFragment extends MVPBaseFragment<ValidatorsPresenter> implements ValidatorsContract.View, OnClickListener {
    private Unbinder unbinder;

    @BindView(R.id.radio_group)
    RadioGroup radio_group;

    @BindView(R.id.btn_all)
    RadioButton all;
    @BindView(R.id.btn_active)
    RadioButton active;
    @BindView(R.id.btn_candidate)
    RadioButton candidate;
    @BindView(R.id.tv_rank)
    TextView tv_rank;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rlv_list)
    ListView rlv_list;

    private String rankType;
    private String nodeState;

    private List<VerifyNode> allList = new ArrayList<>();
    private List<VerifyNode> activeList = new ArrayList<>();
    private List<VerifyNode> candidateList = new ArrayList<>();

    private int allLastRank = 0;
    private int activeLastRank = 0;
    private int candidateLastRank = 0;
    private boolean isLoadMore = false;

    private int rank = 0;
    private ValidatorsAdapter mValidatorsAdapter;

    @Override

    protected ValidatorsPresenter createPresenter() {
        return new ValidatorsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {

    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_validators, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventPublisher.getInstance().register(this);
        initView(view);
        return view;
    }

    private void initView(View view) {
        changeBtnState(R.id.btn_all);

        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);
        tv_rank.setOnClickListener(this);
        mValidatorsAdapter = new ValidatorsAdapter(R.layout.item_validators_list, null);
        rlv_list.setAdapter(mValidatorsAdapter);


        tv_rank.setText(getString(R.string.validators_rank));
        rankType = Constants.ValidatorsType.VALIDATORS_RANK;
        nodeState = Constants.ValidatorsType.ALL_VALIDATORS;

        mPresenter.loadValidatorsData(rankType, nodeState, -1);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isLoadMore = false;
                allList.clear();
                activeList.clear();
                candidateList.clear();

                mPresenter.loadValidatorsData(rankType, nodeState, -1);

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                //加载更多
                isLoadMore = true;
                if (TextUtils.equals(nodeState, Constants.ValidatorsType.ALL_VALIDATORS)) {
                    rank = allLastRank;
                } else if (TextUtils.equals(nodeState, Constants.ValidatorsType.ACTIVE_VALIDATORS)) {
                    rank = activeLastRank;
                } else {
                    rank = candidateLastRank;
                }
                mPresenter.loadDataFromDB(nodeState, rank);

            }
        });

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {

                switch (id) {
                    case R.id.btn_all:
                        changeBtnState(id);
                        nodeState = Constants.ValidatorsType.ALL_VALIDATORS;
                        if (allList.size() == 0) {
                            mPresenter.loadDataFromDB(nodeState, -1);
                        }

//                        showLongToast("所有");
                        break;
                    case R.id.btn_active:
                        changeBtnState(id);
                        nodeState = Constants.ValidatorsType.ACTIVE_VALIDATORS;
                        if (activeList.size() == 0) {
                            mPresenter.loadDataFromDB(nodeState, -1);
                        }

//                        showLongToast("活跃中");
                        break;
                    case R.id.btn_candidate:
                        changeBtnState(id);
                        nodeState = Constants.ValidatorsType.CANDIDATE_VALIDATORS;
                        if (candidateList.size() == 0) {
                            mPresenter.loadDataFromDB(nodeState, -1);
                        }

//                        showLongToast("候选中");
                        break;

                    default:
                        break;
                }


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        RxAdapterView.itemClicks(rlv_list)
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void accept(Integer position) {
                        VerifyNode verifyNode = mValidatorsAdapter.getItem(position);
                        ValidatorsDetailActivity.actionStart(getContext(), verifyNode.getNodeId());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_rank:
                if (TextUtils.equals(rankType, Constants.ValidatorsType.VALIDATORS_RANK)) {
                    tv_rank.setText(getString(R.string.validators_detail_yield));
                    rankType = Constants.ValidatorsType.VALIDATORS_YIELD;

                    allList.clear();
                    activeList.clear();
                    candidateList.clear();
                    //按年化操作
                    mPresenter.loadValidatorsData(rankType, nodeState, -1);
                } else {
                    tv_rank.setText(getString(R.string.validators_rank));
                    rankType = Constants.ValidatorsType.VALIDATORS_RANK;

                    allList.clear();
                    activeList.clear();
                    candidateList.clear();
                    //按排名操作
                    mPresenter.loadValidatorsData(rankType, nodeState, -1);
                }

                break;
            default:
                break;
        }


    }


    public void changeBtnState(int id) {
        switch (id) {
            case R.id.btn_all:
                all.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                active.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                all.setTextColor(getResources().getColor(R.color.color_ffffff));
                active.setTextColor(getResources().getColor(R.color.color_000000));
                candidate.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case R.id.btn_active:
                active.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                all.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                active.setTextColor(getResources().getColor(R.color.color_ffffff));
                all.setTextColor(getResources().getColor(R.color.color_000000));
                candidate.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case R.id.btn_candidate:
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                all.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                active.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                candidate.setTextColor(getResources().getColor(R.color.color_ffffff));
                all.setTextColor(getResources().getColor(R.color.color_000000));
                active.setTextColor(getResources().getColor(R.color.color_000000));
                break;
        }
    }


    @Override
    public void showValidatorsDataOnAll(List<VerifyNode> nodeList) {
        if (nodeList.size() > 0) {
            allLastRank = nodeList.get(nodeList.size() - 1).getRanking();
        }

        if (isLoadMore) {
            allList.addAll(nodeList);
        } else {
            allList.clear();
            allList.addAll(nodeList);
        }
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        mValidatorsAdapter.notifyDataChanged(allList);

    }

    @Override
    public void showValidatorsDataOnActive(List<VerifyNode> nodeList) {
        if (nodeList.size() > 0) {
            activeLastRank = nodeList.get(nodeList.size() - 1).getRanking();

        }
        if (isLoadMore) {
            activeList.addAll(nodeList);
        } else {
            activeList.clear();
            activeList.addAll(nodeList);
        }

        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        mValidatorsAdapter.notifyDataChanged(activeList);

    }

    @Override
    public void showValidatorsDataOnCadidate(List<VerifyNode> nodeList) {
        if (nodeList.size() > 0) {
            candidateLastRank = nodeList.get(nodeList.size() - 1).getRanking();

        }
        if (isLoadMore) {
            candidateList.addAll(nodeList);
        } else {
            candidateList.clear();
            candidateList.addAll(nodeList);
        }

        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        mValidatorsAdapter.notifyDataChanged(activeList);
    }

    @Override
    public void showValidatorsFailed() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    //接收event事件然后刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshValidators(Event.UpdateValidatorsTabEvent tabEvent) {
        if (AppSettings.getInstance().getValidatorsTab()) {
            refreshLayout.autoRefresh();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            AppSettings.getInstance().setValidatorsTab(true);
        } else {
            AppSettings.getInstance().setValidatorsTab(false);
        }
    }
}
