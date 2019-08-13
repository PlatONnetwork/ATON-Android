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
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.popwindow.DelegatePopWindow;
import com.juzix.wallet.component.ui.presenter.DelegatePresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

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

    private String address;//钱包地址
    private String chooseType;//选择的钱包类型（可用余额/锁仓余额）

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
        setDelegateButtonState(false);
        et_amount.setFilters(new InputFilter[]{new PointLengthFilter()});
        et_amount.addTextChangedListener(delegateWatcher);

        RxView.clicks(walletChoose).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });

        RxView.clicks(amounChoose).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //选择余额类型
                        DelegatePopWindow delegatePopWindow = new DelegatePopWindow(getContext(), amounChoose, address,chooseType);
                        mPresenter.getAmountType(delegatePopWindow);
                    }
                });


        RxView.clicks(all)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击全部

                    }
                });


        RxView.clicks(btnDelegate).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击委托操作 todo 是否在点击的时候去查询该节点是否已退出
                        mPresenter.submitDelegate(chooseType);
                    }
                });
    }


    private TextWatcher delegateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            mPresenter.checkDelegateAmount(s.toString().trim());
            mPresenter.updateDelegateButtonState();

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            inputTips.setText(amountMagnitudes);
            inputTips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static void actionStart(Context context, String nodeAddress, String nodeName, String nodeIcon, int tag) {
        Intent intent = new Intent(context, DelegateActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ADDRESS, nodeAddress);
        intent.putExtra(Constants.Extra.EXTRA_NODE_NAME, nodeName);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ICON, nodeIcon);
        intent.putExtra("tag", tag);
        context.startActivity(intent);
    }

    //显示钱包信息
    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        chooseType = "balance";
        address = individualWalletEntity.getAddress();
        //显示钱包基本信息
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
        walletIcon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));

        //显示余额类型和余额
        amountType.setText(getString(R.string.available_balance));
        amount.setText(StringUtil.formatBalance(individualWalletEntity.getBalance(), false));
    }

    @Override
    public void showWalletType(DelegateType delegateType) {
        chooseType = delegateType.getType();
        amountType.setText(TextUtils.equals(delegateType.getType(), "balance") ? getString(R.string.available_balance) : getString(R.string.locked_balance));
        amount.setText(StringUtil.formatBalance(delegateType.getAmount(), false));
    }

    @Override
    public void setDelegateButtonState(boolean isClickable) {
        btnDelegate.setEnabled(isClickable);
    }

    //获取输入的数量
    @Override
    public String getDelegateAmount() {
        return et_amount.getText().toString().trim();
    }

    @Override
    public String getChooseBalance() {
        return amount.getText().toString();
    }

    @Override
    public void showAmountError(String errMsg) {
        inputTips.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        inputTips.setText(errMsg);
    }

    @Override
    public void showTips(boolean isShow) {
        inputError.setText(getString(R.string.delegate_amount_tips));
        inputError.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public String getNodeAddressFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_NODE_ADDRESS);
    }

    @Override
    public String getNodeNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_NODE_NAME);
    }

    @Override
    public String getNodeIconFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_NODE_ICON);
    }

    @Override
    public int getJumpTagFromIntent() {
        return getIntent().getIntExtra("tag", 0);
    }

    @Override
    public void showNodeInfo(String address, String name, String UrlIcon) {
        //显示节点基本信息
        GlideUtils.loadRound(getContext(), UrlIcon, nodeIcon);
        nodeName.setText(name);
        nodeAddress.setText(AddressFormatUtil.formatAddress(address));
    }


    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }
}
