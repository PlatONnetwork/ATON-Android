package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.presenter.WithDrawPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WithDrawActivity extends MVPBaseActivity<WithDrawPresenter> implements WithDrawContract.View {
    private Unbinder unbinder;

    @BindView(R.id.iv_node_icon)
    CircleImageView node_icon;
    @BindView(R.id.tv_withdraw_node_name)
    TextView nodeName;
    @BindView(R.id.tv_withdraw_node_address)
    TextView nodeAddress;
    @BindView(R.id.rl_choose_wallet)
    RelativeLayout chooseWallet;
    @BindView(R.id.iv_wallet_icon)
    CircleImageView wallet_icon;
    @BindView(R.id.tv_withdraw_wallet_name)
    TextView walletName;
    @BindView(R.id.tv_withdraw_wallet_address)
    TextView walletAddress;
    @BindView(R.id.rl_choose_delegate)
    RelativeLayout chooseDelegate;
    @BindView(R.id.tv_delegate_type)
    TextView delegateType;
    @BindView(R.id.tv_delegate_amount)
    TextView delegateAmount;
    @BindView(R.id.et_withdraw_amount)
    EditText withdrawAmount;
    @BindView(R.id.tv_all_amount)
    TextView allAmount;
    @BindView(R.id.tv_error_tips)
    TextView tips;
    @BindView(R.id.tv_wallet_fee)
    TextView fee;
    @BindView(R.id.btn_withdraw)
    ShadowButton btnWithdraw;
    @BindView(R.id.tv_amount_magnitudes)
    TextView etWalletAmount;//显示量级

    @Override
    protected WithDrawPresenter createPresenter() {
        return new WithDrawPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.showWalletInfo();
    }

    private void initView() {
        setWithDrawButtonState(false);
        withdrawAmount.setFilters(new InputFilter[]{new PointLengthFilter()});
        withdrawAmount.addTextChangedListener(mAmountTextWatcher);

        RxView.clicks(chooseWallet).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });
    }

    public static void actionStart(Context context, String nodeAddress, String nodeName, String nodeIcon) {
        Intent intent = new Intent(context, WithDrawActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ADDRESS, nodeAddress);
        intent.putExtra(Constants.Extra.EXTRA_NODE_NAME, nodeName);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ICON, nodeIcon);
        context.startActivity(intent);
    }


    private TextWatcher mAmountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {//改变后
            mPresenter.checkWithDrawAmount(s.toString().trim());
            mPresenter.updateWithDrawButtonState();

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            etWalletAmount.setText(amountMagnitudes);
            etWalletAmount.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

        }

        @Override
        public void afterTextChanged(Editable s) {//修改后

        }
    };


    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
        wallet_icon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));
    }

    @Override
    public void setWithDrawButtonState(boolean isClickable) {
        btnWithdraw.setEnabled(isClickable);//设置按钮是否可点击
    }

    @Override
    public String getWithDrawAmount() {
        return withdrawAmount.getText().toString().trim();
    }

    @Override
    public void showAmountError(String errMsg) {
        tips.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tips.setText(errMsg);
    }
}
