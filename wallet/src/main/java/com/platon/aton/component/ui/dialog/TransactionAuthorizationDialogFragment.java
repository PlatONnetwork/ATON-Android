package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.entity.TransactionAuthorizationData;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;

public class TransactionAuthorizationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_transaction_data)
    ImageView ivTransactionData;
    @BindView(R.id.sbtn_next)
    ShadowButton sbtnNext;

    private Unbinder unbinder;
    private OnNextBtnClickListener nextBtnClickListener;

    public static TransactionAuthorizationDialogFragment newInstance(TransactionAuthorizationData data) {
        TransactionAuthorizationDialogFragment dialogFragment = new TransactionAuthorizationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Bundle.BUNDLE_DATA, data);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public TransactionAuthorizationDialogFragment setOnNextBtnClickListener(OnNextBtnClickListener nextBtnClickListener){
        this.nextBtnClickListener = nextBtnClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_transaction_authorization, null, false);
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

        TransactionAuthorizationData data = getArguments().getParcelable(Constants.Bundle.BUNDLE_DATA);

        RxView
                .clicks(sbtnNext)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        dismiss();
                        if (nextBtnClickListener != null){
                            nextBtnClickListener.onNextBtnClick();
                        }
                    }
                });

        Observable
                .fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        return QRCodeEncoder.syncEncodeQRCode(GZipUtil.compress(data.toJSONString()), DensityUtil.getScreenWidth(getContext()) - DensityUtil.dp2px(getActivity(), 64f));
                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new CustomObserver<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        if (bitmap != null) {
                            ivTransactionData.setImageBitmap(bitmap);
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

    public interface OnNextBtnClickListener{
        void onNextBtnClick();
    }
}
