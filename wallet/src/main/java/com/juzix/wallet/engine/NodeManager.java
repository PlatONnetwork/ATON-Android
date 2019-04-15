package com.juzix.wallet.engine;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.entity.NodeEntity;
import com.juzix.wallet.event.EventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class NodeManager {

    private final static String TAG = NodeManager.class.getSimpleName();
    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{Constants.URL.URL_TEST_A, Constants.URL.URL_TEST_B};

    private NodeEntity curNode;
    private NodeService nodeService;

    private NodeManager() {

    }

    private static class InstanceHolder {
        private static volatile NodeManager INSTANCE = new NodeManager();
    }

    public static NodeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public NodeEntity getCurNode() {
        return curNode;
    }

    public String getCurNodeAddress() {
        return curNode == null ? null : curNode.getNodeAddress();
    }

    public void setCurNode(NodeEntity curNode) {
        this.curNode = curNode;
    }

    public void init() {

        nodeService = new NodeService();

        nodeService
                .insertNode(buildDefaultNodeList()).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        }).map(new Function<Boolean, NodeEntity>() {
            @Override
            public NodeEntity apply(Boolean aBoolean) throws Exception {
                return getCheckedNode().blockingGet();
            }
        }).toSingle()
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<NodeEntity>() {
                    @Override
                    public void accept(NodeEntity nodeEntity) throws Exception {
                        switchNode(nodeEntity);
                        EventPublisher.getInstance().sendNodeChangedEvent();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void switchNode(NodeEntity nodeEntity) {
        setCurNode(nodeEntity);
        Web3jManager.getInstance().init(nodeEntity.getNodeAddress());
    }

    public Single<List<NodeEntity>> getNodeList() {
        return nodeService.getNodeList();
    }

    public Single<NodeEntity> getCheckedNode() {
        return nodeService.getNode(true);
    }

    public Single<Boolean> insertNodeList(List<NodeEntity> nodeEntityList) {
        return nodeService.insertNode(nodeEntityList);
    }

    public Single<Boolean> deleteNode(NodeEntity nodeEntity) {
        return nodeService.deleteNode(nodeEntity.getId());
    }

    public Single<Boolean> updateNode(NodeEntity nodeEntity, boolean isChecked) {
        return nodeService.updateNode(nodeEntity.getId(), isChecked);
    }

    private List<NodeEntity> buildDefaultNodeList() {

        List<NodeEntity> nodeInfoEntityList = new ArrayList<>();

        NodeEntity nodeEntity = null;

        for (int i = 0; i < DEFAULT_NODE_URL_LIST.length; i++) {
            nodeEntity = new NodeEntity.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(DEFAULT_NODE_URL_LIST[i])
                    .isDefaultNode(true)
                    .isChecked(i == 0)
                    .build();
            nodeInfoEntityList.add(nodeEntity);
        }
        return nodeInfoEntityList;
    }

}
