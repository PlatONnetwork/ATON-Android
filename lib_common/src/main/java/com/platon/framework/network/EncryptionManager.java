package com.platon.framework.network;

import com.google.gson.Gson;
import com.platon.framework.utils.JSONParser;
import com.platon.framework.utils.MapUtils;
import com.platon.framework.utils.crypt.AESUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziv
 */
public class EncryptionManager {

    private static final String TAG = "EncryptionManager";
    private static final String PLATFORM_KEY = "pagodabaiguoyuan";
    private static EncryptionManager sInstance = null;

    private String mBusinessEncryptionKey;
    private String mPlatformEncryptionKey;

    private EncryptionManager() {
        mPlatformEncryptionKey = PLATFORM_KEY;
    }

    public static EncryptionManager getInstance() {
        if (sInstance == null) {
            sInstance = new EncryptionManager();
        }
        return sInstance;
    }

    public String getBusinessEncryptionKey() {
        return mBusinessEncryptionKey;
    }

    public void setBusinessEncryptionKey(String businessEncryptionKey) {
        this.mBusinessEncryptionKey = businessEncryptionKey;
    }

    public Map<String, Object> encrypt(RequestInfo reqInfo, Map<String, Object> reqBody) {
        if (reqInfo == null) {
            return null;
        }
        Map<String, Object> encryptedReqBody = new HashMap<>();
        Gson gson = new Gson();
        try {
            if (reqInfo.getEncryptType() == RequestInfo.EncryptionType.TYPE_PLATFORM) {
                String encrypted = AESUtils.encrypt(mPlatformEncryptionKey, gson.toJson(reqBody));
                encryptedReqBody.put("data", encrypted);

            } else if (reqInfo.getEncryptType() == RequestInfo.EncryptionType.TYPE_BUSINESS) {
                String userToken = MapUtils.getString(reqBody, "userToken");
                String encrypted = AESUtils.encrypt(mBusinessEncryptionKey, gson.toJson(reqBody));
                encryptedReqBody.put("data", encrypted);
                encryptedReqBody.put("userToken", userToken);
            } else {
                // 未加密
                encryptedReqBody = reqBody;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedReqBody;
    }

    public String decrypt(RequestInfo reqInfo, String encryptedString) {
        if (reqInfo == null) {
            return null;
        }
        String target = encryptedString;
        try {
            Pattern pattern = Pattern.compile("\\{\"data\":\"(.*)\"(,.*)(,.*)\\}");
            Matcher m = pattern.matcher(encryptedString);
            String encryptedData = null;
            if (m.find()) {
                encryptedData = m.group(1);
            } else {
                return target;
            }
            if (reqInfo.getEncryptType() == RequestInfo.EncryptionType.TYPE_PLATFORM) {
                String decrypted = AESUtils.decrypt(mPlatformEncryptionKey, encryptedData);
                generateBusinessKey(decrypted);

                int end = Math.max(decrypted.lastIndexOf("]"), decrypted.lastIndexOf("}"));
                decrypted = decrypted.substring(0, end + 1);
                generateBusinessKey(decrypted);
                if (decrypted.startsWith("[") || decrypted.startsWith("{")) {
                    target = encryptedString.replace("\"" + encryptedData + "\"", decrypted);
                } else {
                    target = encryptedString.replace(encryptedData, decrypted);
                }

            } else if (reqInfo.getEncryptType() == RequestInfo.EncryptionType.TYPE_BUSINESS) {

                String decrypted = encryptedData.replace("\\", "");

                if (decrypted.startsWith("{")) {
                    target = encryptedString.replace("\"" + encryptedData + "\"", decrypted);
                }

//                String decrypted = AESUtils.decrypt(mBusinessEncryptionKey, encryptedData);
//                int end = Math.max(decrypted.lastIndexOf("]"), decrypted.lastIndexOf("}"));
//                decrypted = decrypted.substring(0, end + 1);
//
//                if (decrypted.startsWith("[") || decrypted.startsWith("{")) {
//                    target = encryptedString.replace("\"" + encryptedData + "\"", decrypted);
//                } else {
//                    target = encryptedString.replace(encryptedData, decrypted);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

    /**
     * 将token利用平台密钥解密，得到用户密钥
     *
     * @param decrypted
     * @throws Exception
     */
    private void generateBusinessKey(String decrypted) throws Exception {
        // TODO Auto-generated method stub
        HashMap<String, Object> dataMap = (HashMap<String, Object>) JSONParser.parse(decrypted);
        if (!dataMap.containsKey("userToken")) {
            return;
        }
        String token = MapUtils.getString(dataMap, "userToken");

        // 解密后内容为如下结构 "9|9151dd8a7fd7|NaN" ，需要拆分取得真正的用户密钥
        String businessKeyContent = AESUtils.decrypt(mPlatformEncryptionKey, token);
        if (businessKeyContent.matches(".+?|.+?|.+?")) {
            mBusinessEncryptionKey = businessKeyContent.split("\\|")[1];
        }
    }

}
