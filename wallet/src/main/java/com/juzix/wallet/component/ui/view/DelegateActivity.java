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
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.presenter.DelegatePresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 委托操作页面
 */

public class DelegateActivity extends MVPBaseActivity<DelegatePresenter> implements DelegateContract.View {
    private Unbinder unbinder;
    @BindView(R.id.iv_delegate_node_icon)
    CircleImageView nodeIcon;
    @BindView(R.id.tv_delegate_node_name)
    TextView nodeName;
    @BindView(R.id.tv_delegate_node_address)
    TextView nodeAddress;
    @BindView(R.id.rl_wallet_choose)
    RelativeLayout walletChoose;
    @BindView(R.id.iv_wallet_icon)
    CircleImageView walletIcon;
    @BindView(R.id.tv_delegate_wallet_name)
    TextView walletName;
    @BindView(R.id.tv_delegate_wallet_address)
    TextView walletAddress;
    @BindView(R.id.rl_amount_choose)
    RelativeLayout amounChoose;
    @BindView(R.id.tv_amount_type)
    TextView amountType;
    @BindView(R.id.tv_amount_delegate)
    TextView amount;
    @BindView(R.id.tv_input_tips)
    TextView inputTips;//输入提示
    @BindView(R.id.et_delegate_amount)
    EditText et_amount;
    @BindView(R.id.tv_all_amount)
    TextView all;
    @BindView(R.id.tv_input_error)
    TextView inputError;
    @BindView(R.id.tv_delegate_fee)
    TextView fee;
    @BindView(R.id.sbtn_delegate)
    ShadowButton btnDelegate;

    @Override
    protected DelegatePresenter createPresenter() {
        return new DelegatePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.showWalletInfo();
    }

    private void initView() {

        RxView.clicks(walletChoose).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });

    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DelegateActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
        walletIcon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));

    }
}
