package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.SendTransationContract;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.AddressFormatUtil;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;


public class SendTransationPresenterTest extends BaseTestCase {

    private SendTransactionPresenter presenter;
    @Mock
    private SendTransationContract.View view;


    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;


    @Override
    public void initSetup() {

        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        view = Mockito.mock(SendTransationContract.View.class);
        presenter = new SendTransactionPresenter();
        presenter.attachView(view);
    }

  /*  @Test
    public void testFetchDefaultWalletInfo() {
        ServerUtils
                .getCommonApi()
                .getAccountBalance(ApiRequestBody.newBuilder()
                        .put("addrs", new String[]{"0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd"})
                        .build())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        for (AccountBalance balance : accountBalances) {
                            System.out.println(balance.getFree() + "==============" + balance.getLock());
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });


        Web3jManager.getInstance().getGasPrice()
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger bigInteger) throws Exception {
                        Log.d("TAG","gasPrice为：  " + bigInteger.longValue());
                        BigInteger minGasPrice = bigInteger.divide(BigInteger.valueOf(2));
                        BigInteger maxGasPrice = bigInteger.multiply(BigInteger.valueOf(6));
                        System.out.println("minGasPrice" + minGasPrice + "====================" + "maxGasPrice" + maxGasPrice);
                    }
                });

    }*/


    @Test
    public void testSubmit() {

    }

    @Test
    public void testGetWalletNameFromAddress() {
        List<Wallet> mWalletList = new ArrayList<>();
        Wallet wallet = new Wallet();
        wallet.setName("wallet-1");
        wallet.setChainId("103");
        wallet.setUuid(UUID.randomUUID().toString());
        wallet.setAddress("0xaf4af5a4f5asd5fas6df");
        mWalletList.add(wallet);

        String name = Single.fromCallable(new Callable<Flowable<String>>() {
            @Override
            public Flowable<String> call() throws Exception {
                return Flowable.fromIterable(mWalletList)
                        .map(new Function<Wallet, String>() {
                            @Override
                            public String apply(Wallet wallet) throws Exception {
                                return wallet.getName();
                            }
                        });
            }
        }).map(new Function<Flowable<String>, String>() {

            @Override
            public String apply(Flowable<String> stringFlowable) throws Exception {
                return stringFlowable.toString();
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).switchIfEmpty(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                String addressName = mWalletList.get(0).getName();
                observer.onSuccess(TextUtils.isEmpty(addressName) ? addressName : String.format("%s(%s)", addressName, AddressFormatUtil.formatTransactionAddress(addressName)));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {

                return !TextUtils.isEmpty(s);
            }
        }).defaultIfEmpty(AddressFormatUtil.formatAddress(""))
                .toSingle().blockingGet();


        assertNotNull(name);
        System.out.println("====================="+name);

    }


}
