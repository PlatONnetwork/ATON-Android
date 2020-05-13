package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.WithDrawPopWindowAdapter;
import com.platon.aton.component.ui.contract.WithDrawContract;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.component.ui.presenter.WithDrawPresenter;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.MyWatcher;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.GuideType;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WithDrawBalance;
import com.platon.aton.entity.WithDrawType;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GlideUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.SoftHideKeyboardUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.aton.utils.UMEventUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.PreferenceTool;
import com.platon.framework.utils.RUtils;
import com.platon.framework.utils.ToastUtil;

import org.web3j.tx.gas.GasProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WithDrawActivity extends BaseActivity<WithDrawContract.View, WithDrawPresenter> implements WithDrawContract.View {

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
    @BindView(R.id.tv_withdraw_wallet_balance)
    TextView walletBalance;
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
    @BindView(R.id.v_tips)
    View v_tips;
    @BindView(R.id.tv_delegate_tips)
    TextView tvDelegateTips;

    private Unbinder unbinder;
    private PopupWindow mPopupWindow;
    private ListView mPopListview;
    private List<WithDrawType> list = new ArrayList<>();
    private WithDrawPopWindowAdapter mPopWindowAdapter;
    private String gasPrice;
    //手续费
    private String withdrawFee;
    //自由金额
    private String freeAccount;

    @Override
    public WithDrawPresenter createPresenter() {
        return new WithDrawPresenter();
    }

    @Override
    public WithDrawContract.View createView() {
        return this;
    }

    @Override
    public void init() {

        unbinder = ButterKnife.bind(this);
        initView();
        //初始化请求数据
        getPresenter().init(getDelegateDetailFromIntent());
        getPresenter().showWalletInfo();
        getPresenter().getBalanceType();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw;
    }

    private void initView() {
        initShade();
        initPopWindow();
        setWithDrawButtonState(false);
        withdrawAmount.addTextChangedListener(mAmountTextWatcher);
        initClicks();
        initGuide();
        SoftHideKeyboardUtils.assistActivity(this);
    }

    private void initShade() {

        ShadowDrawable.setShadowDrawable(chooseDelegate,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 5),
                0,
                DensityUtil.dp2px(this, 2));
    }

    private void initClicks() {
        RxView.clicks(chooseDelegate)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        getPresenter().showSelectDelegationsDialogFragment();
                    }
                });


        RxView.clicks(allAmount)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击全部
                        setAllAmountDelegate();
                    }
                });

        RxView.clicks(btnWithdraw)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {

                        UMEventUtil.onEventCount(WithDrawActivity.this, Constants.UMEventID.WITHDRAW_DELEGATE);

                        getPresenter().checkWithDrawAmount(withdrawAmount.getText().toString().trim().replace(",", ""));

                        if (BigDecimalUtil.sub(freeAccount, withdrawFee).doubleValue() < 0) { //赎回时，自用金额必须大于手续费，才能赎回
                            ToastUtil.showLongToast(getContext(), R.string.withdraw_less_than_fee);
                            return;
                        }

                        long currentTime = System.currentTimeMillis();

                        if (!TransactionManager.getInstance().isAllowSendTransaction(getPresenter().getWalletAddress(), currentTime)) {
                            showLongToast(string(R.string.msg_wait_finished_transaction_tips, DateUtil.millisecondToMinutes(TransactionManager.getInstance().getSendTransactionTimeInterval(getPresenter().getWalletAddress(), currentTime))));
                            return;
                        }

                        getPresenter().submitWithDraw();
                    }
                });
    }

    private void initGuide() {
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_WITHDRAW_OPERATION, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.WITHDRAW_DELEGATE).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_WITHDRAW_OPERATION, true);
                }
            }).show(getSupportFragmentManager(), "showGuideDialogFragment");
        }

    }

    private void initPopWindow() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popwindow_delegate, null);
        mPopListview = view.findViewById(R.id.listview_popwindow);
        mPopListview.setDivider(null);
        mPopListview.setVerticalScrollBarEnabled(false);//隐藏侧滑栏
        mPopWindowAdapter = new WithDrawPopWindowAdapter(getContext(), list);
        mPopListview.setAdapter(mPopWindowAdapter);
        mPopWindowAdapter.setDefSelect(0);//设置默认第一项选中
        mPopListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopWindowAdapter.setDefSelect(position);
                //刷新选中项数据
                mPopupWindow.dismiss();
                refreshData(mPopWindowAdapter.getItem(position));

            }
        });

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        //设置弹出窗体可点击
        mPopupWindow.setFocusable(true);
    }

    private void refreshData(WithDrawType item) {

        if (TextUtils.equals(item.getKey(), WithDrawPopWindowAdapter.TAG_DELEGATED)) {
            delegateType.setText(getString(R.string.withdraw_type_delegated));
            withdrawAmount.setFocusableInTouchMode(true);
            withdrawAmount.setFocusable(true);
            withdrawAmount.setText("");
        } else {
            delegateType.setText(getString(R.string.withdraw_type_released)); //已解除
            withdrawAmount.setText(String.valueOf(item.getValue()));
            withdrawAmount.setFocusableInTouchMode(false);
            withdrawAmount.setFocusable(false);
            getPresenter().getWithDrawGasPrice(gasPrice);//已解除不能操作，所以需要再获取一次手续费
        }
        delegateAmount.setText(String.valueOf(item.getValue()));

    }


    /**
     * 基于某个位置显示弹窗
     *
     * @param view
     */
    public void showPopWindow(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    private TextWatcher mAmountTextWatcher = new MyWatcher(-1, 8) {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {//改变后

            getPresenter().updateWithDrawButtonState();
            getPresenter().getWithDrawGasPrice(gasPrice);

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            etWalletAmount.setText(amountMagnitudes);
            etWalletAmount.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
            v_tips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

            if (count == 0) {
                return;
            }

            if (!TextUtils.isEmpty(s.toString()) && !TextUtils.equals(s.toString(), ".")) {

                if (BigDecimalUtil.sub(delegateAmount.getText().toString().replaceAll(",", ""), s.toString()).doubleValue() > 0) {
                    if (BigDecimalUtil.sub(delegateAmount.getText().toString().replaceAll(",", ""), s.toString()).doubleValue() < getPresenter().getMinDelegationAmount()) {
                        withdrawAmount.setText(delegateAmount.getText().toString().replaceAll(",", ""));
                        withdrawAmount.setSelection(delegateAmount.getText().toString().replaceAll(",", "").length());
                    }
                }
            }


        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
        }
    };

    @Override
    public void showSelectedWalletInfo(Wallet wallet) {
        walletName.setText(wallet.getName());
        CommonTextUtils.richText(walletBalance, getString(R.string.msg_balance_with_unit, AmountUtil.formatAmountText(wallet.getFreeBalance())), AmountUtil.formatAmountText(wallet.getFreeBalance()), new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_000000)));
        wallet_icon.setImageResource(RUtils.drawable(wallet.getAvatar()));
        freeAccount = wallet.getFreeBalance();
    }

    @Override
    public void setWithDrawButtonState(boolean isClickable) {
        btnWithdraw.setEnabled(isClickable);//设置按钮是否可点击
    }

    @Override
    public String getWithDrawAmount() {
        return withdrawAmount.getText().toString().trim().replace(",", "");
    }

    @Override
    public void showTips(boolean isShow, String minDelegationAmount) {
        tips.setText(getString(R.string.withdraw_amount_tips, minDelegationAmount));
        tips.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    //显示节点基本信息
    @Override
    public void showNodeInfo(DelegateItemInfo delegateDetail) {
        GlideUtils.loadRound(getContext(), delegateDetail.getUrl(), node_icon);
        nodeName.setText(delegateDetail.getNodeName());
        nodeAddress.setText(AddressFormatUtil.formatAddress(delegateDetail.getNodeId()));
    }

    @Override
    public void showMinDelegationInfo(String minDelegationAmount) {

        withdrawAmount.setHint(getString(R.string.withdraw_tip, minDelegationAmount));
        tvDelegateTips.setText(getString(R.string.withdraw_title_explain, minDelegationAmount));

    }

    @Override
    public DelegateItemInfo getDelegateDetailFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_DELEGATE_DETAIL);
    }

    //获取输入的数量(edittext)
    @Override
    public String getInputAmount() {
        return withdrawAmount.getText().toString().trim().replace(",", "");
    }

    @Override
    public void withDrawSuccessInfo(Transaction transaction) {
        TransactionDetailActivity.actionStart(getContext(), transaction, Arrays.asList(transaction.getFrom()));
        finish();
    }

    //显示手续费
    @Override
    public void showWithDrawGasPrice(String gas) {
        fee.setText(string(R.string.amount_with_unit, AmountUtil.convertVonToLat(gas, 8)));
        withdrawFee = gas;
    }

    @Override
    public void showGas(GasProvider gasProvider) {
        gasPrice = gasProvider.getGasPrice().toString();
        fee.setText(string(R.string.amount_with_unit, AmountUtil.convertVonToLat(BigIntegerUtil.mul(gasProvider.getGasLimit(), gasProvider.getGasPrice()), 8)));
    }

    @Override
    public void finishDelayed() {
        nodeName.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLongToast(R.string.msg_no_delegation_available);
                finish();
            }
        }, 300);
    }

    @Override
    public void showWithdrawBalance(WithDrawBalance withDrawBalance) {

        delegateType.setText(withDrawBalance.isDelegated() ? getString(R.string.withdraw_type_delegated) : getString(R.string.withdraw_type_released));
        withdrawAmount.setFocusableInTouchMode(withDrawBalance.isDelegated());
        withdrawAmount.setFocusable(withDrawBalance.isDelegated());
        if (withDrawBalance.isDelegated()) {
            withdrawAmount.setText(AmountUtil.formatAmountText(withDrawBalance.getDelegated()));
        } else {
            withdrawAmount.setText(AmountUtil.formatAmountText(withDrawBalance.getReleased()));
        }
        delegateAmount.setText(string(R.string.amount_with_unit, AmountUtil.formatAmountText(withDrawBalance.isDelegated() ? withDrawBalance.getDelegated() : withDrawBalance.getReleased())));

    }

    @Override
    public void showsSelectDelegationsBtnVisibility(int visibility) {
        delegateAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, visibility == View.VISIBLE ? ContextCompat.getDrawable(this, R.drawable.icon_drop_down) : null, null);
    }

    @Override
    public void setAllAmountDelegate() {
        //点击全部
        WithDrawBalance withDrawBalance = getPresenter().getWithDrawBalance();
        if (withDrawBalance != null) {
            String withdrawAmountText = AmountUtil.formatAmountText(withDrawBalance.isDelegated() ? withDrawBalance.getDelegated() : withDrawBalance.getReleased());
            withdrawAmount.setText(withdrawAmountText);
            withdrawAmount.setSelection(withdrawAmountText.length());
        }
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        TransactionSignatureDialogFragment dialogFragment = (TransactionSignatureDialogFragment) getSupportFragmentManager().findFragmentByTag(TransactionSignatureDialogFragment.TAG);
        if (dialogFragment != null) {
            dialogFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, DelegateItemInfo delegateDetail) {
        Intent intent = new Intent(context, WithDrawActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_DETAIL, delegateDetail);
        context.startActivity(intent);
    }

}
