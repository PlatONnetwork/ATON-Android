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
public class DelegateDetailGuideDialogFragment extends BaseDialogFragment {
    @BindView(R.id.iv_guide)
    ImageView iv_guide;
    @BindView(R.id.btn_close)
    Button btn_close;
    private Unbinder unbinder;
    private knowListener mKnowListener;


    public DelegateDetailGuideDialogFragment setKnowListener(knowListener mKnowListener) {
        this.mKnowListener = mKnowListener;
        return this;
    }

    /**
     * @param isEnglish 是否是英文界面
     * @return
     */
    public static DelegateDetailGuideDialogFragment newInstance(boolean isEnglish) {
        DelegateDetailGuideDialogFragment guideDialogFragment = new DelegateDetailGuideDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.Bundle.BUNDLE_ENGLISH, isEnglish);
        guideDialogFragment.setArguments(bundle);
        return guideDialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delegate_detatil_guide, null, false);
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
        Dialog dialog =getDialog();
        if(null !=dialog){
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
    }

    private void initView() {
        boolean isEn = getArguments().getBoolean(Constants.Bundle.BUNDLE_ENGLISH, false);
        if(isEn){
            iv_guide.setImageResource(R.drawable.delegate_detail_en);
        }else {
            iv_guide.setImageResource(R.drawable.delegate_detail_cn);
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