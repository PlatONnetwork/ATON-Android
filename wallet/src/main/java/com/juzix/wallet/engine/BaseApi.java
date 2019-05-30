package com.juzix.wallet.engine;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.entity.CandidateWrap;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.VotedCandidate;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface BaseApi {

    /**
     * 获取版本信息
     *
     * @param url
     * @return
     */
    @Headers("name: " + ServerUtils.HEADER_UPDATE_VERSION)
    @GET
    Single<String> getVersionInfo(@Url String url);

    /**
     * 获取交易记录
     *
     * @param body
     * @return
     */
    @POST("app-{cid}/v060/transaction/list")
    Single<Response<ApiResponse<List<Transaction>>>> getTransactionList(@Path("cid") String cid, @Body ApiRequestBody body);

    /**
     * 获取节点列表
     *
     * @return
     */
    @POST("app-{cid}/v060/node/list")
    Single<Response<ApiResponse<CandidateWrap>>> getCandidateList(@Path("cid") String cid);

    /**
     * 获得用户有投票的节点列表
     *
     * @param cid
     * @param requestBody
     * @return
     */

    @POST("app-{cid}/v060/node/listUserVoteNode")
    Single<Response<ApiResponse<List<VotedCandidate>>>> getVotedCandidateList(@Path("cid") String cid, @Body ApiRequestBody requestBody);

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
    @POST("app-{cid}/v060/transaction/listVote")
    Single<Response<ApiResponse<List<VotedCandidate>>>> getVotedCandidateDetailList(@Path("cid") String cid, @Body ApiRequestBody body);
}
