package com.platon.aton.component.ui.presenter;

import android.content.Intent;

import com.platon.aton.BaseTestCase;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.AddressBookContract;
import com.platon.aton.component.ui.view.AddNewAddressActivity;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Address;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;
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
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;


public class AddressBookPresenterTest extends BaseTestCase {

    private AddressBookPresenter presenter;

    @Mock
    private AddressBookContract.View view;

    private List<AddressEntity> entityList;

    @Override
    public void initSetup() {
        view = Mockito.mock(AddressBookContract.View.class);
        presenter = new AddressBookPresenter();
        presenter.attachView(view);
    }

    @Override
    public void initData() {

        entityList = new ArrayList<>();
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
    }

     @Test
     public void testSelectAddress(){
         presenter.selectAddress(0);
         assertNull(ShadowToast.getTextOfLatestToast());
        // assertEquals(null, ShadowToast.getTextOfLatestToast()); //断言是否弹出toast
     }

     @Test
     public void testEditAddress(){
         presenter.editAddress(0);
        /* Intent expectedIntent = new Intent(application, AddNewAddressActivity.class);
         Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
         assertEquals(expectedIntent.getComponent(), actualIntent.getComponent()); //断言Activity跳转是否正确*/
     }


  /*   @Test
     public void testFetchAddressList2(){
         presenter.fetchAddressList();
         Mockito.verify(view).notifyAddressListChanged(Mockito.anyList());
     }*/


    @Test
    public void testFetchAddressList() {


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
