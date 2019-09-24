package com.juzix.wallet.component.ui.view;

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

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseFragment;

public class ExportPrivateKeyFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export_private_key, container, false);
        view.findViewById(R.id.btn_copy).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.tv_private_key)).setText(getActivity().getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD));
        return view;
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
        }
    }
}
