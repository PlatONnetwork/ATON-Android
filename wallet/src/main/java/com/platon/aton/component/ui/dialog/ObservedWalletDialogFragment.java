package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

//删除观察钱包的提示弹窗
public class ObservedWalletDialogFragment extends BaseDialogFragment {
    @BindView(R.id.button_confirm)
    ShadowButton buttonConfirm;
    @BindView(R.id.tv_cancel)
    TextView textCancel;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    private Unbinder unbinder;
    private ConfirmListener mConfirmListener;

    public ObservedWalletDialogFragment setmConfirmListener(ConfirmListener mConfirmListener) {
        this.mConfirmListener = mConfirmListener;
        return this;
    }

    public static ObservedWalletDialogFragment newInstance() {
        ObservedWalletDialogFragment dialogFragment =new ObservedWalletDialogFragment();
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delete_observed_wallet, null);
        baseDialog.setContentView(view);
        setFullWidthEnable(true);
        setGravity(Gravity.CENTER);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return baseDialog;

    }

    private void initViews() {
        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 6f),
                ContextCompat.getColor(context, R.color.color_33616161)
                , DensityUtil.dp2px(context, 10f),
                0,
                DensityUtil.dp2px(context, 2f));

        RxView.clicks(textCancel)
                .compose(bindToLifecycle())
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {
                        dismiss();
                    }
                });


        RxView.clicks(buttonConfirm)
                .compose(bindToLifecycle())
                .compose(RxUtils.getClickTransformer())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (null != mConfirmListener) {
                            dismiss();
                            mConfirmListener.confirm();
                        }
                    }
                });

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConfirmListener) {
            mConfirmListener = (ConfirmListener) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface ConfirmListener {
        void confirm();
    }
}
