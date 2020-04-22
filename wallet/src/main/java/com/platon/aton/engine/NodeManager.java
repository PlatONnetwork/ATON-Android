package com.platon.aton.engine;

import android.text.TextUtils;

import com.platon.aton.BuildConfig;
import com.platon.aton.entity.Node;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.utils.PreferenceTool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class NodeManager {

    private Node curNode;
    private NodeService nodeService;

    private NodeManager() {

    }

    private static class InstanceHolder {
        private static volatile NodeManager INSTANCE = new NodeManager();
    }

    public static NodeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Node getCurNode() {
        return curNode;
    }

    public String getCurNodeAddress() {
        return curNode == null || TextUtils.isEmpty(curNode.getNodeAddress()) ? PreferenceTool.getString(Constants.Preference.KEY_CURRENT_NODE_ADDRESS) : curNode.getNodeAddress();
    }

    public void setCurNode(Node curNode) {
        this.curNode = curNode;
    }

    public void init() {

        nodeService = new NodeService();

        Flowable
                .fromIterable(buildDefaultNodeList())
                .map(new Function<Node, Node>() {
                    @Override
                    public Node apply(Node nodeEntity) throws Exception {
                        return getInsertNode(nodeEntity).blockingGet();
                    }
                })
                .filter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node nodeEntity) throws Exception {
                        return !nodeEntity.isNull();
                    }
                })
                .toList()
                .map(new Function<List<Node>, Boolean>() {
                    @Override
                    public Boolean apply(List<Node> nodeEntities) throws Exception {
                        return nodeService.insertNode(nodeEntities).blockingGet();
                    }
                })
                .map(new Function<Boolean, Node>() {
                    @Override
                    public Node apply(Boolean aBoolean) throws Exception {
                        return getCheckedNode().blockingGet();
                    }
                })
                .filter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node nodeEntity) throws Exception {
                        return !nodeEntity.isNull();
                    }
                })
                .toSingle()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<Node>() {
                    @Override
                    public void accept(Node nodeEntity) throws Exception {
                        switchNode(nodeEntity);
                        EventPublisher.getInstance().sendNodeChangedEvent(nodeEntity);
                        AppConfigManager.getInstance().init();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public String getChainId() {

        if (getCurNode() == null || TextUtils.isEmpty(getCurNode().getChainId())) {
            if (BuildConfig.RELEASE_TYPE.equals("server.typeC")) {
                return BuildConfig.ID_TEST_CHAIN;
            } else if (BuildConfig.RELEASE_TYPE.equals("server.typeOC")) {
                return BuildConfig.ID_TEST_CHAIN;
            } else if (BuildConfig.RELEASE_TYPE.equals("server.typeTX")) {
                return BuildConfig.ID_TEST_MAIN_CHAIN;
            } else {
                return BuildConfig.ID_MAIN_CHAIN;
            }
        } else {
            return getCurNode().getChainId();
        }
    }

    public void switchNode(Node nodeEntity) {
        setCurNode(nodeEntity);
        PreferenceTool.putString(Constants.Preference.KEY_CURRENT_NODE_ADDRESS, nodeEntity.getNodeAddress());
        Web3jManager.getInstance().init(nodeEntity.getRPCUrl());
    }

    public Single<List<Node>> getNodeList() {
        return nodeService.getNodeList();
    }

    public Single<Node> getCheckedNode() {
        return nodeService.getNode(true);
    }

    public Single<Boolean> insertNodeList(List<Node> nodeEntityList) {
        return nodeService.insertNode(nodeEntityList);
    }

    public Single<Boolean> deleteNode(Node nodeEntity) {
        return nodeService.deleteNode(nodeEntity.getId());
    }

    public Single<Boolean> updateNode(Node nodeEntity, boolean isChecked) {
        return nodeService.updateNode(nodeEntity.getId(), isChecked);
    }


    private List<Node> buildDefaultNodeList() {

        List<Node> nodeInfoEntityList = new ArrayList<>();

        if (BuildConfig.RELEASE_TYPE.equals("server.typeC")) {//内部测试网络 + 开发网络
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_OUTER_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_CHAIN)
                    .build());

            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_DEVELOP_SERVER)
                    .isDefaultNode(false)
                    .isChecked(false)
                    .chainId(BuildConfig.ID_DEVELOP_CHAIN)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeX")) {//测试网络(贝莱世界)
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_MAIN_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_MAIN_CHAIN)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeOC")) {//内部测试网络
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_OUTER_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_CHAIN)
                    .build());
        } else if (BuildConfig.RELEASE_TYPE.equals("server.typeTX")) {//平行网
            nodeInfoEntityList.add(new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(BuildConfig.URL_TEST_MAIN_SERVER)
                    .isDefaultNode(true)
                    .isChecked(true)
                    .chainId(BuildConfig.ID_TEST_MAIN_CHAIN)
                    .build());
        }
        return nodeInfoEntityList;
    }


    private Single<Node> getInsertNode(Node nodeEntity) {
        return Single.create(new SingleOnSubscribe<Node>() {
            @Override
            public void subscribe(SingleEmitter<Node> emitter) throws Exception {
                List<Node> nodeEntityList = nodeService.getNode(nodeEntity.getNodeAddress()).blockingGet();
                if (nodeEntityList == null || nodeEntityList.isEmpty()) {
                    emitter.onSuccess(nodeEntity);
                } else {
                    emitter.onSuccess(Node.createNullNode());
                }
            }
        });
    }

}