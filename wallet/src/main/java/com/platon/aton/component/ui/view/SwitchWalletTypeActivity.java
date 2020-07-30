package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.entity.WalletType;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.LanguageUtil;
import com.platon.framework.utils.PreferenceTool;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SwitchWalletTypeActivity extends BaseActivity {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_switch_ordinary)
    TextView tvSwitchOrdinary;
    @BindView(R.id.tv_switch_hd)
    TextView tvSwitchHD;
    @BindString(R.string.wallet_type_ordinary)
    String walletOrdinary;
    @BindString(R.string.wallet_type_hd)
    String walletHD;

    private Unbinder unbinder;
    private Locale locale;

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch_wallettype;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {
       int data = getIntent().getIntExtra(Constants.Extra.EXTRA_WALLET_TYPE,0);
       updateSelectedWalletStatus(data);
    }




    private void updateSelectedWalletStatus(int walletType) {

        if (walletType == WalletType.ORDINARY_WALLET) {
            tvSwitchOrdinary.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_hook_s, 0);
            tvSwitchHD.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            tvSwitchOrdinary.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvSwitchHD.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_hook_s, 0);
        }

    }

    @OnClick({R.id.tv_switch_ordinary, R.id.tv_switch_hd})
    public void onClick(View view) {
        int walletType = 0;
        switch (view.getId()) {
            case R.id.tv_switch_ordinary:
                walletType = WalletType.ORDINARY_WALLET;
                break;
            case R.id.tv_switch_hd:
                walletType = WalletType.HD_WALLET;
            default:
                break;
        }
        updateSelectedWalletStatus(walletType);

        Intent intent = new Intent();
        intent.putExtra(Constants.Extra.EXTRA_WALLET_TYPE, walletType);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartForResult(Context context, int walletType, int requestCode) {
        Intent intent = new Intent(context, SwitchWalletTypeActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET_TYPE,walletType);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
