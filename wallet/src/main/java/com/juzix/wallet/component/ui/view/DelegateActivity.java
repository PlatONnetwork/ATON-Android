package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
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
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.DelegatePopAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.BaseDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonGuideDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.TransactionSignatureDialogFragment;
import com.juzix.wallet.component.ui.presenter.DelegatePresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.MyWatcher;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.component.widget.VerticalImageSpan;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.DelegateItemInfo;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.entity.GuideType;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.AmountUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.CommonTextUtils;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.NumberParserUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.SoftHideKeyboardUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;
import com.juzix.wallet.utils.UMEventUtil;

import org.web3j.platon.StakingAmountType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 委托操作页面
 */

public class DelegateActivity extends MVPBaseActivity<DelegatePresenter> implements DelegateContract.View {

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
    @BindView(R.id.tv_lat)
    TextView tv_lat;
    @BindView(R.id.v_tips)
    View v_tips;
    @BindView(R.id.tv_no_delegate_tips)
    TextView tv_no_delegate_tips;
    @BindView(R.id.tv_lat_one)
    TextView tv_lat_one;
    @BindView(R.id.tv_lat_two)
    TextView tv_lat_two;

    private Unbinder unbinder;
    private StakingAmountType stakingAmountType;//选择的钱包类型（可用余额/锁仓余额）
    private List<DelegateType> typeList = new ArrayList<>();

    private PopupWindow mPopupWindow;
    private ListView mPopListview;
    private DelegatePopAdapter mAdapter;
    private boolean isAll = false;//是否点击的全部
    private String feeAmount;//手续费
    private String freeBalance;//自由金额
    private boolean isCanDelegate;//是否允许委托

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
        mPresenter.getGas();
    }

    private void initView() {
        initShade();
        initPopWindow();
        setDelegateButtonState(false);
        et_amount.addTextChangedListener(delegateWatcher);
        initClick();
        initGuide();
        SoftHideKeyboardUtils.assistActivity(this);
    }

    private void initShade() {
        ShadowDrawable.setShadowDrawable(amounChoose,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 5),
                0,
                DensityUtil.dp2px(this, 2));
    }

    private void initClick() {

        RxView.clicks(walletChoose).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });

        RxView.clicks(amounChoose)
                .compose(RxUtils.bindToLifecycle(this))
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

                        if (stakingAmountType == StakingAmountType.FREE_AMOUNT_TYPE) {
                            CommonTipsDialogFragment dialogFragment = CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(DelegateActivity.this, R.drawable.icon_dialog_tips), getString(R.string.msg_delegate_all_amount_tips), getString(R.string.action_keep_delegate_balance), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    isAll = false;
                                    //点击全部
                                    mPresenter.getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), true);
                                }
                            }, getString(R.string.action_delegate_all_amount), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    isAll = true;
                                    //点击全部
                                    mPresenter.getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), false);
                                }
                            });

                            getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                                    super.onFragmentStarted(fm, f);
                                    if (f.getClass() == CommonTipsDialogFragment.class) {
                                        TextView textView = ((CommonTipsDialogFragment.FixedDialog) ((CommonTipsDialogFragment) f).getDialog()).buttonConfirm.getTextView();
                                        CommonTextUtils.richText(textView, getString(R.string.action_keep_delegate_balance), "\\(.*\\)", new AbsoluteSizeSpan(DensityUtil.dp2px(DelegateActivity.this, 12)));
                                    }
                                }
                            }, false);
                            dialogFragment.show(getSupportFragmentManager(), "showDelegateAllAmountTipsDialog");
                        } else {
                            isAll = true;
                            //点击全部
                            mPresenter.getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), false);
                        }

                    }
                });


        RxView.clicks(btnDelegate)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {

                        UMEventUtil.onEventCount(DelegateActivity.this, Constants.UMEventID.DELEGATE);

                        //点击委托操作
                        if (BigDecimalUtil.sub(freeBalance, feeAmount).doubleValue() < 0) { //可用余额大于手续费才能委托
                            ToastUtil.showLongToast(getContext(), R.string.delegate_less_than_fee);
                            return;
                        }

                        long currentTime = System.currentTimeMillis();

                        if (!TransactionManager.getInstance().isAllowSendTransaction(mPresenter.getWalletAddress(), currentTime)) {
                            showLongToast(string(R.string.msg_wait_finished_transaction_tips, DateUtil.millisecondToMinutes(TransactionManager.getInstance().getSendTransactionTimeInterval(mPresenter.getWalletAddress(), currentTime))));
                            return;
                        }
                        mPresenter.submitDelegate(stakingAmountType);
                    }
                });
    }

    private void initGuide() {
        if (!AppSettings.getInstance().getDelegateOperationBoolean()) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    AppSettings.getInstance().setDelegateOperationBoolean(true);
                }
            }).show(getSupportFragmentManager(), "showGuideDialogFragment");
        }
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
        stakingAmountType = item.getType() == "0" ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;
        amountType.setText(TextUtils.equals(item.getType(), "0") ? getString(R.string.available_balance) : getString(R.string.locked_balance));
        amount.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getAmount(), "1E18"))), false));
    }

    /**
     * 基于某个位置显示弹窗
     *
     * @param view
     */
    public void ShowPopWindow(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    private TextWatcher delegateWatcher = new MyWatcher(-1, 8) {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            mPresenter.checkDelegateAmount(et_amount.getText().toString().trim().replace(",", ""));

            mPresenter.updateDelegateButtonState();

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            inputTips.setText(amountMagnitudes);
            inputTips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
            v_tips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

            mPresenter.getGasProvider(stakingAmountType); //获取手续费
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
        }
    };

    public static void actionStart(Context context, DelegateItemInfo delegateDetail) {
        Intent intent = new Intent(context, DelegateActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_DETAIL, delegateDetail);
        context.startActivity(intent);
    }

    /**
     * 显示节点基本信息
     */
    @Override
    public void showNodeInfo(DelegateItemInfo delegateDetail) {
        //显示节点基本信息
        GlideUtils.loadRound(getContext(), delegateDetail.getUrl(), nodeIcon);
        nodeName.setText(delegateDetail.getNodeName());
        nodeAddress.setText(AddressFormatUtil.formatAddress(delegateDetail.getNodeId()));
    }

    //显示钱包信息
    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        stakingAmountType = StakingAmountType.FREE_AMOUNT_TYPE;
        //显示钱包基本信息
        walletName.setText(individualWalletEntity.getName());
        walletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));//钱包地址
        walletIcon.setImageResource(RUtils.drawable(individualWalletEntity.getAvatar()));
    }

    private void checkIsClick(DelegateHandle bean) {
        if (TextUtils.equals(bean.getLock(), "0")) {
            //锁仓金额为0，不可点击
            amounChoose.setClickable(false);
            iv_drop_down.setVisibility(View.GONE);
            tv_lat_one.setVisibility(View.GONE);
            tv_lat_two.setVisibility(View.VISIBLE);

        } else {
            iv_drop_down.setVisibility(View.VISIBLE);
            tv_lat_two.setVisibility(View.GONE);
            tv_lat_one.setVisibility(View.VISIBLE);
            amounChoose.setClickable(true);
        }
        freeBalance = bean.getFree();

    }

    @Override
    public void setDelegateButtonState(boolean isClickable) {
        btnDelegate.setEnabled(isClickable);
    }

    @Override
    public DelegateItemInfo getDelegateDetailFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_DELEGATE_DETAIL);
    }

    //获取输入的数量
    @Override
    public String getDelegateAmount() {
        return et_amount.getText().toString().trim();
    }

    @Override
    public void showTips(boolean isShow, String minDelegation) {
        inputError.setText(getString(R.string.delegate_amount_tips, minDelegation));
        inputError.setVisibility(isShow && isCanDelegate ? View.VISIBLE : View.GONE);
    }


    @Override
    public void showIsCanDelegate(DelegateHandle bean) {
        isCanDelegate = bean.isCanDelegation();
        typeList.clear();
        typeList.add(new DelegateType("0", bean.getFree()));
        typeList.add(new DelegateType("1", bean.getLock()));
        amountType.setText(getString(R.string.available_balance));
        amount.setText(StringUtil.formatBalance((BigDecimalUtil.div(bean.getFree(), "1E18"))));
        checkIsClick(bean);
        //判断是否允许委托
        checkIsCanDelegate(bean);

    }

    private void checkIsCanDelegate(DelegateHandle bean) {

        et_amount.setHint(getString(R.string.withdraw_tip, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(bean.getMinDelegation(), "1E18"))));

        if (!bean.isCanDelegation()) {
            all.setClickable(false);
            et_amount.setText("");
            et_amount.setFocusableInTouchMode(false);
            et_amount.setFocusable(false);
            //表示不能委托
            if (TextUtils.equals(bean.getMessage(), "1")) { //不能委托原因：1.解除委托金额大于0
                btnDelegate.setEnabled(false);
                showLongToast(getString(R.string.delegate_no_click));
            } else if (TextUtils.equals(bean.getMessage(), "2")) { //节点已退出或退出中
                btnDelegate.setEnabled(false);
                tv_no_delegate_tips.setVisibility(View.VISIBLE);
                setImageIconForText(tv_no_delegate_tips, getString(R.string.the_validator_has_exited_and_cannot_be_delegated));
            } else if (TextUtils.equals(bean.getMessage(), "3")) {
                btnDelegate.setEnabled(false);
                tv_no_delegate_tips.setVisibility(View.VISIBLE);
                setImageIconForText(tv_no_delegate_tips, getString(R.string.tips_not_delegate));
            } else {
                btnDelegate.setEnabled(false);
                tv_no_delegate_tips.setVisibility(View.VISIBLE);
                setImageIconForText(tv_no_delegate_tips, getString(R.string.tips_not_balance));
            }
        } else {
            all.setClickable(true);
            et_amount.setFocusableInTouchMode(true);
            et_amount.setFocusable(true);
            tv_no_delegate_tips.setVisibility(View.GONE);
            //可以委托,判断数量是否大于10
            if (NumberParserUtils.parseDouble(et_amount.getText().toString()) >= 10) {
                btnDelegate.setEnabled(true);
            } else {
                btnDelegate.setEnabled(false);
            }
        }
    }

    public void setImageIconForText(TextView textView, String content) {
        SpannableString spannableString = new SpannableString("  " + content);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_no_delegate_tips);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        spannableString.setSpan(new VerticalImageSpan(drawable), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    @Override
    public void showTransactionSuccessInfo(Transaction transaction) {
        finish();
        TransactionDetailActivity.actionStart(getContext(), transaction, Arrays.asList(transaction.getFrom()));
    }

    /**
     * 显示手续费
     *
     * @param feeAmount
     */
    @Override
    public void showFeeAmount(String feeAmount) {
        if (!isAll) {
            fee.setText(NumberParserUtils.getPrettyBalance(AmountUtil.convertVonToLat(feeAmount, 8)));
        }
        isAll = false;
        this.feeAmount = feeAmount;
    }

    /**
     * 点击所有获取到的手续费
     */
    @Override
    public void showAllFeeAmount(StakingAmountType stakingAmountType, String delegateAmount, String feeAmount) {
        fee.setText(AmountUtil.convertVonToLat(feeAmount, 8));
        if (BigDecimalUtil.isBiggerThanZero(delegateAmount)) {
            et_amount.setText(AmountUtil.convertVonToLat(delegateAmount, 8));
        } else {
            et_amount.setText(BigDecimalUtil.parseString(0.00));
            ToastUtil.showLongToast(getContext(), R.string.delegate_less_than_fee);
        }
        et_amount.setSelection(et_amount.getText().toString().length());
        this.feeAmount = feeAmount;
    }

    @Override
    public String getFeeAmount() {
        return fee.getText().toString();
    }

    @Override
    public void clearInputDelegateAmount() {
        et_amount.setText("");
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
}
