package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.widget.ShadowDrawable;

public class BackupMnemonicPhraseActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = BackupMnemonicPhraseActivity.class.getSimpleName();
    private BaseDialog mMnemonicDialog;
    private TextView mTvMnemonic;

    public static void actionStart(Context context, String mnemonic) {
        Intent intent = new Intent(context, BackupMnemonicPhraseActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_MNEMONIC, mnemonic);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_mnemonic_phrase);
        showPasswordDialog();
        initView();
        mTvMnemonic.setText(getIntent().getStringExtra(Constants.Extra.EXTRA_MNEMONIC));
    }

    private void initView() {
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.backupWallet);
        findViewById(R.id.btn_next).setOnClickListener(this);
        mTvMnemonic = findViewById(R.id.tv_mnemonic);
        int shapeRadius = AndroidUtil.dip2px(getContext(), 4);
        int shadowRadius = AndroidUtil.dip2px(getContext(), 4);
        ShadowDrawable.setShadowDrawable(mTvMnemonic,
                ContextCompat.getColor(this, R.color.color_1f2841),
                shapeRadius,
                ContextCompat.getColor(this, R.color.color_020527),
                shadowRadius, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                BackupMnemonicPhraseActivity.this.finish();
                MainActivity.actionStart(this);
                break;
            case R.id.btn_next:
                VerificationMnemonicActivity.actionStart(getContext(), getIntent().getStringExtra(Constants.Extra.EXTRA_MNEMONIC));
                BackupMnemonicPhraseActivity.this.finish();
                break;
            case R.id.btn_understood:
                dimissPasswordDialog();
                break;
        }
    }

    private void showPasswordDialog() {
        dimissPasswordDialog();
        mMnemonicDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mMnemonicDialog.setContentView(R.layout.dialog_backup_mnemonic_phrase);
        mMnemonicDialog.show();
        mMnemonicDialog.findViewById(R.id.btn_understood).setOnClickListener(this);
    }

    private void dimissPasswordDialog() {
        if (mMnemonicDialog != null && mMnemonicDialog.isShowing()) {
            mMnemonicDialog.dismiss();
            mMnemonicDialog = null;
        }
    }
}
