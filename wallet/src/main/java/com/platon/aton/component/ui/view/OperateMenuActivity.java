package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.component.widget.ShadowContainer;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.PreferenceTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OperateMenuActivity extends BaseActivity {

    @BindView(R.id.sc_import_wallet)
    ShadowContainer scImportWallet;
    @BindView(R.id.sc_create_wallet)
    ShadowContainer scCreateWallet;

    Unbinder unbinder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_operate_menu;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG,true)) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.sc_create_wallet, R.id.sc_import_wallet})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_create_wallet:
                CreateWalletActivity.actionStart(this);
                break;
            case R.id.sc_import_wallet:
                ImportWalletActivity.actionStart(this);
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, OperateMenuActivity.class);
        context.startActivity(intent);
    }

    public static void actionStartWithFlag(Context context) {
        Intent intent = new Intent(context, OperateMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
