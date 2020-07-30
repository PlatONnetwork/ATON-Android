package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.ValidatorsDetailContract;
import com.platon.aton.component.ui.dialog.DelegateTipsDialog;
import com.platon.aton.component.ui.presenter.ValidatorsDetailPresenter;
import com.platon.aton.component.widget.CircleImageView;
import com.platon.aton.component.widget.RoundedTextView;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.NodeStatus;
import com.platon.aton.entity.VerifyNodeDetail;
import com.platon.aton.entity.WebType;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.GlideUtils;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ziv
 */
public class ValidatorsDetailActivity extends BaseActivity<ValidatorsDetailContract.View, ValidatorsDetailPresenter> implements ValidatorsDetailContract.View {

    @BindView(R.id.civ_wallet_avatar)
    CircleImageView civWalletAvatar;
    @BindView(R.id.tv_detail_node_name)
    TextView tvDetailNodeName;
    @BindView(R.id.rtv_detail_node_state)
    RoundedTextView rtvDetailNodeState;
    @BindView(R.id.iv_detail_node_link)
    ImageView ivDetailNodeLink;
    @BindView(R.id.tv_detail_node_address)
    TextView tvDetailNodeAddress;
    @BindView(R.id.tv_total_staked)
    TextView tvTotalStaked;
    @BindView(R.id.tv_total_staked_amount)
    TextView tvTotalStakedAmount;
    @BindView(R.id.tv_delegated)
    TextView tvDelegated;
    @BindView(R.id.tv_total_delegated_amount)
    TextView tvTotalDelegatedAmount;
    @BindView(R.id.tv_delegators)
    TextView tvDelegators;
    @BindView(R.id.tv_delegators_count)
    TextView tvDelegatorsCount;
    @BindView(R.id.tv_blocks)
    TextView tvBlocks;
    @BindView(R.id.tv_blocks_number)
    TextView tvBlocksNumber;
    @BindView(R.id.tv_blocks_rate)
    TextView tvBlocksRate;
    @BindView(R.id.tv_blocks_rate_number)
    TextView tvBlocksRateNumber;
    @BindView(R.id.tv_slash)
    TextView tvSlash;
    @BindView(R.id.tv_slash_count)
    TextView tvSlashCount;
    @BindView(R.id.tv_detail_introduction)
    TextView tvDetailIntroduction;
    @BindView(R.id.tv_detail_website)
    TextView tvDetailWebsite;
    @BindView(R.id.sv_content)
    ScrollView svContent;
    @BindView(R.id.sbtn_delegate)
    ShadowButton sbtnDelegate;
    @BindView(R.id.tv_no_delegate_tips)
    TextView tvNoDelegateTips;
    @BindView(R.id.ll_guide)
    LinearLayout llGuide;
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;
    @BindView(R.id.layout_no_network)
    LinearLayout layoutNoNetwork;
    @BindView(R.id.tv_delegate_reward_ratio_amount)
    AppCompatTextView tvDelegateRewardRatioAmount;
    @BindView(R.id.tv_total_reward_amount)
    TextView tvTotalRewardAmount;
    @BindView(R.id.tv_delegate_yield_amount)
    TextView tvDelegateYieldAmount;
    @BindView(R.id.tv_delegate_yield)
    TextView tvDelegateYield;
    @BindView(R.id.tv_delegate_reward_ratio)
    TextView tvDelegateRewardRatio;
    @BindView(R.id.group)
    Group group;

    private Unbinder unbinder;

    @Override
    public ValidatorsDetailContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_validators_detail;
    }

    @Override
    public ValidatorsDetailPresenter createPresenter() {
        return new ValidatorsDetailPresenter();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public void onResume() {
        getPresenter().loadValidatorsDetailData();
        MobclickAgent.onPageStart(Constants.UMPages.NODE_DETAIL);
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.NODE_DETAIL);
        super.onPause();
    }

    private void initView() {

        RxView.clicks(sbtnDelegate)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        DelegateActivity.actionStart(getContext(), getPresenter().getDelegateDetail());
                    }
                });

        RxView.clicks(tvDetailWebsite)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), tvDetailWebsite.getText().toString(), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView.clicks(ivDetailNodeLink)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), tvDetailWebsite.getText().toString(), WebType.WEB_TYPE_NODE_DETAIL);
                    }
                });

        RxView.clicks(tvRefresh)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        getPresenter().loadValidatorsDetailData();
                    }
                });

        RxView.clicks(tvDelegateRewardRatio)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //弹出tips
                        DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                                null, null, string(R.string.msg_delegation_reward_ratio), string(R.string.msg_delegation_reward_ratio_tips)).show(getSupportFragmentManager(), "validatorstip");
                    }
                });

        RxView.clicks(tvDelegateYield)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //弹出tips
                        DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                                null, null, string(R.string.msg_delegation_annual_yield), string(R.string.msg_delegation_annual_yield_tips)).show(getSupportFragmentManager(), "validatorstip");
                    }
                });

        getPresenter().loadValidatorsDetailData();

    }


    @Override
    public String getNodeIdFromIntent() {
        return getIntent().getStringExtra(Constants.ValidatorsType.VALIDATORS_NODEID);
    }

    @Override
    public void showValidatorsDetailData(VerifyNodeDetail nodeDetail) {

        if (nodeDetail != null) {

            GlideUtils.loadRound(this, nodeDetail.getUrl(), civWalletAvatar);

            tvDetailNodeName.setText(nodeDetail.getName());
            tvDetailNodeAddress.setText(AddressFormatUtil.formatAddress(nodeDetail.getNodeId()));
            rtvDetailNodeState.setText(getResources().getString(NodeManager.getInstance().getNodeStatusDescRes(nodeDetail.getNodeStatus(),nodeDetail.isConsensus())));

            tvDelegateYieldAmount.setText(String.format("%s%%", NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDelegatedRatePA(), "100"))));
            Drawable delegatedRatePATrend = nodeDetail.isShowDelegatedRatePATrend() ? nodeDetail.isDelegatedRatePATrendRose() ? ContextCompat.getDrawable(this, R.drawable.icon_rose) : ContextCompat.getDrawable(this, R.drawable.icon_fell) : null;
            tvDelegateYieldAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, delegatedRatePATrend, null);
            tvDelegateRewardRatioAmount.setText(String.format("%s%%", NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDelegatedRewardPer(), "100"))));
            tvTotalRewardAmount.setText(string(R.string.amount_with_unit, StringUtil.formatBalance(AmountUtil.convertVonToLatWithFractionDigits(nodeDetail.getCumulativeReward(), 8))));
            tvTotalStakedAmount.setText(AmountUtil.formatAmountText(nodeDetail.getDeposit()));
            tvTotalDelegatedAmount.setText(AmountUtil.formatAmountText(nodeDetail.getDelegateSum()));

            tvDelegatorsCount.setText(getCommonFormatText(nodeDetail.getDelegate()));
            tvSlashCount.setText(StringUtil.formatBalanceWithoutFractionDigits(String.valueOf(nodeDetail.getPunishNumber())));
            tvBlocksNumber.setText(nodeDetail.getBlockOutNumber() == 0 ? "--" : StringUtil.formatBalanceWithoutFractionDigits(String.valueOf(nodeDetail.getBlockOutNumber())));
            tvBlocksRateNumber.setText(String.format("%s%%", NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getBlockRate(), "100"))));

            tvDetailIntroduction.setText(getCommonFormatText(nodeDetail.getIntro()));
            tvDetailWebsite.setText(getCommonFormatText(nodeDetail.getWebsite()));
            tvDetailWebsite.setTextColor(TextUtils.isEmpty(nodeDetail.getWebsite()) ? ContextCompat.getColor(this, R.color.color_000000) : ContextCompat.getColor(this, R.color.color_105cfe));

            ivDetailNodeLink.setVisibility(TextUtils.isEmpty(nodeDetail.getWebsite()) ? View.GONE : View.VISIBLE);

            tvNoDelegateTips.setText(getDelegateTips(nodeDetail));
            tvNoDelegateTips.setVisibility(getTipsVisibility(nodeDetail));
            sbtnDelegate.setEnabled(isDelegateBtnEnable(nodeDetail));

            group.setVisibility(nodeDetail.isInit() ? View.GONE : View.VISIBLE);
        }

        svContent.setVisibility(nodeDetail != null ? View.VISIBLE : View.GONE);
        layoutNoNetwork.setVisibility(nodeDetail != null ? View.GONE : View.VISIBLE);
        llGuide.setVisibility(nodeDetail != null ? View.VISIBLE : View.GONE);

    }

    private String getCommonFormatText(String text) {
        return TextUtils.isEmpty(text) ? "- -" : text;
    }

    private int getTipsVisibility(VerifyNodeDetail nodeDetail) {

        //节点是否退出
        boolean isNodeExit = TextUtils.equals(NodeStatus.EXITED, nodeDetail.getNodeStatus()) || TextUtils.equals(NodeStatus.EXITING, nodeDetail.getNodeStatus());
        //节点状态是否为初始化验证人（收益地址为激励池地址的验证人）
        boolean isInit = nodeDetail.isInit();
        //客户端钱包列表是否为空
        boolean isWalletAddressListEmpty = WalletManager.getInstance().getAddressList().isEmpty();

        return isNodeExit || isInit || isWalletAddressListEmpty ? View.VISIBLE : View.GONE;

    }

    private boolean isDelegateBtnEnable(VerifyNodeDetail nodeDetail) {

        //节点是否退出
        boolean isNodeExit = TextUtils.equals(NodeStatus.EXITED, nodeDetail.getNodeStatus()) || TextUtils.equals(NodeStatus.EXITING, nodeDetail.getNodeStatus());
        //节点是否锁定
        boolean isNodeLocked = TextUtils.equals(NodeStatus.LOCKED, nodeDetail.getNodeStatus()) || TextUtils.equals(NodeStatus.LOCKED, nodeDetail.getNodeStatus());
        //节点状态是否为初始化验证人（收益地址为激励池地址的验证人）
        boolean isInit = nodeDetail.isInit();
        //客户端钱包列表是否为空
        boolean isWalletAddressListEmpty = WalletManager.getInstance().getAddressList().isEmpty();

        return !isNodeExit && !isInit && !isWalletAddressListEmpty && !isNodeLocked;
    }

    private String getDelegateTips(VerifyNodeDetail nodeDetail) {
        //节点是否退出
        boolean isNodeExit = TextUtils.equals(NodeStatus.EXITED, nodeDetail.getNodeStatus()) || TextUtils.equals(NodeStatus.EXITING, nodeDetail.getNodeStatus());
        //节点状态是否为初始化验证人（收益地址为激励池地址的验证人）
        boolean isInit = nodeDetail.isInit();
        //客户端钱包列表是否为空
        boolean isWalletAddressListEmpty = WalletManager.getInstance().getAddressList().isEmpty();

        String delegateTips = "";

        if (isNodeExit) {
            delegateTips = getString(R.string.the_validator_has_exited_and_cannot_be_delegated);
        }

        if (isInit) {
            delegateTips = getString(R.string.validators_details_tips);
        }

        if (isWalletAddressListEmpty) {
            delegateTips = getString(R.string.tips_no_wallet);
        }

        return delegateTips;
    }


    private int getNodeStatusTextAndBorderColor(@NodeStatus String nodeStatus, boolean isConsensus) {
        switch (nodeStatus) {
            case NodeStatus.ACTIVE:
                return isConsensus ? R.color.color_f79d10 : R.color.color_4a90e2;
            case NodeStatus.CANDIDATE:
                return R.color.color_19a20e;
            case NodeStatus.EXITED:
                return R.color.color_9eabbe;
            default:
                return R.color.color_525768;
        }

    }

    @Override
    public void showIsCanDelegate(boolean isCanDelegate) {
        if (!isCanDelegate) {//表示不能委托
            ToastUtil.showLongToast(getContext(), R.string.tips_no_wallet);
        } else {
//            DelegateDetail delegateDetail = new DelegateDetail();
//            delegateDetail.setNodeName(mNodeName);
//            delegateDetail.setNodeId(mNodeAddress);
//            delegateDetail.setUrl(mNodeIcon);
//            DelegateActivity.actionStart(getContext(), delegateDetail);
        }
    }

    /**
     * event事件，刷新的操作
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateValidatorsPageEvent(Event.UpdateValidatorsDetailEvent event) {
        //刷新页面
        getPresenter().loadValidatorsDetailData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, String nodeId) {
        Intent intent = new Intent(context, ValidatorsDetailActivity.class);
        intent.putExtra(Constants.ValidatorsType.VALIDATORS_NODEID, nodeId);
        context.startActivity(intent);
    }
}
