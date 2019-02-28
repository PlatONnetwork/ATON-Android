package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.UnlockWithPasswordContract;
import com.juzix.wallet.component.ui.dialog.SelectWalletInfoDialogFragment;
import com.juzix.wallet.component.ui.presenter.UnlockWithPasswordPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.IndividualWalletEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordActivity extends MVPBaseActivity<UnlockWithPasswordPresenter> implements UnlockWithPasswordContract.View, SelectWalletInfoDialogFragment.OnItemClickListener {

    @BindView(R.id.et_password)
    EditText etWalletPassword;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.btn_unlock)
    Button   btnUnlock;

    private Unbinder unbinder;

    @Override
    protected UnlockWithPasswordPresenter createPresenter() {
        return new UnlockWithPasswordPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_password);
        unbinder = ButterKnife.bind(this);
        initViews();
        enableUnlock(false);
        mPresenter.init();
    }

    private void initViews() {
        CommonTitleBar commonTitleBar = new CommonTitleBar(this);
        commonTitleBar.setLeftDrawable(R.drawable.icon_back_black);
        commonTitleBar.setMiddleTitle(string(R.string.unlockWithWalletPasswordTitle));
        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                UnlockWithPasswordActivity.this.finish();
            }
        });
        commonTitleBar.build();
//        new CommonTitleBar(this).setLeftDrawable(R.drawable.icon_back_black).setMiddleTitle(string(R.string.unlockWithWalletPasswordTitle)).build();
//
//
        etWalletPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etWalletPassword.getText().toString();
                enableUnlock(!TextUtils.isEmpty(password));
            }
        });
    }

    private void enableUnlock(boolean enabled) {
        btnUnlock.setEnabled(enabled);
        btnUnlock.setBackgroundColor(ContextCompat.getColor(getContext(), enabled ? R.color.color_eff0f5 : R.color.color_373e51));
    }

    @OnClick({R.id.layout_change_wallet, R.id.btn_unlock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_change_wallet:
                SelectWalletInfoDialogFragment.newInstance(mPresenter.getSelectedPostion()).show(getSupportFragmentManager(), "selectWallet");
                break;
            case R.id.btn_unlock:
                mPresenter.unlock(etWalletPassword.getText().toString());
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(int selectedPosition) {
        mPresenter.setSelectedPostion(selectedPosition);
    }

    @Override
    public void updateWalletInfo(IndividualWalletEntity walletEntity) {
        tvWalletName.setText(walletEntity.getName());
        tvWalletAddress.setText(walletEntity.getPrefixAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, UnlockWithPasswordActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
