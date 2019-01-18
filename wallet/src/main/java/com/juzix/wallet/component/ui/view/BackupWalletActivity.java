package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

public class BackupWalletActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private final static String TAG = BackupWalletActivity.class.getSimpleName();
    private BaseDialog mPasswordDialog;
    private CustomDialog mFailedDialog;
    private EditText mEtPassword;
    private Button mBtnConfirm;

    public static void actionStart(Context context, String mnemonic, IndividualWalletEntity walletEntity){
        Intent intent = new Intent(context, BackupWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_MNEMONIC, mnemonic);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_backup_wallet);
        initView();
    }

    private void initView(){
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.backupWallet);
        findViewById(R.id.btn_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_left:
                MainActivity.actionStart(this);
                BackupWalletActivity.this.finish();
                break;
            case R.id.btn_start:
                showPasswordDialog();
                break;
            case R.id.btn_cancel:
                dimissPasswordDialog();
                break;
            case R.id.btn_confirm:
                validWallet(mEtPassword.getText().toString());
                break;
        }
    }

    private void showPasswordDialog(){
        dimissPasswordDialog();
        mPasswordDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mPasswordDialog.setContentView(R.layout.dialog_verify_wallet_password);
        mPasswordDialog.show();
        mEtPassword = mPasswordDialog.findViewById(R.id.et_password);
        mEtPassword.addTextChangedListener(this);
        mPasswordDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mBtnConfirm = mPasswordDialog.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    private void dimissPasswordDialog(){
        if (mPasswordDialog != null && mPasswordDialog.isShowing()){
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }
    }

    private void showErrorDialog(String title, String content){
        dimissErrorDialog();
        mFailedDialog = new CustomDialog(getContext());
        mFailedDialog.show(title, content, string(R.string.back), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissErrorDialog();
            }
        });
    }

    private void dimissErrorDialog(){
        if (mFailedDialog != null && mFailedDialog.isShowing()){
            mFailedDialog.dismiss();
            mFailedDialog = null;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mBtnConfirm.setEnabled(mEtPassword.getText().toString().trim().length() >= 6);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void validWallet(String password) {
        showLoadingDialog();
        new Thread(){
            @Override
            public void run() {
                mHandler.sendEmptyMessage(IndividualWalletManager.getInstance().isValidWallet(getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET), password) ? MSG_SUCCESS : MSG_FAILED);
            }
        }.start();
    }

    private static final int MSG_FAILED = -1;
    private static final int MSG_SUCCESS = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_FAILED:
                    dismissLoadingDialogImmediately();
                    showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips));
                    break;
                case MSG_SUCCESS:
                    dismissLoadingDialogImmediately();
                    BackupMnemonicPhraseActivity.actionStart(getContext(), getIntent().getStringExtra(Constants.Extra.EXTRA_MNEMONIC));
                    BackupWalletActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity.actionStart(this);
            BackupWalletActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
