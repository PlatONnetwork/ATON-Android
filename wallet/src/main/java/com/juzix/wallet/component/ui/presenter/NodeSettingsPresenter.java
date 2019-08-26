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
    private final static String IP_WITH_HTTP_PREFIX = "^(http(s?)://)?((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{4})";
    private final static String IP_WITHOUT_HTTP_PREFIX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{4})";

    private boolean mEdited;

    public NodeSettingsPresenter(NodeSettingsContract.View view) {
        super(view);
    }

    @Override
    public void edit() {
        if (isViewAttached()) {
            mEdited = !mEdited;
            if (mEdited) {
                getView().showTitleView(mEdited);
            } else {
                save();
            }
        }
    }

    @Override
    public void cancel() {
        mEdited = false;
        if (isViewAttached()) {
            List<Node> nodeList = getView().getNodeList();
            List<Node> removeNodeList = new ArrayList<>();
            if (nodeList != null && !nodeList.isEmpty()) {
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    if (node.isDefaultNode() || !TextUtils.isEmpty(node.getNodeAddress())) {
                        continue;
                    }

                    String nodeAddress = getView().getNodeAddress(node.getId());
                    if (TextUtils.isEmpty(nodeAddress) || !nodeAddress.trim().matches(IP_WITH_HTTP_PREFIX)) {
                        removeNodeList.add(node);
                    }

                }
            }
            if (!removeNodeList.isEmpty()) {
                getView().removeNodeList(removeNodeList);
            }
            getView().showTitleView(mEdited);
        }
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
    public void save() {
        if (isViewAttached()) {

            ((BaseActivity) getView()).hideSoftInput();

            List<Node> nodeList = getView().getNodeList();
            List<Node> removeNodeList = new ArrayList<>();
            List<Node> errorNodeList = new ArrayList<>();
            List<Node> normalNodeList = new ArrayList<>();
            if (nodeList != null && !nodeList.isEmpty()) {
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    if (node.isDefaultNode()) {
                        continue;
                    }

                    String nodeAddress = getView().getNodeAddress(node.getId());

                    if (TextUtils.isEmpty(nodeAddress)) {
                        removeNodeList.add(node);
                    } else {
                        Node nodeEntity = node.clone();
                        String address = nodeAddress.trim();
                        if (address.matches(IP_WITH_HTTP_PREFIX) || address.matches(IP_WITHOUT_HTTP_PREFIX)) {
                            if (address.matches(IP_WITHOUT_HTTP_PREFIX)) {
                                nodeEntity.setNodeAddress("http://".concat(address));
                            } else {
                                nodeEntity.setNodeAddress(address);
                            }
                            normalNodeList.add(nodeEntity);
                        } else {
                            nodeEntity.setFormatCorrect(true);
                            errorNodeList.add(nodeEntity);
                        }
                    }
                }
            }

            if (errorNodeList.isEmpty()) {
                if (!removeNodeList.isEmpty()) {
                    getView().removeNodeList(removeNodeList);
                }
                if (!normalNodeList.isEmpty()) {
                    insertNodeList(normalNodeList);
                    getView().updateNodeList(normalNodeList);
                }
                getView().showTitleView(mEdited);
            } else {
                showLongToast(R.string.node_format_error);
                getView().updateNodeList(errorNodeList);
            }

        }
    }

    @Override
    public void delete(Node nodeEntity) {
        NodeManager.getInstance()
                .deleteNode(nodeEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean && nodeEntity.isChecked() && isViewAttached()) {
                            getView().setChecked(0);
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
                        return checkIndividualWalletList();
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {

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

    @Override
    public boolean isEdit() {
        return mEdited;
    }

    private Single<Boolean> checkIndividualWalletList() {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                List<WalletEntity> individualWalletInfoEntityList = WalletDao.getWalletInfoList();
                if (individualWalletInfoEntityList.isEmpty()) {
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
