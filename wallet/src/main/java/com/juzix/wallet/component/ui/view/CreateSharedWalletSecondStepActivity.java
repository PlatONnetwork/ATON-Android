package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletSecondStepContract;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.CreateSharedWalletSecondStepPresenter;
import com.juzix.wallet.component.widget.CustomEditText;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class CreateSharedWalletSecondStepActivity extends MVPBaseActivity<CreateSharedWalletSecondStepPresenter> implements CreateSharedWalletSecondStepContract.View {

    @BindView(R.id.list_shared_owner)
    ListView     listSharedOwner;
    @BindView(R.id.sbtn_create_shared_wallet)
    ShadowButton btnCreateSharedWallet;

    private Unbinder unbinder;
    private CommonAdapter<CreateSharedWalletSecondStepContract.ContractEntity> mSharedOwnerListAdapter;
    private View headerView;

    @Override
    protected CreateSharedWalletSecondStepPresenter createPresenter() {
        return new CreateSharedWalletSecondStepPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_create_shared_wallet_second_step);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.init();
    }

    private void initViews() {

        mSharedOwnerListAdapter = new CommonAdapter<CreateSharedWalletSecondStepContract.ContractEntity>(R.layout.item_create_shared_owner_list, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, CreateSharedWalletSecondStepContract.ContractEntity item, int position) {
                viewHolder.setText(R.id.tv_wallet_address_info, context.getString(R.string.member, String.valueOf(position + 2)));
                CustomEditText etWalletName = viewHolder.getView(R.id.et_wallet_name);
                CustomEditText etWalletAddress = viewHolder.getView(R.id.et_wallet_address);
                ImageView ivScan = viewHolder.getView(R.id.iv_scan);
                ImageView ivAddressBook = viewHolder.getView(R.id.iv_address_book);
                TextView tvAddressError = viewHolder.getView(R.id.tv_address_error);
                TextView tvSaveAddress = viewHolder.getView(R.id.tv_save_address);
                int focuse = item.getFocus();
                Object nameTag = etWalletName.getTag();
                Object addressTag = etWalletAddress.getTag();
                if (etWalletName.getTag() instanceof TextChangedListener) {
                    etWalletName.removeTextChangedListener((TextWatcher) nameTag);
                }
                if (etWalletAddress.getTag() instanceof TextChangedListener) {
                    etWalletAddress.removeTextChangedListener((TextWatcher) addressTag);
                }
                etWalletName.setText(item.getName());
                etWalletAddress.setText(item.getAddress());
                switch (focuse) {
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
                        enableSaveAddress(tvSaveAddress, mPresenter.needSaveAddressBook(String.valueOf(s)));
                    }
                };

                View.OnClickListener addressBookListener = new View.OnClickListener() {
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

                View.OnClickListener scanAddressListener = new View.OnClickListener() {
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

                View.OnClickListener saveAddressListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.verifyAddress(position);
                        etWalletAddress.setFocusableInTouchMode(true);
                        etWalletName.setFocusableInTouchMode(false);
                        mPresenter.focusAddress(position);
                        etWalletAddress.requestFocus();
                        etWalletAddress.onWindowFocusChanged(true);
                        showSaveAddressDialog(tvSaveAddress, etWalletAddress.getText().toString());
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
                enableSaveAddress(tvSaveAddress, mPresenter.needSaveAddressBook(item.getAddress()));
                etWalletName.setEnabled(item.isEnabled());
                etWalletAddress.setEnabled(item.isEnabled());
                ivAddressBook.setEnabled(item.isEnabled());
                ivScan.setEnabled(item.isEnabled());
                String errorMsg = item.getErrorMsg();
                tvAddressError.setText(errorMsg);
                tvAddressError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);

                etWalletName.setOnTouchListener(walletNameTouchListener);
                etWalletAddress.setOnTouchListener(walletAddressTouchListener);
                ivAddressBook.setOnClickListener(addressBookListener);
                ivScan.setOnClickListener(scanAddressListener);
                tvSaveAddress.setOnClickListener(saveAddressListener);
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
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_create_shared_owner_list_header, null);
        listSharedOwner.addHeaderView(headerView);

        RxView.clicks(btnCreateSharedWallet)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.createContract();
                    }
                });

        setCreateSharedWalletBtnEnable(false);
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
    public void showWalletInfo(IndividualWalletEntity walletEntity) {
        ((TextView)headerView.findViewById(R.id.tv_wallet_address_info)).setText(getString(R.string.member, String.valueOf("1")));
        ((ImageView)headerView.findViewById(R.id.iv_wallet_avatar)).setImageResource(RUtils.drawable(walletEntity.getAvatar()));
        ((TextView)headerView.findViewById(R.id.tv_wallet_name)).setText(walletEntity.getName());
        ((TextView)headerView.findViewById(R.id.tv_wallet_address)).setText(AddressFormatUtil.formatAddress(walletEntity.getPrefixAddress()));

    }

    @Override
    public void showOwnerList(List<CreateSharedWalletSecondStepContract.ContractEntity> ownerEntityList) {
        mSharedOwnerListAdapter.notifyDataChanged(ownerEntityList);
    }

    @Override
    public void updateOwner(int position) {
        mSharedOwnerListAdapter.updateItem(this, listSharedOwner, mSharedOwnerListAdapter.getItem(position));
    }

    @Override
    public void setCreateSharedWalletBtnEnable(boolean enable) {
        btnCreateSharedWallet.setEnabled(enable);
    }

    private void enableSaveAddress(TextView tvAddress, boolean enable) {
        tvAddress.setEnabled(enable);
        tvAddress.setTextColor(ContextCompat.getColor(getContext(), enable ? R.color.color_105cfe : R.color.color_b6bbd0));
    }

    public void showSaveAddressDialog(final TextView tvSaveAddress, final String address) {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.nameOfWallet), "", InputType.TYPE_CLASS_TEXT, string(R.string.confirm), string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (text.length() > 12) {
                    CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                            string(R.string.formatError), string(R.string.validWalletNameTips), string(R.string.understood), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showSaveAddressDialog(tvSaveAddress, address);
                                }
                            }).show(getSupportFragmentManager(), "showTips");
                } else {
                    enableSaveAddress(tvSaveAddress, !mPresenter.saveWallet(text, address));
                }
            }
        }).show(getSupportFragmentManager(), "showTips");

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
}
