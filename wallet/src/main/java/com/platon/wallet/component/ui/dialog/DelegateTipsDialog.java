package com.platon.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platon.wallet.R;
import com.platon.wallet.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DelegateTipsDialog extends DialogFragment {
    private String mTitleOne;
    private String mContentOne;
    private String mTitleTwo;
    private String mContentTwo;
    private String mTitleThree;
    private String mContentThree;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setWindowAnimations(R.style.Animation_CommonDialog);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(getActivity(), mTitleOne, mContentOne, mTitleTwo, mContentTwo, mTitleThree, mContentThree);

    }

    public static DelegateTipsDialog createWithTitleAndContentDialog(String titleOne, String contentOne, String titleTwo, String contentTwo, String titleThree, String contentThree) {
        return create(titleOne, contentOne, titleTwo, contentTwo, titleThree, contentThree);
    }

    private static DelegateTipsDialog create(String titleOne, String contentOne, String titleTwo, String contentTwo, String titleThree, String contentThree) {
        DelegateTipsDialog dialog = new DelegateTipsDialog();
        dialog.mTitleOne = titleOne;
        dialog.mContentOne = contentOne;
        dialog.mTitleTwo = titleTwo;
        dialog.mContentTwo = contentTwo;
        dialog.mContentThree = contentThree;
        dialog.mTitleThree = titleThree;
        return dialog;
    }

    private FixedDialog createDialog(Context context, CharSequence titleOne, CharSequence contentOne, CharSequence titleTwo, CharSequence contentTwo, CharSequence titleThree, CharSequence contentThree) {

        final FixedDialog dialog = new FixedDialog(context);

        dialog.titleOne.setVisibility(TextUtils.isEmpty(titleOne) ? View.GONE : View.VISIBLE);
        dialog.titleTwo.setVisibility(TextUtils.isEmpty(titleTwo) ? View.GONE : View.VISIBLE);
        dialog.titleThree.setVisibility(TextUtils.isEmpty(titleThree) ? View.GONE : View.VISIBLE);

        dialog.contentOne.setVisibility(TextUtils.isEmpty(contentOne) ? View.GONE : View.VISIBLE);
        dialog.contentTwo.setVisibility(TextUtils.isEmpty(contentTwo) ? View.GONE : View.VISIBLE);
        dialog.contentThree.setVisibility(TextUtils.isEmpty(contentThree) ? View.GONE : View.VISIBLE);

        if (!TextUtils.isEmpty(titleOne)) {
            dialog.titleOne.setText(titleOne);
        }

        if (!TextUtils.isEmpty(titleTwo)) {
            dialog.titleTwo.setText(titleTwo);
        }
        if (!TextUtils.isEmpty(titleThree)) {
            dialog.titleThree.setText(titleThree);
        }
        if (!TextUtils.isEmpty(contentOne)) {
            dialog.contentOne.setText(contentOne);
        }

        if (!TextUtils.isEmpty(contentTwo)) {
            dialog.contentTwo.setText(contentTwo);
        }

        if (!TextUtils.isEmpty(contentThree)) {
            dialog.contentThree.setText(contentThree);
        }
        if(TextUtils.isEmpty(contentTwo) && TextUtils.isEmpty(contentThree)){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dialog.contentOne.getLayoutParams();
            params.setMargins(DensityUtil.dp2px(context,7),0,DensityUtil.dp2px(context,17), DensityUtil.dp2px(context,30));
            dialog.contentOne.setLayoutParams(params);
        }
        return dialog;
    }

    class FixedDialog extends AppCompatDialog {
        @BindView(R.id.text_title_one)
        TextView titleOne;
        @BindView(R.id.tv_content_one)
        TextView contentOne;
        @BindView(R.id.tv_title_two)
        TextView titleTwo;
        @BindView(R.id.tv_content_two)
        TextView contentTwo;
        @BindView(R.id.tv_title_three)
        TextView titleThree;
        @BindView(R.id.tv_content_three)
        TextView contentThree;

        public FixedDialog(Context context) {
            this(context, R.style.CommonDialogStyle);
        }

        public FixedDialog(Context context, int theme) {
            super(context, theme);
            setContentView(R.layout.dialog_delegate_tips);
            ButterKnife.bind(this);
        }
    }
}
