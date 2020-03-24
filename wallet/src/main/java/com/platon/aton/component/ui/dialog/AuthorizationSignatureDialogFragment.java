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
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;

public class AuthorizationSignatureDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_signed_data)
    ImageView ivSignedData;
    @BindView(R.id.sbtn_finish)
    ShadowButton sbtnFinish;

    private Unbinder unbinder;

    public static AuthorizationSignatureDialogFragment newInstance(String data) {
        AuthorizationSignatureDialogFragment dialogFragment = new AuthorizationSignatureDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_DATA, data);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_authorization_signature, null, false);
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

        String data = getArguments().getString(Constants.Bundle.BUNDLE_DATA);

        RxView
                .clicks(sbtnFinish)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        dismiss();
                        getActivity().finish();
                    }
                });

        Observable
                .fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        return QRCodeEncoder.syncEncodeQRCode(GZipUtil.compress(data), DensityUtil.getScreenWidth(getContext()) - DensityUtil.dp2px(getActivity(), 64f));
                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSchedulerTransformer())
                .subscribe(new CustomObserver<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        if (bitmap != null) {
                            ivSignedData.setImageBitmap(bitmap);
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
