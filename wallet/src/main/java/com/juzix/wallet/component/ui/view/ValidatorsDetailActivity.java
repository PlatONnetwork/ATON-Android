package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ValidatorsDetailContract;
import com.juzix.wallet.component.ui.dialog.DelegateTipsDialog;
import com.juzix.wallet.component.ui.presenter.ValidatorsDetailPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ValidatorsDetailActivity extends MVPBaseActivity<ValidatorsDetailPresenter> implements ValidatorsDetailContract.View {
    private Unbinder unbinder;

    @BindView(R.id.commonTitleBar)
    CommonTitleBar titleBar;
    @BindView(R.id.iv_url)
    ImageView iv_url;
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

        RxView.clicks(withdraw).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        WithDrawActivity.actionStart(getContext(),"","","");
                    }
                });

        RxView.clicks(delegate).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {

                    @Override
                    public void accept(Object o) {
                        DelegateActivity.actionStart(getContext(),"","","");
                    }
                });

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ValidatorsDetailActivity.class);
        context.startActivity(intent);
    }

}
