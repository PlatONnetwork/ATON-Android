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

}