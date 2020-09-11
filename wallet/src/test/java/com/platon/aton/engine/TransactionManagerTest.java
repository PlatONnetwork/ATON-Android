package com.platon.aton.engine;

import android.app.Application;
import android.util.Log;

import com.platon.aton.BuildConfig;
import com.platon.aton.db.sqlite.TransactionDao;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionReceipt;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class TransactionManagerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    public Web3jManager web3jManager;
    @Before
    public void setup() {
        Application app = RuntimeEnvironment.application;
        ApiResponse.init(app);

        PreferenceTool.init(app);
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);
         web3jManager = Web3jManager.getInstance();
        //输出日志
        ShadowLog.stream = System.out;

    }

    @Test
    public  void sendTransaction() throws IOException {
//        String privateKey = "705324c0aa7e796a9f13cb015f29c9782cdff35144c09c737c766a45f5065d6e";
//        String from ="0xa577c0230df2cb329415bfebcb936496ab8ae2e4";
//        String toAddress ="0x3d4cee0fb811034dab8ddda086f4448ee3124cc2";
//        BigDecimal  amount = new BigDecimal(1000);
//        BigInteger gasPrice = new BigInteger("10000000");
//        BigInteger gasLimit = new BigInteger("50000000");
//
//
//        Credentials credentials = Credentials.create(privateKey);
//        RawTransaction rawTransaction = RawTransaction.createTransaction(Web3jManager.getInstance().getNonce(from), gasPrice, gasLimit, toAddress, amount.toBigInteger(),
//                "");
//
//        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);
//        String hexValue = Numeric.toHexString(signedMessage);
//
//        PlatonSendTransaction transaction = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(hexValue).send();
//        String hash =transaction.getTransactionHash();
//        Log.debug("======", "得到结果result---------->" + hash);

    }

    @Test
    public  void getTransactionByLoop(){
        Transaction transaction =new Transaction.Builder()
                .hash("0x7c60553fe3dcdf19f641ee58c5691e49b63c4d7055b92fe1b09b517471f26143")
                .from("0xa577c0230df2cb329415bfebcb936496ab8ae2e4")
                .to("0x3d4cee0fb811034dab8ddda086f4448ee3124cc2")
                .senderWalletName("1111")
                .value("100000000")
                .chainId("101")
                .timestamp(System.currentTimeMillis())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .actualTxCost("123580000")
                .unDelegation("20000000000000")
                .nodeName("节点02")
                .nodeId("0x411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c")
                .build();

        Flowable
                .interval(Constants.Common.TRANSACTION_STATUS_LOOP_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Transaction>() {
                    @Override
                    public Transaction apply(Long aLong) throws Exception {
                        Transaction tempTransaction = transaction.clone();
                        //如果pending时间超过4小时，则删除
                        if (System.currentTimeMillis() - transaction.getTimestamp() >= NumberParserUtils.parseLong(AppConfigManager.getInstance().getTimeout())) {
                            tempTransaction.setTxReceiptStatus(TransactionStatus.TIMEOUT.ordinal());
                        } else {

                            tempTransaction.setTxReceiptStatus(1);
                        }
                        return tempTransaction;
                    }
                })
                .takeUntil(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        if (transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                            TransactionDao.deleteTransaction(transaction.getHash());
                            LogUtils.e("getIndividualTransactionByLoop 轮询交易成功" + transaction.toString());
                        }
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .toObservable();


    }

    @Test
    public void getTransactionReceipt() {
        String hashs[] = {"0x6ee4c7c22cae33226884900a2f78aeb2bbdc1bdc88913235a70469f0a248cc3c", "0xf4fa892f8cdab498c1058b78dff1d30e4047950f29188a2197944109ee2c08a3",
                "0x7c60553fe3dcdf19f641ee58c5691e49b63c4d7055b92fe1b09b517471f26143", "0x1f96da0638287b8db23c3b25d4afea1cca7fb881b4dca75a52aeb98af219e9a9"};
        String hash = "0x6ee4c7c22cae33226884900a2f78aeb2bbdc1bdc88913235a70469f0a248cc3c";

        TransactionReceipt receipt = ServerUtils
                .getCommonApi()
                .getTransactionsStatus(ApiRequestBody.newBuilder()
                        .put("hash", Arrays.asList(hashs))
                        .build())
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful();
                    }
                })
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        List<TransactionReceipt> transactionReceiptList = apiResponseResponse.body().getData();
                        return transactionReceiptList != null && !transactionReceiptList.isEmpty();
                    }
                })
                .map(new Function<Response<ApiResponse<List<TransactionReceipt>>>, TransactionReceipt>() {
                    @Override
                    public TransactionReceipt apply(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse.body().getData().get(0);
                    }
                })
                .defaultIfEmpty(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .onErrorReturnItem(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .toSingle()
                .blockingGet();

        Log.d("======", "得到结果result---------->" + receipt.getHash() + "==========" + receipt.getStatus());
    }



}