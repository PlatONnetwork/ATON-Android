package com.platon.aton;

import com.platon.aton.component.ui.contract.AddressBookContract;
import com.platon.aton.component.ui.presenter.AddressBookPresenter;
import com.platon.aton.config.AppSettings;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Address;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;

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
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class AddressBookPresenterTest {
    private AddressBookPresenter presenter;

    @Mock
    private AddressBookContract.View view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SchedulerTestProvider schedulerTestProvider;

    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Before
    public void setup() {
        AppSettings appSettings = AppSettings.getInstance();
        NodeManager nodeManager = NodeManager.getInstance();
        //输出日志
        ShadowLog.stream = System.out;
        schedulerTestProvider = new SchedulerTestProvider();
        view = mock(AddressBookContract.View.class);
        presenter = new AddressBookPresenter(view);
        presenter.attachView(view);
        appSettings.init(RuntimeEnvironment.application);
    }


    @Test
    public void testFetchAddressList() {
        List<AddressEntity> entityList = new ArrayList<>();
        AddressEntity entity = new AddressEntity();
        entity.setAddress("0x15asd35f5ad5fa34s5df4as53");
        entity.setAvatar("");
        entity.setName("qianbao-1");
        entity.setUuid(UUID.randomUUID().toString());
        entityList.add(entity);


        AddressEntity entity2 = new AddressEntity();
        entity2.setAddress("0x15asd35f5ad5fa34s5df4as53");
        entity2.setAvatar("");
        entity2.setName("qianbao-2");
        entity2.setUuid(UUID.randomUUID().toString());
        entityList.add(entity2);


        AddressEntity entity3 = new AddressEntity();
        entity3.setAddress("0x15asd35f5ad5fa34s5df4as53");
        entity3.setAvatar("");
        entity3.setName("qianbao-3");
        entity3.setUuid(UUID.randomUUID().toString());
        entityList.add(entity3);


        AddressEntity entity4 = new AddressEntity();
        entity4.setAddress("0x15asd35f5ad5fa34s5df4as53");
        entity4.setAvatar("");
        entity4.setName("qianbao-4");
        entity4.setUuid(UUID.randomUUID().toString());
        entityList.add(entity4);

        List<Address> list = Flowable.fromIterable(entityList).filter(new Predicate<AddressEntity>() {
            @Override
            public boolean test(AddressEntity addressInfoEntity) throws Exception {
                return addressInfoEntity != null;
            }
        }).map(new Function<AddressEntity, Address>() {
            @Override
            public Address apply(AddressEntity addressInfoEntity) throws Exception {
                return new Address(addressInfoEntity.getUuid(), addressInfoEntity.getName(), addressInfoEntity.getAddress(), addressInfoEntity.getAvatar());
            }
        }).toList().blockingGet();

        assertNotNull(list);
        for(Address address :list){
            System.out.println(address.getUuid() + "=====================" +address.getName());
        }


    }


}
