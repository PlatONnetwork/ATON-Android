package com.platon.aton.component.ui.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.Constants;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.base.BaseFragment;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class ExportKeystoreQRCodeFragment extends BaseFragment {

    private ShadowButton mCopyShadowButton;
    private ImageView mQrCodeIv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_keystore_qr_code, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mCopyShadowButton = view.findViewById(R.id.btn_copy);
        mQrCodeIv = view.findViewById(R.id.iv_qrcode);

        String qrCodeData = getActivity().getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD);

        int size = CommonUtil.getScreenWidth(getActivity()) - DensityUtil.dp2px(getActivity(), 92f);

        RxView
                .clicks(mCopyShadowButton)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        CommonUtil.copyTextToClipboard(getActivity(), qrCodeData);
                    }
                });

        Observable
                .fromCallable(new Callable<Bitmap>() {

                    @Override
                    public Bitmap call() throws Exception {
                        return QRCodeEncoder.syncEncodeQRCode(qrCodeData, size);
                    }
                })
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getSchedulerTransformer())
                .compose(RxUtils.getLoadingTransformer(this))
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        if (bitmap != null) {
                            mQrCodeIv.setImageBitmap(bitmap);
                        }
                    }
                });
    }
}
