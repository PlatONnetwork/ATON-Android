package com.juzix.wallet.component.ui.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.view.AddNewAddressActivity;
import com.juzix.wallet.component.ui.view.AssetsFragment;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.entity.TransactionSignatureData;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.platon.FunctionType;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class TransactionSignatureDialogFragment extends BaseDialogFragment {

    public final static String TAG = TransactionSignatureDialogFragment.class.getSimpleName();

    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_transaction_signature)
    TextView tvTransactionSignature;
    @BindView(R.id.sbtn_send_transaction)
    ShadowButton sbtnSendTransaction;

    private Unbinder unbinder;
    private TransactionSignatureData transactionSignatureData;


    public static TransactionSignatureDialogFragment newInstance(TransactionSignatureData transactionSignatureData) {
        TransactionSignatureDialogFragment dialogFragment = new TransactionSignatureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_TRANSACTION_SIGNATURE_DATA, transactionSignatureData);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static TransactionSignatureDialogFragment newInstance() {
        TransactionSignatureDialogFragment dialogFragment = new TransactionSignatureDialogFragment();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_transaction_signature, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 16f));
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        transactionSignatureData = getArguments().getParcelable(Constants.Extra.EXTRA_TRANSACTION_SIGNATURE_DATA);

        tvTransactionSignature.setMovementMethod(ScrollingMovementMethod.getInstance());

        tvTransactionSignature.setText(getSignedMessage(transactionSignatureData));

        RxTextView
                .textChanges(tvTransactionSignature)
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        sbtnSendTransaction.setEnabled(!TextUtils.isEmpty(charSequence));
                    }
                });

        RxView
                .clicks(ivScan)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        new RxPermissions(getActivity())
                                .requestEach(Manifest.permission.CAMERA)
                                .subscribe(new CustomObserver<Permission>() {
                                    @Override
                                    public void accept(Permission permission) {
                                        if (permission.granted && Manifest.permission.CAMERA.equals(permission.name)) {
                                            ScanQRCodeActivity.startActivityForResult(getActivity(), Constants.RequestCode.REQUEST_CODE_TRANSACTION_SIGNATURE);
                                        }
                                    }
                                });
                    }
                });

        RxView
                .clicks(sbtnSendTransaction)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (transactionSignatureData != null) {
                            Single
                                    .fromCallable(new Callable<String>() {
                                        @Override
                                        public String call() throws Exception {
                                            return TransactionManager.getInstance().sendTransaction(transactionSignatureData.getSignedDatas().get(0));
                                        }
                                    })
                                    .compose(RxUtils.getSingleSchedulerTransformer())
                                    .compose(bindToLifecycle())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            ToastUtil.showLongToast(getActivity(), getContext().getString(R.string.transfer_failed));
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String signature = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
        transactionSignatureData = JSONUtil.parseObject(signature, TransactionSignatureData.class);
        tvTransactionSignature.setText(getSignedMessage(transactionSignatureData));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void doAfterSendTransactionSucceed(int functionType, String hash) {
        dismiss();
        if (functionType == FunctionType.TRANSFER) {

        } else {
            //进入交易详情
        }
    }

    /**
     * 延迟指定时间后返回交易列表页
     */
    private void backToTransactionListWithDelay() {

    }

    private String getSignedMessage(TransactionSignatureData transactionSignatureData) {
        if (isEmpty(transactionSignatureData)) {
            return "";
        }

        return TextUtils.join(",", transactionSignatureData.getSignedDatas());

    }

    private boolean isEmpty(TransactionSignatureData transactionSignatureData) {
        return transactionSignatureData == null || transactionSignatureData.getSignedDatas() == null || transactionSignatureData.getSignedDatas().isEmpty();
    }
}
