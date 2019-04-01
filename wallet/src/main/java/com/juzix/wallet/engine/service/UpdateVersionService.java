package com.juzix.wallet.engine.service;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author matrixelement
 */
public interface UpdateVersionService {

    @GET
    /**
     * 获取版本信息
     */
    Single<String> getVersionInfo(@Url String url);

    @Streaming
    @GET
    /**
     * 下载文件
     */
    Observable<ResponseBody> download(@Url String fileUrl);
}
