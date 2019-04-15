package com.juzix.wallet.engine;

import com.alibaba.fastjson.JSONObject;
import com.juzhen.framework.network.HttpClient;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.engine.service.UpdateVersionService;
import com.juzix.wallet.entity.DownloadEntity;
import com.juzix.wallet.entity.VersionEntity;
import com.juzix.wallet.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class VersionManager {

    private Retrofit                    mRetrofit;
    private Set<String>                 mDownLoadSet = new HashSet<>();

    private VersionManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://10.10.8.8:8060/browser-server/")
                .client(okHttpClient)
                .build();
    }

    public static VersionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<VersionEntity> getVersion() {
        return HttpClient.getInstance()
                .createService(UpdateVersionService.class)
                .getVersionInfo(Constants.URL.UPDATE_URL).flatMap(new Function<String, SingleSource<VersionEntity>>() {
                    @Override
                    public SingleSource<VersionEntity> apply(String body) throws Exception {
                        return getVersionInfo(body);
                    }
                });
    }

    public boolean isDownloading(String url){
        return mDownLoadSet.contains(url);
    }

    public void download(String url, final File destDir, final String filename) {
        VersionManager.getInstance().startDownload(url, destDir, filename)
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DownloadEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DownloadEntity downloadInfo) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        FileUtil.install(MainActivity.sInstance, new File(destDir, filename));
                    }
                });
    }

    private Observable<DownloadEntity> startDownload(String url, final File destDir, final String fileName) {
       return mRetrofit.create(UpdateVersionService.class)
                .download(url).flatMap(new Function<ResponseBody, ObservableSource<DownloadEntity>>() {
                    @Override
                    public ObservableSource<DownloadEntity> apply(final ResponseBody responseBody) throws Exception {
                        return getDownloadEntity(url, responseBody, destDir, fileName);
                    }
                });
    }


    private SingleSource<VersionEntity> getVersionInfo(final String body) {
        return Single.create(new SingleOnSubscribe<VersionEntity>() {
            @Override
            public void subscribe(SingleEmitter<VersionEntity> emitter) throws Exception {
                try {
                    JSONObject jsonObject    = JSONObject.parseObject(body);
                    String     latestVersion = jsonObject.getString("tag_name");
                    String     downUrl       = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                    emitter.onSuccess(new VersionEntity.Builder().version(latestVersion).downloadUrl(downUrl).build());
                } catch (Exception exp) {
                    exp.printStackTrace();
                    emitter.onError(exp);
                }
            }
        });
    }

    private Observable<DownloadEntity> getDownloadEntity(final String url, final ResponseBody responseBody, final File destDir, final String fileName) {
        return Observable.create(new ObservableOnSubscribe<DownloadEntity>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadEntity> emitter) throws Exception {
                final DownloadEntity entity = new DownloadEntity();
                mDownLoadSet.add(url);
                InputStream      inputStream    = null;
                long             total          = 0;
                long             responseLength = 0;
                FileOutputStream fos            = null;
                try {
                    byte[] buf = new byte[2048];
                    int    len = 0;
                    responseLength = responseBody.contentLength();
                    inputStream = responseBody.byteStream();
                    final File file = new File(destDir, fileName.replace(File.separator, ""));
                    entity.setFile(file);
                    entity.setFileSize(responseLength);
                    if (file.exists() && file.length() == responseLength){
                        entity.setCurrentSize(total);
                        emitter.onComplete();
                        return;
                    }
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    fos = new FileOutputStream(file);
                    int  progress     = 0;
                    int  lastProgress = 0;
                    long startTime    = System.currentTimeMillis(); // 开始下载时获取开始时间
                    while ((len = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        total += len;
                        lastProgress = progress;
                        progress = (int) (total * 100 / responseLength);
                        long curTime  = System.currentTimeMillis();
                        long usedTime = (curTime - startTime) / 1000;
                        if (usedTime == 0) {
                            usedTime = 1;
                        }
                        long speed = (total / usedTime); // 平均每秒下载速度
                        if (progress > 0 && progress != lastProgress) {
                            entity.setSpeed(speed);
                            entity.setProgress(progress);
                            entity.setCurrentSize(total);
                            emitter.onNext(entity);
                        }
                    }
                    fos.flush();
                    entity.setFile(file);
                    emitter.onComplete();
                } catch (Exception e) {
                    entity.setErrorMsg(e);
                    emitter.onError(e);
                } finally {
                    mDownLoadSet.remove(url);
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private static class InstanceHolder {
        private static volatile VersionManager INSTANCE = new VersionManager();
    }
}
