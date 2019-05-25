package com.juzix.wallet.component.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.entity.CandidateWrap;
import com.juzix.wallet.entity.Country;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.CountryUtil;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class VotePresenter extends BasePresenter<VoteContract.View> implements VoteContract.Presenter {

    private List<Candidate> mCandidateEntiyList = new ArrayList<>();
    private String mKeyword;
    private SortType mSortType = SortType.SORTED_BY_DEFAULT;

    public VotePresenter(VoteContract.View view) {
        super(view);
    }

    @Override
    public void getCandidateList() {

        ServerUtils
                .getCommonApi()
                .getCandidateList(NodeManager.getInstance().getChainId())
                .flatMap(new Function<Response<ApiResponse<CandidateWrap>>, SingleSource<Response<ApiResponse<CandidateWrap>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<CandidateWrap>>> apply(Response<ApiResponse<CandidateWrap>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
                        } else {
                            CandidateWrap candidateWrapEntity = apiResponseResponse.body().getData();
                            List<Country> countryEntityList = CountryUtil.getCountryList(getContext());
                            return Flowable.fromIterable(candidateWrapEntity.getCandidateEntityList())
                                    .map(new Function<Candidate, Candidate>() {
                                        @Override
                                        public Candidate apply(Candidate candidateEntity) throws Exception {
                                            candidateEntity.setCountryEntity(getCountryEntityByCountryCode(countryEntityList, candidateEntity.getCountryCode()));
                                            candidateEntity.setTicketPrice(candidateWrapEntity.getTicketPrice());
                                            return candidateEntity;
                                        }
                                    })
                                    .toList()
                                    .map(new Function<List<Candidate>, Response<ApiResponse<CandidateWrap>>>() {
                                        @Override
                                        public Response<ApiResponse<CandidateWrap>> apply(List<Candidate> candidateEntityList) throws Exception {
                                            candidateWrapEntity.setCandidateEntityList(candidateEntityList);
                                            return Response.success(new ApiResponse(ApiErrorCode.SUCCESS, candidateWrapEntity));
                                        }
                                    });
                        }
                    }

                })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (isViewAttached()){
                            if (mCandidateEntiyList == null || mCandidateEntiyList.isEmpty()) {
                                showLoadingDialog();
                            }
                        }
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (isViewAttached()){
                            dismissLoadingDialogImmediately();
                        }
                    }
                })
                .subscribe(new ApiSingleObserver<CandidateWrap>() {
                    @Override
                    public void onApiSuccess(CandidateWrap candidateWrapEntity) {
                        if (isViewAttached()) {
                            mCandidateEntiyList = candidateWrapEntity.getCandidateEntityList();
                            getView().setVotedInfo(candidateWrapEntity.getTotalCount(), candidateWrapEntity.getVoteCount(), candidateWrapEntity.getTicketPrice());
                            showCandidateList(mKeyword, mSortType);
                            getView().finishRefresh();
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().finishRefresh();
                        }
                    }
                });

    }

    @Override
    public void sort(SortType sortType) {
        this.mSortType = sortType;
        showCandidateList(mKeyword, sortType);
    }

    @Override
    public void search(String keyword) {
        this.mKeyword = keyword;
        showCandidateList(keyword, mSortType);
    }

    @SuppressLint("CheckResult")
    @Override
    public void voteTicket(Candidate candidateEntity) {

        if (isViewAttached()) {

            List<Wallet> walletEntityList = WalletManager.getInstance().getWalletList();
            if (walletEntityList.isEmpty()) {
                showLongToast(R.string.voteTicketCreateWalletTips);
                return;
            }

            Flowable
                    .fromIterable(walletEntityList)
                    .map(new Function<Wallet, Double>() {
                        @Override
                        public Double apply(Wallet individualWalletEntity) throws Exception {
                            return individualWalletEntity.getBalance();
                        }
                    })
                    .reduce(new BiFunction<Double, Double, Double>() {
                        @Override
                        public Double apply(Double aDouble, Double aDouble2) throws Exception {
                            return BigDecimalUtil.add(aDouble, aDouble2);
                        }
                    })
                    .subscribe(new Consumer<Double>() {
                        @Override
                        public void accept(Double totalBalance) throws Exception {
                            if (totalBalance <= 0) {
                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
                            } else {
                                SubmitVoteActivity.actionStart(currentActivity(), candidateEntity.getNodeId(), candidateEntity.getName(),candidateEntity.getDeposit());
                            }
                        }
                    });
        }

    }

    @Override
    public void clearCandidateList() {

    }

    private void showCandidateList(String keyWord, SortType sortType) {
        if (mCandidateEntiyList == null || mCandidateEntiyList.isEmpty()) {
            return;
        }
        List<Candidate> resultList = getSearchResult(keyWord, mCandidateEntiyList);
        //如果不是默认排序
        Collections.sort(resultList, sortType.getComparator());

        if (isViewAttached()) {
            if (!TextUtils.isEmpty(keyWord) && (resultList == null || resultList.isEmpty())) {
                showLongToast(R.string.query_no_result);
            }
            getView().notifyDataSetChanged(resultList);
        }
    }

    private List<Candidate> getSearchResult(String keyWord, List<Candidate> candidateEntityList) {
        if (TextUtils.isEmpty(keyWord)) {
            return candidateEntityList;
        } else {
            List<Candidate> result = new ArrayList<>();
            for (Candidate candidateEntity : candidateEntityList) {
                String nodeName = candidateEntity.getName();
                if ((!TextUtils.isEmpty(nodeName)) && nodeName.toLowerCase().contains(keyWord.toLowerCase())) {
                    result.add(candidateEntity);
                }
            }
            return result;
        }
    }

    private Country getCountryEntityByCountryCode(List<Country> countryEntityList, String countryCode) {
        if (countryEntityList == null || TextUtils.isEmpty(countryCode)) {
            return Country.getNullInstance();
        }
        return Flowable
                .fromIterable(countryEntityList)
                .filter(new Predicate<Country>() {
                    @Override
                    public boolean test(Country countryEntity) throws Exception {
                        return countryCode.equals(countryEntity.getCountryCode());
                    }
                })
                .firstElement()
                .switchIfEmpty(new SingleSource<Country>() {
                    @Override
                    public void subscribe(SingleObserver<? super Country> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .onErrorReturnItem(Country.getNullInstance())
                .blockingGet();
    }


}
