package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletSecondStepContract;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.component.ui.presenter.CreateSharedWalletSecondStepPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.CustomEditText;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CreateSharedWalletSecondStepActivity extends MVPBaseActivity<CreateSharedWalletSecondStepPresenter> implements CreateSharedWalletSecondStepContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar  commonTitleBar;
    @BindView(R.id.list_shared_owner)
    ListView        listSharedOwner;
    @BindView(R.id.rtv_create_shared_wallet)
    RoundedTextView rtvCreateSharedWallet;

    private Unbinder                                                           unbinder;
    private CommonAdapter<CreateSharedWalletSecondStepContract.ContractEntity> mSharedOwnerListAdapter;
    private BaseDialog                                                         mPasswordDialog;
    private CustomDialog                                                       mFailedDialog;

    @Override
    protected CreateSharedWalletSecondStepPresenter createPresenter() {
        return new CreateSharedWalletSecondStepPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shared_wallet_second_step);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.init();
    }

    private void initViews() {
        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        mSharedOwnerListAdapter = new CommonAdapter<CreateSharedWalletSecondStepContract.ContractEntity>(R.layout.item_create_shared_owner_list, null){
            @Override
            protected void convert(Context context, ViewHolder viewHolder, CreateSharedWalletSecondStepContract.ContractEntity item, int position) {
                viewHolder.setText(R.id.tv_wallet_address_info, context.getString(R.string.member, String.valueOf(position + 1)));
                CustomEditText etWalletName    = viewHolder.getView(R.id.et_wallet_name);
                CustomEditText etWalletAddress = viewHolder.getView(R.id.et_wallet_address);
                ImageView      ivScan          = viewHolder.getView(R.id.iv_scan);
                ImageView      ivAddressBook   = viewHolder.getView(R.id.iv_address_book);
                TextView       tvAddressError  = viewHolder.getView(R.id.tv_address_error);
                int focuse = item.getFocus();
                Object nameTag = etWalletName.getTag();
                Object addressTag = etWalletAddress.getTag();
                if (etWalletName.getTag() instanceof TextChangedListener){
                    etWalletName.removeTextChangedListener((TextWatcher) nameTag);
                }
                if (etWalletAddress.getTag() instanceof TextChangedListener){
                    etWalletAddress.removeTextChangedListener((TextWatcher) addressTag);
                }
                etWalletName.setText(item.getName());
                etWalletAddress.setText(item.getAddress());
                switch (focuse){
                    case CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NAME:
                        etWalletAddress.clearFocus();
                        etWalletName.requestFocus();
                        CharSequence nameText = etWalletName.getText();
                        etWalletName.setSelection(TextUtils.isEmpty(nameText) ? 0 : nameText.length());
                        break;
                    case CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_ADDRESS:
                        etWalletName.clearFocus();
                        etWalletAddress.requestFocus();
                        CharSequence addressText = etWalletAddress.getText();
                        etWalletAddress.setSelection(TextUtils.isEmpty(addressText) ? 0 : addressText.length());
                        break;
                    default:
                        etWalletName.clearFocus();
                        etWalletAddress.clearFocus();
                        break;
                }

                TextChangedListener walletNameListener = new TextChangedListener() {
                    @Override
                    protected void onTextChanged(CharSequence s) {
                        if (TextUtils.isEmpty(s)) {
                            item.setName("");
                        } else {
                            item.setName(String.valueOf(s));
                        }
                        mPresenter.inputName(position, item.getName());
                    }
                };

                TextChangedListener walletAddressListener = new TextChangedListener() {
                    @Override
                    protected void onTextChanged(CharSequence s) {
                        if (TextUtils.isEmpty(s)) {
                            item.setAddress("");
                        } else {
                            item.setAddress(String.valueOf(s));
                        }
                        mPresenter.inputAddress(position, item.getAddress());
                    }
                };

                View.OnClickListener addressBookLisentener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.verifyAddress(position);
                        etWalletAddress.setFocusableInTouchMode(true);
                        etWalletName.setFocusableInTouchMode(false);
                        mPresenter.focusAddress(position);
                        etWalletAddress.requestFocus();
                        etWalletAddress.onWindowFocusChanged(true);
                        mPresenter.selectAddress();
                    }
                };

                View.OnClickListener scanAddressLisentener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.verifyAddress(position);
                        etWalletAddress.setFocusableInTouchMode(true);
                        etWalletName.setFocusableInTouchMode(false);
                        mPresenter.focusAddress(position);
                        etWalletAddress.requestFocus();
                        etWalletAddress.onWindowFocusChanged(true);
                        mPresenter.scanAddress();
                    }
                };

                View.OnTouchListener walletNameTouchListener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            mPresenter.verifyAddress(position);
                            etWalletAddress.setFocusableInTouchMode(false);
                            etWalletName.setFocusableInTouchMode(true);
                            mPresenter.focusName(position);
                            etWalletName.requestFocus();
                            etWalletName.onWindowFocusChanged(true);
                        }
                        return false;
                    }
                };

                View.OnTouchListener walletAddressTouchListener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            mPresenter.verifyAddress(position);
                            etWalletAddress.setFocusableInTouchMode(true);
                            etWalletName.setFocusableInTouchMode(false);
                            mPresenter.focusAddress(position);
                            etWalletAddress.requestFocus();
                            etWalletAddress.onWindowFocusChanged(true);
                        }
                        return false;
                    }
                };

                etWalletName.setEnabled(item.isEnabled());
                etWalletAddress.setEnabled(item.isEnabled());
                ivAddressBook.setEnabled(item.isEnabled());
                ivScan.setEnabled(item.isEnabled());
                String errorMsg = item.getErrorMsg();
                tvAddressError.setText(errorMsg);
                tvAddressError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);

                etWalletName.setOnTouchListener(walletNameTouchListener);
                etWalletAddress.setOnTouchListener(walletAddressTouchListener);
                ivAddressBook.setOnClickListener(addressBookLisentener);
                ivScan.setOnClickListener(scanAddressLisentener);
                etWalletName.addTextChangedListener(walletNameListener);
                etWalletAddress.addTextChangedListener(walletAddressListener);
                etWalletName.setTag(walletNameListener);
                etWalletAddress.setTag(walletAddressListener);
            }
            @Override
            public void updateItemView(Context context, int position, ViewHolder viewHolder) {
                if (mDatas != null && mDatas.size() > position) {
                    convert(context, viewHolder, mDatas.get(position), position);
                }
            }
        };
        listSharedOwner.setAdapter(mSharedOwnerListAdapter);
        rtvCreateSharedWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createContract();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.REQUEST_CODE_GET_ADDRESS:
                    AddressEntity addressEntity = data.getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
                    if (addressEntity != null) {
                        mPresenter.updateAddress(addressEntity.getAddress());
                    }
                    break;
                case Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE:
                    String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                    if (JZWalletUtil.isValidAddress(address)) {
                        mPresenter.updateAddress(address);
                    } else {
                        ToastUtil.showLongToast(this, string(R.string.unrecognized));
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartForResult(Activity context, int requestCode,
                                            int sharedOwners, int requiredSignatures, String walletName, IndividualWalletEntity walletEntity) {
        Intent intent = new Intent(context, CreateSharedWalletSecondStepActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_SHARED_OWNERS, sharedOwners);
        intent.putExtra(Constants.Extra.EXTRA_REQUIRED_SIGNATURES, requiredSignatures);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_NAME, walletName);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public int getSharedOwnersFromIntent() {
        return getIntent().getIntExtra(Constants.Extra.EXTRA_SHARED_OWNERS, 0);
    }

    @Override
    public int getRequiredSignaturesFromIntent() {
        return getIntent().getIntExtra(Constants.Extra.EXTRA_REQUIRED_SIGNATURES, 0);
    }

    @Override
    public String getWalletNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_WALLET_NAME);
    }

    @Override
    public IndividualWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showOwnerList(List<CreateSharedWalletSecondStepContract.ContractEntity> ownerEntityList) {
        mSharedOwnerListAdapter.notifyDataChanged(ownerEntityList);
    }

    @Override
    public void updateOwner(int position) {
        mSharedOwnerListAdapter.updateItem(this, listSharedOwner,mSharedOwnerListAdapter.getItem(position));
    }

    @Override
    public void setCreateSharedWalletBtnEnable(boolean enable) {
        rtvCreateSharedWallet.setEnabled(enable);
    }

    @Override
    public void showPasswordDialog(String gasPrice, String gasLimit){
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
                mPresenter.validPassword(etPassword.getText().toString(), gasPrice, gasLimit);
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
    public void dimissErrorDialog(){
        if (mFailedDialog != null && mFailedDialog.isShowing()){
            mFailedDialog.dismiss();
            mFailedDialog = null;
        }
    }
}
