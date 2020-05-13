package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.CommonAdapter;
import com.platon.aton.component.adapter.base.ViewHolder;
import com.platon.aton.entity.ShareAppInfo;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ShareDialogFragment extends BaseDialogFragment {


    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.iv_close)
    ImageView ivClose;

    Unbinder unbinder;

    OnShareItemClickListener mShareItemClickListener;

    public static ShareDialogFragment newInstance(ArrayList<ShareAppInfo> shareAppInfos) {
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Bundle.BUNDLE_SHARE_APPINFO_LIST, shareAppInfos);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public ShareDialogFragment setOnShareItemClickListener(OnShareItemClickListener shareItemClickListener) {
        this.mShareItemClickListener = shareItemClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_share, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        List<ShareAppInfo> shareAppInfoList = getArguments().getParcelableArrayList(Constants.Bundle.BUNDLE_SHARE_APPINFO_LIST);

        gridView.setAdapter(new CommonAdapter<ShareAppInfo>(R.layout.item_share, shareAppInfoList) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, ShareAppInfo item, int position) {
                viewHolder.setText(R.id.tv_share, getString(item.getTitleRes()));
                viewHolder.setImageResource(R.id.iv_icon, item.getIconRes());
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mShareItemClickListener != null) {
                    mShareItemClickListener.onShareItemClick(ShareDialogFragment.this, (ShareAppInfo) parent.getAdapter().getItem(position));
                }
            }
        });

        RxView.clicks(ivClose)
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
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnShareItemClickListener {

        void onShareItemClick(BaseDialogFragment fragment, ShareAppInfo shareAppInfo);
    }
}
