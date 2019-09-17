package com.juzix.wallet.myvote;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.entity.CandidateWrap;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Response;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27)
public class VoteFragmentPresenterTest {
//    private VotePresenter votePresenter;
//    @Mock
//    private VoteContract.View view;
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
//    @Before
//    public void setup() {
//        AppSettings appSettings = AppSettings.getInstance();
//        NodeManager nodeManager = NodeManager.getInstance();
//        //输出日志
//        ShadowLog.stream = System.out;
//
//        schedulerTestProvider = new SchedulerTestProvider();
//
//        view = mock(VoteContract.View.class);
//
//        votePresenter = new VotePresenter(view);
//
//        votePresenter.attachView(view);
//
//        appSettings.init(RuntimeEnvironment.application);
//    }
//
//
//   @Test
//    public  void  testVoteFragmentData(){
//       ServerUtils
//               .getCommonApi()
//               .getCandidateList(NodeManager.getInstance().getChainId())
//               .flatMap(new Function<Response<ApiResponse<CandidateWrap>>, SingleSource<Response<ApiResponse<CandidateWrap>>>>() {
//                   @Override
//                   public SingleSource<Response<ApiResponse<CandidateWrap>>> apply(Response<ApiResponse<CandidateWrap>> apiResponseResponse) throws Exception {
//                       if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
//                           return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
//                       } else {
//                           CandidateWrap candidateWrapEntity = apiResponseResponse.body().getData();
////                           List<Country> countryEntityList = CountryUtil.getCountryList(getContext());
//                           return Flowable.fromIterable(candidateWrapEntity.getCandidateEntityList())
//                                   .map(new Function<Candidate, Candidate>() {
//                                       @Override
//                                       public Candidate apply(Candidate candidateEntity) throws Exception {
////                                           candidateEntity.setCountryEntity(getCountryEntityByCountryCode(countryEntityList, candidateEntity.getCountryCode()));
//                                           candidateEntity.setTicketPrice(candidateWrapEntity.getTicketPrice());
//                                           return candidateEntity;
//                                       }
//                                   })
//                                   .toList()
//                                   .map(new Function<List<Candidate>, Response<ApiResponse<CandidateWrap>>>() {
//                                       @Override
//                                       public Response<ApiResponse<CandidateWrap>> apply(List<Candidate> candidateEntityList) throws Exception {
//                                           candidateWrapEntity.setCandidateEntityList(candidateEntityList);
//                                           return Response.success(new ApiResponse(ApiErrorCode.SUCCESS, candidateWrapEntity));
//                                       }
//                                   });
//                       }
//                   }
//
//               })
////               .compose(bindUntilEvent(FragmentEvent.STOP))
////               .compose(RxUtils.getSingleSchedulerTransformer())
//               .doOnSubscribe(new Consumer<Disposable>() {
//                   @Override
//                   public void accept(Disposable disposable) throws Exception {
////                       if (isViewAttached()){
////                           if (mCandidateEntiyList == null || mCandidateEntiyList.isEmpty()) {
////                               showLoadingDialog();
////                           }
////                       }
//                   }
//               })
//               .doFinally(new Action() {
//                   @Override
//                   public void run() throws Exception {
////                       if (isViewAttached()){
////                           dismissLoadingDialogImmediately();
////                       }
//                   }
//               })
//               .subscribe(new ApiSingleObserver<CandidateWrap>() {
//                   @Override
//                   public void onApiSuccess(CandidateWrap candidateWrapEntity) {
////                       if (isViewAttached()) {
////                           mCandidateEntiyList = candidateWrapEntity.getCandidateEntityList();
////                           getView().setVotedInfo(candidateWrapEntity.getTotalCount(), candidateWrapEntity.getVoteCount(), candidateWrapEntity.getTicketPrice());
////                           showCandidateList(mKeyword, mSortType);
////                           getView().finishRefresh();
////                       }
//
//                       Log.debug("reuslt","-------------->" +candidateWrapEntity);
////                       verify(view).getVoteDetailListDataSuccess(entityList);
//                        votePresenter.getView().setVotedInfo(candidateWrapEntity.getTotalCount(),candidateWrapEntity.getVoteCount(),candidateWrapEntity.getTicketPrice());
//                        votePresenter.getView().notifyDataSetChanged(candidateWrapEntity.getCandidateEntityList());
//                       Log.debug("result执行完","=====================>" +candidateWrapEntity.getCandidateEntityList().toString());
//
//                   }
//
//                   @Override
//                   public void onApiFailure(ApiResponse response) {
////                       if (isViewAttached()) {
////                           getView().finishRefresh();
////                       }
//                       Log.debug("shibai","---------------" +response.getErrorCode() +"" +response.getErrMsg(RuntimeEnvironment.application));
//                   }
//               });
//   }

}
