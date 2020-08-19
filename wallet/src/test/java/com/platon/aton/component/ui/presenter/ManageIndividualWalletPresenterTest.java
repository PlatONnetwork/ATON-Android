package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ManageWalletContract;
import com.platon.aton.entity.Wallet;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class ManageIndividualWalletPresenterTest extends BaseTestCase {
    private ManageWalletPresenter presenter;

    @Mock
    private ManageWalletContract.View view;



    @Override
    public void initSetup() {
        view = Mockito.mock(ManageWalletContract.View.class);
        presenter = new ManageWalletPresenter();
        presenter.attachView(view);
    }


    @Test
    public void testDeleteWallet() {
        List<Wallet> mWalletList = new ArrayList<>();
        Wallet wallet = new Wallet();
        wallet.setName("wallet-1");
        wallet.setChainId("103");
        wallet.setUuid(UUID.randomUUID().toString());
        wallet.setAddress("0xaf4af5a4f5asd5fas6df");
        mWalletList.add(wallet);

        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mWalletList.remove(0);
                return mWalletList.size() == 0 ? true : false;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                System.out.println(isSuccess);
            }
        });

    }

    @Test
    public void testModifyName() {
        List<Wallet> mWalletList = new ArrayList<>();
        Wallet wallet = new Wallet();
        wallet.setName("wallet-1");
        wallet.setChainId("103");
        wallet.setUuid(UUID.randomUUID().toString());
        wallet.setAddress("0xaf4af5a4f5asd5fas6df");
        mWalletList.add(wallet);

        Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, Wallet>() {
                    @Override
                    public Wallet apply(Wallet wallet) throws Exception {
                        wallet.setName("wallet-2");
                        return wallet;
                    }
                }).filter(new Predicate<Wallet>() {
            @Override
            public boolean test(Wallet wallet) throws Exception {
                return TextUtils.isEmpty(wallet.getName()) ? false : true;
            }
        }).map(new Function<Wallet, Boolean>() {
            @Override
            public Boolean apply(Wallet wallet) throws Exception {
                return TextUtils.isEmpty(wallet.getName()) ? false : true;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                System.out.println(aBoolean);
            }
        });


        Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, Wallet>() {
                    @Override
                    public Wallet apply(Wallet wallet) throws Exception {
                        wallet.setName("wallet-2");
                        return wallet;
                    }
                }).filter(new Predicate<Wallet>() {
            @Override
            public boolean test(Wallet wallet) throws Exception {
                return TextUtils.isEmpty(wallet.getName()) ? false : true;
            }
        }).subscribe(new Consumer<Wallet>() {
            @Override
            public void accept(Wallet wallet) throws Exception {
                 System.out.println(wallet.getName());
            }
        });

    }



}
