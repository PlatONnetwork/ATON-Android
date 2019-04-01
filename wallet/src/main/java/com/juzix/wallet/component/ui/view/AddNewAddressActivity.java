package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.AddNewAddressContract;
import com.juzix.wallet.component.ui.presenter.AddNewAddressPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.entity.AddressEntity;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AddNewAddressActivity extends MVPBaseActivity<AddNewAddressPresenter> implements AddNewAddressContract.View {

    @BindView(R.id.et_address_name)
    EditText etAddressName;
    @BindView(R.id.tv_name_error)
    TextView tvNameError;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.iv_address_scan)
    ImageView ivAddressScan;
    @BindView(R.id.tv_address_error)
    TextView tvAddressError;
    @BindString(R.string.add_new_address)
    String   addNewAddressTitle;
    @BindString(R.string.edit_address)
    String   editAddress;
    @BindView(R.id.btn_add_address)
    Button   btnAddAddress;
    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;

    private Unbinder unbinder;

    @Override
    protected AddNewAddressPresenter createPresenter() {
        return new AddNewAddressPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadAddressInfo();
    }

    @OnClick({R.id.btn_add_address, R.id.iv_address_scan})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_address:
                hideSoftInput();
                mPresenter.addAddress();
                break;
            case R.id.iv_address_scan:
                hideSoftInput();
                requestPermission(AddNewAddressActivity.this, 100, new PermissionConfigure.PermissionCallback() {
                    @Override
                    public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                        ScanQRCodeActivity.startActivityForResult(AddNewAddressActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onHasPermission(int what) {
                        ScanQRCodeActivity.startActivityForResult(AddNewAddressActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onFail(int what, @NonNull List<String> deniedPermissions) {

                    }
                }, Manifest.permission.CAMERA);
                break;
        }
    }

    private void initViews() {

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkAddress(s.toString());
                mPresenter.updateAddNewAddressButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddressName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkAddressName(s.toString());
                mPresenter.updateAddNewAddressButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (getIntent().hasExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA)){
            etAddress.setText(getIntent().getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE) {
                String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                mPresenter.validQRCode(address);
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

    @Override
    public AddressEntity getAddressFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    @Override
    public String getName() {
        return etAddressName.getText().toString();
    }

    @Override
    public String getAddress() {
        return etAddress.getText().toString();
    }

    @Override
    public void showNameError(String errContent) {
        tvNameError.setVisibility(View.VISIBLE);
        tvNameError.setText(errContent);
    }

    @Override
    public void showAddressError(String errContent) {
        tvAddressError.setVisibility(View.VISIBLE);
        tvAddressError.setText(errContent);
    }

    @Override
    public void setNameVisibility(int visibility) {
        tvNameError.setVisibility(visibility);
    }

    @Override
    public void setAddressVisibility(int visibility) {
        tvAddressError.setVisibility(visibility);
    }

    @Override
    public void setAddressInfo(AddressEntity addressInfo) {
        commonTitleBar.setTitle(editAddress);
        etAddressName.setText(addressInfo.getName());
        etAddress.setText(addressInfo.getAddress());
    }

    @Override
    public void setBottonBtnText(String text) {
        btnAddAddress.setText(text);
    }

    @Override
    public void setAddNewAddressButtonEnable(boolean enable) {
        btnAddAddress.setEnabled(enable);
        btnAddAddress.setBackgroundResource(enable ? R.drawable.bg_shape_button2 : R.drawable.bg_shape_button1);
        btnAddAddress.setTextColor(ContextCompat.getColor(getContext(), enable ? R.color.color_f6f6f6 : R.color.color_d8d8d8));
    }

    public static void actionStartWithExtraForResult(Context context, AddressEntity addressEntity) {
        Intent intent = new Intent(context, AddNewAddressActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, addressEntity);
        ((Activity) context).startActivityForResult(intent, addressEntity == null ? Constants.RequestCode.REQUEST_CODE_ADD_ADDRESS : Constants.RequestCode.REQUEST_CODE_EDIT_ADDRESS);
    }

    public static void actionStartWithAddress(Context context, String address) {
        Intent intent = new Intent(context, AddNewAddressActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA, address);
        context.startActivity(intent);
    }

    @Override
    public void setResult(AddressEntity addressEntity) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, addressEntity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showAddress(String address) {
        etAddress.setText(address);
    }
}
