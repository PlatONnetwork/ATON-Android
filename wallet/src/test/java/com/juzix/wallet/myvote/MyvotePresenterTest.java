package com.juzix.wallet.myvote;



import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;




@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 27)
public class MyvotePresenterTest {
//
////    private BaseApi baseApi;
//
//    private MyVotePresenter myVotePresenter;
//    @Mock
//    private MyVoteContract.View view;
//
//    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();
//
//    @Rule
//    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();
//
//    @Mock
//    public NodeManager nodeManager;
//    @Mock
//    public Node node;
//
//
//
//    @Before
//    public void setup() {
//
////        baseApi = ServerUtils.getCommonApi();
//        Application app = RuntimeEnvironment.application;
//        ApiResponse.init(app);
//
//
//        AppSettings appSettings = AppSettings.getInstance();
//        nodeManager = NodeManager.getInstance();
//        node = new Node.Builder().build();
//        nodeManager.setCurNode(node);
//
//        //输出日志
//        ShadowLog.stream = System.out;
//        myVotePresenter = new MyVotePresenter();
//        myVotePresenter.attachView(view);
//
//        appSettings.init(RuntimeEnvironment.application);
//
//    }
//
//    @Test
//    public void testLoadMyVoteData() {
//        String[] strings = {"0xfeee1657553f08fb1d12f35d492b1b8f5aa2fa4e", "0x493301712671ada506ba6ca7891f436d29185821", "0x19a74462197bf1bebbab51c246a948ffb7791b5d"};
////        myVotePresenter.getBatchVoteTransaction(strings);
//
//        List<VotedCandidate> votedCandidates = ServerUtils.getCommonApi().getVotedCandidateList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
//                .put("walletAddrs", strings)
//                .build()).
//                flatMap(new Function<Response<ApiResponse<List<VotedCandidate>>>, SingleSource<Response<ApiResponse<List<VotedCandidate>>>>>() {
//                    @Override
//                    public SingleSource<Response<ApiResponse<List<VotedCandidate>>>> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
//                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
//                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
//                        } else {
//                            List<VotedCandidate> list = apiResponseResponse.body().getData();
////                            List<Country> countryEntityList = CountryUtil.getCountryList(RuntimeEnvironment.application);
//                            return Flowable.fromIterable(list)
//                                    .map(new Function<VotedCandidate, VotedCandidate>() {
//                                        @Override
//                                        public VotedCandidate apply(VotedCandidate votedCandidateEntity) throws Exception {
//                                            //                                    votedCandidateEntity.setCountryEntity(getCountryEntityByCountryCode(countryEntityList, votedCandidateEntity.getCountryCode()));
//                                            return votedCandidateEntity;
//                                        }
//                                    }).toList()
//                                    .map(new Function<List<VotedCandidate>, Response<ApiResponse<List<VotedCandidate>>>>() {
//                                        @Override
//                                        public Response<ApiResponse<List<VotedCandidate>>> apply(List<VotedCandidate> entityList) throws Exception {
//                                            return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, entityList));
//                                        }
//                                    });
//                        }
//                    }
//                })
////                .compose(RxUtils.getSingleSchedulerTransformer())
//                .map(new Function<Response<ApiResponse<List<VotedCandidate>>>, List<VotedCandidate>>() {
//                    @Override
//                    public List<VotedCandidate> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
//                        return apiResponseResponse.body().getData();
//                    }
//                })
//                .blockingGet();
//
//
//        //        verify(view).showNoVoteSummary(new ArrayList<>());
////        verify(view).showMyVoteListData(votedCandidates);
////        assertEquals(2,votedCandidates.size());
//        assertNotNull(votedCandidates);
//
//        Log.debug("result", "vote------------>" + votedCandidates);
//
//    }
//
//
//    @Test
//    public void testMyVoteRequest() {
//        String[] addressList = {"0xfeee1657553f08fb1d12f35d492b1b8f5aa2fa4e", "0x493301712671ada506ba6ca7891f436d29185821", "0x19a74462197bf1bebbab51c246a948ffb7791b5d"};
//
//        ServerUtils.getCommonApi().getVotedCandidateList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
//                .put("walletAddrs", addressList)
//                .build())
////                .flatMap(new Function<Response<ApiResponse<List<VotedCandidate>>>, SingleSource<Response<ApiResponse<List<VotedCandidate>>>>>() {
////                    @Override
////                    public SingleSource<Response<ApiResponse<List<VotedCandidate>>>> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
////                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
////                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
////                        } else {
////                            List<VotedCandidate> list = apiResponseResponse.body().getData();
//////                            List<Country> countryEntityList = CountryUtil.getCountryList(getContext());
////                            return Flowable.fromIterable(list)
////                                    .map(new Function<VotedCandidate, VotedCandidate>() {
////                                        @Override
////                                        public VotedCandidate apply(VotedCandidate votedCandidateEntity) throws Exception {
//////                                            votedCandidateEntity.setCountryEntity(getCountryEntityByCountryCode(countryEntityList, votedCandidateEntity.getCountryCode()));
////                                            return votedCandidateEntity;
////                                        }
////                                    }).toList()
////                                    .map(new Function<List<VotedCandidate>, Response<ApiResponse<List<VotedCandidate>>>>() {
////                                        @Override
////                                        public Response<ApiResponse<List<VotedCandidate>>> apply(List<VotedCandidate> entityList) throws Exception {
////                                            return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, entityList));
////                                        }
////                                    });
////                        }
////
////                    }
////                })
////                .compose(bindToLifecycle())
////                .compose(RxUtils.getSingleSchedulerTransformer())
//                .subscribe(new ApiSingleObserver<List<VotedCandidate>>() {
//                    @Override
//                    public void onApiSuccess(List<VotedCandidate> entityList) {
//                        assertEquals("测试结果", entityList);
////                        verify(view).showMyVoteListData(entityList);
//
//                    }
//
//                    @Override
//                    public void onApiFailure(ApiResponse response) {
//                        //请求数据失败
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        android.util.Log.e("test", e.toString());
//                    }
//                });
//    }




}
