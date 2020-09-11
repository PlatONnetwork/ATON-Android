package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WalletHDMoreDialogFragment extends BaseDialogFragment {


    @BindView(R.id.ll_rename)
    LinearLayout llRename;
    @BindView(R.id.ll_mnemonics_backup)
    LinearLayout llMnemonicsBackup;
    @BindView(R.id.ll_delete_hd)
    LinearLayout llDeleteHd;
    @BindView(R.id.view_delete_hd)
    View viewDeleteHd;

    private Unbinder unbinder;
    private OnWalletHDMoreClickListener mOnWalletHDMoreClickListener;

    public static WalletHDMoreDialogFragment newInstance(Wallet rootWallet) {
        WalletHDMoreDialogFragment dialogFragment = new WalletHDMoreDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Bundle.BUNDLE_DATA, rootWallet);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public WalletHDMoreDialogFragment setOnWalletHDMoreClickListener(OnWalletHDMoreClickListener walletHDMoreClickListener) {
        this.mOnWalletHDMoreClickListener = walletHDMoreClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_activity_more_manager_hd, null);
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

        Wallet wallet = getArguments().getParcelable(Constants.Bundle.BUNDLE_DATA);
        if(wallet.isBackedUp()){
            llRename.setVisibility(View.VISIBLE);
            llMnemonicsBackup.setVisibility(View.VISIBLE);
            llDeleteHd.setVisibility( View.VISIBLE);
        }else{
            llRename.setVisibility(View.VISIBLE);
            llMnemonicsBackup.setVisibility(View.VISIBLE);
            llDeleteHd.setVisibility( View.GONE);
            viewDeleteHd.setVisibility(View.GONE);
        }




        RxView
                .clicks(llRename)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnWalletHDMoreClickListener != null) {
                            mOnWalletHDMoreClickListener.onWalletRenameClick();
                            dismiss();
                        }
                    }
                });

        RxView
                .clicks(llMnemonicsBackup)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnWalletHDMoreClickListener != null) {
                            mOnWalletHDMoreClickListener.onWalletMnemonicsBackupClick();
                            dismiss();
                        }
                    }
                });

        RxView
                .clicks(llDeleteHd)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (mOnWalletHDMoreClickListener != null) {
                            mOnWalletHDMoreClickListener.onWalletDeleteClick();
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



    public interface OnWalletHDMoreClickListener {

        void onWalletRenameClick();

        void onWalletMnemonicsBackupClick();

        void onWalletDeleteClick();
    }
}
