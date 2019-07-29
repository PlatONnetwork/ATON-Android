package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.presenter.WithDrawPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.RxUtils;

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
        RxView.clicks(chooseWallet).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WithDrawActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
        wallet_icon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));
    }
}
