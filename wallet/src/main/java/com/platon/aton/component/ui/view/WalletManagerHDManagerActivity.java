package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.component.adapter.WalletManagerAdapter;
import com.platon.aton.component.ui.contract.WalletManagerHDManagerContract;
import com.platon.aton.component.ui.dialog.CommonEditDialogFragment;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.dialog.WalletHDMoreDialogFragment;
import com.platon.aton.component.ui.presenter.WalletManagerHDManagerPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.ShadowContainer;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.netlistener.NetStateChangeObserver;
import com.platon.aton.netlistener.NetStateChangeReceiver;
import com.platon.aton.netlistener.NetworkType;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import org.web3j.crypto.Credentials;

import butterknife.BindView;
import butterknife.ButterKnife;
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

        rootWallet = WalletDao.getWalletByUuid(rootWallet.getUuid()).buildWallet();
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


    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initView() {
        rootWallet = getIntent().getParcelableExtra("rootWallet");

        commonTitleBar.setTitle(rootWallet.getName());
        commonTitleBar.findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
        commonTitleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletHDMoreDialogFragment.newInstance(rootWallet).setOnWalletHDMoreClickListener(new WalletHDMoreDialogFragment.OnWalletHDMoreClickListener() {
                    @Override
                    public void onWalletRenameClick() {
                        showModifyNameDialog("");
                    }

                    @Override
                    public void onWalletMnemonicsBackupClick() {
                        showWalletMnemonicsBackup(rootWallet);
                    }

                    @Override
                    public void onWalletDeleteClick() {
                        showWalletDelete(rootWallet);
                    }
                }).show(getSupportFragmentManager(),"showWalletHDMore");
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
    public void showWalletMnemonicsBackup(Wallet wallet) {

        InputWalletPasswordDialogFragment.newInstance(wallet, InputWalletPasswordFromType.BACKUPS).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password, Wallet wallet) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, wallet, BackupMnemonicPhraseActivity.BackupMnemonicExport.MAIN_ACTIVITY);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void showWalletDelete(Wallet wallet) {

        InputWalletPasswordDialogFragment.newInstance(wallet, InputWalletPasswordFromType.TRANSACTION, string(R.string.msg_delete_wallet)).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                //getPresenter().validPassword(type, credentials);
            }
        }).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password, Wallet wallet) {
                getPresenter().deleteHDWallet(wallet);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");

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



    @Override
    public void onNetDisconnected() {
        //getPresenter().fetchHDWalletList();
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        //getPresenter().fetchHDWalletList();
    }
}
