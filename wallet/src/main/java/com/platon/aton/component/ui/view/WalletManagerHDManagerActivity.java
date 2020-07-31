package com.platon.aton.component.ui.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.WalletManagerAdapter;
import com.platon.aton.component.ui.contract.WalletManagerContract;
import com.platon.aton.component.ui.contract.WalletManagerHDManagerContract;
import com.platon.aton.component.ui.dialog.CommonEditDialogFragment;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.presenter.WalletManagerHDManagerPresenter;
import com.platon.aton.component.ui.presenter.WalletManagerPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.ShadowContainer;
import com.platon.aton.entity.Wallet;
import com.platon.aton.netlistener.NetStateChangeObserver;
import com.platon.aton.netlistener.NetStateChangeReceiver;
import com.platon.aton.netlistener.NetworkType;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 钱包管理_HD子钱包管理
 */
public class WalletManagerHDManagerActivity extends BaseActivity<WalletManagerHDManagerContract.View, WalletManagerHDManagerPresenter> implements WalletManagerHDManagerContract.View, NetStateChangeObserver {

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

    private Wallet rootWallet;

    public static void actionStart(Context context, Wallet rootWallet) {
        Intent intent = new Intent(context, WalletManagerHDManagerActivity.class);
        intent.putExtra("rootWallet",rootWallet);
        context.startActivity(intent);
    }

    @Override
    public WalletManagerHDManagerPresenter createPresenter() {
        return new WalletManagerHDManagerPresenter();
    }

    @Override
    public WalletManagerHDManagerContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        NetStateChangeReceiver.registerReceiver(this);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manager;
    }

    @Override
    protected void onResume() {
        getPresenter().fetchHDWalletList(rootWallet.getUuid());
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

 /*   @OnClick({R.id.sc_create_wallet, R.id.sc_import_wallet})
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
    }*/

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initView() {
        rootWallet = getIntent().getParcelableExtra("rootWallet");
        commonTitleBar.setTitle(rootWallet.getName());
        TextView tvRignt = commonTitleBar.findViewById(R.id.tv_right);
        tvRignt.setVisibility(View.VISIBLE);
        tvRignt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        commonTitleBar.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyNameDialog("");
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWallet.setLayoutManager(linearLayoutManager);
        mAdapter = new WalletManagerAdapter(getPresenter().getDataSource(), getContext());
        mAdapter.setOnBackupClickListener(new WalletManagerAdapter.OnBackupClickListener() {
            @Override
            public void onBackupClick(int position) {
            }

            @Override
            public void onItemClick(int position) {
                getPresenter().startAction(position);
            }
        });
        rvWallet.setAdapter(mAdapter);
       /* rvWallet.addOnItemTouchListener(new WalletManagerAdapter.OnRecyclerItemClickListener(rvWallet) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {

            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

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
        //3.在ItemHelper的接口回调中过滤开启长按拖动，拓展其他操作*/
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

    @Override
    public void showWalletName(String name) {
        commonTitleBar.setTitle(name);
    }


    @Override
    public void showModifyNameDialog(String name) {
        CommonEditDialogFragment commonEditDialogFragment = CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.rename_wallet), name, InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 20) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(WalletManagerHDManagerActivity.this, R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showModifyNameDialog(text);
                                }
                            }).show(getSupportFragmentManager(), "showTips");
                } else {
                    getPresenter().modifyName(text,rootWallet.getUuid());
                  /*  if (getPresenter().isExists(text,rootWallet.getUuid())) {
                        showLongToast(string(R.string.wallet_name_exists));
                    } else {
                        getPresenter().modifyName(text,rootWallet.getUuid());
                    }*/
                }
            }
        });
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentStarted(fm, f);
                if (f.getClass() == CommonEditDialogFragment.class) {
                    CommonEditDialogFragment.FixedDialog fixedDialog = (CommonEditDialogFragment.FixedDialog) ((CommonEditDialogFragment) f).getDialog();
                    fixedDialog.etInputInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                }
            }
        }, false);
        commonEditDialogFragment.show(getSupportFragmentManager(), "showModifyName");

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
                    Collections.swap(getPresenter().getDataSource(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(getPresenter().getDataSource(), i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            getPresenter().sortWalletList();
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
        //getPresenter().fetchHDWalletList();
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        //getPresenter().fetchHDWalletList();
    }
}
