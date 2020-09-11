package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.platon.aton.R;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.Wallet;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;

import org.web3j.crypto.Credentials;

public class BackupWalletActivity extends BaseActivity implements View.OnClickListener {

    public static void actionStart(Context context, Wallet walletEntity) {
        Intent intent = new Intent(context, BackupWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_backup_wallet;
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
    public void init() {
        findViewById(R.id.sc_start_backup).setOnClickListener(this);
        findViewById(R.id.ll_skip).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_start_backup:
                showPasswordDialog(getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET));
                break;
            case R.id.ll_skip:
                MainActivity.actionStart(this);
                BackupWalletActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public void showPasswordDialog(Wallet walletEntity) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity, InputWalletPasswordFromType.BACKUPS).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password,Wallet wallet) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, wallet, BackupMnemonicPhraseActivity.BackupMnemonicExport.BACKUP_WALLET_ACTIVITY);
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
