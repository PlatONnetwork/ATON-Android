package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.presenter.ValidatorsDetailPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.VerifyNodeDetail;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.GlideUtils;
import com.juzix.wallet.utils.RxUtils;

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
    @BindView(R.id.ll_validators_withdraw)
    LinearLayout withdraw;
    @BindView(R.id.ll_validators_delegate)
    LinearLayout delegate;


    @Override
    protected ValidatorsDetailPresenter createPresenter() {
        return new ValidatorsDetailPresenter(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validators_detail);
        unbinder = ButterKnife.bind(this);
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


        //todo 测试暂时写这里
        clickViewListener(null);

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

    public void clickViewListener(VerifyNodeDetail nodeDetail) {

        RxView.clicks(withdraw).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo
//                        WithDrawActivity.actionStart(getContext(), nodeDetail.getNodeUrl(), nodeDetail.getName(), nodeDetail.getUrl());
                        WithDrawActivity.actionStart(getContext(), "", "", "");
                    }
                });

        RxView.clicks(delegate).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        // todo
//                        DelegateActivity.actionStart(getContext(), nodeDetail.getNodeUrl(), nodeDetail.getName(), nodeDetail.getUrl());
                        DelegateActivity.actionStart(getContext(), "", "", "");
                    }
                });

        RxView.clicks(webSite).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo  暂时写的百度链接
                        Uri uri = Uri.parse("https://www.baidu.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

        RxView.clicks(nodeLink).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo  暂时写的百度链接
                        Uri uri = Uri.parse("https://www.baidu.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

    }

    @Override
    public void showValidatorsDetailData(VerifyNodeDetail nodeDetail) {
        GlideUtils.loadRound(this, nodeDetail.getUrl(), iv_url);
        nodeName.setText(nodeDetail.getName());
        nodeAddress.setText(AddressFormatUtil.formatAddress(nodeDetail.getNodeUrl()));
        nodeState.setText(nodeDetail.getNodeStatus());
        rate.setText(nodeDetail.getRatePA() + "%");
        totalStaked.setText(nodeDetail.getDeposit());
        delegation.setText(nodeDetail.getDelegateSum() + "");
        delegators.setText(nodeDetail.getDelegate());
        slash.setText(nodeDetail.getPunishNumber() + "");
        blocks.setText(nodeDetail.getBlockOutNumber() + "");
        blockRate.setText(nodeDetail.getBlockRate());
        introduction.setText(nodeDetail.getIntro());
        webSite.setText(nodeDetail.getWebsite());//官网
        clickViewListener(nodeDetail);

    }

    @Override
    public void showValidatorsDetailFailed() {

    }
}
