package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.WithDrawPopWindowAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.dialog.CommonGuideDialogFragment;
import com.juzix.wallet.component.ui.presenter.WithDrawPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.entity.WithDrawType;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;
import com.juzix.wallet.utils.UMEventUtil;

import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    @BindView(R.id.v_tips)
    View v_tips;

    private PopupWindow mPopupWindow;
    private ListView mPopListview;
    private List<WithDrawType> list = new ArrayList<>();
    private WithDrawPopWindowAdapter mPopWindowAdapter;
    private long transactionTime;
    private String gasPrice;
    private String withdraw_fee;//手续费
    private String free_account;//自由金额

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
        //初始化请求数据
        mPresenter.showWalletInfo();
        mPresenter.getBalanceType();
        mPresenter.getGas();
    }

    private void initView() {
        initPopWindow();
        setWithDrawButtonState(false);
        withdrawAmount.setFilters(new InputFilter[]{new PointLengthFilter()});
        withdrawAmount.addTextChangedListener(mAmountTextWatcher);

        RxView.clicks(chooseDelegate)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //弹窗选择类型
                        ShowPopWindow(chooseDelegate);
                    }
                });


        RxView.clicks(allAmount)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击全部
                        withdrawAmount.setText(delegateAmount.getText().toString().replaceAll(",", ""));
                    }
                });

        RxView.clicks(btnWithdraw)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        UMEventUtil.onEventCount(WithDrawActivity.this, Constants.UMEventID.WITHDRAW_DELEGATE);
                        //点击赎回按钮操作
                        String chooseType = WithDrawPopWindowAdapter.TAG_DELEGATED;

                        if (TextUtils.equals(delegateType.getText().toString(), getString(R.string.withdraw_type_delegated))) {
                            chooseType = WithDrawPopWindowAdapter.TAG_DELEGATED; //已委托
                        } else if (TextUtils.equals(delegateType.getText().toString(), getString(R.string.withdraw_type_unlocked))) {
                            chooseType = WithDrawPopWindowAdapter.TAG_UNLOCKED; //未锁定
                        } else {
                            chooseType = WithDrawPopWindowAdapter.TAG_RELEASED; //已解除
                        }

                        if(BigDecimalUtil.sub(free_account,withdraw_fee) < 0){ //赎回时，自用金额必须大于手续费，才能赎回
                            ToastUtil.showLongToast(getContext(),R.string.withdraw_less_than_fee);
                            return;
                        }
                        transactionTime = System.currentTimeMillis();
                        mPresenter.submitWithDraw(chooseType);
                    }
                });
        initGuide();

    }

    private void initGuide() {
        boolean isShowWithdrawOperation = AppSettings.getInstance().getWithdrawOperation();
        boolean isEnglish = Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage()) == true ? false : true;
        if(!isShowWithdrawOperation){
            CommonGuideDialogFragment.newInstance(CommonGuideDialogFragment.WITHDRAW_OPERATION,isEnglish)
                    .setKnowListener(new CommonGuideDialogFragment.knowListener() {
                        @Override
                        public void know() {
                            AppSettings.getInstance().setWithdrawOperation(true);
                        }
                    }).show(getSupportFragmentManager(),"withdrawOperation");
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
        } else if (TextUtils.equals(item.getKey(), WithDrawPopWindowAdapter.TAG_UNLOCKED)) {
            delegateType.setText(getString(R.string.withdraw_type_unlocked));
            withdrawAmount.setFocusableInTouchMode(true);
            withdrawAmount.setFocusable(true);
            withdrawAmount.setText("");
        } else {
            delegateType.setText(getString(R.string.withdraw_type_released)); //已解除
            withdrawAmount.setText(item.getValue().replace(",", ""));
            withdrawAmount.setFocusableInTouchMode(false);
            withdrawAmount.setFocusable(false);
            mPresenter.getWithDrawGasPrice(gasPrice);//已解除不能操作，所以需要再获取一次手续费
        }
        delegateAmount.setText(item.getValue());

    }


    /**
     * 基于某个位置显示弹窗
     *
     * @param view
     */
    public void ShowPopWindow(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    private TextWatcher mAmountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {//改变后

            mPresenter.checkWithDrawAmount(s.toString().trim());
            mPresenter.updateWithDrawButtonState();
            mPresenter.getWithDrawGasPrice(gasPrice);

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            etWalletAmount.setText(amountMagnitudes);
            etWalletAmount.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
            v_tips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

            if (count == 0) {
                return;
            }

            if (!TextUtils.isEmpty(s.toString()) && !TextUtils.equals(s.toString(), ".")) {

                if (BigDecimalUtil.sub(delegateAmount.getText().toString().replaceAll(",", ""), s.toString()) > 0) {
                    if (BigDecimalUtil.sub(delegateAmount.getText().toString().replaceAll(",", ""), s.toString()) < 10) {
                        withdrawAmount.setText(delegateAmount.getText().toString().replaceAll(",", ""));
                        withdrawAmount.setSelection(delegateAmount.getText().toString().replaceAll(",", "").length());
                    }
                }
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
        wallet_icon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));
        free_account = NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(individualWalletEntity.getAccountBalance().getFree(), "1E18"));
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

    }

    @Override
    public void showTips(boolean isShow) {
        tips.setText(getString(R.string.delegate_amount_tips));
        tips.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
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
    public String getBlockNumFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_NODE_BLOCK_NUM);
    }

    @Override
    public String getWalletAddressFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_WALLET_ADDRESS);
    }

    //显示节点基本信息
    @Override
    public void showNodeInfo(String address, String name, String nodeIcon) {
        GlideUtils.loadRound(getContext(), nodeIcon, node_icon);
        nodeName.setText(name);
        nodeAddress.setText(AddressFormatUtil.formatAddress(address));
    }

    @Override
    public void showBalanceType(double delegated, double unlocked, double released) {
        delegateType.setText(getString(R.string.withdraw_type_delegated));
        delegateAmount.setText(StringUtil.formatBalance(delegated, false));
        list.clear();
        list.add(new WithDrawType(WithDrawPopWindowAdapter.TAG_DELEGATED, StringUtil.formatBalance(delegated, false)));
        list.add(new WithDrawType(WithDrawPopWindowAdapter.TAG_UNLOCKED, StringUtil.formatBalance(unlocked, false)));
        list.add(new WithDrawType(WithDrawPopWindowAdapter.TAG_RELEASED, StringUtil.formatBalance(released, false)));
        mPopWindowAdapter.notifyDataSetChanged();
    }

    //获取输入的数量(edittext)
    @Override
    public String getInputAmount() {
        return withdrawAmount.getText().toString().trim();
    }

    //获取选中的类型数量
    @Override
    public String getChooseType() {
        return delegateAmount.getText().toString().replaceAll(",", ""); //这里要把逗号去掉，不然返回是0
    }

    @Override
    public void withDrawSuccessInfo(String platonSendTransaction,String hash, String from, String to, long time, String txType, String value, String actualTxCost, String nodeName, String nodeId, int txReceiptStatus) {
        Transaction transaction = new Transaction.Builder()
                .from(from)
                .to(to)
                .timestamp(transactionTime)
                .txType(txType)
                .unDelegation(Convert.toVon(withdrawAmount.getText().toString(), Convert.Unit.LAT).toBigInteger().toString())
                .actualTxCost(Convert.toVon(actualTxCost, Convert.Unit.LAT).toBigInteger().toString())
                .nodeName(nodeName)
                .nodeId(nodeId)
                .txReceiptStatus(txReceiptStatus)
                .hash(hash)
                .build();

        TransactionDetailActivity.actionStart(getContext(), transaction, from, "", platonSendTransaction);
        finish();
    }

    //显示手续费
    @Override
    public void showWithDrawGasPrice(String gas) {
        fee.setText(string(R.string.amount_with_unit, gas));
        withdraw_fee = gas;
    }

    //获取手续费
    @Override
    public String getGas() {
        return fee.getText().toString();
    }

    @Override
    public void showGas(BigInteger bigInteger) {
        gasPrice = bigInteger.toString();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    public static void actionStart(Context context, String nodeAddress, String nodeName, String nodeIcon, String stakingBlockNum, String walletAddress, String walletName, String walletIcon) {
        Intent intent = new Intent(context, WithDrawActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ADDRESS, nodeAddress);
        intent.putExtra(Constants.Extra.EXTRA_NODE_NAME, nodeName);
        intent.putExtra(Constants.Extra.EXTRA_NODE_ICON, nodeIcon);
        intent.putExtra(Constants.Extra.EXTRA_NODE_BLOCK_NUM, stakingBlockNum);//块高
        intent.putExtra(Constants.Extra.EXTRA_WALLET_ADDRESS, walletAddress);//钱包地址
        intent.putExtra(Constants.Extra.EXTRA_WALLET_NAME, walletName);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_ICON, walletIcon);
        context.startActivity(intent);
    }

}
