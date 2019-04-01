package com.juzix.wallet.engine.service;

import com.juzix.wallet.entity.RegionEntity;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author matrixelement
 */
public interface RegionService {

    @Headers({"name:url_ip"})
    @POST("batch/")
    Single<Response<List<RegionEntity>>> getRegionInfoList(@Body RequestBody requestBody);

}
