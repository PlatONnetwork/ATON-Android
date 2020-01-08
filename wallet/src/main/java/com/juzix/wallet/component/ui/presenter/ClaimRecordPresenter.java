package com.juzix.wallet.component.ui.presenter;

import android.app.Activity;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ClaimRecordContract;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.ClaimRewardRecord;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

public class ClaimRecordPresenter extends BasePresenter<ClaimRecordContract.View> implements ClaimRecordContract.Presenter {

    private List<ClaimRewardRecord> mOldClaimRewardRecordList;


    public ClaimRecordPresenter(ClaimRecordContract.View view) {
        super(view);
    }

    @Override
    public void getRewardTransactions(String direction) {

        boolean isLoadMore = Direction.DIRECTION_OLD.equals(direction);

        ServerUtils
                .getCommonApi()
                .getRewardTransactions(ApiRequestBody.newBuilder()
                        .put("walletAddrs", WalletManager.getInstance().getWalletList())
                        .put("beginSequence", getBeginSequence(direction))
                        .put("listSize", 10)
                        .put("direction", direction)
                        .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(RxUtils.bindToLifecycle(getView()))
                .subscribe(new ApiSingleObserver<List<ClaimRewardRecord>>() {
                    @Override
                    public void onApiSuccess(List<ClaimRewardRecord> claimRewardRecords) {
                        if (isViewAttached()) {
                            //新数据
                            List<ClaimRewardRecord> newList = getNewList(mOldClaimRewardRecordList, claimRewardRecords, isLoadMore);
                            //刷新页面
                            getView().getRewardTransactionsResult(mOldClaimRewardRecordList, newList);

                            mOldClaimRewardRecordList = newList;

                            if (isLoadMore) {
                                getView().finishLoadMore();
                            } else {
                                getView().finishRefresh();
                            }
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
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

    private long getBeginSequence(@Direction String direction) {
        if (Direction.DIRECTION_NEW.equals(direction)) {
            return -1;
        }

        if (mOldClaimRewardRecordList == null || mOldClaimRewardRecordList.isEmpty()) {
            return -1;
        }

        return mOldClaimRewardRecordList.get(mOldClaimRewardRecordList.size() - 1).getSequence();
    }

    private List<ClaimRewardRecord> getNewList(List<ClaimRewardRecord> oldClaimRewardRecordList, List<ClaimRewardRecord> newClaimRewardRecordList, boolean isLoadMore) {
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
