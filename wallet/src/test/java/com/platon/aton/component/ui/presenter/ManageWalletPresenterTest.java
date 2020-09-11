package com.platon.aton.component.ui.presenter;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.platon.aton.BaseTestCase;
import com.platon.aton.BuildConfig;
import com.platon.aton.component.ui.contract.ManageWalletContract;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Wallet;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.realm.Realm;

import static org.mockito.Mockito.mock;


public class ManageWalletPresenterTest extends BaseTestCase {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    private ManageWalletPresenter presenter;
    @Mock
    private ManageWalletContract.View view;


    @Override
    public void initSetup() {
        view =mock(ManageWalletContract.View.class);
        presenter =new ManageWalletPresenter();
        presenter.attachView(view);


        Wallet wallet = new Wallet();
        wallet.setChainId("101");
        wallet.setAddress("0x0d395e21b23f8d920f8556cbf573af9ba2ad1a59");
        wallet.setAvatar("avatar_3");
        presenter.init(wallet);
    }



    @Test
    public void showWalletInfo(){


        presenter.showWalletInfo();
        Mockito.verify(view).showWalletInfo(Mockito.any(Wallet.class));

    }

    @Test
    public void deleteObservedWallet() {


    }

    @Test
    public void deleteWallet() {
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);
        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);
        list.add(wallet3);

        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        System.out.println("---------------------------");
        Single.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return list.remove(wallet4);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        Log.d("==============", "boolean" + "=====>" + isSuccess);
                        System.out.println( "boolean" + "=====>" + isSuccess);

                    }
                });

        Log.d("==============" , "list" + "=====>" + list.toString() + "length" + "==========>" +list.size());
    }

    @Test
    public void modifyName(){
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);
        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);
        list.add(wallet3);

        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        Single.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        wallet4.setName("0000111");
                        return !TextUtils.isEmpty(wallet4.getName());
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                       return true;
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
                .toSingle()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                            presenter.getView().showWalletName("0000111");
                    }
                });
    }


    @Test
    public  void  testRXjava(){
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.d("rxjava","触发重订阅");
            }
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            private int n = 0;
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {

                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        if (n != 3) {
                            n++;
                            return Observable.timer(3, TimeUnit.SECONDS);
                        } else {
                            return Observable.empty();
                        }
                    }
                });
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d("onNext",s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("onError","onError"+e);
            }

            @Override
            public void onComplete() {
                Log.d("onComplete","onComplete");
            }
        });
    }



}