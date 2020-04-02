package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.TransactionDiffCallback;
import com.platon.aton.component.adapter.TransactionListAdapter;
import com.platon.aton.component.ui.contract.TransactionRecordsContract;
import com.platon.aton.component.ui.presenter.TransactionRecordsPresenter;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.component.widget.WalletListPop;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.DensityUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.RUtils;
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
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class TransactionRecordsActivity extends BaseActivity<TransactionRecordsContract.View, TransactionRecordsPresenter> implements TransactionRecordsContract.View {

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
    public TransactionRecordsPresenter createPresenter() {
        return new TransactionRecordsPresenter();
    }

    @Override
    public TransactionRecordsContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_records;
    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(Constants.UMPages.TRANSACTION_RECORD);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.TRANSACTION_RECORD);
        super.onPause();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        Wallet selectedWallet = getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);

        mAddressList = WalletManager.getInstance().getAddressList();

        ShadowDrawable.setShadowDrawable(layoutSelectWallets,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 10),
                0,
                DensityUtil.dp2px(this, 2));

        mTransactionListAdapter = new TransactionListAdapter(TransactionListAdapter.EntranceType.ME_PAGE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listTransactions.setLayoutManager(linearLayoutManager);
        listTransactions.setAdapter(mTransactionListAdapter);

        mTransactionListAdapter.setOnItemClickListener(new TransactionListAdapter.OnItemClickListener() {
            @Override
            public void onCommonTransactionItemClick(Transaction transaction, int position) {
                TransactionDetailActivity.actionStart(TransactionRecordsActivity.this, transaction, mAddressList);
            }

            @Override
            public void onMoreTransactionItemClick() {

            }
        });

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPresenter().fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, mAddressList, false);
            }
        });

        layoutRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getPresenter().fetchTransactions(TransactionRecordsPresenter.DIRECTION_OLD, mAddressList, false);
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
                            showSelectWalletInfo(wallet);
                            getPresenter().fetchTransactions(TransactionRecordsPresenter.DIRECTION_NEW, mAddressList, true);
                        }
                    }, getSelectedWalletPosition(selectedWallet));
                }
                if (mWalletListPop.isShowing()) {
                    mWalletListPop.dismiss();
                } else {
                    mWalletListPop.showAsDropDown(layoutSelectWallets, 0, -DensityUtil.dp2px(TransactionRecordsActivity.this, 12));
                }
            }
        });

        showSelectWalletInfo(selectedWallet);

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

    private int getSelectedWalletPosition(Wallet selectedWallet) {
        if (selectedWallet == null || WalletManager.getInstance().getWalletList() == null || WalletManager.getInstance().getWalletList().isEmpty()) {
            return 0;
        }

        return Flowable
                .range(0, WalletManager.getInstance().getWalletList().size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return WalletManager.getInstance().getWalletList().get(integer).getPrefixAddress().equalsIgnoreCase(selectedWallet.getPrefixAddress());
                    }
                })
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer + 1;
                    }
                })
                .firstElement()
                .defaultIfEmpty(0)
                .onErrorReturnItem(0)
                .blockingGet();
    }

    private void showSelectWalletInfo(Wallet selectedWallet) {
        if (selectedWallet == null || selectedWallet.isNull()) {
            ivSelectedWalletAvatar.setImageResource(R.drawable.icon_all_wallets);
            tvSelectedWalletName.setText(getString(R.string.msg_all_wallets));
            mAddressList = WalletManager.getInstance().getAddressList();
        } else {
            ivSelectedWalletAvatar.setImageResource(RUtils.drawable(selectedWallet.getAvatar()));
            tvSelectedWalletName.setText(selectedWallet.getName());
            mAddressList = Arrays.asList(selectedWallet.getPrefixAddress());
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransactionRecordsActivity.class);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, Wallet wallet) {
        Intent intent = new Intent(context, TransactionRecordsActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
