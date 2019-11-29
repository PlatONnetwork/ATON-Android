package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.ShareAppInfo;
import com.juzix.wallet.utils.DensityUtil;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ShareDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.gridview)
    GridView gridView;

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
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 24));
        setyOffset(DensityUtil.dp2px(getContext(), 16));
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
                if (mShareItemClickListener != null){
                    mShareItemClickListener.onShareItemClick(ShareDialogFragment.this,(ShareAppInfo) parent.getAdapter().getItem(position));
                }
            }
        });
    }

    @OnClick({R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;

        }
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
