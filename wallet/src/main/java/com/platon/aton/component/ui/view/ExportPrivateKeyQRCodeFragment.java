package com.platon.aton.component.ui.view;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class ExportPrivateKeyQRCodeFragment extends BaseLazyFragment {

    private ShadowButton mCopyShadowButton;
    private ImageView mQrCodeIv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_export_private_key_qr_code;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init(View rootView) {
        initViews(rootView);
    }

    private void initViews(View view) {

        mCopyShadowButton = view.findViewById(R.id.btn_copy);
        mQrCodeIv = view.findViewById(R.id.iv_qrcode);

        String qrCodeData = getActivity().getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD);

        int size = CommonUtil.getScreenWidth(getActivity()) - DensityUtil.dp2px(getActivity(), 92f);

        RxView
                .clicks(mCopyShadowButton)
                .compose(bindToLifecycle())
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
                .compose(bindToLifecycle())
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
