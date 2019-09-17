package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MainContract;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.VersionManager;
import com.juzix.wallet.engine.VersionUpdate;
import com.juzix.wallet.entity.VersionInfo;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.RxUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private VersionUpdate mVersionUpdate;

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    public void checkVersion() {
        long lastUpdateTime = AppSettings.getInstance().getUpdateVersionTime();
        if (lastUpdateTime != 0 && DateUtil.isToday(lastUpdateTime)) {
            return;
        }
        VersionManager.getInstance().getVersion()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<VersionInfo>() {
                    @Override
                    public void accept(VersionInfo versionEntity) {
                        if (isViewAttached()) {
                            String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                            if (!oldVersion.startsWith("v")) {
                                oldVersion = "v" + oldVersion;
                            }
                            String newVersion = versionEntity.getVersion().toLowerCase();
                            if (!newVersion.startsWith("v")) {
                                newVersion = "v" + newVersion;
                            }
                            if (oldVersion.compareTo(newVersion) < 0) {
                                mVersionUpdate = new VersionUpdate(currentActivity(), versionEntity.getDownloadUrl(), versionEntity.getVersion(), false);
                                AppSettings.getInstance().setUpdateVersionTime(System.currentTimeMillis());
                                showUpdateVersionDialog(versionEntity);
                            }
                        }
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
                                        if (isViewAttached()) {
                                            if (permission.granted && Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission.name)){
                                                mVersionUpdate.execute();
                                            }
                                        }
                                    }
                                });
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

}
