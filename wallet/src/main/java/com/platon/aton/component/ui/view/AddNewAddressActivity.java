package com.platon.aton.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.AddNewAddressContract;
import com.platon.aton.component.ui.presenter.AddNewAddressPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.CustomUnderlineEditText;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.entity.Address;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

/**
 * @author matrixelement
 */
public class AddNewAddressActivity extends BaseActivity<AddNewAddressContract.View, AddNewAddressPresenter> implements AddNewAddressContract.View {

    @BindView(R.id.et_address_name)
    EditText etAddressName;
    @BindView(R.id.tv_name_error)
    TextView tvNameError;
    @BindView(R.id.et_address)
    CustomUnderlineEditText etAddress;
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
    public AddNewAddressPresenter createPresenter() {
        return new AddNewAddressPresenter();
    }

    @Override
    public AddNewAddressContract.View createView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        getPresenter().getIntentData();
        initViews();
        getPresenter().loadAddressInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_new_address;
    }

    private void initViews() {

        Observable.combineLatest(RxTextView.textChanges(etAddress).skipInitialValue(), RxTextView.textChanges(etAddressName).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence address, CharSequence addressName) throws Exception {
                return getPresenter().checkAddress(address.toString()) && getPresenter().checkAddressName(addressName.toString());
            }
        }).compose(bindToLifecycle())
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean enabled) {
                        sbtnAddAddress.setEnabled(enabled);
                    }
                });

        etAddress.setDrawableClickListener(new CustomUnderlineEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                new RxPermissions(AddNewAddressActivity.this)
                        .request(Manifest.permission.CAMERA)
                        .subscribe(new CustomObserver<Boolean>() {
                            @Override
                            public void accept(Boolean success) {
                                if (success) {
                                    ScanQRCodeActivity.startActivityForResult(AddNewAddressActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                                }
                            }
                        })
                ;
            }
        });

        RxView.clicks(sbtnAddAddress)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        hideSoftInput();
                        getPresenter().addAddress();
                    }
                });

        if (getIntent().hasExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA)) {
            etAddress.setText(getIntent().getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA));
        }

        etAddressName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getPresenter().checkAddressName(etAddressName.getText().toString().trim());
                }
            }
        });

        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getPresenter().checkAddress(etAddress.getText().toString().trim());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE) {
                String address = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                String unzip = GZipUtil.unCompress(address);
                String newStr = TextUtils.isEmpty(unzip) ? address : unzip;
                if (TextUtils.isEmpty(newStr)) {
                    ToastUtil.showLongToast(getContext(), R.string.unrecognized_content);
                    return;
                }
                getPresenter().validQRCode(newStr);
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
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public Address getAddressFromIntent() {
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
    public void setAddressInfo(Address addressInfo) {
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
        etAddress.setSelection(address.length());
    }

    public static void actionStartWithExtraForResult(Context context, Address addressEntity) {
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
    public void setResult(Address addressEntity) {
        Intent intent = new Intent(this, AddressBookActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, addressEntity);
        setResult(RESULT_OK, intent);
        finish();
    }

}
