package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.IndividualWalletEntity;

import org.web3j.crypto.Credentials;

public class BackupWalletActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = BackupWalletActivity.class.getSimpleName();

    public static void actionStart(Context context, IndividualWalletEntity walletEntity){
        Intent intent = new Intent(context, BackupWalletActivity.class);
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
        findViewById(R.id.sc_start_backup).setOnClickListener(this);
        findViewById(R.id.ll_skip).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sc_start_backup:
                showPasswordDialog(getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET));
                break;
            case R.id.ll_skip:
                MainActivity.actionStart(this);
                BackupWalletActivity.this.finish();
                break;
        }
    }

    public void showPasswordDialog(IndividualWalletEntity walletEntity) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity);
                BackupWalletActivity.this.finish();
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

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
