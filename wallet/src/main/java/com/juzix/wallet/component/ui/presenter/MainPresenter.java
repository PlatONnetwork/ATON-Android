package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MainContract;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.VersionUpdate;
import com.juzix.wallet.entity.VersionInfo;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.RxUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private VersionUpdate mVersionUpdate;

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    public void checkVersion() {
        ServerUtils
                .getCommonApi()
                .getVersionInfo()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<VersionInfo>() {

                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        if (isViewAttached()) {
                            if (shouldUpdate(versionInfo)) {
                                mVersionUpdate = new VersionUpdate(currentActivity(), versionInfo.getDownloadUrl(), versionInfo.getVersion(), false);
                                //如果不是强制更新，则保存上次弹框时间
                                if (!versionInfo.getAndroidVersionInfo().isForce()) {
                                    AppSettings.getInstance().setUpdateVersionTime(System.currentTimeMillis());
                                }
                                showUpdateVersionDialog(versionInfo);
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    private boolean shouldUpdate(VersionInfo versionInfo) {
        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
        if (!oldVersion.startsWith("v")) {
            oldVersion = "v" + oldVersion;
        }
        String newVersion = versionInfo.getVersion().toLowerCase();
        if (!newVersion.startsWith("v")) {
            newVersion = "v" + newVersion;
        }

        long lastUpdateTime = AppSettings.getInstance().getUpdateVersionTime();
        boolean shouldUpdate = oldVersion.compareTo(newVersion) < 0;
        boolean isForce = versionInfo.getAndroidVersionInfo().isForce();
        boolean shouldShowUpdateDialog = !(lastUpdateTime != 0 && DateUtil.isToday(lastUpdateTime));

        return (shouldUpdate && isForce) || (shouldUpdate && shouldShowUpdateDialog);
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
                        requestPermission();
                    }
                },
                string(R.string.not_now), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (versionInfo.getAndroidVersionInfo().isForce()) {
                            CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips), "退出应用?", "取消", new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showUpdateVersionDialog(versionInfo);
                                }
                            }, "确认", new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    if (isViewAttached()) {
                                        getView().exitApp();
                                    }
                                }
                            }).show(((MainActivity) getContext()).getSupportFragmentManager(), "showExistDialog");
                        }
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                    }
                }, !versionInfo.getAndroidVersionInfo().isForce()).show(currentActivity().getSupportFragmentManager(), "showTips");
    }

    private void requestPermission() {

        new RxPermissions(currentActivity())
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new CustomObserver<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (isViewAttached()) {
                            if (permission.granted && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission.name)) {
                                mVersionUpdate.execute();
                            }
                        }
                    }
                });
    }

}
