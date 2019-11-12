package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.UnlockWithPasswordContract;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.component.ui.presenter.UnlockWithPasswordPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.Wallet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class UnlockWithPasswordActivity extends MVPBaseActivity<UnlockWithPasswordPresenter> implements UnlockWithPasswordContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.et_password)
    EditText etWalletPassword;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.iv_wallet_avatar)
    ImageView ivWalletAvatar;
    @BindView(R.id.btn_unlock)
    ShadowButton btnUnlock;

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

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        commonTitleBar.setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                UnlockWithPasswordActivity.this.finish();
            }
        });

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
    }

    @OnClick({R.id.layout_change_wallet, R.id.btn_unlock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_change_wallet:
                SelectWalletDialogFragment.newInstance(mPresenter.getSelectedWallet().getUuid()).setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet walletEntity) {
                        mPresenter.setSelectWallet(walletEntity);
                    }
                }).show(currentActivity().getSupportFragmentManager(), SelectWalletDialogFragment.SELECT_UNLOCK_WALLET);
                break;
            case R.id.btn_unlock:
                mPresenter.unlock(etWalletPassword.getText().toString());
                break;
            default:
                break;
        }

    }

    @Override
    public void updateWalletInfo(Wallet walletEntity) {
        int walletAvatar = RUtils.drawable(walletEntity.getAvatar());
        if (walletAvatar != -1) {
            ivWalletAvatar.setImageResource(walletAvatar);
        }
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
