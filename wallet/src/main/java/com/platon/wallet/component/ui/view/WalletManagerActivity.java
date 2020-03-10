package com.platon.wallet.component.ui.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.platon.wallet.R;
import com.platon.wallet.app.Constants;
import com.platon.wallet.component.adapter.WalletManagerAdapter;
import com.platon.wallet.component.ui.base.MVPBaseActivity;
import com.platon.wallet.component.ui.contract.WalletManagerContract;
import com.platon.wallet.component.ui.presenter.WalletManagerPresenter;
import com.platon.wallet.component.widget.CommonTitleBar;
import com.platon.wallet.component.widget.ShadowContainer;
import com.platon.wallet.netlistener.NetStateChangeObserver;
import com.platon.wallet.netlistener.NetStateChangeReceiver;
import com.platon.wallet.netlistener.NetworkType;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class WalletManagerActivity extends MVPBaseActivity<WalletManagerPresenter> implements WalletManagerContract.View, NetStateChangeObserver {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.rv_wallet)
    RecyclerView rvWallet;
    @BindView(R.id.layout_empty)
    View emptyView;
    @BindView(R.id.sc_import_wallet)
    ShadowContainer scImportWallet;
    @BindView(R.id.sc_create_wallet)
    ShadowContainer scCreateWallet;
    //itemHelper的回调
    private ItemTouchHelper mItemTouchHelper;
    private WalletManagerAdapter mAdapter;
    Unbinder unbinder;

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, WalletManagerActivity.class));
    }

    @Override
    protected WalletManagerPresenter createPresenter() {
        return new WalletManagerPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_manager);
        unbinder = ButterKnife.bind(this);
        NetStateChangeReceiver.registerReceiver(this);
        initView();
    }

    @Override
    protected void onResume() {
        mPresenter.fetchWalletList();
        MobclickAgent.onPageStart(Constants.UMPages.WALLET_MANAGER);
        NetStateChangeReceiver.registerObserver(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.WALLET_MANAGER);
        NetStateChangeReceiver.unRegisterObserver(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        NetStateChangeReceiver.unRegisterReceiver(this);
    }

    @OnClick({R.id.sc_create_wallet, R.id.sc_import_wallet})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_create_wallet:
                CreateWalletActivity.actionStart(this);
                break;
            case R.id.sc_import_wallet:
                ImportWalletActivity.actionStart(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWallet.setLayoutManager(linearLayoutManager);
        mAdapter = new WalletManagerAdapter(mPresenter.getDataSource(),getContext());
        mAdapter.setOnBackupClickListener(new WalletManagerAdapter.OnBackupClickListener() {
            @Override
            public void onBackupClick(int position) {
                mPresenter.backupWallet(position);
            }

            @Override
            public void onItemClick(int position) {
                mPresenter.startAction(position);
            }
        });
        rvWallet.setAdapter(mAdapter);
        rvWallet.addOnItemTouchListener(new WalletManagerAdapter.OnRecyclerItemClickListener(rvWallet) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
//                mPresenter.startAction(vh.getLayoutPosition());
//                Toast.makeText(getContext(), mPresenter.getDataSource().get(vh.getLayoutPosition()).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                //判断被拖拽的是否是前两个，如果不是则执行拖拽
//                if (vh.getLayoutPosition() != 0 && vh.getLayoutPosition() != 1) {
//                    mItemTouchHelper.startDrag(vh);
//
//                    //获取系统震动服务
//                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
//                    vib.vibrate(70);
//
//                }

                mItemTouchHelper.startDrag(vh);
                //获取系统震动服务
                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                vib.vibrate(70);
            }
        });
        //1.创建item helper
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        //2.绑定到RecyclerView上面去
        mItemTouchHelper.attachToRecyclerView(rvWallet);
        //3.在ItemHelper的接口回调中过滤开启长按拖动，拓展其他操作
    }

    @Override
    public void notifyWalletListChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWalletList() {
        emptyView.setVisibility(View.GONE);
        rvWallet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        rvWallet.setVisibility(View.GONE);
    }

    private ItemTouchHelper.Callback mCallback = new ItemTouchHelper.Callback() {

        /**
         * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
         * @param recyclerView
         * @param viewHolder
         * @return
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
//                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mPresenter.getDataSource(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mPresenter.getDataSource(), i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            mPresenter.sortWalletList();
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                myAdapter.notifyItemRemoved(position);
//                datas.remove(position);
        }

        /**
         * 重写拖拽可用
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        /**
         * 长按选中Item的时候开始调用
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_f9fbff));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开的时候还原
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
        }
    };

    @Override
    public void onNetDisconnected() {
        mPresenter.fetchWalletList();
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
       mPresenter.fetchWalletList();
    }
}
