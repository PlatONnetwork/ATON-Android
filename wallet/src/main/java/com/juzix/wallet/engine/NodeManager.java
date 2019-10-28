package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{"http://192.168.9.190:1000", "http://192.168.9.190:443"};
//    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{"https://aton.test.platon.network","https://aton.main.platon.network"};
    private final static String[] DEFAULT_NODE_CHAINID_LIST = new String[]{"101", "103"};

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
        return curNode == null || TextUtils.isEmpty(curNode.getNodeAddress()) ? AppSettings.getInstance().getCurrentNodeAddress() : curNode.getNodeAddress();
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public String getChainId() {
        return TextUtils.isEmpty(getCurNode().getChainId()) ? DEFAULT_NODE_CHAINID_LIST[1] : getCurNode().getChainId();
    }

    public String getChainId(String nodeAddress) {
        int index = Arrays.asList(DEFAULT_NODE_URL_LIST).indexOf(nodeAddress);
        return index != -1 ? DEFAULT_NODE_CHAINID_LIST[index] : DEFAULT_NODE_CHAINID_LIST[1];
    }

    public void switchNode(Node nodeEntity) {
        setCurNode(nodeEntity);
        AppSettings.getInstance().setCurrentNodeAddress(nodeEntity.getNodeAddress());
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

        for (int i = 0; i < DEFAULT_NODE_URL_LIST.length; i++) {
            Node nodeEntity = new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(DEFAULT_NODE_URL_LIST[i])
                    .isDefaultNode(i != 2)
                    .isChecked(i == 0)
                    .chainId(DEFAULT_NODE_CHAINID_LIST[i])
                    .build();
            nodeInfoEntityList.add(nodeEntity);
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