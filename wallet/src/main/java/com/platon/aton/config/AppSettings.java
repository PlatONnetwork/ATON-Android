package com.platon.aton.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.platon.framework.app.Constants;

/**
 * @author matrixelement
 */
public class AppSettings {

    /**
     * 默认大额提醒金额1000 LAT
     */
    private static final String DEFAULT_REMINDER_THRESHOLD_AMOUNT = "1000";
    /**
     * 默认重发提醒开启
     */
    private static final boolean DEFAULT_RESEND_REMINDER = true;

    private static final String PREFERENCES_NAME = "com.platon.aton.appsettings";

    private static final AppSettings APP_SETTINGS = new AppSettings();
    /**
     * 文件存储
     */
    private SharedPreferences preferences;

    private AppSettings() {

    }

    public static AppSettings getInstance() {
        return APP_SETTINGS;
    }

    public void init(Context ctx) {
        preferences = ctx
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        preferences.edit().clear().commit();
    }

    public String getReminderThresholdAmount() {
        return getStringItem(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, DEFAULT_REMINDER_THRESHOLD_AMOUNT);
    }

    public void setReminderThresholdAmount(String reminderThresholdAmount) {
        setStringItem(Constants.Preference.KEY_REMINDER_THRESHOLD_AMOUNT, reminderThresholdAmount);
    }

    public boolean getFaceTouchIdFlag() {
        return getBooleanItem(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
    }

    public void setFaceTouchIdFlag(boolean supportFaceTouchId) {
        setBooleanItem(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, supportFaceTouchId);
    }

    public boolean getRecordBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_RECORD, false);
    }

    public void setRecordBoolean(boolean isShowRecord) {
        setBooleanItem(Constants.Preference.KEY_SHOW_RECORD, isShowRecord);
    }

    public boolean getDelegateDetailBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_DELEGATE_DETAIL, false);
    }

    public void setDelegateDetailBoolean(boolean isShowDelegateDetail) {
        setBooleanItem(Constants.Preference.KEY_SHOW_DELEGATE_DETAIL, isShowDelegateDetail);
    }

    public boolean getDelegateOperationBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_DELEGATE_OPERATION, false);
    }

    public void setDelegateOperationBoolean(boolean isShowDelegateOperation) {
        setBooleanItem(Constants.Preference.KEY_SHOW_DELEGATE_OPERATION, isShowDelegateOperation);
    }

    public boolean getValidatorsBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_VALIDATORS, false);
    }

    public void setValidatorsBoolean(boolean isShowValidators) {
        setBooleanItem(Constants.Preference.KEY_SHOW_VALIDATORS, isShowValidators);
    }

    public boolean getObservedWalletBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_OBSERVED_WALLET, false);
    }

    public void setObservedWalletBoolean(boolean isShowObservedWallet) {
        setBooleanItem(Constants.Preference.KEY_SHOW_OBSERVED_WALLET, isShowObservedWallet);
    }

    public boolean getMyDelegateBoolean() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_MY_DELEGATE, false);
    }

    public void setMyDelegateBoolean(boolean isShowMydelegate) {
        setBooleanItem(Constants.Preference.KEY_SHOW_MY_DELEGATE, isShowMydelegate);
    }

    public boolean getWithdrawOperation() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_WITHDRAW_OPERATION, false);
    }

    public void setWithdrawOperation(boolean isShowWithdrawOperation) {
        setBooleanItem(Constants.Preference.KEY_SHOW_WITHDRAW_OPERATION, isShowWithdrawOperation);
    }

    public boolean getShowAssetsFlag() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
    }

    public void setShowAssetsFlag(boolean showAssetsFlag) {
        setBooleanItem(Constants.Preference.KEY_SHOW_ASSETS_FLAG, showAssetsFlag);
    }


    private String getStringItem(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    private void setStringItem(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    private int getIntItem(String key, int defaultvalue) {
        return preferences.getInt(key, defaultvalue);
    }

    private void setIntItem(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    private long getLongItem(String key, long defaultvalue) {
        return preferences.getLong(key, defaultvalue);
    }

    private void setLongItem(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    private boolean getBooleanItem(String key, boolean defaultvalue) {
        return preferences.getBoolean(key, defaultvalue);
    }

    private void setBooleanItem(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public boolean removeSharedPreferenceByKey(String key) {
        preferences.edit().remove(key);
        return preferences.edit().commit();
    }

}
