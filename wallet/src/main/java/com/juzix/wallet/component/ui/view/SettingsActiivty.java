package com.juzix.wallet.component.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.biometric.BiometricPromptCompat;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.togglebutton.ToggleButton;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SettingsActiivty extends BaseActivity {

    @BindView(R.id.tv_node_setting)
    TextView tvNodeSetting;
    @BindView(R.id.tgb_switch)
    ToggleButton tgbSwitch;
    @BindView(R.id.layout_face_touch_id)
    LinearLayout layoutFaceTouchId;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.layout_switch_language)
    LinearLayout layoutSwitchLanguage;
    @BindString(R.string.chinese)
    String chinese;
    @BindString(R.string.english)
    String english;

    private Unbinder unbinder;
    private OptionsPickerView optionsPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        unbinder = ButterKnife.bind(this);
        iniViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void iniViews() {

        Locale locale = LanguageUtil.getLocale(this);

        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            tvLanguage.setText(chinese);
        } else {
            tvLanguage.setText(english);
        }
        if (!BiometricPromptCompat.isHardwareDetected(this)){
            layoutFaceTouchId.setVisibility(View.GONE);
        }else {
            layoutFaceTouchId.setVisibility(View.VISIBLE);
        }
        switchToggleButton(AppSettings.getInstance().getFaceTouchIdFlag());
        tgbSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    switchToggleButton(!on);
                    if (BiometricPromptCompat.supportBiometricPromptCompat(SettingsActiivty.this)) {
                        startAuth();
                    }else{
                        showLongToast(string(R.string.openFigerprintError));
                    }
                }else {
                    switchToggleButton(!on);
                    if (BiometricPromptCompat.supportBiometricPromptCompat(SettingsActiivty.this)) {
                        startAuth();
                    }else{
                        showLongToast(string(R.string.openFigerprintError));
                    }
//                    AppSettings.getInstance().setFaceTouchIdFlag(false);
                }
            }
        });
        RxView.clicks(tvNodeSetting)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object){
                        NodeSettingsActivity.actionStart(getContext());
                    }
                });

        RxView.clicks(layoutSwitchLanguage)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        SwitchLanguageActivity.actionStart(getContext());
                    }
                });
    }

    private void switchToggleButton(boolean faceTouchFlag){
        if (faceTouchFlag){
            tgbSwitch.setToggleOn();
        }else {
            tgbSwitch.setToggleOff();
        }
    }

    private OptionsPickerView getOptionsPickerView() {
        if (optionsPickerView == null) {
            optionsPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    if (options1 == 0) {
                        LanguageUtil.switchLanguage(SettingsActiivty.this, Locale.CHINESE);
                    } else {
                        LanguageUtil.switchLanguage(SettingsActiivty.this, Locale.ENGLISH);
                    }
                }
            }).setSubmitColor(ContextCompat.getColor(this, R.color.color_007aff))
                    .setSubCalSize(15)
                    .setSubmitText(getResources().getString(R.string.submit))
                    .setCancelColor(ContextCompat.getColor(this, R.color.color_007aff))
                    .setCancelText(getResources().getString(R.string.cancel))
                    .setTextColorCenter(ContextCompat.getColor(this, R.color.color_292929))
                    .setContentTextSize(18)
                    .setTitleBgColor(ContextCompat.getColor(this, R.color.color_ffffff))
                    .setBgColor(ContextCompat.getColor(this, R.color.color_f8f8f8))
                    .setDividerType(WheelView.DividerType.FILL)
                    .setDividerColor(ContextCompat.getColor(this, R.color.color_aba9a2))
                    .setTextColorOut(ContextCompat.getColor(this, R.color.color_9d9d9d))
                    .setLineSpacingMultiplier(2.0f)
                    .isDialog(false)
                    .build();
            optionsPickerView.setPicker(Arrays.asList(chinese, english));
        }

        return optionsPickerView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startAuth() {
        final BiometricPromptCompat mBiometricPromptCompat = new BiometricPromptCompat.Builder(SettingsActiivty.this)
                .setNegativeButton(string(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .build();
        mBiometricPromptCompat.authenticate(mCallback);
    }

    private BiometricPromptCompat.IAuthenticationCallback mCallback = new BiometricPromptCompat.IAuthenticationCallback(){
        @Override
        public void onAuthenticationError(int errorCode, @Nullable CharSequence errString) {
            //多次指纹密码验证错误后，进入此方法；并且，不可再验（短时间）
//            switchToggleButton(AppSettings.getInstance().getFaceTouchIdFlag());

        }

        @Override
        public void onAuthenticationHelp(int helpCode, @Nullable CharSequence helpString) {
            //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
//            switchToggleButton(AppSettings.getInstance().getFaceTouchIdFlag());
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPromptCompat.IAuthenticationResult result) {
            boolean flag = AppSettings.getInstance().getFaceTouchIdFlag();
            if (flag){
                //指纹密码验证成功
                AppSettings.getInstance().setFaceTouchIdFlag(false);
                switchToggleButton(false);
            }else {
                AppSettings.getInstance().setFaceTouchIdFlag(true);
                switchToggleButton(true);
            }
        }

        @Override
        public void onAuthenticationFailed() {
            //指纹验证失败，指纹识别失败，可再验，错误原因为：该指纹不是系统录入的指纹
//            switchToggleButton(AppSettings.getInstance().getFaceTouchIdFlag());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingsActiivty.class);
        context.startActivity(intent);
    }
}
