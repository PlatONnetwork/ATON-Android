package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.component.ui.presenter.ManageIndividualWalletPresenter;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

public class ManageIndividualWalletActivity extends MVPBaseActivity<ManageIndividualWalletPresenter> implements ManageIndividualWalletContract.View, View.OnClickListener {

    private final static String TAG = ManageIndividualWalletActivity.class.getSimpleName();

    private BaseDialog   mNameDialog;
    private CustomDialog mFailedDialog;
    private BaseDialog   mPasswordDialog;

    public static void actionStart(Context context, IndividualWalletEntity walletEntity){
        Intent intent = new Intent(context, ManageIndividualWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }

    @Override
    protected ManageIndividualWalletPresenter createPresenter() {
        return new ManageIndividualWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_individual_wallet);
        initView();
        mPresenter.start();
    }

    private void initView(){
        findViewById(R.id.ll_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.manage);
        findViewById(R.id.rl_description).setOnClickListener(this);
        findViewById(R.id.ll_private_key).setOnClickListener(this);
        findViewById(R.id.ll_keystore).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
    }

    @Override
    public IndividualWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showWalletName(String name) {
        ((TextView)findViewById(R.id.tv_name)).setText(name);
    }

    @Override
    public void showWalletAddress(String address) {
        ((TextView) findViewById(R.id.tv_address)).setText(AddressFormatUtil.formatAddress(address));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_left:
                finish();
                break;
            case R.id.rl_description:
                showModifyNameDialog();
                break;
            case R.id.ll_private_key:
                showPasswordDialog(TYPE_EXPORT_PRIVATE_KEY);
                break;
            case R.id.ll_keystore:
                showPasswordDialog(TYPE_EXPORT_KEYSTORE);
                break;
            case R.id.btn_delete:
                showPasswordDialog(TYPE_DELETE_WALLET);
                break;
        }
    }

    @Override
    public void showModifyNameDialog(){
        dimissModifyNameDialog();
        mNameDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mNameDialog.setContentView(R.layout.dialog_change_wallet_name);
        mNameDialog.show();
        EditText etName = mNameDialog.findViewById(R.id.et_name);
        etName.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(12)});
        Button btnConfirm = mNameDialog.findViewById(R.id.btn_confirm);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(!TextUtils.isEmpty(etName.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mNameDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissModifyNameDialog();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.modifyName(etName.getText().toString());
            }
        });
    }

    @Override
    public void dimissModifyNameDialog(){
        if (mNameDialog != null && mNameDialog.isShowing()){
            mNameDialog.dismiss();
            mNameDialog = null;
        }
    }

    @Override
    public void showErrorDialog(String title, String content){
        dimissErrorDialog();
        mFailedDialog = new CustomDialog(getContext());
        mFailedDialog.show(title, content, string(R.string.back), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissErrorDialog();
            }
        });
    }

    @Override
    public void showWalletAvatar(String avatar) {
        ((ImageView) findViewById(R.id.iv_icon)).setImageResource(RUtils.drawable(avatar));
    }

    @Override
    public void dimissErrorDialog(){
        if (mFailedDialog != null && mFailedDialog.isShowing()){
            mFailedDialog.dismiss();
            mFailedDialog = null;
        }
    }

    @Override
    public void showPasswordDialog(int type){
        dimissPasswordDialog();
        mPasswordDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mPasswordDialog.setContentView(R.layout.dialog_verify_wallet_password);
        mPasswordDialog.show();
        final EditText etPassword = mPasswordDialog.findViewById(R.id.et_password);
        Button btnConfirm = mPasswordDialog.findViewById(R.id.btn_confirm);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(etPassword.getText().toString().trim().length() >= 6);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissPasswordDialog();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.validPassword(type, etPassword.getText().toString());
            }
        });
    }

    @Override
    public void dimissPasswordDialog(){
        if (mPasswordDialog != null && mPasswordDialog.isShowing()){
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }
    }
}
