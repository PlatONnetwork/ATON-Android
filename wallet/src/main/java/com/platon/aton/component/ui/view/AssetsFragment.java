package com.platon.aton.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.AssetsWalletListAdapter;
import com.platon.aton.component.adapter.TransactionDiffCallback;
import com.platon.aton.component.adapter.TransactionListAdapter;
import com.platon.aton.component.adapter.WalletListDiffCallback;
import com.platon.aton.component.adapter.base.CommonHorizontalItemDecoration;
import com.platon.aton.component.ui.contract.AssetsContract;
import com.platon.aton.component.ui.dialog.AssetsMoreDialogFragment;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.ui.dialog.DelegateTipsDialog;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.component.ui.presenter.AssetsPresenter;
import com.platon.aton.component.ui.presenter.TransactionsPresenter;
import com.platon.aton.component.widget.AutofitTextView;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.EmptyRecyclerView;
import com.platon.aton.component.widget.RoundedTextView;
import com.platon.aton.component.widget.WrapContentLinearLayoutManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.GuideType;
import com.platon.aton.entity.QrCodeType;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.entity.TransactionSignatureData;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.JSONUtil;
import com.platon.aton.utils.QrCodeParser;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;
import com.platon.framework.utils.RUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.crypto.Credentials;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ziv
 * date On 2020-03-25
 */
public class AssetsFragment extends BaseLazyFragment<AssetsContract.View, AssetsPresenter> implements AssetsContract.View {

    @BindView(R.id.tv_total_assets_amount)
    TextView tvTotalAssetsAmount;
    @BindView(R.id.iv_assets_add)
    ImageView ivAssetsAdd;
    @BindView(R.id.iv_assets_scan_qrcode)
    ImageView ivAssetsScanQrcode;
    @BindView(R.id.rv_assets_wallet_list)
    RecyclerView rvAssetsWalletList;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_restricted_balance_amount)
    TextView tvRestrictedBalanceAmount;
    @BindView(R.id.tv_restricted_balance_text)
    TextView tvRestrictedBalanceText;
    @BindView(R.id.tv_observed_wallet_tag)
    TextView tvObservedWalletTag;
    @BindView(R.id.rtv_receive_transaction)
    RoundedTextView rtvReceiveTransaction;
    @BindView(R.id.rtv_send_transaction)
    RoundedTextView rtvSendTransaction;
    @BindView(R.id.rv_assets_transaction_list)
    EmptyRecyclerView rvAssetsTransactionList;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout layoutRefresh;
    @BindView(R.id.tv_wallet_amount_unit)
    TextView tvWalletAmountUnit;
    @BindView(R.id.rtv_backup_wallet)
    RoundedTextView rtvBackupWallet;
    @BindView(R.id.iv_close_security_reminders)
    ImageView ivCloseSecurityReminders;
    @BindView(R.id.layout_security_reminders)
    ConstraintLayout layoutSecurityReminders;
    @BindView(R.id.rtv_got_it)
    RoundedTextView rtvGotIt;
    @BindView(R.id.iv_close_device_offline_prompt)
    ImageView ivCloseDeviceOfflinePrompt;
    @BindView(R.id.layout_device_offline_prompt)
    ConstraintLayout layoutDeviceOfflinePrompt;
    @BindView(R.id.iv_manage_wallet)
    ImageView ivManageWallet;
    @BindView(R.id.layout_no_wallet)
    RelativeLayout layoutNoWallet;
    @BindView(R.id.layout_assets_wallet)
    ConstraintLayout layoutAssetsWallet;
    @BindView(R.id.layout_assets_transactions)
    LinearLayout layoutAssetsTransactions;
    @BindView(R.id.civ_wallet_avatar)
    CircleImageView civWalletAvatar;
    @BindView(R.id.layout_wallet_amount)
    LinearLayout layoutWalletAmount;

    Unbinder unbinder;

    AppCompatTextView tvWalletAmount;
    private AssetsWalletListAdapter mWalletListAdapter;
    private TransactionListAdapter mTransactionListAdapter;
    private int observedWalletTagWidth = 0;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_assets;
    }

    @Override
    public AssetsPresenter createPresenter() {
        return new AssetsPresenter();
    }

    @Override
    public AssetsContract.View createView() {
        return this;
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        //获取余额
        getPresenter().fetchWalletBalance();
        //加载交易记录数据
        getPresenter().loadData();
    }

    @Override
    public void onFragmentFirst() {
        super.onFragmentFirst();
        //获取余额
        getPresenter().fetchWalletBalance();
        //加载交易记录数据
        getPresenter().loadData();
    }

    @Override
    public void init(View rootView) {
        //绑定ButterKnife
        unbinder = ButterKnife.bind(this, rootView);
        //注册eventBus
        EventPublisher.getInstance().register(this);
        //初始化指引页
        initGuide();
        //展示钱包列表信息
        showAssetsWalletList();
        //展示选中的钱包信息
        showSelectedWalletInfo(WalletManager.getInstance().getSelectedWallet());
        //展示交易列表
        showAssetsTransactionList();
        //初始化点击事件
        initListener();

        tvWalletAmountUnit.post(new Runnable() {
            @Override
            public void run() {
                observedWalletTagWidth = tvObservedWalletTag.getWidth();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletSelectedChangedEvent(Event.WalletSelectedChangedEvent event) {
        showSelectedWalletInfo(WalletManager.getInstance().getSelectedWallet());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletNumberChangeEvent(Event.WalletNumberChangeEvent event) {
        List<Wallet> walletList = WalletManager.getInstance().getWalletList();
        mWalletListAdapter.notifyDataSetChanged(walletList);
        layoutNoWallet.setVisibility(walletList.isEmpty() ? View.VISIBLE : View.GONE);
        layoutAssetsWallet.setVisibility(walletList.isEmpty() ? View.GONE : View.VISIBLE);
        layoutAssetsTransactions.setVisibility(walletList.isEmpty() ? View.GONE : View.VISIBLE);

        showSelectedWalletInfo(WalletManager.getInstance().getSelectedWallet());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent netWorkStateChangedEvent) {
        showSelectedWalletInfo(WalletManager.getInstance().getSelectedWallet());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        getPresenter().addNewTransaction(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTransactionEvent(Event.DeleteTransactionEvent event) {
        getPresenter().deleteTransaction(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        getPresenter().loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        //获取最新
        getPresenter().loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSumAccountBalanceChanged(Event.SumAccountBalanceChanged event) {
        getPresenter().loadNewData(TransactionsPresenter.DIRECTION_NEW);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletListOrderChangedEvent(Event.WalletListOrderChangedEvent event) {
        mWalletListAdapter.notifyDataSetChanged(WalletManager.getInstance().getWalletList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackedUpWalletSuccessedEvent(Event.BackedUpWalletSuccessedEvent event) {
        if (TextUtils.equals(WalletManager.getInstance().getSelectedWallet().getUuid(), event.uuid)) {
            layoutSecurityReminders.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MainActivity.REQ_ASSETS_TAB_QR_CODE:
                String result = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                String unzip = GZipUtil.unCompress(result);
                if (TextUtils.isEmpty(unzip) && TextUtils.isEmpty(result)) {
                    showLongToast(currentActivity().string(R.string.unrecognized_content));
                    return;
                }
                @QrCodeType int qrCodeType = QrCodeParser.parseQrCode(TextUtils.isEmpty(unzip) ? result : unzip);

                if (qrCodeType == QrCodeType.NONE) {
                    showLongToast(currentActivity().string(R.string.unrecognized));
                    return;
                }

                if (qrCodeType == QrCodeType.TRANSACTION_AUTHORIZATION) {
                    TransactionAuthorizationDetailActivity.actionStart(currentActivity(), JSONUtil.parseObject(unzip, TransactionAuthorizationData.class));
                    return;
                }

                if (qrCodeType == QrCodeType.TRANSACTION_SIGNATURE) {
                    TransactionSignatureDialogFragment.newInstance(JSONUtil.parseObject(unzip, TransactionSignatureData.class))
                            .setOnSendTransactionSucceedListener(new TransactionSignatureDialogFragment.OnSendTransactionSucceedListener() {
                                @Override
                                public void onSendTransactionSucceed(Transaction transaction) {
                                    getPresenter().afterSendTransactionSucceed(transaction);
                                }
                            })
                            .show(getActivity().getSupportFragmentManager(), TransactionSignatureDialogFragment.TAG);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_ADDRESS) {
                    //有钱包进入发送界面，如果是地址的话无钱包进入导入观察者钱包页面
                    if (WalletManager.getInstance().getWalletList().isEmpty()) {
                        //进入导入观察者钱包
                        ImportWalletActivity.actionStart(getActivity(), ImportWalletActivity.TabIndex.IMPORT_OBSERVED, unzip);
                    } else {
                        SendTransactionActivity.actionStart(getActivity());
                    }
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_KEYSTORE) {
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_KEYSTORE, TextUtils.isEmpty(unzip) ? result : unzip);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_MNEMONIC) {
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_MNEMONIC, TextUtils.isEmpty(unzip) ? result : unzip);
                    return;
                }

                if (qrCodeType == QrCodeType.WALLET_PRIVATEKEY) {
                    ImportWalletActivity.actionStart(currentActivity(), ImportWalletActivity.TabIndex.IMPORT_PRIVATEKEY, TextUtils.isEmpty(unzip) ? result : unzip);
                    return;
                }
                break;
            case Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE:
                getActivity().getSupportFragmentManager().findFragmentByTag(TransactionSignatureDialogFragment.TAG).onActivityResult(Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE, resultCode, data);
                break;
            default:
                break;
        }
    }

    private void initGuide() {
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_RECORD, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.TRANSACTION_LIST).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_RECORD, true);
                }
            }).show(getActivity().getSupportFragmentManager(), "showGuideDialogFragment");
        }
    }

    private void initListener() {

        mWalletListAdapter.setOnItemClickListener(new AssetsWalletListAdapter.OnItemClickListener() {
            @Override
            public void onCommonWalletItemClick(Wallet wallet, int position) {
                WalletManager.getInstance().setWalletList(mWalletListAdapter.getDatas());
                showSelectedWalletInfo(wallet);
                getPresenter().loadData();
            }

            @Override
            public void onCreateWalletItemClick() {
                CreateWalletActivity.actionStart(Objects.requireNonNull(getActivity()));
            }

            @Override
            public void onImportWalletItemClick() {
                ImportWalletActivity.actionStart(Objects.requireNonNull(getActivity()));
            }
        });

        mTransactionListAdapter.setOnItemClickListener(new TransactionListAdapter.OnItemClickListener() {
            @Override
            public void onCommonTransactionItemClick(int position) {
                LogUtils.e(position + ":" + mTransactionListAdapter.getTransactionList().get(position).toString());
                TransactionDetailActivity.actionStart(getContext(), mTransactionListAdapter.getTransactionList().get(position), Collections.singletonList(WalletManager.getInstance().getSelectedWalletAddress()));
            }

            @Override
            public void onMoreTransactionItemClick() {
                TransactionRecordsActivity.actionStart(getActivity(), WalletManager.getInstance().getSelectedWallet());
            }
        });

        layoutRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPresenter().fetchWalletBalance();
            }
        });

        RxView
                .clicks(tvTotalAssetsAmount)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        boolean showAssetsTag = PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
                        PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, !showAssetsTag);
                        showAssetsInfo();
                    }
                });

        RxView.clicks(ivAssetsAdd)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {

                        AssetsMoreDialogFragment.newInstance().setOnAssetMoreClickListener(new AssetsMoreDialogFragment.OnAssetMoreClickListener() {
                            @Override
                            public void onCreateWalletClick() {
                                CreateWalletActivity.actionStart(getContext());
                            }

                            @Override
                            public void onImportWalletClick() {
                                ImportWalletActivity.actionStart(getContext());
                            }
                        }).show(getChildFragmentManager(), "showAssetsMore");
                    }
                });


        RxView.clicks(ivAssetsScanQrcode)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        new RxPermissions(currentActivity())
                                .requestEach(Manifest.permission.CAMERA)
                                .subscribe(new CustomObserver<Permission>() {
                                    @Override
                                    public void accept(Permission permission) {
                                        if (permission.granted) {
                                            ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_TAB_QR_CODE);
                                        } else if (permission.shouldShowRequestPermissionRationale) {
                                            // Denied permission without ask never again
                                        } else {
                                            showLongToast("使用该功能需要拍照和SD卡存储权限，请前往系统设置开启权限");
                                        }
                                    }
                                });
                    }
                });

        RxView.clicks(ivCloseSecurityReminders)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        layoutSecurityReminders.setVisibility(View.GONE);
                        Wallet selectedWallet = WalletManager.getInstance().getSelectedWallet();
                        if (selectedWallet != null) {
                            WalletManager.getInstance().updateWalletBackedUpPromptWithUUID(selectedWallet.getUuid(), false);
                            notifyWalletList();
                        }
                    }
                });

        RxView.clicks(layoutDeviceOfflinePrompt)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe();

        RxView.clicks(layoutSecurityReminders)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe();


        RxView.clicks(ivCloseDeviceOfflinePrompt)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        layoutDeviceOfflinePrompt.setVisibility(View.GONE);
                    }
                });

        RxView.clicks(rtvGotIt)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        layoutDeviceOfflinePrompt.setVisibility(View.GONE);
                    }
                });

        RxView.clicks(rtvBackupWallet)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        Wallet walletEntity = WalletManager.getInstance().getSelectedWallet();
                        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
                            @Override
                            public void onCorrect(Credentials credentials, String password) {
                                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, BackupMnemonicPhraseActivity.BackupMnemonicExport.MAIN_ACTIVITY);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
                    }
                });

        RxView.clicks(tvRestrictedBalanceAmount)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //弹出tips
                        DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                                null, null, string(R.string.msg_restricted_plan), string(R.string.restricted_amount_tips)).show(getChildFragmentManager(), "restrictedTips");
                    }
                });
        RxView.clicks(tvRestrictedBalanceText)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //弹出tips
                        DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                                null, null, string(R.string.msg_restricted_plan), string(R.string.restricted_amount_tips)).show(getChildFragmentManager(), "restrictedTips");
                    }
                });

        RxView.clicks(ivManageWallet)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        ManageWalletActivity.actionStart(currentActivity(), WalletManager.getInstance().getSelectedWallet());
                    }
                });
        RxView.clicks(rtvSendTransaction)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (NetConnectivity.getConnectivityManager().isConnected() || WalletManager.getInstance().getSelectedWallet().isObservedWallet()) {
                            SendTransactionActivity.actionStart(getContext());
                        } else {
                            new RxPermissions(currentActivity())
                                    .requestEach(Manifest.permission.CAMERA)
                                    .subscribe(new CustomObserver<Permission>() {
                                        @Override
                                        public void accept(Permission permission) {
                                            if (permission.granted) {
                                                ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_TAB_QR_CODE);
                                            } else if (permission.shouldShowRequestPermissionRationale) {
                                                // Denied permission without ask never again
                                            } else {
                                                showLongToast("使用该功能需要拍照和SD卡存储权限，请前往系统设置开启权限");
                                            }
                                        }
                                    });
                        }
                    }
                });
        RxView.clicks(rtvReceiveTransaction)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        ReceiveTransactionActivity.actionStart(getContext());
                    }
                });

    }

    /**
     * 获取钱包金额的最大长度
     *
     * @return
     */
    private int getWalletAmountMaxWidth() {
        return (int) (CommonUtil.getScreenWidth(getActivity()) - DensityUtil.dp2px(getActivity(), 34) - getTextViewLength(tvWalletAmountUnit, "LAT"));
    }

    private void showSelectedWalletInfo(Wallet selectedWallet) {

        if (layoutWalletAmount.getChildCount() == 2) {
            layoutWalletAmount.removeViewAt(0);
            tvWalletAmount = (AppCompatTextView) getLayoutInflater().inflate(R.layout.view_wallet_amount_text, null);
            layoutWalletAmount.addView(tvWalletAmount, 0);
            tvWalletAmount.setMaxWidth(getWalletAmountMaxWidth());
        }

        tvWalletName.setText(selectedWallet.getName());
        boolean visible = PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
        tvWalletAmount.setText(visible ? StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(selectedWallet.getFreeBalance(), 8)) : "***");
        tvWalletAmountUnit.setVisibility(visible ? View.VISIBLE : View.GONE);
        showLockBalance(selectedWallet.getLockBalance());
        tvObservedWalletTag.setVisibility(selectedWallet.isObservedWallet() || !NetConnectivity.getConnectivityManager().isConnected() ? View.VISIBLE : View.INVISIBLE);
        tvObservedWalletTag.setText(selectedWallet.isObservedWallet() ? string(R.string.msg_observed_wallet) : string(R.string.msg_cold_wallet));
        civWalletAvatar.setImageResource(RUtils.drawable(selectedWallet.getAvatar()));

        if (selectedWallet.isObservedWallet()) {
            rtvSendTransaction.setRoundedBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_ffffff));
            rtvSendTransaction.setRoundedBorderColor(ContextCompat.getColor(getActivity(), R.color.color_78a2fa));
            rtvSendTransaction.setText(string(R.string.msg_send_transaction));
            rtvSendTransaction.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_105cfe));
        } else {
            rtvSendTransaction.setRoundedBackgroundColor(NetConnectivity.getConnectivityManager().isConnected() ? ContextCompat.getColor(getActivity(), R.color.color_ffffff) : ContextCompat.getColor(getActivity(), R.color.color_f59a23));
            rtvSendTransaction.setRoundedBorderColor(NetConnectivity.getConnectivityManager().isConnected() ? ContextCompat.getColor(getActivity(), R.color.color_78a2fa) : ContextCompat.getColor(getActivity(), R.color.color_ffcd8b));
            rtvSendTransaction.setText(NetConnectivity.getConnectivityManager().isConnected() ? string(R.string.msg_send_transaction) : string(R.string.msg_offline_signature));
            rtvSendTransaction.setTextColor(NetConnectivity.getConnectivityManager().isConnected() ? ContextCompat.getColor(getActivity(), R.color.color_105cfe) : ContextCompat.getColor(getActivity(), R.color.color_ffffff));
        }

        layoutSecurityReminders.setVisibility(selectedWallet.showBackedUpPrompt() ? View.VISIBLE : View.GONE);
        layoutDeviceOfflinePrompt.setVisibility(NetConnectivity.getConnectivityManager().isConnected() ? View.GONE : View.VISIBLE);

    }

    private void showAssetsInfo() {

        Wallet selectedWallet = WalletManager.getInstance().getSelectedWallet();
        String totalBalance = WalletManager.getInstance().getTotal().blockingFirst().toPlainString();
        showFreeBalance(selectedWallet.getFreeBalance());
        showLockBalance(selectedWallet.getLockBalance());
        showTotalBalance(totalBalance);
    }

    private void showAssetsWalletList() {

        List<Wallet> walletList = WalletManager.getInstance().getWalletList();

        mWalletListAdapter = new AssetsWalletListAdapter(walletList);

        rvAssetsWalletList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvAssetsWalletList.addItemDecoration(new CommonHorizontalItemDecoration(getActivity(), R.drawable.shape_assets_wallet_list_divider));
        rvAssetsWalletList.setAdapter(mWalletListAdapter);

        layoutNoWallet.setVisibility(walletList.isEmpty() ? View.VISIBLE : View.GONE);
        layoutAssetsWallet.setVisibility(walletList.isEmpty() ? View.GONE : View.VISIBLE);
        layoutAssetsTransactions.setVisibility(walletList.isEmpty() ? View.GONE : View.VISIBLE);

    }

    private void showAssetsTransactionList() {

        mTransactionListAdapter = new TransactionListAdapter(TransactionListAdapter.EntranceType.MAIN_PAGE);

        rvAssetsTransactionList.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.include_no_transaction, null));
        rvAssetsTransactionList.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //解决数据加载完成后, 没有停留在顶部的问题
        rvAssetsTransactionList.setFocusable(false);
        rvAssetsTransactionList.setAdapter(mTransactionListAdapter);
    }

    private void notifyWalletList() {

        List<Wallet> oldWalletList = mWalletListAdapter.getDatas();

        List<Wallet> newWalletList = WalletManager.getInstance().getWalletList();

        if (oldWalletList == null || oldWalletList.isEmpty()) {
            mWalletListAdapter.notifyDataSetChanged(newWalletList);
        } else {
            WalletListDiffCallback diffCallback = new WalletListDiffCallback(oldWalletList, newWalletList);
            DiffUtil.calculateDiff(diffCallback, true).dispatchUpdatesTo(mWalletListAdapter);
        }

    }

    protected View getStatusBarView() {
        View view = new View(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }


    /**
     * 显示总金额
     *
     * @param totalBalance
     */
    @Override
    public void showTotalBalance(String totalBalance) {
        boolean visible = PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
        tvTotalAssetsAmount.setText(visible ? StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(totalBalance, 8)) : "***");
    }

    /**
     * 显示自由金额
     *
     * @param balance
     */
    @Override
    public void showFreeBalance(String balance) {//当前钱包的资产
        boolean visible = PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
        if (layoutWalletAmount.getChildCount() == 2) {
            layoutWalletAmount.removeViewAt(0);
            tvWalletAmount = (AppCompatTextView) getLayoutInflater().inflate(R.layout.view_wallet_amount_text, null);
            layoutWalletAmount.addView(tvWalletAmount, 0);
            tvWalletAmount.setMaxWidth(getWalletAmountMaxWidth());
        }
        tvWalletAmount.setText(visible ? StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(balance, 8)) : "***");
        tvWalletAmountUnit.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示锁仓金额
     *
     * @param balance
     */
    @Override
    public void showLockBalance(String balance) { //当前选中钱包的锁仓金额
        boolean visible = PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
        String restrictedAmountText = visible ? string(R.string.restricted_amount_with_unit, StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(balance, 8))) : "***";
        int restrictedBalanceMaxLength = DensityUtil.getScreenWidth(getContext()) - DensityUtil.dp2px(getContext(), 26) - observedWalletTagWidth;
        float restrictedBalanceActualLength = getTextViewLength(tvRestrictedBalanceText, restrictedAmountText);
        tvRestrictedBalanceAmount.setVisibility(BigDecimalUtil.isBiggerThanZero(balance) ? View.VISIBLE : View.GONE);
        if (restrictedBalanceActualLength > restrictedBalanceMaxLength) {
            tvRestrictedBalanceText.setText(string(R.string.restricted_balance_amount));
            tvRestrictedBalanceAmount.setText(StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(balance, 8)));
        } else {
            tvRestrictedBalanceText.setText(restrictedAmountText);
        }
        tvRestrictedBalanceAmount.setVisibility(restrictedBalanceActualLength > restrictedBalanceMaxLength ? View.VISIBLE : View.GONE);
    }

    @Override
    public void finishRefresh() {
        layoutRefresh.finishRefresh();
    }

    @Override
    public void notifyTransactionSetChanged(List<Transaction> oldTransactionList, List<Transaction> newTransactionList, String queryAddress, boolean loadLatestData) {

        mTransactionListAdapter.setQueryAddressList(Arrays.asList(queryAddress));
        if (loadLatestData || newTransactionList == null || newTransactionList.isEmpty()) {
            mTransactionListAdapter.notifyDataSetChanged(newTransactionList);
        } else {
            TransactionDiffCallback transactionDiffCallback = new TransactionDiffCallback(oldTransactionList, newTransactionList);
            mTransactionListAdapter.setTransactionList(newTransactionList);
            DiffUtil.calculateDiff(transactionDiffCallback, true).dispatchUpdatesTo(mTransactionListAdapter);
        }

    }

    public static float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少  
        return paint.measureText(text);
    }

}
