package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.presenter.ValidatorsDetailPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.component.widget.VerticalImageSpan;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.juzix.wallet.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ValidatorsDetailActivity extends MVPBaseActivity<ValidatorsDetailPresenter> implements ValidatorsDetailContract.View {
    private Unbinder unbinder;

    @BindView(R.id.commonTitleBar)
    CommonTitleBar titleBar;
    @BindView(R.id.iv_url)
    CircleImageView iv_url;
    @BindView(R.id.tv_detail_node_name)
    TextView nodeName;
    @BindView(R.id.tv_detail_node_state)
    RoundedTextView nodeState;
    @BindView(R.id.iv_detail_node_link)
    ImageView nodeLink;
    @BindView(R.id.tv_detail_node_address)
    TextView nodeAddress;
    @BindView(R.id.tv_detail_rate)
    TextView rate;
    @BindView(R.id.tv_total_staked)
    TextView totalStaked;
    @BindView(R.id.tv_delegations)
    TextView delegation;
    @BindView(R.id.tv_delegators)
    TextView delegators;
    @BindView(R.id.tv_slash)
    TextView slash;
    @BindView(R.id.tv_blocks)
    TextView blocks;
    @BindView(R.id.tv_block_rate)
    TextView blockRate;
    @BindView(R.id.tv_detail_introduction)
    TextView introduction;
    @BindView(R.id.tv_detail_website)
    TextView webSite;
    @BindView(R.id.sbtn_delegate)
    ShadowButton delegate;
    @BindView(R.id.tv_no_delegate_tips)
    TextView tips;
    @BindView(R.id.ll_shade)
    LinearLayout ll_shade;
    @BindView(R.id.layout_no_network)
    LinearLayout noNetworkLayout;
    @BindView(R.id.sv_content)
    ScrollView contentSV;
    @BindView(R.id.ll_guide)
    LinearLayout ll_guide;
    @BindView(R.id.tv_refresh)
    TextView refreshTv;

    public static final String STATE_ACTIVE = "Active";
    public static final String STATE_CANDIDATE = "Candidate";
    public static final String STATE_EXITING = "Exiting";
    public static final String STATE_EXITED = "Exited";
    private String websiteUrl;
    private String mNodeAddress;
    private String mNodeName;
    private String mNodeIcon;

    @Override
    protected ValidatorsDetailPresenter createPresenter() {
        return new ValidatorsDetailPresenter(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validators_detail);
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initView();
    }

    @Override
    public void onResume() {
        mPresenter.loadValidatorsDetailData();
        MobclickAgent.onPageStart(Constants.UMPages.NODE_DETAIL);
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.NODE_DETAIL);
        super.onPause();
    }

    private void initView() {
        ShadowDrawable.setShadowDrawable(ll_shade,
                ContextCompat.getColor(this, R.color.color_ffffff),
                DensityUtil.dp2px(this, 4),
                ContextCompat.getColor(this, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(this, 10),
                0,
                DensityUtil.dp2px(this, 2));

        titleBar.setRightDrawable(R.drawable.icon_tips);
        titleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出tips
                DelegateTipsDialog.createWithTitleAndContentDialog(null, null,
                        null, null, string(R.string.expected_annualized_rate), string(R.string.expected_annualized_rate_des)).show(getSupportFragmentManager(), "validatorstip");
            }
        });
        clickViewListener();

    }

    public static void actionStart(Context context, String nodeId) {
        Intent intent = new Intent(context, ValidatorsDetailActivity.class);
        intent.putExtra(Constants.ValidatorsType.VALIDATORS_NODEID, nodeId);
        context.startActivity(intent);
    }

    @Override
    public String getNodeIdFromIntent() {
        return getIntent().getStringExtra(Constants.ValidatorsType.VALIDATORS_NODEID);
    }

    public void clickViewListener() {

        RxView.clicks(delegate)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        //点击委托不需要做任何判断，跳转就行
                        DelegateDetail delegateDetail = new DelegateDetail();
                        delegateDetail.setNodeName(mNodeName);
                        delegateDetail.setNodeId(mNodeAddress);
                        delegateDetail.setUrl(mNodeIcon);
                        DelegateActivity.actionStart(getContext(), delegateDetail);
                    }
                });

        RxView.clicks(webSite)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), webSite.getText().toString(), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView.clicks(nodeLink)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), websiteUrl, WebType.WEB_TYPE_NODE_DETAIL);
                    }
                });

        RxView.clicks(refreshTv)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.loadValidatorsDetailData();
                    }
                });

    }

    @Override
    public void showValidatorsDetailData(VerifyNodeDetail nodeDetail) {
        nodeState.setVisibility(View.VISIBLE);
        websiteUrl = nodeDetail.getWebsite();
        mNodeAddress = nodeDetail.getNodeId();
        mNodeName = nodeDetail.getName();
        mNodeIcon = nodeDetail.getUrl();

        GlideUtils.loadRound(this, nodeDetail.getUrl(), iv_url);
        nodeName.setText(nodeDetail.getName());
        nodeAddress.setText(AddressFormatUtil.formatAddress(nodeDetail.getNodeId()));

        if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_ACTIVE)) {
            nodeState.setText(nodeDetail.isConsensus() ? getString(R.string.validators_verifying) : getString(R.string.validators_state_active));
        } else if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_CANDIDATE)) {
            nodeState.setText(getString(R.string.validators_state_candidate));
        } else if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) {
            nodeState.setText(getString(R.string.validators_state_exited));
        } else {
            nodeState.setText(getString(R.string.validators_state_exiting));
        }

        changeTextBgAndTextColor(nodeState, nodeDetail);

        rate.setText(nodeDetail.isInit() ? "- -" : String.format("%s%%", StringUtil.formatBalance(BigDecimalUtil.div(nodeDetail.getRatePA(), "100"))));

        if (TextUtils.isEmpty(nodeDetail.getDeposit())) {
            totalStaked.setText("- -");
        } else {
            totalStaked.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble
                    (NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDeposit(), "1E18"))), false)); //总质押
        }

        if (TextUtils.isEmpty(nodeDetail.getDelegateSum())) {
            delegation.setText("- -");
        } else {
            delegation.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble
                    (NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDelegateSum(), "1E18"))), false)); // //接受委托
        }

        delegators.setText(nodeDetail.getDelegate());
        slash.setText(nodeDetail.getPunishNumber() + "");  //处罚次数
        blocks.setText(nodeDetail.getBlockOutNumber() == 0 ? "--" : nodeDetail.getBlockOutNumber() + "");
        blockRate.setText(NumberParserUtils.parseDouble(nodeDetail.getBlockRate()) / 100 + "%");

        introduction.setText(TextUtils.isEmpty(nodeDetail.getIntro()) ? "- -" : nodeDetail.getIntro());
        webSite.setText(TextUtils.isEmpty(websiteUrl) ? "- -" : websiteUrl);//官网
        webSite.setTextColor(TextUtils.isEmpty(websiteUrl) ? ContextCompat.getColor(this, R.color.color_000000) : ContextCompat.getColor(this, R.color.color_105cfe));

        if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITING) || TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) {
            //b.节点退出中或已退出
            tips.setVisibility(View.VISIBLE);
            setImageIconForText(tips, getString(R.string.the_validator_has_exited_and_cannot_be_delegated));
            delegate.setEnabled(false);
        } else if (nodeDetail.isInit()) { //c.节点状态为初始化验证人（收益地址为激励池地址的验证人）
            tips.setVisibility(View.VISIBLE);
            setImageIconForText(tips, getString(R.string.validators_details_tips));
            delegate.setEnabled(false);
        } else if (WalletManager.getInstance().getAddressList().isEmpty()) { // a.客户端本地没有钱包
            tips.setVisibility(View.VISIBLE);
            setImageIconForText(tips, getString(R.string.tips_no_wallet));
            delegate.setEnabled(false);
        } else {
            delegate.setEnabled(true);
            tips.setVisibility(View.GONE);
        }

        contentSV.setVisibility(View.VISIBLE);
        noNetworkLayout.setVisibility(View.GONE);
        ll_guide.setVisibility(View.VISIBLE);
        nodeLink.setVisibility(TextUtils.isEmpty(websiteUrl) ? View.GONE : View.VISIBLE);

    }

    private void changeTextBgAndTextColor(RoundedTextView textView, VerifyNodeDetail verifyNodeDetail) {
        switch (verifyNodeDetail.getNodeStatus()) {
            case STATE_ACTIVE:
                textView.setRoundedBorderColor(ContextCompat.getColor(textView.getContext(), verifyNodeDetail.isConsensus() ? R.color.color_f79d10 : R.color.color_4a90e2));
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), verifyNodeDetail.isConsensus() ? R.color.color_f79d10 : R.color.color_4a90e2));
                break;
            case STATE_CANDIDATE:
                textView.setRoundedBorderColor(ContextCompat.getColor(textView.getContext(), R.color.color_19a20e));
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_19a20e));
                break;
            case STATE_EXITED:
                textView.setRoundedBorderColor(ContextCompat.getColor(textView.getContext(), R.color.color_9eabbe));
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_9eabbe));
                break;
            case STATE_EXITING:
                textView.setRoundedBorderColor(ContextCompat.getColor(textView.getContext(), R.color.color_525768));
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.color_525768));
                break;
            default:
                break;
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
    public void showValidatorsDetailFailed() {
        nodeState.setVisibility(View.GONE);
        tips.setVisibility(View.GONE);

        contentSV.setVisibility(View.GONE);
        noNetworkLayout.setVisibility(View.VISIBLE);
        ll_guide.setVisibility(View.GONE);
    }

    @Override
    public void showIsCanDelegate(boolean isCanDelegate) {
        if (!isCanDelegate) {//表示不能委托
            ToastUtil.showLongToast(getContext(), R.string.tips_no_wallet);
        } else {
            DelegateDetail delegateDetail = new DelegateDetail();
            delegateDetail.setNodeName(mNodeName);
            delegateDetail.setNodeId(mNodeAddress);
            delegateDetail.setUrl(mNodeIcon);
            DelegateActivity.actionStart(getContext(), delegateDetail);
        }
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }


    /**
     * event事件，刷新的操作
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateValidatorsPageEvent(Event.UpdateValidatorsDetailEvent event) {
        //刷新页面
        mPresenter.loadValidatorsDetailData();
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updatePageEvent(Event.UpdateRefreshPageEvent event) {
//        mPresenter.loadValidatorsDetailData();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
