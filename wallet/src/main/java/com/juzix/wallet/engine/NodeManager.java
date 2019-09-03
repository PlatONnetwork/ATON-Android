package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

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

//    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{Constants.URL.URL_HTTP_A, Constants.URL.URL_TEST_B};
    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{"http://192.168.9.190:1000/rpc"};
    //线上A网
    private final static String CHAINID_TEST_NET_A = "103";
    //线上B网
    private final static String CHAINID_TEST_NET_B = "104";
    //测试环境
    private final static String CHAINID_TEST_NET_C = "203";
    //

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

    public void switchNode(Node nodeEntity) {
        setCurNode(nodeEntity);
        AppSettings.getInstance().setCurrentNodeAddress(nodeEntity.getNodeAddress());
        Web3jManager.getInstance().init(nodeEntity.getNodeAddress());
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

    public String getChainId() {
        return getChainId(getCurNodeAddress());
    }

    public String getChainId(String curNodeAddress) {
        if (Constants.URL.URL_TEST_A.equals(curNodeAddress)) {
            return CHAINID_TEST_NET_A;
        } else if (Constants.URL.URL_TEST_B.equals(curNodeAddress)) {
            return CHAINID_TEST_NET_B;
        } else {
            return CHAINID_TEST_NET_C;
        }
    }

    private List<Node> buildDefaultNodeList() {

        List<Node> nodeInfoEntityList = new ArrayList<>();

        Node nodeEntity = null;

        for (int i = 0; i < DEFAULT_NODE_URL_LIST.length; i++) {
            nodeEntity = new Node.Builder()
                    .id(UUID.randomUUID().hashCode())
                    .nodeAddress(DEFAULT_NODE_URL_LIST[i])
                    .isDefaultNode(true)
                    .isChecked(i == 0)
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
