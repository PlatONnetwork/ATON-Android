package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.juzhen.framework.util.AndroidUtil;
import com.juzhen.framework.util.crypt.Base64Utils;
import com.juzhen.framework.util.crypt.MD5Utils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MainContract;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.config.JZDirType;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.VersionManager;
import com.juzix.wallet.entity.VersionEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.DateUtil;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter{

    private       WalletEntity    mSelectedWallet;

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        checkVersion();
    }

    private void checkVersion() {
        long lastUpdateTime = AppSettings.getInstance().getUpdateVersionTime();
        if (lastUpdateTime != 0 && DateUtil.isToday(lastUpdateTime)) {
            return;
        }
        VersionManager.getInstance().getVersion()
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VersionEntity>() {
                    @Override
                    public void accept(VersionEntity versionEntity) {
                        String oldVersion = AndroidUtil.getVersionName(getContext()).toLowerCase();
                        if (!oldVersion.startsWith("v")){
                            oldVersion = "v" + oldVersion;
                        }
                        String newVersion = versionEntity.getVersion().toLowerCase();
                        if (!newVersion.startsWith("v")){
                            newVersion = "v" + newVersion;
                        }
                        if (oldVersion.compareTo(newVersion) < 0) {
                            AppSettings.getInstance().setUpdateVersionTime(System.currentTimeMillis());
                            CommonTipsDialogFragment.createDialogWithTitleAndTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                                    string(R.string.version_update),
                                    string(R.string.version_update_tips, newVersion),
                                    string(R.string.update_now), new OnDialogViewClickListener() {
                                        @Override
                                        public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                            if (fragment != null) {
                                                fragment.dismiss();
                                            }
                                            requestPermission(currentActivity(), 100, new PermissionConfigure.PermissionCallback() {
                                                @Override
                                                public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                                                    download(versionEntity.getDownloadUrl());
                                                }

                                                @Override
                                                public void onHasPermission(int what) {
                                                    download(versionEntity.getDownloadUrl());
                                                }

                                                @Override
                                                public void onFail(int what, @NonNull List<String> deniedPermissions) {

                                                }
                                            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    private void download(String url) {
        JZAppConfigure.getInstance().getDir(currentActivity(), JZDirType.raw, new JZAppConfigure.DirCallback() {
            @Override
            public void callback(File dir) {
                if (dir == null) {
                    return;
                }
                VersionManager.getInstance().download(url, dir, new String(Base64Utils.encodeToString(MD5Utils.encode(url))) + ".apk");
            }
        });

    }

    @Override
    public WalletEntity getSelectedWallet() {
        return mSelectedWallet;
    }

    @Override
    public void setSelectedWallet(WalletEntity walletEntity) {
        mSelectedWallet = walletEntity;
        EventPublisher.getInstance().sendUpdateSelectedWalletEvent(mSelectedWallet);
    }
}
