package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.DelegatePopAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.presenter.DelegatePresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.PointLengthFilter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.iv_drop_down)
    ImageView iv_drop_down;

    private String address;//钱包地址
    private String chooseType;//选择的钱包类型（可用余额/锁仓余额）
    private List<AccountBalance> balanceList = new ArrayList<>();
    private List<DelegateType> typeList = new ArrayList<>();

    private PopupWindow mPopupWindow;
    private ListView mPopListview;
    private DelegatePopAdapter mAdapter;

    private long transactionTime;

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
        mPresenter.checkIsCanDelegate();
    }

    private void initView() {
        initPopWindow();
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
                        ShowPopWindow(amounChoose);
                    }
                });


        RxView.clicks(all)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击全部
                        et_amount.setText(amount.getText().toString().replace(",", ""));
                        Log.d("DelegateActivity11111111", " ======================" + amount.getText().toString());
                    }
                });


        RxView.clicks(btnDelegate).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //点击委托操作
                        transactionTime = System.currentTimeMillis();
                        mPresenter.submitDelegate(chooseType);
                    }
                });
    }

    private void initPopWindow() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popwindow_delegate, null);
        mPopListview = view.findViewById(R.id.listview_popwindow);
        mPopListview.setDivider(null);
        mPopListview.setVerticalScrollBarEnabled(false);//隐藏侧滑栏
        mAdapter = new DelegatePopAdapter(getContext(), typeList);
        mPopListview.setAdapter(mAdapter);
        mAdapter.setDefSelect(0);//设置默认第一项选中
        mPopListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setDefSelect(position);
                mPopupWindow.dismiss();
                //刷新数据
                refreshData(mAdapter.getItem(position));
            }
        });

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        //设置弹出窗体可点击
        mPopupWindow.setFocusable(true);

    }

    private void refreshData(DelegateType item) {
        chooseType = item.getType() == "0" ? "balance" : "locked";
        amountType.setText(TextUtils.equals(item.getType(), "0") ? getString(R.string.available_balance) : getString(R.string.locked_balance));
        String number = StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getAmount(), "1E18"))), false);
        amount.setText(number);
    }

    /**
     * 基于某个位置显示弹窗
     *
     * @param view
     */
    public void ShowPopWindow(View view) {
        mPopupWindow.showAsDropDown(view);
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
        address = individualWalletEntity.getPrefixAddress();
        //显示钱包基本信息
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));//钱包地址
        walletIcon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));

        //显示余额类型和余额
        amountType.setText(getString(R.string.available_balance));
        amount.setText(StringUtil.formatBalance(individualWalletEntity.getFreeBalance(), false));

        typeList.clear();
        for (AccountBalance bean : balanceList) {
            if (TextUtils.equals(address, bean.getAddr())) {
                //获取当前选中钱包的余额信息
                typeList.add(new DelegateType("0", bean.getFree()));
                typeList.add(new DelegateType("1", bean.getLock()));
                checkIsClick(bean);
                return;
            }
        }
        mAdapter.notifyDataSetChanged();

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
        return amount.getText().toString().replaceAll(",","");
    }

    /**
     * 获取所有钱包余额列表
     *
     * @param accountBalances
     */
    @Override
    public void getWalletBalanceList(List<AccountBalance> accountBalances) {
        balanceList.clear();
        typeList.clear();
        balanceList.addAll(accountBalances);
        //选中钱包
        String walletAddress = address;
        for (AccountBalance bean : accountBalances) {
            // todo 这里先写的一个假的钱包地址
            if (TextUtils.equals(walletAddress, bean.getAddr())) {
                //获取当前选中钱包的余额信息
                typeList.add(new DelegateType("0", bean.getFree()));
                typeList.add(new DelegateType("1", bean.getLock()));
                checkIsClick(bean);
                return;
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private void checkIsClick(AccountBalance bean) {
        if (TextUtils.isEmpty(bean.getLock())) {
            //不可选择余额类型
//            amounChoose.setOnClickListener(null);
            amounChoose.setClickable(false);
            amounChoose.setEnabled(false);
            iv_drop_down.setVisibility(View.INVISIBLE);
        } else {
            iv_drop_down.setVisibility(View.VISIBLE);
            amounChoose.setClickable(true);
        }


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
    public void showNodeInfo(String address, String name, String UrlIcon) {
        //显示节点基本信息
        GlideUtils.loadRound(getContext(), UrlIcon, nodeIcon);
        nodeName.setText(name);
        nodeAddress.setText(AddressFormatUtil.formatAddress(address));
    }

    @Override
    public void showIsCanDelegate(DelegateHandle bean) {
        //判断是否允许委托
        if (!bean.isCanDelegation()) {
            //表示不能委托
            if (TextUtils.equals(bean.getMessage(), "1")) { //不能委托原因：1.解除委托金额大于0
                btnDelegate.setEnabled(false);
                showLongToast(getString(R.string.delegate_no_click));
            } else { //节点已退出或退出中
                btnDelegate.setEnabled(false);
                showLongToast(getString(R.string.the_Validator_has_exited_and_cannot_be_delegated));
            }
        } else {
            //可以委托
            btnDelegate.setEnabled(true);
        }


    }

    @Override
    public void transactionSuccessInfo(String from, String to, long time, String txType, String value, String actualTxCost, String nodeName, String nodeId, int txReceiptStatus) {
        finish();
        Transaction transaction = new Transaction.Builder()
                .from(from)
                .to(to)
                .timestamp(transactionTime)
                .txType(txType)
                .value(value)
                .actualTxCost(actualTxCost)
                .nodeName(nodeName)
                .nodeId(nodeId)
                .txReceiptStatus(txReceiptStatus)
                .build();

        TransactionDetailActivity.actionStart(getContext(), transaction, from);
    }

    /**
     * 下面四个方法是获取intent传递的值
     *
     * @return
     */
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
    protected boolean immersiveBarViewEnabled() {
        return true;
    }
}
