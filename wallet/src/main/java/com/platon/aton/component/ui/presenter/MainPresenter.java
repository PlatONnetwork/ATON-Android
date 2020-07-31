package com.platon.aton.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.MainContract;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.component.ui.view.MainActivity;
import com.platon.aton.engine.DeviceManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.VersionUpdate;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.VersionInfo;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.utils.PreferenceTool;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private ArrayList<Wallet> wallets = new ArrayList<>();

    @Override
    public void checkVersion() {
        ServerUtils
                .getCommonApi()
                .getVersionInfo(ApiRequestBody.newBuilder()
                        .put("versionCode", BuildConfig.VERSION_CODE)
                        .put("deviceType", DeviceManager.getInstance().getOS())
                        .put("channelCode", DeviceManager.getInstance().getChannel())
                        .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<VersionInfo>() {

                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        if (isViewAttached()) {
                            if (shouldUpdate(versionInfo)) {
                                //如果不是强制更新，则保存上次弹框时间
                                if (!versionInfo.isForce()) {
                                    PreferenceTool.putLong(Constants.Preference.KEY_UPDATE_VERSION_TIME, System.currentTimeMillis());
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

    @Override
    public void loadData(int walletTypeSearch, String keywords) {
        String name = "";
        String address = "";
        if(!TextUtils.isEmpty(keywords)){
            if("NULL".equals(keywords)){//关键词搜索且输入内容为空
                address = keywords;
            }else if(checkKeywordsAddress(keywords)){//地址关键词搜索
                address = keywords;
            }else{//钱包名称关键词搜索
                name = keywords;
            }
        }

        List<Wallet> newWallet = WalletManager.getInstance().getWalletListByAddressAndNameAndType(walletTypeSearch,name,address);
        if(getDataSource().size() > 0){
            getDataSource().clear();
        }
        //设置选中钱包
       Wallet selectedWallet =  WalletManager.getInstance().getSelectedWallet();
        for (int i = 0; i < newWallet.size(); i++) {
             if(selectedWallet.getUuid().equals(newWallet.get(i).getUuid())){
                 newWallet.get(i).setSelectedIndex(WalletSelectedIndex.SELECTED);
                 break;
             }
        }
        getDataSource().addAll(newWallet);
        getView().notifyDataSetChanged();
    }

    @Override
    public void updateSelectedWalletnotifyData(Wallet selectedWallet) {


        WalletManager.getInstance().addAndSelectedWalletStatusNotice(selectedWallet);


    }


    public boolean checkKeywordsAddress(String input){
        if(input.length() > 5){
            String prefix = input.subSequence(0,4).toString();
            if((prefix.equalsIgnoreCase("lat1") || prefix.equalsIgnoreCase("lax1"))){
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Wallet> getDataSource() {
        return this.wallets;
    }


    private boolean shouldUpdate(VersionInfo versionInfo) {
        long lastUpdateTime = PreferenceTool.getLong(Constants.Preference.KEY_UPDATE_VERSION_TIME, 0L);
        boolean shouldShowUpdateDialog = !(lastUpdateTime != 0 && DateUtil.isToday(lastUpdateTime));
        return (versionInfo.isNeed() && versionInfo.isForce()) || (versionInfo.isNeed() && shouldShowUpdateDialog);
    }

    private void showUpdateVersionDialog(VersionInfo versionInfo) {
        CommonTipsDialogFragment.createDialogWithTitleAndTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                string(R.string.version_update),
                versionInfo.getNewVersion(),
                //string(R.string.version_update_tips, versionInfo.getNewVersion()),
                versionInfo.getDesc(),
                string(R.string.update_now), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                        requestPermission(versionInfo);
                    }
                },
                string(R.string.not_now), new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (versionInfo.isForce()) {
                            CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips), "退出应用?", "取消", new OnDialogViewClickListener() {
                                @Override
                                public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                    showUpdateVersionDialog(versionInfo);
                                }
                            }, string(R.string.confirm), new OnDialogViewClickListener() {
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
                }, !versionInfo.isForce())
                .show(currentActivity().getSupportFragmentManager(), "showTips");
    }

    public void requestPermission(VersionInfo versionInfo) {

        new RxPermissions(currentActivity())
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new CustomObserver<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (isViewAttached()) {
                            if (permission.granted && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission.name)) {
                                new VersionUpdate(currentActivity(), versionInfo.getUrl(), versionInfo.getNewVersion(), false).execute();
                            }
                        }
                    }
                });
    }

}
