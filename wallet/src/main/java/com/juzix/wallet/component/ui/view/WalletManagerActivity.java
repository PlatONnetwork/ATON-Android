package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class WalletManagerActivity extends BaseActivity {

    @BindView(R.id.iv_scan)
    ImageView   ivBack;
    @BindView(R.id.iv_add)
    ImageView   ivAdd;
    @BindView(R.id.rb_individual_wallet)
    RadioButton rbIndividualWallet;
    @BindView(R.id.rb_shared_wallet)
    RadioButton rbSharedWallet;

    Unbinder unbinder;

    enum Page {
        INDIVIDUAL_WALLET, SHARED_WALLET
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, WalletManagerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_manager);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivBack.setImageResource(R.drawable.icon_back_black);
        ivAdd.setVisibility(View.GONE);
        showPage(Page.INDIVIDUAL_WALLET);
    }

    @OnClick({R.id.iv_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                this.finish();
                break;
            default:
                break;
        }
    }

    @OnCheckedChanged({R.id.rb_individual_wallet, R.id.rb_shared_wallet})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!isChecked) {
            return;
        }

        switch (buttonView.getId()) {
            case R.id.rb_individual_wallet:
                rbIndividualWallet.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
                rbIndividualWallet.setTextSize(16);
                rbSharedWallet.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                rbSharedWallet.setTextSize(12);
                showPage(Page.INDIVIDUAL_WALLET);
                break;
            case R.id.rb_shared_wallet:
                rbIndividualWallet.setTextColor(ContextCompat.getColor(this, R.color.color_cdcdcd));
                rbIndividualWallet.setTextSize(12);
                rbSharedWallet.setTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
                rbSharedWallet.setTextSize(16);
                showPage(Page.SHARED_WALLET);
                break;
            default:
                break;
        }
    }

    private void showPage(Page page) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(page.toString());

        if (fragment != null) {
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment);
            }
        }

        for (Page p : Page.values()) {
            Fragment f = fragmentManager.findFragmentByTag(p.toString());
            if (p != page && f != null) {
                if (f.isAdded()) {
                    fragmentTransaction.hide(f);
                }
            }
        }

        if (fragment == null) {
            switch (page) {
                case INDIVIDUAL_WALLET:
                    fragmentTransaction.add(R.id.layout_content, IndividualWalletManagerFragment.newInstance(), page.toString());
                    break;
                case SHARED_WALLET:
                    fragmentTransaction.add(R.id.layout_content, SharedWalletManagerFragment.newInstance(), page.toString());
                    break;
                default:
                    break;
            }
        }

        fragmentTransaction.commitNowAllowingStateLoss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
