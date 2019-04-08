package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.ManageIndividualWalletPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import org.web3j.crypto.Credentials;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ManageIndividualWalletActivity extends MVPBaseActivity<ManageIndividualWalletPresenter> implements ManageIndividualWalletContract.View {

    private final static String TAG = ManageIndividualWalletActivity.class.getSimpleName();

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

    private Unbinder unbinder;

    @Override
    protected ManageIndividualWalletPresenter createPresenter() {
        return new ManageIndividualWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_individual_wallet);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.showIndividualWalletInfo();
    }

    private void initView() {
        commonTitleBar.setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();

            }
        });
    }

    @OnClick({R.id.rl_rename, R.id.rl_private_key, R.id.rl_keystore, R.id.rl_backup, R.id.tv_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_rename:
                showModifyNameDialog("");
                break;
            case R.id.rl_private_key:
                showPasswordDialog(TYPE_EXPORT_PRIVATE_KEY, getWalletEntityFromIntent());
                break;
            case R.id.rl_keystore:
                showPasswordDialog(TYPE_EXPORT_KEYSTORE, getWalletEntityFromIntent());
                break;
            case R.id.rl_backup:
                mPresenter.backup();
                break;
            case R.id.tv_delete:
                showPasswordDialog(TYPE_DELETE_WALLET, getWalletEntityFromIntent());
                break;
        }
    }

    @Override
    public void showWalletName(String name) {
        tvReName.setText(name);
        commonTitleBar.setTitle(name);
    }

    @Override
    public void showWalletAddress(String address) {
        tvAddress.setText(AddressFormatUtil.formatAddress(address));
    }

    @Override
    public void showModifyNameDialog(String name) {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.rename_wallet), name, InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 12) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(ManageIndividualWalletActivity.this, R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showModifyNameDialog(text);
                                }
                            }).show(getSupportFragmentManager(), "showTips");
                } else {
                    if (mPresenter.isExists(text)) {
                        showLongToast(string(R.string.wallet_name_exists));
                    } else {
                        mPresenter.modifyName(text);
                    }
                }
            }
        }).show(getSupportFragmentManager(), "showModifyName");

    }

    @Override
    public void showErrorDialog(String title, String content, int type, IndividualWalletEntity walletEntity) {
        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), title, content, string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                showPasswordDialog(type, walletEntity);
            }
        }).show(getSupportFragmentManager(), "showError");
    }

    @Override
    public void showWalletAvatar(String avatar) {

    }

    @Override
    public void showPasswordDialog(int type, IndividualWalletEntity walletEntity) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                mPresenter.validPassword(type, credentials);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void enableBackup(boolean enabled) {
        llBackup.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void enableDelete(boolean enabled) {
        tvDelete.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public IndividualWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, IndividualWalletEntity walletEntity) {
        Intent intent = new Intent(context, ManageIndividualWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
}
