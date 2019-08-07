package com.juzix.wallet.engine;

import com.google.common.base.Verify;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.entity.CandidateWrap;
import com.juzix.wallet.entity.DelegateDetail;
import com.juzix.wallet.entity.DelegateRecord;
import com.juzix.wallet.entity.MyDelegate;
import com.juzix.wallet.entity.NodeStates;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.entity.VerifyNodeDetail;
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


    /**--------------v0.7.0---------------------*/

    /**
     * 获取验证节点列表
     * "sortType" //rangking —— 排名  PA —— 年化率
     */

    @POST("app-{cid}/v060/node/nodelist")
    Single<Response<ApiResponse<List<VerifyNode>>>> getVerifyNodeList(@Path("cid") String cid, @Body ApiRequestBody body);


    /**
     * 验证节点详情
     * nodeId   //节点id
     */

    @POST("app-{cid}/v060/node/nodeDetails")
    Single<Response<ApiResponse<VerifyNodeDetail>>> getNodeCandidateDetail(@Path("cid") String cid, @Body ApiRequestBody body);


    /**
     * 获取节点状态
     */

    @POST("app-{cid}/v060/node/getNodeStatus")
    Single<Response<ApiResponse<NodeStates>>> getNodeStatus(@Path("cid") String cid, @Body ApiRequestBody body);
    
    /**
     * 获取我的委托列表
     *
     * @param cid "walletAddrs":[                     //地址列表
     *            "address1",
     *            "address2"
     *            ]
     * @return
     */
    @POST("app-{cid}/v060/transaction/myDelegate")
    Single<Response<ApiResponse<MyDelegate>>> getMyDelegateList(@Path("cid") String cid, @Body ApiRequestBody body);


    /**
     * 获取委托详情数据
     *
     * @param "cid": "",                          //链Id
     *               "walletAddrs":"address1"            //地址列表
     *               "beginSequence":120,                //起始序号 (必填) 客户端首次进入页面时传-1，-1：代表最新记录
     *               "listSize":100,                     //列表大小 (必填)
     *               "direction":""                      //方向 (必填) new：朝最新记录方向, old：朝最旧记录方向,
     *               客户端首次进入页面时或者上拉时传old。客户端自动获取最新记录时传new。
     */

    @POST("app-{cid}/v060/transaction/delegateDetails")
    Single<Response<ApiResponse<List<DelegateDetail>>>> getDelegateDetailList(@Path("cid") String cid, @Body ApiRequestBody body);


    /**
     * 获取委托记录的数据
     *
     * @param "cid": "",                          //链Id
     *               "beginSequence":120,                //起始序号 (必填) 客户端首次进入页面时传-1，-1：代表最新记录
     *               "listSize":100,                     //列表大小 (必填)
     *               "direction":""                      //方向 (必填) new：朝最新记录方向, old：朝最旧记录方向,
     *               客户端首次进入页面时或者上拉时传old。客户端自动获取最新记录时传new。
     *               "type":"all",                       //类型
     *               all —— 全部
     *               redeem —— 赎回
     *               delegate —— 委托
     *               "walletAddrs":[                     //地址列表
     *               "address1",
     *               "address2"
     *               ]
     */

    @POST("app-{cid}/v060/transaction/delegateRecord")
    Single<Response<ApiResponse<List<DelegateRecord>>>> getDelegateRecordList(@Path("cid") String cid, @Body ApiRequestBody body);


}
