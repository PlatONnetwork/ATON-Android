package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.db.entity.VerifyNodeEntity;
import com.juzix.wallet.db.sqlite.VerifyNodeDao;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.ValidatorsService;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {

    private List<VerifyNode> mVerifyNodeList;

    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);
    }

    @Override
    public void loadValidatorsData(int tab, SortType sortType, String keywords) {
        ServerUtils.getCommonApi().getVerifyNodeList()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            getView().showValidatorsDataOnAll(nodeList);
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showValidatorsFailed();
                        }
                    }
                });
    }

    @Override
    public void loadValidatorsData(String sortType, String nodeState, int rank) {
        getValidatorsData(sortType, nodeState, rank);
    }


    /**
     * 刷新调用的方法
     *
     * @param sortType  排序类型 (年化率/排名)
     * @param nodeState
     * @param sequence  加载更多序号
     */
    private void getValidatorsData(String nodeState, SortType sortType, int sequence) {
        ServerUtils.getCommonApi().getVerifyNodeList()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            getView().showValidatorsDataOnAll(nodeList);
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showValidatorsFailed();
                        }
                    }
                });

    }


    @Override
    public void loadDataFromDB(String sortType, String state, int ranking) {
        loadVerifyNodeDataFromDB(sortType, state, ranking);
    }

    private void loadVerifyNodeDataFromDB(String sortType, String state, int ranking) {
        //从数据库读取数据，并更新UI
        if (TextUtils.equals(state, Constants.ValidatorsType.ALL_VALIDATORS)) {
            //在加一个判断
            if (TextUtils.equals(sortType, Constants.ValidatorsType.VALIDATORS_RANK)) {   //（在所有tab）按排序操作
                //(在所有页签)
                Flowable
                        .fromIterable(VerifyNodeDao.getVerifyNodeByAll(ranking))
                        .filter(new Predicate<VerifyNodeEntity>() {
                            @Override
                            public boolean test(VerifyNodeEntity entity) throws Exception {
                                return entity != null;
                            }
                        })
                        .compose(((BaseFragment) getView()).bindToLifecycle()).map(new Function<VerifyNodeEntity, VerifyNode>() {

                    @Override
                    public VerifyNode apply(VerifyNodeEntity entity) throws Exception {
                        //转换对象并赋值
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit(), entity.isConsensus());
                    }
                })
                        .toList()
                        .compose(RxUtils.getSingleSchedulerTransformer())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {
                                    getView().showValidatorsDataOnAll(nodeList);
                                }
                            }
                        });

            } else {
                //(在所有页签)(按年化率操作)
                Flowable
                        .fromIterable(VerifyNodeDao.getVerifyNodeAllByRate(ranking))
                        .filter(new Predicate<VerifyNodeEntity>() {
                            @Override
                            public boolean test(VerifyNodeEntity entity) throws Exception {
                                return entity != null;
                            }
                        })
                        .map(new Function<VerifyNodeEntity, VerifyNode>() {
                            @Override
                            public VerifyNode apply(VerifyNodeEntity entity) throws Exception {
                                //转换对象并赋值
                                return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit(), entity.isConsensus());
                            }
                        })
                        .toList()
                        .compose(((BaseFragment) getView()).bindToLifecycle())
                        .compose(RxUtils.getSingleSchedulerTransformer())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {

                                    getView().showValidatorsDataOnAll(sort(nodeList));
                                }
                            }
                        });

            }


        } else {
            //在活跃或者候选中状态
            //在加一个判断
            if (TextUtils.equals(sortType, Constants.ValidatorsType.VALIDATORS_RANK)) {   //按排序操作
                Flowable
                        .fromIterable(VerifyNodeDao.getVerifyNodeDataByState(state, ranking))
                        .filter(new Predicate<VerifyNodeEntity>() {
                            @Override
                            public boolean test(VerifyNodeEntity entity) throws Exception {
                                return entity != null;
                            }
                        })
                        .compose(((BaseFragment) getView()).bindToLifecycle()).map(new Function<VerifyNodeEntity, VerifyNode>() {

                    @Override
                    public VerifyNode apply(VerifyNodeEntity entity) throws Exception {
                        //转换对象并赋值
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit(), entity.isConsensus());
                    }
                })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {
                                    if (TextUtils.equals(state, Constants.ValidatorsType.ACTIVE_VALIDATORS)) {
                                        getView().showValidatorsDataOnActive(nodeList);

                                    } else {
                                        getView().showValidatorsDataOnCadidate(nodeList);
                                    }

                                }
                            }
                        });

            } else { //按年化率操作

                Flowable
                        .fromIterable(VerifyNodeDao.getVerifyNodeByStateAndRate(state, ranking))
                        .filter(new Predicate<VerifyNodeEntity>() {
                            @Override
                            public boolean test(VerifyNodeEntity entity) throws Exception {
                                return entity != null;
                            }
                        })
                        .compose(((BaseFragment) getView()).bindToLifecycle()).map(new Function<VerifyNodeEntity, VerifyNode>() {

                    @Override
                    public VerifyNode apply(VerifyNodeEntity entity) throws Exception {
                        //转换对象并赋值
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit(), entity.isConsensus());
                    }
                })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {
                                    if (TextUtils.equals(state, Constants.ValidatorsType.ACTIVE_VALIDATORS)) {
                                        getView().showValidatorsDataOnActive(sort(nodeList));

                                    } else {
                                        getView().showValidatorsDataOnCadidate(sort(nodeList));
                                    }

                                }
                            }
                        });


            }


        }
    }


    /**
     * 下面三个方法是增删查
     *
     * @return
     */
    public List<VerifyNodeEntity> getNodeList() {
        return Single.fromCallable(new Callable<List<VerifyNodeEntity>>() {
            @Override
            public List<VerifyNodeEntity> call() throws Exception {
                return VerifyNodeDao.getVerifyNodeList();
            }
        }).subscribeOn(Schedulers.io())
                .blockingGet();
    }

    public Single<Boolean> deleteVerifyNodeList() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return VerifyNodeDao.deleteVerifyNode();
            }
        });
    }

}



