package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.component.widget.TextViewDrawable;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

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
    TextView nodeState;
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
    TextViewDrawable tips;

//    @BindView(R.id.ll_validators_withdraw)
//    LinearLayout withdraw;
//    @BindView(R.id.ll_validators_delegate)
//    LinearLayout delegate;

    public static final String STATE_ACTIVE = "Active";
    public static final String STATE_CANDIDATE = "Candidate";
    public static final String STATE_EXITING = "Exiting";
    public static final String STATE_EXITED = "Exited";
    private String websiteUrl;

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

    private void initView() {
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
        mPresenter.loadValidatorsDetailData();
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

//        RxView.clicks(withdraw).compose(RxUtils.bindToLifecycle(this))
//                .compose(RxUtils.getClickTransformer())
//                .subscribe(new CustomObserver<Object>() {
//                    @Override
//                    public void accept(Object o) {
//                        //todo
////                        WithDrawActivity.actionStart(getContext(), nodeDetail.getNodeUrl(), nodeDetail.getName(), nodeDetail.getUrl());
//                        WithDrawActivity.actionStart(getContext(), "", "", "");
//                    }
//                });

        RxView.clicks(delegate).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        // todo
//                        DelegateActivity.actionStart(getContext(), nodeDetail.getNodeUrl(), nodeDetail.getName(), nodeDetail.getUrl());
                        DelegateActivity.actionStart(getContext(), "", "", "", 1);
                    }
                });

        RxView.clicks(webSite).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), webSite.getText().toString(), WebType.WEB_TYPE_COMMON);
//                        Uri uri = Uri.parse("https://www.baidu.com");
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
                    }
                });

        RxView.clicks(nodeLink).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
//                        Uri uri = Uri.parse("https://www.baidu.com");
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
                        CommonHybridActivity.actionStart(getContext(), websiteUrl,WebType.WEB_TYPE_COMMON);
                    }
                });

    }

    @Override
    public void showValidatorsDetailData(VerifyNodeDetail nodeDetail) {
        websiteUrl = nodeDetail.getWebsite();

        GlideUtils.loadRound(this, nodeDetail.getUrl(), iv_url);
        nodeName.setText(nodeDetail.getName());
        nodeAddress.setText(AddressFormatUtil.formatAddress(nodeDetail.getNodeUrl()));
        if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_ACTIVE)) {
            nodeState.setText(getString(R.string.validators_state_active));
        } else if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_CANDIDATE)) {
            nodeState.setText(getString(R.string.validators_state_candidate));
        } else if (TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) {
            nodeState.setText(getString(R.string.validators_state_exited));
        } else {
            nodeState.setText(getString(R.string.validators_state_exiting));
        }

        rate.setText((NumberParserUtils.parseDouble(nodeDetail.getRatePA())) / 100 + "%");

        if (TextUtils.isEmpty(nodeDetail.getDeposit())) {
            totalStaked.setText("--");
        } else {
            totalStaked.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble
                    (NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDeposit(), "1E18"))), false)); //总质押
        }

        if (TextUtils.isEmpty(nodeDetail.getDelegateSum())) {
            delegation.setText("--");
        } else {
            delegation.setText(StringUtil.formatBalance(NumberParserUtils.parseDouble
                    (NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(nodeDetail.getDelegateSum(), "1E18"))), false)); // //接受委托
        }

        delegators.setText(nodeDetail.getDelegate());
        slash.setText(nodeDetail.getPunishNumber() + "");  //处罚次数
        blocks.setText(nodeDetail.getBlockOutNumber() == 0 ? "--" : nodeDetail.getBlockOutNumber() + "");
        blockRate.setText(NumberParserUtils.parseInt(nodeDetail.getBlockRate()) * 100 + "%");

        introduction.setText(nodeDetail.getIntro());
        webSite.setText(nodeDetail.getWebsite());//官网

        //判断按钮是否可点击
        if ((TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITING) || TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) && nodeDetail.isInit()) {
            tips.setVisibility(View.VISIBLE);
            delegate.setEnabled(false);

        } else if ((TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITING) || TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) && !nodeDetail.isInit()) {
            tips.setVisibility(View.GONE);
            delegate.setEnabled(false);

        } else if (!(TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITING) || TextUtils.equals(nodeDetail.getNodeStatus(), STATE_EXITED)) && nodeDetail.isInit()) {
            tips.setVisibility(View.VISIBLE);
            delegate.setEnabled(false);
        } else {
            tips.setVisibility(View.GONE);
            delegate.setEnabled(true);
        }


    }

    @Override
    public void showValidatorsDetailFailed() {

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
    public void onUpdateTransactionEvent(Event.UpdateValidatorsDetailEvent event) {
        //刷新页面
        mPresenter.loadValidatorsDetailData();
    }


}
