package com.juzix.wallet.engine;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.entity.CandidateDetailEntity;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.VotedCandidateEntity;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface BaseApi {

    /**
     * 获取版本信息
     *
     * @param url
     * @return
     */
    @GET()
    Single<String> getVersionInfo(@Url String url);
    /**
     * 获取交易记录
     *
     * @param body
     * @return
     */
    @POST("transaction/list")
    Single<Response<ApiResponse<List<Transaction>>>> getTransactionList(@Body ApiRequestBody body);

    /**
     * 获取节点列表
     *
     * @return
     */
    @POST("v060/node/listAll")
    Single<Response<ApiResponse<CandidateExtraEntity>>> getCandidateList();

    /**
     * 获取节点详情
     *
     * @param nodeId
     * @return
     */
    @FormUrlEncoded
    @POST("v060/node/details")
    Single<Response<ApiResponse<CandidateDetailEntity>>> getCandidateDetail(@Field("nodeId") String nodeId);

    /**
     * 获得用户有投票的节点列表
     *
     * @param walletAddrs
     * @return
     */
    @FormUrlEncoded
    @POST("v060/node/listUserVoteNode")
    Single<Response<ApiResponse<List<VotedCandidateEntity>>>> getVotedCandidateList(@Field("walletAddrs") String[] walletAddrs);

    /**
     * 参数查询投票交易列表
     *
     * @param body "cid":"",                            //链ID (必填)
     *             "beginSequence":120,                 //起始序号 (必填)
     *             "listSize":100,                      //列表大小 (必填)
     *             "nodeId":"0x",                       //节点ID
     *             "walletAddrs":[                      //地址列表
     *             "address1",
     *             "address2"
     *             ]
     * @return
     */
    @POST("v060/transaction/list")
    Single<Response<ApiResponse<List<VotedCandidateEntity>>>> getVotedCandidateDetailList(@Body ApiRequestBody body);
}
