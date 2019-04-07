package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.FlowableSchedulersTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class VotePresenter extends BasePresenter<VoteContract.View> implements VoteContract.Presenter {

    private final static String TAG = VotePresenter.class.getSimpleName();
    private final static int MAX_TICKET_POOL_SIZE = 51200;
    private final static int DEFAULT_VOTE_NUM = 100;
    private final static int DEFAULT_DEPOSIT_RANKING = 100;
    private static final int REFRESH_TIME = 5000;

    private List<CandidateEntity> mCandidateEntiyList = new ArrayList<>();
    private List<CandidateEntity> mVerifiersList = new ArrayList<>();
    private String mKeyword;
    private SortType mSortType = SortType.SORTED_BY_DEFAULT;
    private Disposable mTicketInfoDisposable;
    private Disposable mCandidateLsitDisposable;

    public VotePresenter(VoteContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        getTicketInfo();
        getCandidateList();
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

    @Override
    public void voteTicket(CandidateEntity candidateEntity) {

        if (isViewAttached()) {

            ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
            if (walletEntityList.isEmpty()) {
                showLongToast(R.string.voteTicketCreateWalletTips);
                return;
            }

            Flowable
                    .fromIterable(walletEntityList)
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
                    .subscribe(new Consumer<Double>() {
                        @Override
                        public void accept(Double totalBalance) throws Exception {
                            if (totalBalance <= 0) {
                                showLongToast(R.string.voteTicketInsufficientBalanceTips);
                            } else {
                                SubmitVoteActivity.actionStart(currentActivity(), candidateEntity);
                            }
                        }
                    });
        }

    }

    private void getTicketInfo() {
        if (mTicketInfoDisposable != null && !mTicketInfoDisposable.isDisposed()) {
            mTicketInfoDisposable.dispose();
        }
        mTicketInfoDisposable = Single
                .zip(VoteManager.getInstance().getPoolRemainder(), VoteManager.getInstance().getTicketPrice(), new BiFunction<Long, String, String>() {
                    @Override
                    public String apply(Long poolRemainder, String ticketPrice) throws Exception {
                        return poolRemainder + ":" + ticketPrice;
                    }
                })
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
                    }
                })
                .compose(new FlowableSchedulersTransformer())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        if (isViewAttached()) {
                            String poolRemainder = result.split(":", 2)[0];
                            String ticketPrice = result.split(":", 2)[1];
                            long votedNum = MAX_TICKET_POOL_SIZE - NumberParserUtils.parseLong(poolRemainder);
                            getView().setVotedInfo(MAX_TICKET_POOL_SIZE, votedNum, ticketPrice);
                        }
                    }
                });
    }

    private void getCandidateList() {
        if (mCandidateLsitDisposable != null && !mCandidateLsitDisposable.isDisposed()) {
            mCandidateLsitDisposable.dispose();
        }
        mCandidateLsitDisposable = CandidateManager
                .getInstance()
                .getCandidateList()
                .zipWith(CandidateManager.getInstance().getVerifiersList(), new BiFunction<List<CandidateEntity>, List<CandidateEntity>, List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> apply(List<CandidateEntity> candidateEntities, List<CandidateEntity> verifiersList) throws Exception {
                        mVerifiersList = verifiersList;
                        return candidateEntities;
                    }
                })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(new SchedulersTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (mCandidateEntiyList == null || mCandidateEntiyList.isEmpty()) {
                            showLoadingDialog();
                        }
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissLoadingDialogImmediately();
                    }
                })
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<List<CandidateEntity>>() {
                    @Override
                    public void accept(List<CandidateEntity> candidateEntityList) throws Exception {
                        if (isViewAttached()) {
                            mCandidateEntiyList = candidateEntityList;
                            showCandidateList(mKeyword, mSortType);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private List<CandidateEntity> getDefaultCandidateEntityList(List<CandidateEntity> candidateEntityList, List<CandidateEntity> verifiersList) {
        List<CandidateEntity> candidateList = new ArrayList<>();
        List<CandidateEntity> alternativeList = new ArrayList<>();
        if (candidateEntityList != null) {
            for (int i = 0; i < candidateEntityList.size(); i++) {
                //剔除排名200后的节点
                if (i < 2 * DEFAULT_DEPOSIT_RANKING) {
                    CandidateEntity entity = candidateEntityList.get(i);
                    entity.setStakedRanking(i + 1);
                    if (i < DEFAULT_DEPOSIT_RANKING && entity.getVotedNum() >= DEFAULT_VOTE_NUM) {
                        entity.setStatus(CandidateEntity.STATUS_CANDIDATE);
                        candidateList.add(entity);
                    } else {
                        entity.setStatus(CandidateEntity.STATUS_RESERVE);
                        alternativeList.add(entity);
                    }
                }
            }
        }
        //加入不在候选池中，就添加到候选池列表后面
        candidateList.addAll(getVerifiersList(candidateList, alternativeList, verifiersList));
        candidateList.addAll(alternativeList);
        return candidateList;
    }

    private List<CandidateEntity> getVerifiersList(List<CandidateEntity> candidateEntityList, List<CandidateEntity> alternativeList, List<CandidateEntity> verifiersList) {
        List<CandidateEntity> candidateEntities = new ArrayList<>();
        for (CandidateEntity candidateEntity : verifiersList) {
            if (!candidateEntityList.contains(candidateEntity) && !alternativeList.contains(candidateEntity)) {
                candidateEntities.add(candidateEntity);
            }
        }

        return candidateEntities;
    }

    private void showCandidateList(String keyWord, SortType sortType) {

        if (mCandidateEntiyList != null) {

            List<CandidateEntity> candidateEntities = getSearchResult(keyWord, mCandidateEntiyList);

            Collections.sort(candidateEntities, sortType.getComparator());

            if (sortType == SortType.SORTED_BY_DEFAULT) {
                candidateEntities = getDefaultCandidateEntityList(candidateEntities, mVerifiersList);
            }

            if (isViewAttached()) {
                if (!TextUtils.isEmpty(keyWord) && (candidateEntities == null || candidateEntities.isEmpty())) {
                    showLongToast(R.string.query_no_result);
                }
                getView().notifyDataSetChanged(candidateEntities);
            }
        }
    }

    private List<CandidateEntity> getSearchResult(String keyWord, List<CandidateEntity> candidateEntityList) {
        if (TextUtils.isEmpty(keyWord)) {
            return candidateEntityList;
        } else {
            List<CandidateEntity> result = new ArrayList<>();
            for (CandidateEntity candidateEntity : mCandidateEntiyList) {
                CandidateExtraEntity candidateExtraEntity = candidateEntity.getCandidateExtraEntity();
                if (candidateExtraEntity != null) {
                    String nodeName = candidateExtraEntity.getNodeName();
                    if ((!TextUtils.isEmpty(nodeName)) && nodeName.toLowerCase().contains(mKeyword.toLowerCase())) {
                        result.add(candidateEntity);
                    }
                }
            }
            return result;
        }
    }

}
