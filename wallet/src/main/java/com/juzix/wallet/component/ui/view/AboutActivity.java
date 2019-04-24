package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.VersionManager;
import com.juzix.wallet.engine.VersionUpdate;
import com.juzix.wallet.entity.VersionEntity;
import com.juzix.wallet.utils.ShareUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class AboutActivity extends BaseActivity {

    private final static String TAG = AboutActivity.class.getSimpleName();

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

    private void initViews() {

        String versionName = AndroidUtil.getVersionName(this);
        if (!versionName.toLowerCase().startsWith("v"))
            versionName = "v" + versionName;
        vNewMsg.setVisibility(View.GONE);
        tvUpdate.setText(string(R.string.current_version, versionName));
        RxView.clicks(tvAboutUs)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getContext(), "https://www.platon.network");
                    }
                });
        RxView.clicks(llUpdate)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
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
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<VersionEntity>() {
                    @Override
                    public void accept(VersionEntity versionEntity) {
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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    private void update() {
        VersionManager.getInstance().getVersion()
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(this))
                .subscribe(new Consumer<VersionEntity>() {
                    @Override
                    public void accept(VersionEntity versionEntity) {
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

                        mVersionUpdate = new VersionUpdate(AboutActivity.this, versionEntity.getDownloadUrl(), versionEntity.getVersionWithoutPrefix(), false);

                        tvUpdate.setText(string(R.string.latest_version, newVersion));
                        vNewMsg.setVisibility(View.VISIBLE);
                        if (VersionManager.getInstance().isDownloading(versionEntity.getDownloadUrl())) {
                            showLongToast(string(R.string.download_tips));
                            return;
                        }

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
                                                mVersionUpdate.execute();
                                            }

                                            @Override
                                            public void onHasPermission(int what) {
                                                mVersionUpdate.execute();
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
                                }).show(getSupportFragmentManager(), "showTips");
                    }
                });
    }
}
