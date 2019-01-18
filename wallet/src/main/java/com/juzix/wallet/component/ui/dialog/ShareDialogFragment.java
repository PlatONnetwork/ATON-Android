package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.ShareAppInfo;

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

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.gridview)
    GridView gridView;

    Unbinder unbinder;

    public static ShareDialogFragment newInstance(ArrayList<ShareAppInfo> shareAppInfos) {
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Bundle.BUNDLE_SHARE_APPINFO_LIST, shareAppInfos);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
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
                ShareAppInfo shareAppInfo = (ShareAppInfo) parent.getAdapter().getItem(position);
                if (shareAppInfo.actionStart(context)) {
                    dismiss();
                }
            }
        });
    }

    @OnClick({R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
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
}
