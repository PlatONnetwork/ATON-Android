package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.TransactionAdapter;
import com.juzix.wallet.component.adapter.TransactionDiffCallback;
import com.juzix.wallet.component.adapter.TransactionListAdapter;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.component.ui.presenter.TransactionRecordsPresenter;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.component.widget.WalletListPop;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.utils.DensityUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransactionRecordsActivity extends MVPBaseActivity<TransactionRecordsPresenter> implements TransactionRecordsContract.View {

    @BindView(R.id.list_transactions)
    RecyclerView listTransactions;
    @BindView(R.id.layout_no_data)
    LinearLayout layoutNoData;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;
    @BindView(R.id.layout_select_wallets)
    LinearLayout layoutSelectWallets;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.iv_selected_wallet_avatar)
    ImageView ivSelectedWalletAvatar;
    @BindView(R.id.iv_selected_wallet_name)
    TextView tvSelectedWalletName;

    private Unbinder unbinder;
    private TransactionListAdapter mTransactionListAdapter;
    private WalletListPop mWalletListPop;
    private List<String> mAddressList;

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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(Constants.UMPages.TRANSACTION_RECORD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(Constants.UMPages.TRANSACTION_RECORD);
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {


        mAddressList = WalletManager.getInstance().getAddressList();

        ShadowDrawable.setShadowDrawable(layoutSelectWallets,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 10),
                0,
                DensityUtil.dp2px(this, 2));

        mTransactionListAdapter = new TransactionListAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listTransactions.addItemDecoration(new CommonVerticalItemDecoration(this, R.drawable.bg_transation_list_divider));
        listTransactions.setLayoutManager(linearLayoutManager);
        listTransactions.setAdapter(mTransactionListAdapter);

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, mAddressList, false);
            }
        });

        layoutRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_OLD, mAddressList, false);
            }
        });

        layoutSelectWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWalletListPop == null) {
                    List<Wallet> walletList = new ArrayList<>();
                    walletList.add(Wallet.getNullInstance());
                    walletList.addAll(WalletManager.getInstance().getWalletList());
                    mWalletListPop = new WalletListPop(TransactionRecordsActivity.this, walletList, new WalletListPop.OnWalletItemClickListener() {
                        @Override
                        public void onWalletItemClick(int position) {
                            Wallet wallet = walletList.get(position);
                            ivSelectedWalletAvatar.setImageResource(wallet.isNull() ? R.drawable.icon_all_wallets : RUtils.drawable(wallet.getAvatar()));
                            tvSelectedWalletName.setText(wallet.isNull() ? getString(R.string.msg_all_wallets) : wallet.getName());
                            mAddressList = position == 0 ? WalletManager.getInstance().getAddressList() : Arrays.asList(wallet.getPrefixAddress());
                            mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, mAddressList, true);
                        }
                    });
                }
                if (mWalletListPop.isShowing()) {
                    mWalletListPop.dismiss();
                } else {
                    mWalletListPop.showAsDropDown(layoutSelectWallets, 0, -DensityUtil.dp2px(TransactionRecordsActivity.this, 12));
                }
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
    public void notifyDataSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, boolean isWalletChanged) {
        layoutNoData.setVisibility(newTransactionList != null && !newTransactionList.isEmpty() ? View.GONE : View.VISIBLE);
        mTransactionListAdapter.setQueryAddressList(mAddressList);
        if (oldTransactionList == null || oldTransactionList.isEmpty() || isWalletChanged) {
            mTransactionListAdapter.notifyDataSetChanged(newTransactionList);
        } else {
            TransactionDiffCallback transactionDiffCallback = new TransactionDiffCallback(oldTransactionList, newTransactionList);
            mTransactionListAdapter.setTransactionList(newTransactionList);
            DiffUtil.calculateDiff(transactionDiffCallback, true).dispatchUpdatesTo(mTransactionListAdapter);
        }
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
}
