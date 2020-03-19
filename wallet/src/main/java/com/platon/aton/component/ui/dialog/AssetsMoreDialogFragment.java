package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AssetsMoreDialogFragment extends BaseDialogFragment {

    @BindView(R.id.ll_create_wallet)
    LinearLayout llCreateWallet;
    @BindView(R.id.ll_import_wallet)
    LinearLayout llImportWallet;

    private Unbinder unbinder;
    private OnAssetMoreClickListener mOnAssetMoreClickListener;

    public static AssetsMoreDialogFragment newInstance() {
        AssetsMoreDialogFragment dialogFragment = new AssetsMoreDialogFragment();
        return dialogFragment;
    }

    public AssetsMoreDialogFragment setOnAssetMoreClickListener(OnAssetMoreClickListener assetMoreClickListener) {
        this.mOnAssetMoreClickListener = assetMoreClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_assets_more, null);
        baseDialog.setContentView(contentView);
        setFullHeightEnable(false);
        setFullWidthEnable(false);
        setGravity(Gravity.RIGHT | Gravity.TOP);
        setxOffset(DensityUtil.dp2px(getContext(), 8));
        setyOffset(DensityUtil.dp2px(getContext(), 36));
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        RxView
                .clicks(llCreateWallet)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnAssetMoreClickListener != null) {
                            mOnAssetMoreClickListener.onCreateWalletClick();
                            dismiss();
                        }
                    }
                });

        RxView
                .clicks(llImportWallet)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnAssetMoreClickListener != null) {
                            mOnAssetMoreClickListener.onImportWalletClick();
                            dismiss();
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

    public interface OnAssetMoreClickListener {

        void onCreateWalletClick();

        void onImportWalletClick();
    }
}
