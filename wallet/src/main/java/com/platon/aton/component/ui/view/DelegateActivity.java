package com.platon.aton.component.ui.view;

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
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.DelegatePopAdapter;
import com.platon.aton.component.ui.contract.DelegateContract;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.dialog.TransactionSignatureDialogFragment;
import com.platon.aton.component.ui.presenter.DelegatePresenter;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.MyWatcher;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.component.widget.VerticalImageSpan;
import com.platon.aton.engine.AppConfigManager;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegateType;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.aton.entity.GuideType;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GlideUtils;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.SoftHideKeyboardUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.aton.utils.UMEventUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.PreferenceTool;
import com.platon.framework.utils.RUtils;
import com.platon.framework.utils.ToastUtil;

import org.web3j.platon.StakingAmountType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 委托操作页面
 *
 * @author ziv
 */

public class DelegateActivity extends BaseActivity<DelegateContract.View, DelegatePresenter> implements DelegateContract.View {

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
    /**
     * 选择的钱包类型（可用余额/锁仓余额）
     */
    private StakingAmountType stakingAmountType;
    private List<DelegateType> typeList = new ArrayList<>();

    private PopupWindow mPopupWindow;
    private ListView mPopListview;
    private DelegatePopAdapter mAdapter;
    /**
     * 是否点击的全部
     */
    private boolean isAll = false;
    /**
     * 手续费
     */
    private String feeAmount;
    /**
     * 自由金额
     */
    private String freeBalance;

    @Override

    public DelegatePresenter createPresenter() {
        return new DelegatePresenter();
    }

    @Override
    public DelegateContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        initView();
        getPresenter().init(getDelegateDetailFromIntent());
        getPresenter().showWalletInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_delegate;
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
                        getPresenter().showSelectWalletDialogFragment();
                    }
                });

        RxView.clicks(amounChoose)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //选择余额类型
                        showPopWindow(amounChoose);
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
                                    getPresenter().getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), true);
                                }
                            }, getString(R.string.action_delegate_all_amount), new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    isAll = true;
                                    //点击全部
                                    getPresenter().getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), false);
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
                            getPresenter().getAllPrice(stakingAmountType, amount.getText().toString().replace(",", ""), false);
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

                        if (!TransactionManager.getInstance().isAllowSendTransaction(getPresenter().getWalletAddress(), currentTime)) {
                            showLongToast(string(R.string.msg_wait_finished_transaction_tips, DateUtil.millisecondToMinutes(TransactionManager.getInstance().getSendTransactionTimeInterval(getPresenter().getWalletAddress(), currentTime))));
                            return;
                        }
                        getPresenter().submitDelegate(stakingAmountType);
                    }
                });
    }

    private void initGuide() {
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_DELEGATE_OPERATION, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_DELEGATE_OPERATION, true);
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
        stakingAmountType = item.getStakingAmountType();
        amountType.setText(stakingAmountType == StakingAmountType.FREE_AMOUNT_TYPE ? getString(R.string.available_balance) : getString(R.string.locked_balance));
        amount.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(item.getAmount(), "1E18"))), false));
    }

    /**
     * 基于某个位置显示弹窗
     *
     * @param view
     */
    public void showPopWindow(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    private TextWatcher delegateWatcher = new MyWatcher(-1, 8) {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            getPresenter().checkDelegateAmount(et_amount.getText().toString().trim().replace(",", ""));

            getPresenter().updateDelegateButtonState();

            String amountMagnitudes = StringUtil.getAmountMagnitudes(getContext(), s.toString().trim());
            inputTips.setText(amountMagnitudes);
            inputTips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);
            v_tips.setVisibility(TextUtils.isEmpty(amountMagnitudes) ? View.GONE : View.VISIBLE);

            getPresenter().getGasProvider(stakingAmountType); //获取手续费
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
    public void showSelectedWalletInfo(Wallet wallet) {
        stakingAmountType = StakingAmountType.FREE_AMOUNT_TYPE;
        //显示钱包基本信息
        walletName.setText(wallet.getName());
        //钱包地址
        walletAddress.setText(AddressFormatUtil.formatAddress(wallet.getPrefixAddress()));
        walletIcon.setImageResource(RUtils.drawable(wallet.getAvatar()));
    }

    private void checkIsClick(EstimateGasResult estimateGasResult) {
        if (BigDecimalUtil.isBiggerThanZero(estimateGasResult.getLock())) {
            iv_drop_down.setVisibility(View.VISIBLE);
            tv_lat_two.setVisibility(View.GONE);
            tv_lat_one.setVisibility(View.VISIBLE);
            amounChoose.setClickable(true);
        } else {
            //锁仓金额为0，不可点击
            amounChoose.setClickable(false);
            iv_drop_down.setVisibility(View.GONE);
            tv_lat_one.setVisibility(View.GONE);
            tv_lat_two.setVisibility(View.VISIBLE);
        }
        freeBalance = estimateGasResult.getFree();

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
        inputError.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    @Override
    public void showIsCanDelegate(EstimateGasResult estimateGasResult) {
        et_amount.setHint(getString(R.string.withdraw_tip, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(estimateGasResult.getMinDelegation(), "1E18"))));
        typeList.clear();
        typeList.add(new DelegateType(StakingAmountType.FREE_AMOUNT_TYPE, estimateGasResult.getFree()));
        typeList.add(new DelegateType(StakingAmountType.RESTRICTING_AMOUNT_TYPE, estimateGasResult.getLock()));
        amountType.setText(getString(R.string.available_balance));
        amount.setText(StringUtil.formatBalance((BigDecimalUtil.div(estimateGasResult.getFree(), "1E18"))));
        checkIsClick(estimateGasResult);
    }

    @Override
    public void showDelegateException(int errorCode) {

        et_amount.setHint(getString(R.string.withdraw_tip, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(AppConfigManager.getInstance().getMinDelegation(), "1E18"))));

        all.setClickable(false);
        et_amount.setText("");
        et_amount.setFocusableInTouchMode(false);
        et_amount.setFocusable(false);
        if (errorCode == 3006) {
            //节点已退出或退出中
            btnDelegate.setEnabled(false);
            tv_no_delegate_tips.setVisibility(View.VISIBLE);
            setImageIconForText(tv_no_delegate_tips, getString(R.string.the_validator_has_exited_and_cannot_be_delegated));
        } else if (errorCode == 3007) {
            btnDelegate.setEnabled(false);
            tv_no_delegate_tips.setVisibility(View.VISIBLE);
            setImageIconForText(tv_no_delegate_tips, getString(R.string.tips_not_delegate));
        } else {
            btnDelegate.setEnabled(false);
            tv_no_delegate_tips.setVisibility(View.VISIBLE);
            setImageIconForText(tv_no_delegate_tips, getString(R.string.tips_not_balance));
        }
    }

    @Override
    public void showDelegateResult(String minDelegation) {

        et_amount.setHint(getString(R.string.withdraw_tip, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(minDelegation, "1E18"))));

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
