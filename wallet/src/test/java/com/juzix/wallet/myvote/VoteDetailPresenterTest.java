package com.juzix.wallet.myvote;


import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;
import com.juzix.wallet.schedulers.SchedulerTestProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Response;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27)
public class VoteDetailPresenterTest {
//    private static final String TAG = "VoteDetailPresenterTest";
//
//    private VoteDetailPresenter voteDetailPresenter;
//    @Mock
//    private VoteDetailContract.View view;
//
//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();
//
//    @Mock
//    private SchedulerTestProvider schedulerTestProvider;
//
//    @Rule
//    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();
//
//    @Mock
//    public NodeManager nodeManager;
//    @Mock
//    public Node node;
//
////    @Mock
////    VotedCandidate candidate;
//
//    @Before
//    public void setup() {
////        candidate = new VotedCandidate();
//        ApiResponse.init(RuntimeEnvironment.application);
//
//        AppSettings appSettings = AppSettings.getInstance();
//        nodeManager = NodeManager.getInstance();
//        node = new Node.Builder().build();
//        nodeManager.setCurNode(node);
//
//        //输出日志
//        ShadowLog.stream = System.out;
//
//        schedulerTestProvider = new SchedulerTestProvider();
//        view = mock(VoteDetailContract.View.class);
//
//        voteDetailPresenter = new VoteDetailPresenter(view, schedulerTestProvider);
//
//        voteDetailPresenter.attachView(view);
//
//        appSettings.init(RuntimeEnvironment.application);
//    }
//
//
//    @Test
//    public void testLoadVoteDetailData() {
//
//        String nodeId = "0x3b53564afbc3aef1f6e0678171811f65a7caa27a927ddd036a46f817d075ef0a5198cd7f480829b53fe62bdb063bc6a17f800d2eebf7481b091225aabac2428d";
//        String[] addressList = {"0xfeee1657553f08fb1d12f35d492b1b8f5aa2fa4e", "0x493301712671ada506ba6ca7891f436d29185821", "0x19a74462197bf1bebbab51c246a948ffb7791b5d"};
//        List<VotedCandidate> list = ServerUtils.getCommonApi().getVotedCandidateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
//                .put("beginSequence", -1)
//                .put("listSize", 10)
//                .put("nodeId", nodeId)
//                .put("direction", "old")
//                .put("walletAddrs", addressList)
//                .build())
//                .flatMap(new Function<Response<ApiResponse<List<VotedCandidate>>>, SingleSource<Response<ApiResponse<List<VotedCandidate>>>>>() {
//
//                    @Override
//                    public SingleSource<Response<ApiResponse<List<VotedCandidate>>>> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
//                        if (!apiResponseResponse.isSuccessful() && apiResponseResponse == null) {
//
//                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
//                        } else {
//                            List<VotedCandidate> data = apiResponseResponse.body().getData();
//                            return Flowable.fromIterable(data).map(new Function<VotedCandidate, VotedCandidate>() {
//
//                                @Override
//                                public VotedCandidate apply(VotedCandidate votedCandidate) throws Exception {
//                                    return votedCandidate;
//                                }
//                            }).toList().map(new Function<List<VotedCandidate>, Response<ApiResponse<List<VotedCandidate>>>>() {
//                                @Override
//                                public Response<ApiResponse<List<VotedCandidate>>> apply(List<VotedCandidate> votedCandidates) throws Exception {
//                                    return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, votedCandidates));
//                                }
//                            });
//
//                        }
//
//                    }
//                })
//                .map(new Function<Response<ApiResponse<List<VotedCandidate>>>, List<VotedCandidate>>() {
//
//                    @Override
//                    public List<VotedCandidate> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
//                        return apiResponseResponse.body().getData();
//                    }
//                }).blockingGet();
//
//        assertNotNull(list);
//
//        Log.debug(TAG, "vote------------>" + list.toString());
//
//        verify(view).getVoteDetailListDataSuccess(list);
//
//        Log.debug(TAG, "vote====================>" + list.size());
//    }
//
//    @Test
//    public void vertifyResult() {
//        String nodeId = "0x3b53564afbc3aef1f6e0678171811f65a7caa27a927ddd036a46f817d075ef0a5198cd7f480829b53fe62bdb063bc6a17f800d2eebf7481b091225aabac2428d";
//        String[] addressList = {"0xfeee1657553f08fb1d12f35d492b1b8f5aa2fa4e", "0x493301712671ada506ba6ca7891f436d29185821", "0x19a74462197bf1bebbab51c246a948ffb7791b5d"};
//        ServerUtils.getCommonApi().getVotedCandidateDetailList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
//                .put("beginSequence", -1)
//                .put("listSize", 10)
//                .put("nodeId", nodeId)
//                .put("direction", "old")
//                .put("walletAddrs", addressList)
//                .build())
//                .flatMap(new Function<Response<ApiResponse<List<VotedCandidate>>>, SingleSource<Response<ApiResponse<List<VotedCandidate>>>>>() {
//
//                    @Override
//                    public SingleSource<Response<ApiResponse<List<VotedCandidate>>>> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
//                        if (!apiResponseResponse.isSuccessful() && apiResponseResponse == null) {
//
//                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
//                        } else {
//                            List<VotedCandidate> data = apiResponseResponse.body().getData();
//                            return Flowable.fromIterable(data).map(new Function<VotedCandidate, VotedCandidate>() {
//
//                                @Override
//                                public VotedCandidate apply(VotedCandidate votedCandidate) throws Exception {
//                                    return votedCandidate;
//                                }
//                            }).toList().map(new Function<List<VotedCandidate>, Response<ApiResponse<List<VotedCandidate>>>>() {
//                                @Override
//                                public Response<ApiResponse<List<VotedCandidate>>> apply(List<VotedCandidate> votedCandidates) throws Exception {
//                                    return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, votedCandidates));
//                                }
//                            });
//
//                        }
//
//                    }
//                })
//                .subscribe(new ApiSingleObserver<List<VotedCandidate>>() {
//                    @Override
//                    public void onApiSuccess(List<VotedCandidate> entityList) {
//                        Log.debug("reuslt", "-------------->" + entityList.toString());
////                        verify(view).getVoteDetailListDataSuccess(entityList);
//                        voteDetailPresenter.getView().getVoteDetailListDataSuccess(entityList);
//                        Log.debug("result执行完", "=====================>" + entityList.toString());
//
//                    }
//
//                    @Override
//                    public void onApiFailure(ApiResponse response) {
//                        verify(view).getVoteDetailListDataFailed();
//                        Log.debug("shibai", "---------------" + response.getErrorCode() + "" + response.getErrMsg(RuntimeEnvironment.application));
//
//                    }
//                });
//    }


}
