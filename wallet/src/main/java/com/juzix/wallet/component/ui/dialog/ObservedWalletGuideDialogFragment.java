package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * 通用引导图的弹窗
 */
public class ObservedWalletGuideDialogFragment extends BaseDialogFragment {
    @BindView(R.id.iv_guide)
    ImageView iv_guide;
    @BindView(R.id.btn_close)
    Button btn_close;
    private Unbinder unbinder;
    private knowListener mKnowListener;


    public ObservedWalletGuideDialogFragment setKnowListener(knowListener mKnowListener) {
        this.mKnowListener = mKnowListener;
        return this;
    }

    /**
     * @param isEnglish 是否是英文界面
     * @return
     */
    public static ObservedWalletGuideDialogFragment newInstance(boolean isEnglish) {
        ObservedWalletGuideDialogFragment guideDialogFragment = new ObservedWalletGuideDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.Bundle.BUNDLE_ENGLISH, isEnglish);
        guideDialogFragment.setArguments(bundle);
        return guideDialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_observed_wallet_guide, null, false);
        baseDialog.setContentView(contentView);
        setFullHeightEnable(false);
        setFullWidthEnable(true);
        unbinder = ButterKnife.bind(this, contentView);
        initView();
        return baseDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

//        Window win = getDialog().getWindow();
//
//        DisplayMetrics dm =new DisplayMetrics();
//
//        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_00000000)));
//
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
//
//        win.setLayout( dm.widthPixels,dm.heightPixels );
//
//        // 一定要设置Background，如果不设置，window属性设置无效
//
//        WindowManager.LayoutParams params = win.getAttributes();
//
//        params.gravity = Gravity.BOTTOM;
//
//       // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
//
//        params.width =  ViewGroup.LayoutParams.MATCH_PARENT;
//
//        int v = win.getAttributes().flags;
//
//       // 全屏 66816 - 非全屏65792
//
//        if(v !=66816){//非全屏
//
//            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        }else{//取消全屏
//
//            params.height = dm.heightPixels;
//
//        }
//
//        win.setAttributes(params);

        Dialog dialog =getDialog();
        if(null !=dialog){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
    }

    private void initView() {
        boolean isEn = getArguments().getBoolean(Constants.Bundle.BUNDLE_ENGLISH, false);
        if(isEn){
            iv_guide.setImageResource(R.drawable.import_observed_wallet_en);
        }else {
            iv_guide.setImageResource(R.drawable.import_observed_wallet_cn);
        }

        RxView.clicks(btn_close)
                .compose(bindToLifecycle())
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mKnowListener) {
                            dismiss();
                            mKnowListener.know();
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

    public interface knowListener {
        void know();
    }

}