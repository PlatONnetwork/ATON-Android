package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.MapUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Country;
import com.juzix.wallet.entity.VoteSummary;
import com.juzix.wallet.entity.VotedCandidate;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.CountryUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class MyVotePresenter extends BasePresenter<MyVoteContract.View> implements MyVoteContract.Presenter {

    private final static String TAG = MyVotePresenter.class.getSimpleName();
    private final static String TAG_LOCKED = "tag_locked";//锁定
    private final static String TAG_EARNINGS = "tag_earnings";//收益
    private final static String TAG_INVALIDNUM = "tag_inValidNum";//失效
    private final static String TAG_VALIDNUM = "tag_validNum";//有效

    public MyVotePresenter(MyVoteContract.View view) {
        super(view);
    }

    @Override
    public void loadMyVoteData() {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        getBatchVoteTransaction(walletAddressList.toArray(new String[walletAddressList.size()]));
    }


    public void voteTicket(String nodeId, String nodeName) {
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
                                SubmitVoteActivity.actionStart(currentActivity(), nodeId, nodeName);
                            }
                        }
                    });
        }

    }

    private void getBatchVoteTransaction(String[] addressList) {

        ServerUtils.getCommonApi().getVotedCandidateList(NodeManager.getInstance().getChainId(), ApiRequestBody.newBuilder()
                .put("walletAddrs", addressList)
                .build())
                .flatMap(new Function<Response<ApiResponse<List<VotedCandidate>>>, SingleSource<Response<ApiResponse<List<VotedCandidate>>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<List<VotedCandidate>>>> apply(Response<ApiResponse<List<VotedCandidate>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse == null || !apiResponseResponse.isSuccessful()) {
                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.NETWORK_ERROR)));
                        } else {
                            List<VotedCandidate> list = apiResponseResponse.body().getData();
                            List<Country> countryEntityList = CountryUtil.getCountryList(getContext());
                            return Flowable.fromIterable(list)
                                    .map(new Function<VotedCandidate, VotedCandidate>() {
                                        @Override
                                        public VotedCandidate apply(VotedCandidate votedCandidateEntity) throws Exception {
                                            votedCandidateEntity.setCountryEntity(getCountryEntityByCountryCode(countryEntityList, votedCandidateEntity.getCountryCode()));
                                            return votedCandidateEntity;
                                        }
                                    }).toList()
                                    .map(new Function<List<VotedCandidate>, Response<ApiResponse<List<VotedCandidate>>>>() {
                                        @Override
                                        public Response<ApiResponse<List<VotedCandidate>>> apply(List<VotedCandidate> entityList) throws Exception {
                                            return Response.success(new ApiResponse<>(ApiErrorCode.SUCCESS, entityList));
                                        }
                                    });
                        }

                    }
                })
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VotedCandidate>>() {
                    @Override
                    public void onApiSuccess(List<VotedCandidate> entityList) {
                        if(isViewAttached()){
                            if (entityList != null && entityList.size() > 0) {
                                getView().showBatchVoteSummary(buildVoteTitleList(entityList));
//                            getView().showMyVoteListData(entityList);
                                getView().showMyVoteListData(BuildSortList(entityList));

                            } else {
                                getView().showBatchVoteSummary(buildDefaultVoteSummaryList());
                                getView().showMyVoteListData(entityList);
                            }
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        //请求数据失败
                        if(isViewAttached()){
                            getView().showMyVoteListDataFailed();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().showMyVoteListDataFailed();
                    }
                });

    }

    private List<VoteSummary> buildVoteTitleList(List<VotedCandidate> entityList) {
        Map<String, Object> stringObjectMap = new HashMap<>();
        double totalTicketNum = 0;//总票数
        double lockedNum = 0;//投票锁定
        double voteReward = 0;//投票收益
        double validVoteNum = 0;//有效票数

        stringObjectMap.clear();

        for (VotedCandidate entity : entityList) {

            totalTicketNum += NumberParserUtils.parseDouble(entity.getTotalTicketNum());
//            lockedNum += NumberParserUtils.parseDouble(entity.getLocked());
            lockedNum += NumberParserUtils.parseDouble(NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(entity.getLocked(), "1E18"), 4));
//            voteReward += NumberParserUtils.parseDouble(entity.getEarnings());
            voteReward += NumberParserUtils.parseDouble(NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(entity.getEarnings(), "1E18"), 4));

            validVoteNum += NumberParserUtils.parseDouble(entity.getValidNum());
        }

        double invalidVoteNum = BigDecimalUtil.sub(totalTicketNum, validVoteNum); //失效票数

        stringObjectMap.put(TAG_LOCKED, NumberParserUtils.parseDouble(lockedNum + MapUtils.getDouble(stringObjectMap, TAG_LOCKED)));
        stringObjectMap.put(TAG_EARNINGS, NumberParserUtils.parseDouble(voteReward + MapUtils.getDouble(stringObjectMap, TAG_EARNINGS)));
        stringObjectMap.put(TAG_INVALIDNUM, invalidVoteNum + MapUtils.getDouble(stringObjectMap, TAG_INVALIDNUM));
        stringObjectMap.put(TAG_VALIDNUM, BigDecimalUtil.add(validVoteNum, MapUtils.getDouble(stringObjectMap, TAG_VALIDNUM)));

        return buildVoteSummaryList(stringObjectMap);

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

    private List<VotedCandidate> BuildSortList(List<VotedCandidate> list) {
        Collections.sort(list, Collections.reverseOrder());
        return list;
    }

    private List<VoteSummary> buildDefaultVoteSummaryList() {
        List<VoteSummary> voteSummaryEntityList = new ArrayList<>();
        voteSummaryEntityList.add(new VoteSummary(String.format("%s%s", string(R.string.lockVote), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummary(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummary(String.format("%s", string(R.string.validInvalidTicket)), String.format("%s/%s", "-", "-")));
        return voteSummaryEntityList;
    }

    private List<VoteSummary> buildVoteSummaryList(Map<String, Object> map) {
        List<VoteSummary> voteSummaryEntityList = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            double locked = MapUtils.getDouble(map, TAG_LOCKED);
            double earnings = MapUtils.getDouble(map, TAG_EARNINGS);
            double invalidNum = MapUtils.getDouble(map, TAG_INVALIDNUM);
            double validNum = MapUtils.getDouble(map, TAG_VALIDNUM);

            voteSummaryEntityList.add(new VoteSummary(String.format("%s%s", string(R.string.lockVote), "(Energon)"), NumberParserUtils.getPrettyNumber(locked, 0)));
            voteSummaryEntityList.add(new VoteSummary(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), NumberParserUtils.getPrettyNumber(earnings, 4)));
            voteSummaryEntityList.add(new VoteSummary(String.format("%s", string(R.string.validInvalidTicket)), String.format("%s/%s", NumberParserUtils.getPrettyNumber(validNum, 0), NumberParserUtils.getPrettyNumber(invalidNum, 0))));
        }

        return voteSummaryEntityList;
    }


}
