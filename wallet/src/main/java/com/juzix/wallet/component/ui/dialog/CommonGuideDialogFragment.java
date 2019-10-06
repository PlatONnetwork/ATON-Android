package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * 通用引导图的弹窗
 */
public class CommonGuideDialogFragment extends BaseDialogFragment {
    @BindView(R.id.iv_guide)
    ImageView iv_guide;
    @BindView(R.id.btn_close)
    Button btn_close;
    private Unbinder unbinder;
    private knowListener mKnowListener;

    public static final String RECORD = "1";
    public static final String DELEGATE_DETAIL = "2";
    public static final String DELEGATE_OPERATION = "3";
    public static final String VALIDATORS = "4";
    public static final String OBSERVED_WALLET = "5";
    public static final String MY_DELEGATE = "6";
    public static final String WITHDRAW_OPERATION = "7";


    public CommonGuideDialogFragment setKnowListener(knowListener mKnowListener) {
        this.mKnowListener = mKnowListener;
        return this;
    }

    /**
     * @param type      表示当前是哪个页面弹出引导页
     * @param isEnglish 是否是英文界面
     * @return
     */
    public static CommonGuideDialogFragment newInstance(String type, boolean isEnglish) {
        CommonGuideDialogFragment guideDialogFragment = new CommonGuideDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_PAGE, type);
        bundle.putBoolean(Constants.Bundle.BUNDLE_ENGLISH, isEnglish);
        guideDialogFragment.setArguments(bundle);
        return guideDialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_common_guide, null, false);
        baseDialog.setContentView(contentView);
        baseDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setFullHeightEnable(false);
        setFullWidthEnable(true);
        unbinder = ButterKnife.bind(this, contentView);
        initView();
        return baseDialog;
    }

    private void initView() {
        String type = getArguments().getString(Constants.Bundle.BUNDLE_PAGE);
        boolean isEn = getArguments().getBoolean(Constants.Bundle.BUNDLE_ENGLISH, false);

        if (isEn) {
            switch (type) {
                case RECORD:
                    iv_guide.setImageResource(R.drawable.wallet_transction_record_en);
                    break;
                case DELEGATE_DETAIL:
                    iv_guide.setImageResource(R.drawable.delegate_detail_en);
                    break;
                case DELEGATE_OPERATION:
                    iv_guide.setImageResource(R.drawable.delegate_operation_en);
                    break;
                case VALIDATORS:
                    iv_guide.setImageResource(R.drawable.delegate_validators_en);
                    break;
                case OBSERVED_WALLET:
                    iv_guide.setImageResource(R.drawable.import_observed_wallet_en);
                    break;
                case MY_DELEGATE:
                    iv_guide.setImageResource(R.drawable.my_delegate_en);
                    break;
                case WITHDRAW_OPERATION:
                    iv_guide.setImageResource(R.drawable.withdraw_operation_en);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case RECORD:
                    iv_guide.setImageResource(R.drawable.wallet_transction_record_cn);
                    break;
                case DELEGATE_DETAIL:
                    iv_guide.setImageResource(R.drawable.delegate_detail_cn);
                    break;
                case DELEGATE_OPERATION:
                    iv_guide.setImageResource(R.drawable.delegate_operation_cn);
                    break;
                case VALIDATORS:
                    iv_guide.setImageResource(R.drawable.delegate_validators_cn);
                    break;
                case OBSERVED_WALLET:
                    iv_guide.setImageResource(R.drawable.import_observed_wallet_cn);
                    break;
                case MY_DELEGATE:
                    iv_guide.setImageResource(R.drawable.my_delegate_cn);
                    break;
                case WITHDRAW_OPERATION:
                    iv_guide.setImageResource(R.drawable.withdraw_operation_cn);
                    break;
                default:
                    break;
            }
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


//    private String pageType;
//    private boolean isEnglish;
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setWindowAnimations(R.style.Animation_CommonDialog);
//            dialog.setCanceledOnTouchOutside(true);
//            dialog.setCancelable(true);
//        }
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return createDialog(getActivity());
//
//    }
//
//    private Dialog createDialog(Context context) {
//        final FixedDialog dialog = new FixedDialog(context);
//        return dialog;
//    }
//
//    public static CommonGuideDialogFragment createCommonDialog(String type, boolean isEnglish) {
//        return create(type, isEnglish);
//    }
//
//    private static CommonGuideDialogFragment create(String type, boolean isEnglish) {
//        CommonGuideDialogFragment dialog = new CommonGuideDialogFragment();
//        dialog.pageType = type;
//        dialog.isEnglish = isEnglish;
//        return dialog;
//    }
//
//    class FixedDialog extends AppCompatDialog {
////        @BindView(R.id.iv_guide)
////        ImageView iv_guide;
//        @BindView(R.id.btn_close)
//        Button btn_close;
//
//        public FixedDialog(Context context) {
//            this(context, R.style.CommonDialogStyle);
//        }
//
//        public FixedDialog(Context context, int theme) {
//            super(context, theme);
//            setContentView(R.layout.dialog_fragment_common_guide);
//            ButterKnife.bind(this);
//        }
//    }

}