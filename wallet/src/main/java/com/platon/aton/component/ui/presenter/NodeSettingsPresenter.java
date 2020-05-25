package com.platon.aton.component.ui.presenter;

import android.util.Log;

import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.SchedulersTransformer;
import com.platon.aton.R;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.NodeSettingsContract;
import com.platon.aton.component.ui.view.OperateMenuActivity;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Node;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.utils.PreferenceTool;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class NodeSettingsPresenter extends BasePresenter<NodeSettingsContract.View> implements NodeSettingsContract.Presenter {

    private final static String TAG = NodeSettingsPresenter.class.getSimpleName();
    private final static String IP_WITH_HTTP_PREFIX = "^(http(s?)://)?((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})";
    private final static String IP_WITHOUT_HTTP_PREFIX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})";

    @Override
    public void fetchNodes() {

        NodeManager.getInstance()
                .getNodeList()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(((BaseActivity) getView()).bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Node>>() {
                    @Override
                    public void accept(List<Node> nodeEntityList) throws Exception {
                        if (isViewAttached()) {
                            getView().notifyDataChanged(nodeEntityList);
                        }
                    }
                });

    }

    @Override
    public void updateNode(Node nodeEntity, boolean isChecked) {
        NodeManager
                .getInstance()
                .updateNode(nodeEntity, isChecked)
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success && isChecked;
                    }
                })
                .toSingle()
                .doOnSuccess(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        NodeManager.getInstance().switchNode(nodeEntity);
                        EventPublisher.getInstance().sendNodeChangedEvent(nodeEntity);
                    }
                })
                .flatMap(new Function<Boolean, SingleSource<Boolean>>() {
                    @Override
                    public SingleSource<Boolean> apply(Boolean aBoolean) throws Exception {
                        return checkWalletList();
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (isViewAttached() && success){
                            WalletManager.getInstance().init();
                            EventPublisher.getInstance().sendWalletListOrderChangedEvent();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG,true);
                                OperateMenuActivity.actionStartWithFlag(currentActivity());
                                currentActivity().finish();
                            }
                        }
                    }
                });
    }

    private Single<Boolean> checkWalletList() {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                List<WalletEntity> walletEntityList = WalletDao.getWalletInfoList();
                if (walletEntityList.isEmpty()) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_EXIST_VALID_WALLET));
                } else {
                    emitter.onSuccess(true);
                }
            }
        });
    }

    private void insertNodeList(List<Node> nodeEntityList) {

        NodeManager.getInstance()
                .insertNodeList(nodeEntityList)
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean.booleanValue()) {
                            showLongToast(string(R.string.save_node_succeed));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }
}
