package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.Constants;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.utils.CommonUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NodeDetailMoreDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_copy_link)
    TextView tvCopyLink;
    @BindView(R.id.tv_open_browser)
    TextView tvOpenBrowser;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    Unbinder unbinder;


    public static NodeDetailMoreDialogFragment newInstance(String url) {
        NodeDetailMoreDialogFragment dialogFragment = new NodeDetailMoreDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_URL, url);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_node_detail_more, null);
        baseDialog.setContentView(view);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 16f));
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        String url = getArguments().getString(Constants.Extra.EXTRA_URL);

        RxView
                .clicks(tvCopyLink)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        CommonUtil.copyTextToClipboard(getActivity(), url);
                        dismiss();
                    }
                });

        RxView
                .clicks(tvOpenBrowser)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        CommonUtil.openABrowser(getActivity(), url);
                        dismiss();
                    }
                });

        RxView
                .clicks(tvCancel)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        dismiss();
                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
