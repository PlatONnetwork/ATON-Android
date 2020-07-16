package com.platon.aton.component.ui.presenter;

import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.ClaimRecordContract;
import com.platon.aton.engine.Optional;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.ClaimRewardRecord;
import com.platon.aton.utils.RxUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

public class ClaimRecordPresenter extends BasePresenter<ClaimRecordContract.View> implements ClaimRecordContract.Presenter {

    private Disposable mDisposable;
    private List<ClaimRewardRecord> mOldClaimRewardRecordList;

    @Override
    public void getRewardTransactions(String direction) {

        boolean isLoadMore = Direction.DIRECTION_OLD.equals(direction);

        mDisposable = ServerUtils
                .getCommonApi()
                .getRewardTransactions(ApiRequestBody.newBuilder()
                        .put("walletAddrs", WalletManager.getInstance().getAddressList())
                        .put("beginSequence", getBeginSequence(direction))
                        .put("listSize", 10)
                        .put("direction", direction)
                        .build())
                .map(new Function<Response<ApiResponse<List<ClaimRewardRecord>>>, Optional<List<ClaimRewardRecord>>>() {
                    @Override
                    public Optional<List<ClaimRewardRecord>> apply(Response<ApiResponse<List<ClaimRewardRecord>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body() != null ? new Optional<List<ClaimRewardRecord>>(apiResponseResponse.body().getData()) : new Optional<List<ClaimRewardRecord>>(null);
                    }
                })
                .toFlowable()
                .filter(new Predicate<Optional<List<ClaimRewardRecord>>>() {
                    @Override
                    public boolean test(Optional<List<ClaimRewardRecord>> listOptional) throws Exception {
                        return !listOptional.isEmpty();
                    }
                })
                .switchIfEmpty(new Publisher<Optional<List<ClaimRewardRecord>>>() {
                    @Override
                    public void subscribe(Subscriber<? super Optional<List<ClaimRewardRecord>>> s) {
                        s.onError(new Throwable());
                    }
                })
                .flatMap(new Function<Optional<List<ClaimRewardRecord>>, Publisher<ClaimRewardRecord>>() {
                    @Override
                    public Publisher<ClaimRewardRecord> apply(Optional<List<ClaimRewardRecord>> listOptional) throws Exception {
                        return Flowable.fromIterable(listOptional.get());
                    }
                })
                .map(new Function<ClaimRewardRecord, ClaimRewardRecord>() {
                    @Override
                    public ClaimRewardRecord apply(ClaimRewardRecord claimRewardRecord) throws Exception {
                        claimRewardRecord.setWalletAvatar(WalletManager.getInstance().getWalletIconByWalletAddress(claimRewardRecord.getAddress()));
                        claimRewardRecord.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(claimRewardRecord.getAddress()));
                        return claimRewardRecord;
                    }
                })
                .toList()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(getView()))
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<List<ClaimRewardRecord>>() {
                    @Override
                    public void accept(List<ClaimRewardRecord> claimRewardRecords) throws Exception {
                        if (isViewAttached()) {
                            //新数据
                            List<ClaimRewardRecord> newList = getNewList(mOldClaimRewardRecordList, claimRewardRecords, isLoadMore);
                            //刷新页面
                            getView().getRewardTransactionsResult(newList);

                            mOldClaimRewardRecordList = newList;

                            if (isLoadMore) {
                                getView().finishLoadMore();
                            } else {
                                getView().finishRefresh();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (isLoadMore) {
                                getView().finishLoadMore();
                            } else {
                                getView().finishRefresh();
                            }
                        }
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public long getBeginSequence(@Direction String direction) {
        if (Direction.DIRECTION_NEW.equals(direction)) {
            return -1;
        }

        if (mOldClaimRewardRecordList == null || mOldClaimRewardRecordList.isEmpty()) {
            return -1;
        }

        return mOldClaimRewardRecordList.get(mOldClaimRewardRecordList.size() - 1).getSequence();
    }

    public List<ClaimRewardRecord> getNewList(List<ClaimRewardRecord> oldClaimRewardRecordList, List<ClaimRewardRecord> newClaimRewardRecordList, boolean isLoadMore) {
        List<ClaimRewardRecord> oldList = oldClaimRewardRecordList == null ? new ArrayList<ClaimRewardRecord>() : oldClaimRewardRecordList;
        List<ClaimRewardRecord> curList = newClaimRewardRecordList;
        List<ClaimRewardRecord> newList = new ArrayList<>();
        if (isLoadMore) {
            newList.addAll(oldList);
            newList.addAll(curList);
        } else {
            newList = curList;
        }
        return newList;
    }
}
