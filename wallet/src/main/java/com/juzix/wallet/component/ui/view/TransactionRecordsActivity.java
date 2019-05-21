package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.component.ui.presenter.TransactionRecordsPresenter;
import com.juzix.wallet.component.ui.presenter.TransactionsPresenter;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;
import com.juzix.wallet.utils.DateUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransactionRecordsActivity extends MVPBaseActivity<TransactionRecordsPresenter> implements TransactionRecordsContract.View {

    private final static String TAG = TransactionRecordsActivity.class.getSimpleName();
    @BindView(R.id.list_transactions)
    RecyclerView listTransactions;
    @BindView(R.id.layout_no_data)
    LinearLayout layoutNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;

    private Unbinder unbinder;
    private RecyclerAdapter<Transaction> mTransactionAdapter;

    @Override
    protected TransactionRecordsPresenter createPresenter() {
        return new TransactionRecordsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_records);
        unbinder = ButterKnife.bind(this);
        initViews();

    }

    private void initViews() {

        mTransactionAdapter = new TransactionAdapter(this, null, R.layout.item_transaction_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listTransactions.addItemDecoration(new CommonVerticalItemDecoration(this, R.drawable.bg_transation_list_divider));
        listTransactions.setLayoutManager(linearLayoutManager);
        listTransactions.setAdapter(mTransactionAdapter);
        mTransactionAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ////                TransactionEntity item = (TransactionEntity) parent.getAdapter().getItem(position);
////                if (item instanceof IndividualTransactionEntity) {
////                    IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) item, null);
////                } else if (item instanceof VoteTransactionEntity) {
////                    IndividualVoteDetailActivity.actionStart(currentActivity(), item.getUuid());
////                }
            }
        });

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW);
            }
        });

        layoutRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_OLD);
            }
        });

        layoutRefresh.autoRefresh();
    }

    @Override
    public void finishLoadMore() {
        layoutRefresh.finishLoadMore();
    }

    @Override
    public void finishRefresh() {
        layoutRefresh.finishRefresh();
    }

    @Override
    public void showTransactions(List<Transaction> transactionList) {
        layoutNoData.setVisibility(transactionList != null && !transactionList.isEmpty() ? View.GONE : View.VISIBLE);
        listTransactions.setVisibility(transactionList != null && !transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyDataSetChanged(transactionList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransactionRecordsActivity.class);
        context.startActivity(intent);
    }

    static class TransactionAdapter extends RecyclerAdapter<Transaction> {

        public TransactionAdapter(Context mContext, List<Transaction> mDatas, int mLayoutId) {
            super(mContext, mDatas, mLayoutId);
        }

        @Override
        public void convert(RecycleHolder holder, Transaction data, int position) {
            holder.setText(R.id.tv_name, mContext.getString(R.string.action_send_transation));
            holder.setText(R.id.tv_amount, mContext.getString(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(data.getValue(), 4)));
            holder.setText(R.id.tv_time, DateUtil.format(data.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
        }
    }
}
