package com.juzix.wallet.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.juzix.wallet.app.Constants;

/**
 * @author matrixelement
 */
public class AppSettings {

    private static final String PREFERENCES_NAME = "com.juzix.wallet.appsettings";

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

    public boolean getServiceTermsFlag() {
        return getBooleanItem(Constants.Preference.KEY_SERVICE_TERMS_FLAG, true);
    }

    public void setServiceTermsFlag(boolean serviceTermsFlag) {
        setBooleanItem(Constants.Preference.KEY_SERVICE_TERMS_FLAG, serviceTermsFlag);
    }

    public boolean getOperateMenuFlag() {
        return getBooleanItem(Constants.Preference.KEY_OPERATE_MENU_FLAG, true);
    }

    public void setOperateMenuFlag(boolean operateMenuFlag) {
        setBooleanItem(Constants.Preference.KEY_OPERATE_MENU_FLAG, operateMenuFlag);
    }

    public void setFromDelegateOrValidators(String tag) {
        setStringItem(Constants.Preference.KEY_DELEGATE_OR_VALIDATORS_TAG, tag);
    }

    public String getTagFromDelegateOrValidators() {
      return  getStringItem(Constants.Preference.KEY_DELEGATE_OR_VALIDATORS_TAG, null);
    }
    public boolean getMydelegateTab() {
        return getBooleanItem(Constants.Preference.KEY_MYDELEGATETAB, true);
    }

    public void setMydelegateTab(boolean isChoosedTab) {
        setBooleanItem(Constants.Preference.KEY_MYDELEGATETAB, isChoosedTab);
    }

    public boolean getValidatorsTab() {
        return getBooleanItem(Constants.Preference.KEY_VALIDATORSTAB, false);
    }

    public void setValidatorsTab(boolean isChoosedTab) {
        setBooleanItem(Constants.Preference.KEY_VALIDATORSTAB, isChoosedTab);
    }
    public boolean getFaceTouchIdFlag() {
        return getBooleanItem(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
    }

    public void setFaceTouchIdFlag(boolean supportFaceTouchId) {
        setBooleanItem(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, supportFaceTouchId);
    }

    public boolean getShowAssetsFlag() {
        return getBooleanItem(Constants.Preference.KEY_SHOW_ASSETS_FLAG, true);
    }

    public void setShowAssetsFlag(boolean showAssetsFlag) {
        setBooleanItem(Constants.Preference.KEY_SHOW_ASSETS_FLAG, showAssetsFlag);
    }

    public long getUpdateVersionTime() {
        return getLongItem(Constants.Preference.KEY_UPDATE_VERSION_TIME, 0);
    }

    public void setUpdateVersionTime(long updateVersionTime) {
        setLongItem(Constants.Preference.KEY_UPDATE_VERSION_TIME, updateVersionTime);
    }

    private String getStringItem(String key, String defaultvalue) {
        return preferences.getString(key, defaultvalue);
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

    public void setLanguage(String language) {
        setStringItem(Constants.Preference.KEY_LANGUAGE, language);
    }

    public String getLanguage() {
        return getStringItem(Constants.Preference.KEY_LANGUAGE, null);
    }

    public void setFirstEnter(boolean isFirstEnter) {
        setBooleanItem(Constants.Preference.KEY_FIRST_ENTER, isFirstEnter);
    }

    public boolean isFirstEnter() {
        return getBooleanItem(Constants.Preference.KEY_FIRST_ENTER, true);
    }

    public void setCurrentNodeAddress(String nodeAddress) {
        setStringItem(Constants.Preference.KEY_CURRENT_NODE_ADDRESS, nodeAddress);
    }

    public String getCurrentNodeAddress(){
        return getStringItem(Constants.Preference.KEY_CURRENT_NODE_ADDRESS,null);
    }

    public void setDownloadManagerId(long downloadManagerId) {
        setLongItem("downloadManagerId", downloadManagerId);
    }

    public long getDownloadManagerId() {
        return getLongItem("downloadManagerId", -1);
    }
}
