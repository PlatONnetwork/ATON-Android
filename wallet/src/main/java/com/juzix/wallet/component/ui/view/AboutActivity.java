package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.network.SchedulersTransformer;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.engine.VersionManager;
import com.juzix.wallet.engine.VersionUpdate;
import com.juzix.wallet.entity.VersionInfo;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ShareUtil;
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

        RxView.clicks(tvAboutUs)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), "https://www.platon.network", WebType.WEB_TYPE_COMMON);
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
        VersionManager.getInstance().getVersion()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new Consumer<VersionInfo>() {
                    @Override
                    public void accept(VersionInfo versionEntity) {
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (!oldVersion.startsWith("v")) {
                            oldVersion = "v" + oldVersion;
                        }
                        String newVersion = versionEntity.getVersion().toLowerCase();
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
                });
    }

    private void update() {
        VersionManager.getInstance().getVersion()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new Consumer<VersionInfo>() {
                    @Override
                    public void accept(VersionInfo versionEntity) {
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (!oldVersion.startsWith("v")) {
                            oldVersion = "v" + oldVersion;
                        }
                        String newVersion = versionEntity.getVersion().toLowerCase();
                        if (!newVersion.startsWith("v")) {
                            newVersion = "v" + newVersion;
                        }
                        if (oldVersion.compareTo(newVersion) >= 0) {
                            tvUpdate.setText(string(R.string.current_version, oldVersion));
                            vNewMsg.setVisibility(View.GONE);
                            showLongToast(string(R.string.latest_version_tips));
                            return;
                        }

                        mVersionUpdate = new VersionUpdate(AboutActivity.this, versionEntity.getDownloadUrl(), versionEntity.getVersion(), false);

                        tvUpdate.setText(string(R.string.latest_version, newVersion));
                        vNewMsg.setVisibility(View.VISIBLE);
                        if (VersionManager.getInstance().isDownloading(versionEntity.getDownloadUrl())) {
                            showLongToast(string(R.string.download_tips));
                            return;
                        }
                        showUpdateVersionDialog(versionEntity);
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
                                .subscribe(new Consumer<Permission>() {
                                    @Override
                                    public void accept(Permission permission) throws Exception {
                                        if (permission.granted && Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission.name)) {
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
