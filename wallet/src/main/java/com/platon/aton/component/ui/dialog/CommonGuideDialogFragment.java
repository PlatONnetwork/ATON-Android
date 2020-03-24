package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.entity.GuideType;
import com.platon.aton.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommonGuideDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_guide)
    ImageView ivGuide;

    Unbinder unbinder;

    public static CommonGuideDialogFragment newInstance(@GuideType int guideType) {
        CommonGuideDialogFragment dialogFragment = new CommonGuideDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Extra.EXTRA_TYPE, guideType);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_common_guide, null);
        baseDialog.setContentView(view);
        setFullHeightEnable(true);
        setFullWidthEnable(true);
        setCancelable(false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return baseDialog;
    }

    @Override
    protected int getThemeResId() {
        return R.style.Dialog_FullScreen;
    }

    private void initViews() {
        @GuideType int guideType = getArguments().getInt(Constants.Extra.EXTRA_TYPE);
        ivGuide.setImageResource(getGuideImageRes(guideType));

        RxView
                .clicks(ivGuide)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        dismiss();
                        if (dissmissListener != null) {
                            dissmissListener.onDismiss();
                        }
                    }
                });
    }


    private int getGuideImageRes(@GuideType int guideType) {

        TypedArray typedArray = getResources().obtainTypedArray(R.array.guide_image);

        int guideImageRes = typedArray.getResourceId(guideType, 0);

        typedArray.recycle();

        return guideImageRes;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
