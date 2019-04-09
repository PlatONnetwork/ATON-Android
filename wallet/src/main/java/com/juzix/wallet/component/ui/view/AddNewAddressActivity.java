package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.ClickTransformer;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.AddNewAddressContract;
import com.juzix.wallet.component.ui.presenter.AddNewAddressPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.entity.AddressEntity;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

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
    String addNewAddressTitle;
    @BindString(R.string.edit_address)
    String editAddress;
    @BindView(R.id.sbtn_add_address)
    ShadowButton sbtnAddAddress;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_add_new_address);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadAddressInfo();
    }

    private void initViews() {

        Observable.combineLatest(RxTextView.textChanges(etAddress).skipInitialValue(), RxTextView.textChanges(etAddressName).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence address, CharSequence addressName) throws Exception {
                return mPresenter.checkAddress(address.toString()) && mPresenter.checkAddressName(addressName.toString());
            }
        }).compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean enabled) throws Exception {
                        sbtnAddAddress.setEnabled(enabled);
                    }
                });

        RxView.clicks(sbtnAddAddress)
                .compose(new ClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        hideSoftInput();
                        mPresenter.addAddress();
                    }
                });

        RxView.clicks(ivAddressScan)
                .compose(new ClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
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
                    }
                });

        if (getIntent().hasExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA)) {
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
        sbtnAddAddress.setText(text);
    }

    @Override
    public void showAddress(String address) {
        etAddress.setText(address);
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

}
