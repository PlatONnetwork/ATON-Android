package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.NodeSettingsContract;
import com.juzix.wallet.component.ui.view.OperateMenuActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.entity.WalletEntity;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
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

    public NodeSettingsPresenter(NodeSettingsContract.View view) {
        super(view);
    }

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
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            if (throwable instanceof CustomThrowable) {
                                AppSettings.getInstance().setOperateMenuFlag(true);
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
