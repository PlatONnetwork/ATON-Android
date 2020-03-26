package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseFragment;
import com.platon.framework.base.BaseLazyFragment;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;

public class ExportKeystoreFragment extends BaseLazyFragment implements View.OnClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_export_keystore;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init(View rootView) {
        ((TextView) rootView.findViewById(R.id.tv_keystore)).setText(getActivity().getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD));
        rootView.findViewById(R.id.btn_copy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy:
                Activity activity = getActivity();
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", activity.getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD));
                cm.setPrimaryClip(clipData);
                showLongToast(string(R.string.textCopiedTips));
                break;
            default:
                break;
        }
    }
}
