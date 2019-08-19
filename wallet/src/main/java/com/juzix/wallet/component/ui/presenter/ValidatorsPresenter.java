package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.db.entity.VerifyNodeEntity;
import com.juzix.wallet.db.sqlite.VerifyNodeDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.ValidatorsService;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.web3j.tuples.Tuple;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {
    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);
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
    private void getValidatorsData(String sortType, String nodeState, int sequence) {
        Log.d("ValidatorsPresenter", "=========nodeState=" + nodeState + "==============rank=" + sequence + "===============sortType=" + sortType);
        ServerUtils.getCommonApi().getVerifyNodeList(NodeManager.getInstance().getChainId())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            if (nodeList != null && nodeList.size() > 0) {
                                if (getNodeList().size() > 0) { //读取数据库数据
                                    //删除数据库
                                    if (deleteVerifyNodeList()) {
                                        //插入数据库
                                        if (insertVerifyNodeIntoDB(nodeList)) {
                                            //读取数据
//                                            loadDataFromDB(nodeState, rank);
                                            loadDataFromDB(sortType, nodeState, sequence);
                                        }
                                    }

                                } else {
                                    if (insertVerifyNodeIntoDB(nodeList)) {
                                        //读取数据
//                                        loadDataFromDB(nodeState, sequnce);
                                        loadDataFromDB(sortType, nodeState, sequence);
                                    }
                                }
                            }

                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        getView().showValidatorsFailed();
                    }
                });

    }


    @Override
    public void loadDataFromDB(String sortType, String state, int ranking) {
        loadVerifyNodeDataFromDB(sortType, state, ranking);

    }

    private void loadVerifyNodeDataFromDB(String soryType, String state, int ranking) {
        //从数据库读取数据，并更新UI
        if (TextUtils.equals(state, Constants.ValidatorsType.ALL_VALIDATORS)) {
            //在加一个判断
            if (TextUtils.equals(soryType, Constants.ValidatorsType.VALIDATORS_RANK)) {   //（在所有tab）按排序操作

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
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit());
                    }
                })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {
                                    getView().showValidatorsDataOnAll(nodeList);
                                }
                            }
                        });

            }else {
                //(在所有页签)(按年化率操作)
                Flowable
                        .fromIterable(VerifyNodeDao.getVerifyNodeAllByRate(ranking))
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
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit());
                    }
                })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BiConsumer<List<VerifyNode>, Throwable>() {
                            @Override
                            public void accept(List<VerifyNode> nodeList, Throwable throwable) throws Exception {
                                if (isViewAttached()) {
                                    getView().showValidatorsDataOnAll(nodeList);
                                }
                            }
                        });

            }


        } else {
            //在活跃或者候选中状态
            //在加一个判断
            if (TextUtils.equals(soryType, Constants.ValidatorsType.VALIDATORS_RANK)) {   //按排序操作
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
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit());
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

            }else { //按年化率操作

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
                        return new VerifyNode(entity.getNodeId(), entity.getRanking(), entity.getName(), entity.getDeposit(), entity.getUrl(), String.valueOf(entity.getRatePA()), entity.getNodeStatus(), entity.isInit());
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

    public boolean deleteVerifyNodeList() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return VerifyNodeDao.deleteVerifyNode();
            }
        }).subscribeOn(Schedulers.io())
                .blockingGet();
    }

    public boolean insertVerifyNodeIntoDB(List<VerifyNode> nodeList) {
        return ValidatorsService.insertVerifyNodeList(nodeList).blockingGet();
    }


}



