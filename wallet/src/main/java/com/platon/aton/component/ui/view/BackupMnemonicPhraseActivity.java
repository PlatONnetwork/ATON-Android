package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.ShadowContainer;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;

/**
 * @author ziv
 */
public class BackupMnemonicPhraseActivity extends BaseActivity {

    @IntDef({
            BackupMnemonicExport.BACKUP_WALLET_ACTIVITY,
            BackupMnemonicExport.MAIN_ACTIVITY
    })
    public @interface BackupMnemonicExport {

        /**
         * 备份钱包入口
         */
        int BACKUP_WALLET_ACTIVITY = 0;
        /**
         * 首页
         */
        int MAIN_ACTIVITY = 1;
    }

    private CommonTitleBar mCtb;
    private ShadowContainer mShadowContainer;

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_mnemonic_phrase;
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
        showTipsDialog();
        initView();
        Wallet walletEntity = getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
        setMnemonic(JZWalletUtil.decryptMnenonic(walletEntity.getKey(), walletEntity.getMnemonic(), getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD)));
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("allBtn")
    private void initView() {

        mCtb = findViewById(R.id.commonTitleBar);
        mShadowContainer = findViewById(R.id.sc_next);

        mCtb.setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        mCtb.setLeftTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        mShadowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationMnemonicActivity.actionStart(getContext(), getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD),
                        getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET), getIntent().getIntExtra(Constants.Extra.EXTRA_TYPE, 0));
                finish();
            }
        });
    }

    private void exit() {
        CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                string(R.string.backup_exit_tips),
                string(R.string.confirm),
                new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                        if (getIntent().getIntExtra(Constants.Extra.EXTRA_TYPE, 0) == 0) {
                            MainActivity.actionStart(BackupMnemonicPhraseActivity.this);
                        }
                        BackupMnemonicPhraseActivity.this.finish();
                    }
                }, string(R.string.cancel),
                new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                    }
                }).show(getSupportFragmentManager(), "showTips");
    }

    private void setMnemonic(String text) {
        String[] words = text.split(" ");
        if (words != null && words.length == 12) {
            findViewById(R.id.sc_next).setEnabled(true);
            ((TextView) findViewById(R.id.tv_mnemonic1)).setText(words[0]);
            ((TextView) findViewById(R.id.tv_mnemonic2)).setText(words[1]);
            ((TextView) findViewById(R.id.tv_mnemonic3)).setText(words[2]);
            ((TextView) findViewById(R.id.tv_mnemonic4)).setText(words[3]);
            ((TextView) findViewById(R.id.tv_mnemonic5)).setText(words[4]);
            ((TextView) findViewById(R.id.tv_mnemonic6)).setText(words[5]);
            ((TextView) findViewById(R.id.tv_mnemonic7)).setText(words[6]);
            ((TextView) findViewById(R.id.tv_mnemonic8)).setText(words[7]);
            ((TextView) findViewById(R.id.tv_mnemonic9)).setText(words[8]);
            ((TextView) findViewById(R.id.tv_mnemonic10)).setText(words[9]);
            ((TextView) findViewById(R.id.tv_mnemonic11)).setText(words[10]);
            ((TextView) findViewById(R.id.tv_mnemonic12)).setText(words[11]);
        } else {
            findViewById(R.id.sc_next).setEnabled(false);
        }
    }

    private void showTipsDialog() {
        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), string(R.string.donotScreenshot), string(R.string.backupMnemonicResume), string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                if (fragment != null) {
                    fragment.dismiss();
                }
            }
        }).show(getSupportFragmentManager(), "showTips");
    }

    public static void actionStart(Context context, String password, Wallet walletEntity, @BackupMnemonicExport int entrance) {
        Intent intent = new Intent(context, BackupMnemonicPhraseActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_PASSWORD, password);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        intent.putExtra(Constants.Extra.EXTRA_TYPE, entrance);
        context.startActivity(intent);
    }
}
