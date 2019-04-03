package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletContract;
import com.juzix.wallet.component.ui.presenter.CreateSharedWalletPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ShadowContainer;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class CreateSharedWalletActivity extends MVPBaseActivity<CreateSharedWalletPresenter> implements CreateSharedWalletContract.View {


    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.iv_wallet_avatar)
    ImageView ivWalletAvatar;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.layout_shared_owners)
    RelativeLayout layoutSharedOwners;
    @BindView(R.id.layout_required_signatures)
    RelativeLayout layoutRequiredSignatures;
    @BindView(R.id.sc_next)
    ShadowContainer scNext;
    @BindView(R.id.layout_change_wallet)
    RelativeLayout layoutChangeWallet;
    @BindArray(R.array.shared_owners)
    String[] sharedOwners;
    @BindArray(R.array.required_signatures)
    String[] requiredSignatures;
    @BindView(R.id.tv_shared_owners)
    TextView tvSharedOwners;
    @BindView(R.id.tv_required_signatures)
    TextView tvRequiredSignatures;
    @BindView(R.id.tv_wallet_name_error)
    TextView tvWalletNameError;

    private Unbinder unbinder;
    private OptionsPickerView optionsPickerView;
    private String mCurSharedOwnerSelectedNum;
    private String mCurRequirderSignaturesSelectedNum;

    @Override
    protected CreateSharedWalletPresenter createPresenter() {
        return new CreateSharedWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shared_wallet);
        unbinder = ButterKnife.bind(this);
        initData();
        mPresenter.init();
    }

    private void initData() {
        mCurSharedOwnerSelectedNum = tvSharedOwners.getText().toString();
        mCurRequirderSignaturesSelectedNum = tvRequiredSignatures.getText().toString();

        etWalletName.addTextChangedListener(new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                mPresenter.updateWalletName(s.toString().trim());
            }
        });

        RxView.focusChanges(etWalletName).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkWalletName(etWalletName.getText().toString().trim());
                }
            }
        });

        setNextButtonEnable(false);
    }


    private List<String> getCurSharedOwnerNumList() {
        return Arrays.asList(sharedOwners);
    }

    private List<String> getCurRequiredSignaturesNumList() {

        List<String> list = new ArrayList<>();

        for (int i = 0; i < requiredSignatures.length; i++) {
            if (NumberParserUtils.parseInt(requiredSignatures[i]) <= NumberParserUtils.parseInt(mCurSharedOwnerSelectedNum)) {
                list.add(requiredSignatures[i]);
            }
        }

        return list;
    }


    @OnClick({R.id.layout_change_wallet, R.id.layout_shared_owners, R.id.layout_required_signatures, R.id.sc_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_change_wallet:
                hideSoftInput();
                mPresenter.showSelectWalletDialogFragment();
                break;
            case R.id.layout_shared_owners:
                hideSoftInput();
                getOptionsPickerView().setPicker(getCurSharedOwnerNumList());
                getOptionsPickerView().show(layoutSharedOwners);
                break;
            case R.id.layout_required_signatures:
                hideSoftInput();
                getOptionsPickerView().setPicker(getCurRequiredSignaturesNumList());
                getOptionsPickerView().show(layoutRequiredSignatures);
                break;
            case R.id.sc_next:
                if (NumberParserUtils.parseInt(mCurRequirderSignaturesSelectedNum) > NumberParserUtils.parseInt(mCurSharedOwnerSelectedNum)) {
                    showLongToast(string(R.string.createSharedWalletTips1));
                    getOptionsPickerView().setPicker(getCurRequiredSignaturesNumList());
                    getOptionsPickerView().show(layoutRequiredSignatures);
                } else {
                    mPresenter.next();
                }
                break;
            default:
                break;
        }
    }

    private OptionsPickerView getOptionsPickerView() {
        if (optionsPickerView == null) {
            optionsPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    if (v.getId() == R.id.layout_shared_owners) {
                        mCurSharedOwnerSelectedNum = getCurSharedOwnerNumList().get(options1);
                        tvSharedOwners.setText(mCurSharedOwnerSelectedNum);
                        if (Integer.parseInt(mCurSharedOwnerSelectedNum) < Integer.parseInt(mCurRequirderSignaturesSelectedNum)) {
                            mCurRequirderSignaturesSelectedNum = mCurSharedOwnerSelectedNum;
                            tvRequiredSignatures.setText(mCurRequirderSignaturesSelectedNum);
                        }
                    } else {
                        mCurRequirderSignaturesSelectedNum = getCurRequiredSignaturesNumList().get(options1);
                        tvRequiredSignatures.setText(mCurRequirderSignaturesSelectedNum);
                    }
                }
            }).setSubmitColor(ContextCompat.getColor(this, R.color.color_007aff))
                    .setSubCalSize(15)
                    .setSubmitText(getResources().getString(R.string.submit))
                    .setCancelColor(ContextCompat.getColor(this, R.color.color_007aff))
                    .setCancelText(getResources().getString(R.string.cancel))
                    .setTextColorCenter(ContextCompat.getColor(this, R.color.color_292929))
                    .setContentTextSize(18)
                    .setTitleBgColor(ContextCompat.getColor(this, R.color.color_ffffff))
                    .setBgColor(ContextCompat.getColor(this, R.color.color_f8f8f8))
                    .setDividerType(WheelView.DividerType.FILL)
                    .setDividerColor(ContextCompat.getColor(this, R.color.color_aba9a2))
                    .setTextColorOut(ContextCompat.getColor(this, R.color.color_9d9d9d))
                    .setLineSpacingMultiplier(2.0f)
                    .isDialog(false)
                    .build();
        }

        return optionsPickerView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CREATE_SHARED_WALLET_SECOND_STEP) {
                hideSoftInput();
                finish();
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

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreateSharedWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void updateSelectOwner(IndividualWalletEntity walletEntity) {
        ivWalletAvatar.setImageResource(RUtils.drawable(walletEntity.getAvatar()));
        tvWalletName.setText(walletEntity.getName());
        tvWalletAddress.setText(AddressFormatUtil.formatAddress(walletEntity.getPrefixAddress()));
    }

    @Override
    public void setNextButtonEnable(boolean enable) {
        scNext.setEnabled(enable);
    }

    @Override
    public String getWalletName() {
        return etWalletName.getText().toString().trim();
    }

    @Override
    public void showWalletNameError(String errMsg) {
        tvWalletNameError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvWalletNameError.setText(errMsg);
    }

    @Override
    public int getSharedOwners() {
        return NumberParserUtils.parseInt(mCurSharedOwnerSelectedNum);
    }

    @Override
    public int getRequiredSignatures() {
        return NumberParserUtils.parseInt(mCurRequirderSignaturesSelectedNum);
    }
}
