package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ManageWalletContract;
import com.platon.aton.component.ui.dialog.CommonEditDialogFragment;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.presenter.ManageWalletPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletDepth;
import com.platon.aton.utils.CommonUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;

import org.web3j.crypto.Credentials;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ManageWalletActivity extends BaseActivity<ManageWalletContract.View, ManageWalletPresenter> implements ManageWalletContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_rename)
    TextView tvReName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_private_key)
    RelativeLayout llPrivateKey;
    @BindView(R.id.rl_keystore)
    RelativeLayout llKeystore;
    @BindView(R.id.rl_backup)
    RelativeLayout llBackup;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.rl_rename)
    ConstraintLayout rename;
    @BindView(R.id.iv_copy_wallet_address)
    ImageView ivCopyAddress;
    private Unbinder unbinder;
    @BindView(R.id.rl_wallet_address)
    RelativeLayout rl_wallet_address;

    @Override
    public ManageWalletPresenter createPresenter() {
        return new ManageWalletPresenter();
    }

    @Override
    public ManageWalletContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        getPresenter().init(getWalletEntityFromIntent());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manager_wallet;
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().showWalletInfo();
    }

    @OnClick({R.id.rl_rename, R.id.rl_private_key, R.id.rl_keystore, R.id.rl_backup, R.id.tv_delete, R.id.iv_copy_wallet_address, R.id.rl_wallet_address})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_rename:
                showModifyNameDialog("");
                break;
            case R.id.rl_private_key:
                showPasswordDialog(TYPE_EXPORT_PRIVATE_KEY, getPresenter().getWalletData());
                break;
            case R.id.rl_keystore:
                showPasswordDialog(TYPE_EXPORT_KEYSTORE, getPresenter().getWalletData());
                break;
            case R.id.rl_backup:
                getPresenter().backup();
                break;
            //删除钱包按钮
            case R.id.tv_delete:
                if (TextUtils.isEmpty(getWalletEntityFromIntent().getKey()) && getWalletEntityFromIntent().getDepth() == WalletDepth.DEPTH_ZERO) {
                    getPresenter().deleteObservedWallet();
                } else {
                    showPasswordDialog(TYPE_DELETE_WALLET, getWalletEntityFromIntent());
                }
                break;
            case R.id.iv_copy_wallet_address:
            case R.id.rl_wallet_address:
                CommonUtil.copyTextToClipboard(this, tvAddress.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void showWalletName(String name) {
        tvReName.setText(name);
        commonTitleBar.setTitle(name);
    }

    //显示钱包基本信息
    @Override
    public void showWalletInfo(Wallet wallet) {
        commonTitleBar.setTitle(wallet.getName());
        tvReName.setText(wallet.getName());
        tvAddress.setText(wallet.getPrefixAddress());

        if(wallet.isHD()){
            llPrivateKey.setVisibility(View.VISIBLE);
            llKeystore.setVisibility(View.VISIBLE);
            Wallet rootWallet = WalletManager.getInstance().getWalletInfoByUuid(wallet.getParentId());
            tvDelete.setVisibility(rootWallet.isDeletedEnabled() ? View.VISIBLE : View.GONE);
            //是否可以备份
            llBackup.setVisibility(View.GONE);
        }else{
            if (TextUtils.isEmpty(wallet.getKey())) {
                llPrivateKey.setVisibility(View.GONE);
                llKeystore.setVisibility(View.GONE);
            }
            tvDelete.setVisibility(wallet.isDeletedEnabled() ? View.VISIBLE : View.GONE);
            //是否可以备份
            if(wallet.getMnemonic() != null && !wallet.getMnemonic().equals("")){//钱包通过APP创建的
                llBackup.setVisibility(View.VISIBLE);
            }else{
                llBackup.setVisibility(!wallet.isBackedUp() ? View.VISIBLE : View.GONE);
            }

        }
    }

    @Override
    public void showModifyNameDialog(String name) {
        CommonEditDialogFragment commonEditDialogFragment = CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.rename_wallet), name, InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 20) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(ManageWalletActivity.this, R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showModifyNameDialog(text);
                                }
                            }).show(getSupportFragmentManager(), "showTips");
                } else {
                    if (getPresenter().isExists(text)) {
                        showLongToast(string(R.string.wallet_name_exists));
                    } else {
                        Wallet wallet = getPresenter().getWalletData();
                        if(wallet.isHD()){
                            getPresenter().modifyHDName(text);
                        }else{
                            getPresenter().modifyName(text);
                        }

                    }
                }
            }
        });
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentStarted(fm, f);
                if (f.getClass() == CommonEditDialogFragment.class) {
                    CommonEditDialogFragment.FixedDialog fixedDialog = (CommonEditDialogFragment.FixedDialog) ((CommonEditDialogFragment) f).getDialog();
                    fixedDialog.etInputInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                }
            }
        }, false);
        commonEditDialogFragment.show(getSupportFragmentManager(), "showModifyName");

    }

    @Override
    public void showErrorDialog(String title, String content, int type, Wallet walletEntity) {
        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), title, content, string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                showPasswordDialog(type, walletEntity);
            }
        }).show(getSupportFragmentManager(), "showError");
    }


    @Override
    public void showPasswordDialog(int type, Wallet walletEntity) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity, InputWalletPasswordFromType.TRANSACTION, type == TYPE_DELETE_WALLET ? string(R.string.msg_delete_wallet) : null).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                //getPresenter().validPassword(type, credentials);
            }
        }).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password, Wallet wallet) {
                getPresenter().init(wallet);
                getPresenter().validPassword(type, credentials);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public Wallet getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, Wallet walletEntity) {
        Intent intent = new Intent(context, ManageWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
}
