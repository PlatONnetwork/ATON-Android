package com.juzix.wallet.component.ui.presenter;

import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.network.FlowableSchedulersTransformer;
import com.juzhen.framework.network.SchedulersTransformer;
import com.juzhen.framework.util.MapUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.VoteSummaryEntity;
import com.juzix.wallet.entity.VotedCandidateEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
        List<String> walletAddressList = IndividualWalletManager.getInstance().getAddressList();
        getBatchVoteTransaction(walletAddressList.toArray(new String[walletAddressList.size()]));
    }


    public void voteTicket(String nodeId,String nodeName) {

        if (isViewAttached()) {

            Flowable.just(IndividualWalletManager.getInstance().getWalletList())
                    .filter(new Predicate<ArrayList<IndividualWalletEntity>>() {
                        @Override
                        public boolean test(ArrayList<IndividualWalletEntity> individualWalletEntities) throws Exception {
                            return !individualWalletEntities.isEmpty();
                        }
                    })
                    .switchIfEmpty(new Flowable<ArrayList<IndividualWalletEntity>>() {
                        @Override
                        protected void subscribeActual(Subscriber<? super ArrayList<IndividualWalletEntity>> s) {
                            s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_EXIST_VALID_WALLET));
                        }
                    })
                    .flatMap(new Function<ArrayList<IndividualWalletEntity>, Publisher<IndividualWalletEntity>>() {
                        @Override
                        public Publisher<IndividualWalletEntity> apply(ArrayList<IndividualWalletEntity> individualWalletEntities) throws Exception {
                            return Flowable.fromIterable(individualWalletEntities);
                        }
                    })
                    .map(new Function<IndividualWalletEntity, Double>() {
                        @Override
                        public Double apply(IndividualWalletEntity individualWalletEntity) throws Exception {
                            return individualWalletEntity.getBalance();
                        }
                    })
                    .reduce(new BiFunction<Double, Double, Double>() {
                        @Override
                        public Double apply(Double aDouble, Double aDouble2) throws Exception {
                            return BigDecimalUtil.add(aDouble, aDouble2);
                        }
                    })
                    .filter(new Predicate<Double>() {
                        @Override
                        public boolean test(Double totalBalance) throws Exception {
                            return totalBalance > 0;
                        }
                    })
                    .switchIfEmpty(new Single<Double>() {
                        @Override
                        protected void subscribeActual(SingleObserver<? super Double> observer) {
                            observer.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_VOTE_TICKET_INSUFFICIENT_BALANCE));
                        }
                    })
                    .flatMap(new Function<Double, SingleSource<CandidateEntity>>() {
                        @Override
                        public SingleSource<CandidateEntity> apply(Double aDouble) throws Exception {
                            return null;
                        }
                    })
                    .compose(RxUtils.getSingleSchedulerTransformer())
                    .compose(bindToLifecycle())
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new Consumer<CandidateEntity>() {
                        @Override
                        public void accept(CandidateEntity candidateEntity) throws Exception {
                            if (isViewAttached()) {
                                SubmitVoteActivity.actionStart(currentActivity(), nodeId,nodeName);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (throwable instanceof CustomThrowable) {
                                showLongToast(((CustomThrowable) throwable).getDetailMsgRes());
                            }
                        }
                    });

        }

    }

    private void getBatchVoteTransaction(String[] addressList) {

        ServerUtils.getCommonApi().getVotedCandidateList(NodeManager.getInstance().getChainId(),ApiRequestBody.newBuilder()
                .put("walletAddrs",addressList)
                .build())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VotedCandidateEntity>>() {
                    @Override
                    public void onApiSuccess(List<VotedCandidateEntity> entityList) {
                        if (entityList != null && entityList.size() > 0) {
                            getView().showBatchVoteSummary(buildVoteTitleList(entityList));
//                            getView().showMyVoteListData(entityList);
                            getView().showMyVoteListData(BuildSortList(entityList));

                        } else {
                            getView().showBatchVoteSummary(buildDefaultVoteSummaryList());
                            getView().showMyVoteListData(entityList);
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        //请求数据失败
                        getView().showMyVoteListDataFailed();

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().showMyVoteListDataFailed();
                    }
                });

    }

    private List<VoteSummaryEntity> buildVoteTitleList(List<VotedCandidateEntity> entityList) {
        Map<String, Object> stringObjectMap = new HashMap<>();
        double totalTicketNum = 0;//总票数
        double lockedNum = 0;//投票锁定
        double voteReward = 0;//投票收益
        double validVoteNum = 0;//有效票数

        stringObjectMap.clear();

        for (VotedCandidateEntity entity : entityList) {
            totalTicketNum += NumberParserUtils.parseDouble(entity.getTotalTicketNum());
            lockedNum += NumberParserUtils.parseDouble(entity.getLocked());
            voteReward += NumberParserUtils.parseDouble(entity.getEarnings());
            validVoteNum += NumberParserUtils.parseDouble(entity.getValidNum());
        }

        double invalidVoteNum = BigDecimalUtil.sub(totalTicketNum, validVoteNum); //失效票数

        stringObjectMap.put(TAG_LOCKED, NumberParserUtils.parseDouble(lockedNum + MapUtils.getDouble(stringObjectMap, TAG_LOCKED)));
        stringObjectMap.put(TAG_EARNINGS, NumberParserUtils.parseDouble(voteReward + MapUtils.getDouble(stringObjectMap, TAG_EARNINGS)));
        stringObjectMap.put(TAG_INVALIDNUM, invalidVoteNum + MapUtils.getDouble(stringObjectMap, TAG_INVALIDNUM));
        stringObjectMap.put(TAG_VALIDNUM, BigDecimalUtil.add(validVoteNum, MapUtils.getDouble(stringObjectMap, TAG_VALIDNUM)));

        return buildVoteSummaryList(stringObjectMap);

    }

    private List<VotedCandidateEntity> BuildSortList(List<VotedCandidateEntity> list) {
        Collections.sort(list, Collections.reverseOrder());
        return list;
    }

    private List<VoteSummaryEntity> buildDefaultVoteSummaryList() {
        List<VoteSummaryEntity> voteSummaryEntityList = new ArrayList<>();
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.lockVote), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s", string(R.string.validInvalidTicket)), String.format("%s/%s", "-", "-")));
        return voteSummaryEntityList;
    }

    private List<VoteSummaryEntity> buildVoteSummaryList(Map<String, Object> map) {
        List<VoteSummaryEntity> voteSummaryEntityList = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            double locked = MapUtils.getDouble(map, TAG_LOCKED);
            double earnings = MapUtils.getDouble(map, TAG_EARNINGS);
            double invalidNum = MapUtils.getDouble(map, TAG_INVALIDNUM);
            double validNum = MapUtils.getDouble(map, TAG_VALIDNUM);

            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.lockVote), "(Energon)"), NumberParserUtils.getPrettyNumber(locked, 0)));
            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(earnings, 1E18), 4)));
            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s", string(R.string.validInvalidTicket)), String.format("%s/%s", NumberParserUtils.getPrettyNumber(validNum, 0), NumberParserUtils.getPrettyNumber(invalidNum, 0))));
        }

        return voteSummaryEntityList;
    }


}
