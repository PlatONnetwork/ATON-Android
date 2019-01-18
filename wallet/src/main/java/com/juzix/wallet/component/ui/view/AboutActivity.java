package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.utils.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        tvAppVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.tv_about_us, R.id.tv_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_about_us:
                ShareUtil.shareUrl(getContext(), "https://www.platon.network");
                break;
            case R.id.tv_update:
                ShareUtil.shareUrl(getContext(), "https://github.com/PlatONnetwork");
                break;
        }
    }
}
