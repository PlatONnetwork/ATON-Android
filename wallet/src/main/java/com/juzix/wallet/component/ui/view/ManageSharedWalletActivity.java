package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.component.ui.presenter.ManageSharedWalletPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ListViewForScrollView;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class ManageSharedWalletActivity extends MVPBaseActivity<ManageSharedWalletPresenter> implements ManageSharedWalletContract.View, View.OnClickListener {

    private CommonAdapter<OwnerEntity> mAdapter;
    private CustomDialog                          mErrorDialog;
    private BaseDialog                            mModifyWalletNameDialog;
    private BaseDialog                            mModifyMemberNameDialog;
    private BaseDialog                            mPasswordDialog;


    public static void actionStart(Context context, SharedWalletEntity walletEntity) {
        Intent intent = new Intent(context, ManageSharedWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }

    @Override
    protected ManageSharedWalletPresenter createPresenter() {
        return new ManageSharedWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shared_wallet);
        initView();
        mPresenter.start();
    }

    @Override
    public SharedWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_modify_member_name:
                showModifyWalletNameDialog();
                break;
            case R.id.rtv_delete_wallet:
                mPresenter.deleteAction(TYPE_DELETE_WALLET);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        ((CommonTitleBar)findViewById(R.id.commonTitleBar)).setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        ListViewForScrollView lvMember = findViewById(R.id.list_member);
        mAdapter = new CommonAdapter<OwnerEntity>(R.layout.item_manage_shared_wallet_members, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, OwnerEntity item, int position) {
                viewHolder.setText(R.id.tv_member_name, item.getName());
                viewHolder.setText(R.id.tv_member_address, AddressFormatUtil.formatAddress(item.getPrefixAddress()));
                viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedWalletEntity walletEntity = getWalletEntityFromIntent();
                        if (!item.getPrefixAddress().equals(walletEntity.getPrefixAddress())) {
                            showModifyMemberNameDialog(position);
                        }
                    }
                });
            }
        };
        lvMember.setAdapter(mAdapter);
        findViewById(R.id.layout_modify_member_name).setOnClickListener(this);
        findViewById(R.id.rtv_delete_wallet).setOnClickListener(this);
    }

    @Override
    public void showWallet(SharedWalletEntity walletEntity) {
        ((ImageView) findViewById(R.id.iv_wallet_pic)).setImageResource(RUtils.drawable(walletEntity.getAvatar()));
        ((TextView) findViewById(R.id.tv_wallet_name)).setText(walletEntity.getName());
        ((TextView) findViewById(R.id.tv_wallet_address)).setText(AddressFormatUtil.formatAddress(walletEntity.getPrefixContractAddress()));
    }

    @Override
    public void showMember(ArrayList<OwnerEntity> addressEntityList) {
        mAdapter.notifyDataChanged(addressEntityList);
    }

    @Override
    public void showOwner(String individualWalletName, String individualWalletAddress) {
        View llOwner = findViewById(R.id.ll_owner);
        ((TextView) llOwner.findViewById(R.id.tv_member_name)).setText(individualWalletName);
        ((TextView) llOwner.findViewById(R.id.tv_member_address)).setText(AddressFormatUtil.formatAddress(individualWalletAddress));
    }

    @Override
    public void showErrorDialog(String title, String content) {
        dimissErrorDialog();
        mErrorDialog = new CustomDialog(getContext());
        mErrorDialog.show(title, content, string(R.string.back), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissErrorDialog();
            }
        });
    }

    @Override
    public void dimissErrorDialog() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
            mErrorDialog = null;
        }
    }

    @Override
    public void showModifyWalletNameDialog() {
        dimissModifyWalletNameDialog();
        mModifyWalletNameDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mModifyWalletNameDialog.setContentView(R.layout.dialog_change_wallet_name);
        mModifyWalletNameDialog.show();
        EditText etName     = mModifyWalletNameDialog.findViewById(R.id.et_name);
        Button   btnConfirm = mModifyWalletNameDialog.findViewById(R.id.btn_confirm);
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
        mModifyWalletNameDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissModifyWalletNameDialog();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.modifyWalletName(etName.getText().toString());
            }
        });
    }

    @Override
    public void dimissModifyWalletNameDialog() {
        if (mModifyWalletNameDialog != null && mModifyWalletNameDialog.isShowing()) {
            mModifyWalletNameDialog.dismiss();
            mModifyWalletNameDialog = null;
        }
    }

    @Override
    public void dimissModifyMemberNameDialog() {
        if (mModifyMemberNameDialog != null && mModifyMemberNameDialog.isShowing()) {
            mModifyMemberNameDialog.dismiss();
            mModifyMemberNameDialog = null;
        }
    }

    @Override
    public void showModifyMemberNameDialog(int memberIndex) {
        dimissModifyMemberNameDialog();
        mModifyMemberNameDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mModifyMemberNameDialog.setContentView(R.layout.dialog_change_wallet_name);
        mModifyMemberNameDialog.show();
        ((TextView) mModifyMemberNameDialog.findViewById(R.id.tv_title)).setText(R.string.changeMemberName);
        EditText etName     = mModifyMemberNameDialog.findViewById(R.id.et_name);
        Button   btnConfirm = mModifyMemberNameDialog.findViewById(R.id.btn_confirm);
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
        mModifyMemberNameDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissModifyMemberNameDialog();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.modifyMemberName(memberIndex, etName.getText().toString());
            }
        });
    }

    @Override
    public void showPasswordDialog(int type, int index) {
        dimissPasswordDialog();
        mPasswordDialog = new BaseDialog(this, R.style.Dialog_FullScreen);
        mPasswordDialog.setContentView(R.layout.dialog_verify_wallet_password);
        mPasswordDialog.show();
        final EditText etPassword = mPasswordDialog.findViewById(R.id.et_password);
        Button         btnConfirm = mPasswordDialog.findViewById(R.id.btn_confirm);
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
                mPresenter.validPassword(type, etPassword.getText().toString(), index);
            }
        });
    }

    @Override
    public void dimissPasswordDialog() {
        if (mPasswordDialog != null && mPasswordDialog.isShowing()) {
            mPasswordDialog.dismiss();
            mPasswordDialog = null;
        }
    }
}
