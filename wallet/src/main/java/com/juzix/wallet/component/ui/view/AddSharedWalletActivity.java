package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.AddSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletDialogFragment;
import com.juzix.wallet.component.ui.presenter.AddSharedWalletPresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AddSharedWalletActivity extends MVPBaseActivity<AddSharedWalletPresenter> implements AddSharedWalletContract.View {

    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.layout_change_wallet)
    RelativeLayout layoutChangeWallet;
    @BindView(R.id.iv_wallet_avatar)
    ImageView    ivWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView     tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView     tvWalletAddress;
    @BindView(R.id.tv_wallet_address_info)
    TextView     tvWalletAddressInfo;
    @BindView(R.id.et_wallet_address)
    EditText     etWalletAddress;
    @BindView(R.id.iv_address_book)
    ImageView    ivAddressBook;
    @BindView(R.id.iv_address_scan)
    ImageView    ivAddressScan;
    @BindView(R.id.sbtn_add_shared_wallet)
    ShadowButton btnAddSharedWallet;
    @BindView(R.id.tv_wallet_name_error)
    TextView     tvWalletNameError;
    @BindView(R.id.tv_wallet_address_error)
    TextView     tvWalletAddressError;

    private Unbinder unbinder;

    @Override
    protected AddSharedWalletPresenter createPresenter() {
        return new AddSharedWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_wallet);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.init();
    }

    private void initViews() {

        setAddSharedWalletBtnEnable(false);

        etWalletAddress.addTextChangedListener(new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                mPresenter.checkAddSharedWalletBtnEnable();
            }
        });
        etWalletAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    mPresenter.checkWalletAddress(etWalletAddress.getText().toString());
                }
            }
        });

        etWalletName.addTextChangedListener(new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                mPresenter.checkAddSharedWalletBtnEnable();
            }
        });
        etWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mPresenter.checkWalletName(getWalletName());
                }
            }
        });
    }

    @OnClick({R.id.layout_change_wallet, R.id.iv_address_book, R.id.sbtn_add_shared_wallet, R.id.iv_address_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_change_wallet:
                mPresenter.showSelectOwnerDialogFragment();
                break;
            case R.id.iv_address_book:
                SelectAddressActivity.actionStartForResult(this, Constants.Action.ACTION_GET_ADDRESS, Constants.RequestCode.REQUEST_CODE_GET_ADDRESS);
                break;
            case R.id.iv_address_scan:
                requestPermission(AddSharedWalletActivity.this, 100, new PermissionConfigure.PermissionCallback() {
                    @Override
                    public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                        ScanQRCodeActivity.startActivityForResult(AddSharedWalletActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onHasPermission(int what) {
                        ScanQRCodeActivity.startActivityForResult(AddSharedWalletActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onFail(int what, @NonNull List<String> deniedPermissions) {

                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.sbtn_add_shared_wallet:
                mPresenter.addWallet(getWalletName(), getWalletAddress());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.REQUEST_CODE_GET_ADDRESS:
                    AddressEntity addressEntity = data.getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
                    if (addressEntity != null) {
//                        etWalletAddress.setText(AddressFormatUtil.formatAddress(addressEntity.getPrefixAddress()));
                        etWalletAddress.setText(addressEntity.getAddress());
                    }
                    break;
                case Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE:
                    String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                    if (JZWalletUtil.isValidAddress(address)) {
//                        etWalletAddress.setText(AddressFormatUtil.formatAddress(data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA)));
                        etWalletAddress.setText(address);
                    } else {
                        ToastUtil.showLongToast(this, string(R.string.unrecognized));
                    }
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddSharedWalletActivity.class);
        context.startActivity(intent);
    }

//    @Override
//    public void onItemClick(IndividualWalletEntity walletEntity) {
//        mPresenter.updateSelectOwner(walletEntity);
//    }

    @Override
    public void setSelectOwner(IndividualWalletEntity walletEntity) {
        ivWalletAvatar.setImageResource(RUtils.drawable(walletEntity.getAvatar()));
        tvWalletName.setText(walletEntity.getName());
        tvWalletAddress.setText(AddressFormatUtil.formatAddress(walletEntity.getPrefixAddress()));
    }

    @Override
    public void showWalletNameError(String errorMsg) {
        tvWalletNameError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletNameError.setText(errorMsg);
    }

    @Override
    public void showWalletAddressError(String errorMsg) {
        tvWalletAddressError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletAddressError.setText(errorMsg);
    }

    @Override
    public void setAddSharedWalletBtnEnable(boolean enable) {
        btnAddSharedWallet.setEnabled(enable);
    }

    @Override
    public String getWalletName() {
        return etWalletName.getText().toString().trim();
    }

    @Override
    public String getWalletAddress() {
        return etWalletAddress.getText().toString().trim();
    }
}
