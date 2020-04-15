package com.platon.aton.component.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.dialog.ReminderThresholdAmountDialogFragment;
import com.platon.aton.component.widget.togglebutton.ToggleButton;
import com.platon.aton.utils.RxUtils;
import com.platon.aton.utils.StringUtil;
import com.platon.biometric.BiometricPromptCompat;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.LanguageUtil;
import com.platon.framework.utils.PreferenceTool;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SettingsActivity extends BaseActivity {

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
    @BindView(R.id.tv_reminder_threshold_amount)
    TextView tvReminderThresholdAmount;
    @BindView(R.id.layout_large_transaction_reminder)
    LinearLayout layoutLargeTransactionReminder;
    @BindView(R.id.tgb_resend_reminder)
    ToggleButton tgbResendReminder;

    private Unbinder unbinder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
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
        iniViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void iniViews() {

        Locale locale = LanguageUtil.getLocale();

        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            tvLanguage.setText(chinese);
        } else {
            tvLanguage.setText(english);
        }
        if (!BiometricPromptCompat.isHardwareDetected(this)) {
            layoutFaceTouchId.setVisibility(View.GONE);
        } else {
            layoutFaceTouchId.setVisibility(View.VISIBLE);
        }

        tvReminderThresholdAmount.setText(getString(R.string.amount_with_unit, StringUtil.formatBalanceWithoutMinFraction(PreferenceTool.getString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, "1000"))));

        tgbSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (BiometricPromptCompat.supportBiometricPromptCompat(SettingsActivity.this)) {
                    startAuth();
                } else {
                    showLongToast(string(R.string.openFigerprintError));
                }
                switchToggleButton(!on);
            }
        });

        tgbResendReminder.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                PreferenceTool.putBoolean(Constants.Preference.KEY_RESEND_REMINDER, on);
                switchResendReminder(on);
            }
        });

        RxView.clicks(tvNodeSetting)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        NodeSettingsActivity.actionStart(getContext());
                    }
                });

        RxView.clicks(layoutSwitchLanguage)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        SwitchLanguageActivity.actionStart(getContext());
                    }
                });

        RxView.clicks(layoutLargeTransactionReminder)
                .compose(RxUtils.getClickTransformer())
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        ReminderThresholdAmountDialogFragment.newInstance(PreferenceTool.getString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, "1000"))
                                .setOnReminderThresholdAmountItemClickListener(new ReminderThresholdAmountDialogFragment.OnReminderThresholdAmountItemClickListener() {
                                    @Override
                                    public void onReminderThresholdAmountItemClick(String reminderThresholdAmount) {
                                        PreferenceTool.putString(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, reminderThresholdAmount);
                                        tvReminderThresholdAmount.setText(getString(R.string.amount_with_unit, StringUtil.formatBalanceWithoutMinFraction(reminderThresholdAmount)));
                                    }
                                })
                                .show(getSupportFragmentManager(), "showReminderThresholdAmountDialogFragment");
                    }
                });

        switchToggleButton(PreferenceTool.getBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false));

        switchResendReminder(PreferenceTool.getBoolean(Constants.Preference.KEY_RESEND_REMINDER, true));
    }

    private void switchResendReminder(boolean resendReminder) {
        if (resendReminder) {
            tgbResendReminder.setToggleOn();
        } else {
            tgbResendReminder.setToggleOff();
        }
    }

    private void switchToggleButton(boolean faceTouchFlag) {
        if (faceTouchFlag) {
            tgbSwitch.setToggleOn();
        } else {
            tgbSwitch.setToggleOff();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startAuth() {
        final BiometricPromptCompat mBiometricPromptCompat = new BiometricPromptCompat.Builder(SettingsActivity.this)
                .setNegativeButton(string(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .build();
        mBiometricPromptCompat.authenticate(mCallback);
    }

    private BiometricPromptCompat.IAuthenticationCallback mCallback = new BiometricPromptCompat.IAuthenticationCallback() {
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
            boolean flag = PreferenceTool.getBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
            if (flag) {
                //指纹密码验证成功
                PreferenceTool.putBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
                switchToggleButton(false);
            } else {
                PreferenceTool.putBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, true);
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
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
