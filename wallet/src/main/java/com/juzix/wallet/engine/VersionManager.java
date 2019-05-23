package com.juzix.wallet.engine;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.entity.VersionInfo;
import com.juzix.wallet.utils.JSONUtil;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;

/**
 * todo
 */
public class VersionManager {

    private Retrofit mRetrofit;
    private Set<String> mDownLoadSet = new HashSet<>();

    private VersionManager() {

    }

    private static class InstanceHolder {
        private static volatile VersionManager INSTANCE = new VersionManager();
    }

    public static VersionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<VersionInfo> getVersion() {
        return ServerUtils.getCommonApi()
                .getVersionInfo(Constants.URL.UPDATE_URL)
                .flatMap(new Function<String, SingleSource<VersionInfo>>() {
                    @Override
                    public SingleSource<VersionInfo> apply(String body) throws Exception {
                        return Single.just(JSONUtil.parseObject(body, VersionInfo.class));
                    }
                });
    }

    public boolean isDownloading(String url) {
        return mDownLoadSet.contains(url);
    }

}
