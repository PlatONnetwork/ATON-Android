package com.juzix.wallet.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.juzix.wallet.entity.ShareAppInfo;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA3Digest;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {

    /**
     * 验证密码是否可用；大小写英文字母、数字，长度6~32字符
     *
     * @param walletPwd 密码
     * @return true：可用  false：不可用
     */
    public static boolean validWalletPwd(String walletPwd) {
        Pattern p = Pattern.compile("(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_@#!~%\\^&\\*]*$).{6,32}");
        Matcher m = p.matcher(walletPwd);
        return m.matches();
    }

    /**
     * 验证名称是否可用；大小写英文字母、数字，长度1~12字符
     *
     * @param walletName 名称
     * @return true：可用  false：不可用
     */
    public static boolean validWalletName(String walletName) {
        Pattern p = Pattern.compile("(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_@#!~%\\^&\\*]*$).{1,12}");
        Matcher m = p.matcher(walletName);
        return m.matches();
    }

    /**
     * 验证身份证号码是否可用
     *
     * @param id 身份证号
     * @return true：可用  false：不可用
     */
    public static boolean validIdentify(String id) {
        Pattern p = Pattern.compile("\\d{15}|\\d{17}[[0-9],0-9xX]");
        Matcher m = p.matcher(id);
        return m.matches();
    }

    /**
     * 获取sha3后的密码
     *
     * @param pwd 密码
     * @return
     */
    public static String getSha3Pwd(String pwd) {
        return Numeric.toHexStringNoPrefix(Hash.sha3(Numeric.hexStringToByteArray(AppUtil.toHexString(pwd.trim())))).toUpperCase();
    }

    // SHA3-256 算法
    public static String sha3256(byte[] bytes) {
        Digest digest = new SHA3Digest(256);
        digest.update(bytes, 0, bytes.length);
        byte[] rsData = new byte[digest.getDigestSize()];
        digest.doFinal(rsData, 0);
        return Hex.toHexString(rsData);
    }

    /**
     * 转为十六进制字符串
     *
     * @param text 需要转换的字符串
     * @return
     */
    public static String toHexString(String text) {
        StringBuilder builder = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            builder.append(Integer.toHexString(text.charAt(i)));
        }
        return builder.toString();
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @return
     */
    public static List<ShareAppInfo> getShareAppInfoList(Context context) {
        List<ShareAppInfo> shareAppInfoList = new ArrayList<>();
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //从pinfo中将包名字逐一取出，压入packageInfost中
        if (packageInfos != null) {
            for (ShareAppInfo appInfo : ShareAppInfo.values()) {
                for (int i = 0; i < packageInfos.size(); i++) {
                    String packName = packageInfos.get(i).packageName;
                    if (appInfo.packageName.equals(packName)) {
                        shareAppInfoList.add(appInfo);
                    }
                }
            }
        }
        return shareAppInfoList;
    }

    public static boolean isInstall(Context context, ShareAppInfo shareAppInfo) {
        return getShareAppInfoList(context).contains(shareAppInfo);
    }
}
