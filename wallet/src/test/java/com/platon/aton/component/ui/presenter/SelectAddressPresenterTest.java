package com.platon.aton.component.ui.presenter;

import android.app.Application;

import com.platon.framework.network.ApiResponse;
import com.platon.aton.BuildConfig;
import com.platon.aton.component.ui.contract.SelectAddressContract;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Address;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;

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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class SelectAddressPresenterTest{
    private SelectAddressPresenter presenter;
    @Mock
    private SelectAddressContract.View view;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    @Before
    public void setup() throws Exception {
        Application app = RuntimeEnvironment.application;
        ApiResponse.init(app);

        AppSettings appSettings = AppSettings.getInstance();
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        //输出日志
        ShadowLog.stream = System.out;

        appSettings.init(app);
        view = mock(SelectAddressContract.View.class);
        presenter = new SelectAddressPresenter(view);

    }

    @Test
    public void fetchAddressList() {
        List<Address> addressList = new ArrayList<>();
        Address address = new Address();
        address.setName("001");
        address.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        address.setUuid("44d912d4-61d2-4eee-b5a0-326fa4dea0c0");
        addressList.add(address);

        Address address2 = new Address();
        address2.setName("002");
        address2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        address2.setUuid("1762c410-4db8-4b13-bd39-35cd3e2ee2f5");
        addressList.add(address2);

        Address address3 = new Address();
        address3.setName("003");
        address3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        address3.setUuid("388ee8dd-bfee-4f4d-ba0e-e2fda6128782");
        addressList.add(address3);


        Flowable.fromIterable(addressList).filter(new Predicate<Address>() {
            @Override
            public boolean test(Address addressInfoEntity) throws Exception {
                return addressInfoEntity != null;
            }
        }).map(new Function<Address, Address>() {
            @Override
            public Address apply(Address addressInfoEntity) throws Exception {
                return new Address(addressInfoEntity.getUuid(), addressInfoEntity.getName(), addressInfoEntity.getAddress(), addressInfoEntity.getAvatar());
            }
        }).toList()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<Address>, Throwable>() {
                    @Override
                    public void accept(List<Address> addressEntities, Throwable throwable) throws Exception {
                            presenter.getView().notifyAddressListChanged(addressEntities);
                    }
                });

    }

    @Test
    public void selectAddress() {

    }
}