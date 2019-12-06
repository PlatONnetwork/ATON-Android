package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.engine.DeviceManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.VersionUpdate;
import com.juzix.wallet.entity.VersionInfo;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.utils.RxUtils;
import com.meituan.android.walle.WalleChannelReader;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.v_new_msg)
    View vNewMsg;
    @BindView(R.id.tv_privacy_policy)
    TextView tvPrivacyPolicy;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    private Unbinder unbinder;
    private VersionUpdate mVersionUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        unbinder = ButterKnife.bind(this);
        initViews();
        checkVersion();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        String versionName = AndroidUtil.getVersionName(this);
        if (!versionName.toLowerCase().startsWith("v"))
            versionName = "v" + versionName;
        vNewMsg.setVisibility(View.GONE);
        tvUpdate.setText(string(R.string.current_version, versionName));

        RxView
                .longClicks(ivLogo)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        showLongToast(DeviceManager.getInstance().getChannel());
                    }
                });

        RxView.clicks(tvAboutUs)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), getResources().getString(R.string.web_url_web_portals), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView.clicks(llUpdate)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        update();
                    }
                });

        RxView.clicks(tvPrivacyPolicy)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        CommonHybridActivity.actionStart(AboutActivity.this, getString(R.string.web_url_privacy_policy), WebType.WEB_TYPE_COMMON);
                    }
                });
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

    private void checkVersion() {
        ServerUtils
                .getCommonApi()
                .getVersionInfo()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new ApiSingleObserver<VersionInfo>() {
                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (!oldVersion.startsWith("v")) {
                            oldVersion = "v" + oldVersion;
                        }
                        String newVersion = versionInfo.getVersion().toLowerCase();
                        if (!newVersion.startsWith("v")) {
                            newVersion = "v" + newVersion;
                        }
                        if (oldVersion.compareTo(newVersion) < 0) {
                            tvUpdate.setText(string(R.string.latest_version, newVersion));
                            vNewMsg.setVisibility(View.VISIBLE);
                        } else {
                            tvUpdate.setText(string(R.string.current_version, oldVersion));
                            vNewMsg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    private void update() {
        ServerUtils
                .getCommonApi()
                .getVersionInfo()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new ApiSingleObserver<VersionInfo>() {
                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (!oldVersion.startsWith("v")) {
                            oldVersion = "v" + oldVersion;
                        }
                        String newVersion = versionInfo.getVersion().toLowerCase();
                        if (!newVersion.startsWith("v")) {
                            newVersion = "v" + newVersion;
                        }
                        if (oldVersion.compareTo(newVersion) >= 0) {
                            tvUpdate.setText(string(R.string.current_version, oldVersion));
                            vNewMsg.setVisibility(View.GONE);
                            showLongToast(string(R.string.latest_version_tips));
                            return;
                        }

                        mVersionUpdate = new VersionUpdate(AboutActivity.this, versionInfo.getDownloadUrl(), versionInfo.getVersion(), false);

                        tvUpdate.setText(string(R.string.latest_version, newVersion));
                        vNewMsg.setVisibility(View.VISIBLE);

                        showUpdateVersionDialog(versionInfo);
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }


    private void showUpdateVersionDialog(VersionInfo versionInfo) {
        CommonTipsDialogFragment.createDialogWithTitleAndTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                string(R.string.version_update),
                string(R.string.version_update_tips, versionInfo.getAndroidVersionInfo().getVersion()),
                string(R.string.update_now), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                        new RxPermissions(currentActivity())
                                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new CustomObserver<Permission>() {
                                    @Override
                                    public void accept(Permission permission) {
                                        if (permission.granted && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission.name)) {
                                            mVersionUpdate.execute();
                                        }
                                    }
                                });
                    }
                },
                string(R.string.not_now), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                    }
                }).show(currentActivity().getSupportFragmentManager(), "showTips");
    }
}
