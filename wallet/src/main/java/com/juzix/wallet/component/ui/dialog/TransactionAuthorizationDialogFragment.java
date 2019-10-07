package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.QRCodeEncoder;
import com.juzix.wallet.utils.RxUtils;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class TransactionAuthorizationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_transaction_data)
    ImageView ivTransactionData;
    @BindView(R.id.sbtn_next)
    ShadowButton sbtnNext;

    private Unbinder unbinder;

    public static TransactionAuthorizationDialogFragment newInstance(String data) {
        TransactionAuthorizationDialogFragment dialogFragment = new TransactionAuthorizationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_DATA, data);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_transaction_authorization, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 16f));
        setAnimation(R.style.Animation_slide_in_bottom);
        setCancelable(false);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        String data = getArguments().getString(Constants.Bundle.BUNDLE_DATA);

        RxView
                .clicks(sbtnNext)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                            dismiss();
                            TransactionSignatureDialogFragment.newInstance().show(getActivity().getSupportFragmentManager(),TransactionSignatureDialogFragment.TAG);
                    }
                });

        Observable
                .fromCallable(new Callable<Bitmap>() {

                    @Override
                    public Bitmap call() throws Exception {
                        return QRCodeEncoder.syncEncodeQRCode(data, DensityUtil.dp2px(getActivity(), 200f));
                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new CustomObserver<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        if (bitmap != null) {
                            ivTransactionData.setBackground(new BitmapDrawable(bitmap));
                        }
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
