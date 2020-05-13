package com.platon.aton.engine;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.platon.framework.app.Constants;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;
import com.platon.framework.utils.ToastUtil;

import java.io.File;

/**
 * 下载更新，支持实时监听下载进度、网络变化断点续传
 * <br>参数isForceUpdate：
 * <br>为true执行mHandler.sendMessage(MSG_DWSIZE,downloadedBytes,totalBytes,status);
 * <br>否则执行mHandler.sendEmptyMessage(MSG_NULL);
 *
 * @author Robert
 */
public class VersionUpdate {
    private static final String TAG = VersionUpdate.class.getSimpleName();
    private static final String DOWNLOAD_FOLDER_NAME = "ATON";
    private final Context mContext;
    private String url;
    private final String notificationTitle = "ATON";
    private final String notificationDescription = "版本升级";
    private Handler mHandler;
    private boolean isForceUpdate;
    private long lastDownloadId = -1;
    private String mVersionName;

    private DownloadManager mDownloadManager;
    private CompleteReceiver mCompleteReceiver;
    private DownloadManagerPro mDownloadManagerPro;
    private DownloadChangeObserver mDownloadChangeObserver;

    public static final int MSG_DWSIZE = 1000;
    public static final int MSG_NULL = 2000;
    private String apkFilePath;

    public VersionUpdate(Context context, String url, String versionName, boolean isForceUpdate) {
        super();
        this.mContext = context;
        this.url = url;
        this.isForceUpdate = isForceUpdate;
        mVersionName = "aton-android-" + versionName + ".apk";
        init(context);
    }

    private void init(Context context) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);

        mDownloadChangeObserver = new DownloadChangeObserver();
        context.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true,
                mDownloadChangeObserver);

        mCompleteReceiver = new CompleteReceiver();
        context.registerReceiver(mCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator).append(mVersionName)
                .toString();
    }

    /**
     * 执行下载
     */
    public void execute() {
        lastDownloadId = PreferenceTool.getLong(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID, -1);
        if (lastDownloadId != -1 && !invalidDownLoadManager()) {
            mDownloadManager.remove(lastDownloadId);
            PreferenceTool.remove(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID);
        }
        initDownLoadPath();
        //2.创建下载请求对象，并且把下载的地址放进去
        Request request = new Request(Uri.parse(url));
        request.setTitle(notificationTitle);
        //设置显示在文件下载Notification（通知栏）中显示的文字。6.0的手机Description不显示
        request.setDescription(notificationDescription);
        //设置在什么连接状态下执行下载操作
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        //设置为可见和可管理
        request.setVisibleInDownloadsUi(false);
        //给下载的文件指定路径
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, mVersionName);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);

        //检查系统管理器知是否正常状态
        if (invalidDownLoadManager()) {//不可用
            ToastUtil.showLongToast(mContext, "当前下载器被禁用，请在设置中开启");
        } else {
            lastDownloadId = mDownloadManager.enqueue(request);
            PreferenceTool.putLong(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID, lastDownloadId);
            updateView();
        }
    }

    /**
     * 配置下载路径
     */
    private void initDownLoadPath() {
        File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        //如果已经存在该版本app，则删除之前的。
        File file = new File(apkFilePath);
        if (file.exists()) {
            if(!file.delete()){
               LogUtils.d("delete  file fail");
            }

        }
    }

    /**
     * 安装业务代理
     *
     * @param context
     */
    private void installProxy(Context context) {
        unregisterReceiver(context);

        install(context, apkFilePath);
        if (isForceUpdate) {
            System.exit(0);
        }
    }

    /**
     * 注销相关广播以及观察者
     *
     * @param context
     */
    private void unregisterReceiver(Context context) {
        if (mCompleteReceiver != null) {
            context.unregisterReceiver(mCompleteReceiver);
        }

        if (mDownloadChangeObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadChangeObserver);
        }

    }

    /**
     * 安装App
     *
     * @param context
     * @param filePath
     * @return
     */
    private boolean install(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(context, "com.platon.aton.fileprovider", new File(filePath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return false;
    }

    /**
     * 下载完成广播接收
     *
     * @author Robert
     */
    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("[CompleteReceiver]");
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if (completeDownloadId == lastDownloadId) {
                updateView();
                if (mDownloadManagerPro.getStatusById(lastDownloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    installProxy(context);
                }
            }
        }
    }

    /**
     * 监听下载进度
     *
     * @author Robert
     */
    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(mHandler);
        }

        @Override
        public void onChange(boolean selfChange) {
            LogUtils.d("[DownloadChangeObserver]");
            if (isForceUpdate) {
                updateView();
            }
        }

    }

    /**
     * 更新进度
     */
    private void updateView() {
        if (mHandler != null) {
            if (isForceUpdate) {
                int[] bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(lastDownloadId);
                int downloadedBytes = bytesAndStatus[0];
                int totalBytes = bytesAndStatus[1];
                int downloadStatus = bytesAndStatus[2];
                LogUtils.d("[cursize,total,status]" + "[" + downloadedBytes + "," + totalBytes + "," + downloadStatus + "]");
                mHandler.sendMessage(mHandler.obtainMessage(MSG_DWSIZE, downloadedBytes, totalBytes, downloadStatus));
            } else {
                mHandler.sendEmptyMessage(MSG_NULL);
            }
        }
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        if (mDownloadManager != null) {
            mDownloadManager.remove(lastDownloadId);
        }
    }


    /**
     * 判断当前是否为下载状态
     *
     * @param downloadManagerStatus
     * @return
     */
    public boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    /**
     * 检查系统下载管理器知是否正常
     *
     * @return
     */
    public boolean invalidDownLoadManager() {
        int state = mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        boolean invalid = state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED;
        return invalid;
    }
}
