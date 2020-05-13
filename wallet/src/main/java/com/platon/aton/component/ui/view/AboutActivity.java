package com.platon.aton.component.ui.view;

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
import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.dialog.CommonTipsDialogFragment;
import com.platon.aton.component.ui.dialog.OnDialogViewClickListener;
import com.platon.aton.engine.DeviceManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.VersionUpdate;
import com.platon.aton.entity.VersionInfo;
import com.platon.aton.entity.WebType;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.utils.AndroidUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import retrofit2.Response;

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
    @BindView(R.id.tv_service_agreement)
    TextView tvServiceAgreement;

    private Unbinder unbinder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
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
        checkVersion();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {

        String versionName = AndroidUtil.getVersionName(this);
        if (!versionName.toLowerCase().startsWith("v")) {
            versionName = "v" + versionName;
        }
        vNewMsg.setVisibility(View.GONE);
        tvUpdate.setText(string(R.string.current_version, versionName));

        RxView
                .longClicks(ivLogo)
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        showLongToast(DeviceManager.getInstance().getChannel());
                    }
                });

        RxView.clicks(tvAboutUs)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), getResources().getString(R.string.web_url_web_portals), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView.clicks(llUpdate)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        update();
                    }
                });

        RxView.clicks(tvPrivacyPolicy)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        CommonHybridActivity.actionStart(AboutActivity.this, getString(R.string.web_url_privacy_policy, NodeManager.getInstance().getCurNodeAddress()), WebType.WEB_TYPE_COMMON);
                    }
                });
        RxView.clicks(tvServiceAgreement)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(AboutActivity.this, getString(R.string.web_url_agreement, NodeManager.getInstance().getCurNodeAddress()), WebType.WEB_TYPE_COMMON);
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
                .getVersionInfo(ApiRequestBody.newBuilder()
                        .put("versionCode", BuildConfig.VERSION_CODE)
                        .put("deviceType", DeviceManager.getInstance().getOS())
                        .put("channelCode", DeviceManager.getInstance().getChannel())
                        .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new ApiSingleObserver<VersionInfo>() {
                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        if (versionInfo.isNeed()) {
                            tvUpdate.setText(string(R.string.latest_version, versionInfo.getNewVersion()));
                            vNewMsg.setVisibility(View.VISIBLE);
                        } else {
                            tvUpdate.setText(string(R.string.current_version, BuildConfig.VERSION_NAME));
                            vNewMsg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    private void update() {
        getVersionInfo()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new ApiSingleObserver<VersionInfo>() {
                    @Override
                    public void onApiSuccess(VersionInfo versionInfo) {
                        if (versionInfo.isNeed()) {
                            tvUpdate.setText(string(R.string.latest_version, versionInfo.getNewVersion()));
                            vNewMsg.setVisibility(View.VISIBLE);

                            showUpdateVersionDialog(versionInfo);
                        } else {
                            tvUpdate.setText(string(R.string.current_version, BuildConfig.VERSION_NAME));
                            vNewMsg.setVisibility(View.GONE);
                            showLongToast(string(R.string.latest_version_tips));
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }


    private void showUpdateVersionDialog(VersionInfo versionInfo) {
        CommonTipsDialogFragment.createDialogWithTitleAndTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                string(R.string.version_update),
                string(R.string.version_update_tips, versionInfo.getNewVersion()),
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
                                            new VersionUpdate(currentActivity(), versionInfo.getUrl(), versionInfo.getNewVersion(), false).execute();
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

    private Single<Response<ApiResponse<VersionInfo>>> getVersionInfo() {
        return ServerUtils
                .getCommonApi()
                .getVersionInfo(ApiRequestBody.newBuilder()
                        .put("versionCode", BuildConfig.VERSION_CODE)
                        .put("deviceType", DeviceManager.getInstance().getOS())
                        .put("channelCode", DeviceManager.getInstance().getChannel())
                        .build());
    }
}
