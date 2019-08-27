package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.TransactionAdapter;
import com.juzix.wallet.component.adapter.base.RecycleHolder;
import com.juzix.wallet.component.adapter.base.RecyclerAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.component.ui.presenter.TransactionRecordsPresenter;
import com.juzix.wallet.component.widget.CommonVerticalItemDecoration;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.component.widget.WalletListPop;
import com.juzix.wallet.db.entity.TransactionEntity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jnr.constants.platform.PRIO;

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
    private TransactionAdapter mTransactionAdapter;
    private WalletListPop mWalletListPop;

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
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawable(layoutSelectWallets,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 10),
                0,
                DensityUtil.dp2px(this, 2));

        mTransactionAdapter = new TransactionAdapter(this, null, R.layout.item_transaction_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listTransactions.addItemDecoration(new CommonVerticalItemDecoration(this, R.drawable.bg_transation_list_divider));
        listTransactions.setLayoutManager(linearLayoutManager);
        listTransactions.setAdapter(mTransactionAdapter);
        mTransactionAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Transaction transaction = mTransactionAdapter.getDatas().get(position);
                TransactionDetailActivity.actionStart(TransactionRecordsActivity.this, transaction, WalletManager.getInstance().getSelectedWalletAddress(),"","");
            }
        });

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, WalletManager.getInstance().getAddressList());
            }
        });

        layoutRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_OLD, WalletManager.getInstance().getAddressList());
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
                            mPresenter.fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, position == 0 ? WalletManager.getInstance().getAddressList() : Arrays.asList(wallet.getPrefixAddress()));
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
    public void notifyItemRangeInserted(List<Transaction> transactionList, int positionStart, int itemCount) {
        layoutNoData.setVisibility(transactionList != null && !transactionList.isEmpty() ? View.GONE : View.VISIBLE);
        mTransactionAdapter.notifyItemRangeInserted(transactionList, WalletManager.getInstance().getAddressList(), positionStart, itemCount);
    }

    @Override
    public void showTransactions(List<Transaction> transactionList) {
        layoutNoData.setVisibility(transactionList != null && !transactionList.isEmpty() ? View.GONE : View.VISIBLE);
        listTransactions.setVisibility(transactionList != null && !transactionList.isEmpty() ? View.VISIBLE : View.GONE);
        mTransactionAdapter.notifyDataSetChanged(transactionList, WalletManager.getInstance().getAddressList());
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
