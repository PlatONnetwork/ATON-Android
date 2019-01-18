package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.AppSettings;

public class OperateMenuActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = OperateMenuActivity.class.getSimpleName();

    public static void actionStart(Context context){
        context.startActivity(new Intent(context, OperateMenuActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_menu);
        findViewById(R.id.btn_create_wallet).setOnClickListener(this);
        findViewById(R.id.btn_import_wallet).setOnClickListener(this);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.color_1b2137;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AppSettings.getInstance().getOperateMenuFlag()){
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create_wallet:
                CreateIndividualWalletActivity.actionStart(this);
                break;
            case R.id.btn_import_wallet:
                ImportIndividualWalletActivity.actionStart(this);
                break;
        }
    }
}
