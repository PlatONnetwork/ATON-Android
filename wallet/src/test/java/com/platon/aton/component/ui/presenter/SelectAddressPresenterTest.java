package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.SelectAddressContract;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.entity.Address;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


public class SelectAddressPresenterTest extends BaseTestCase{
    private SelectAddressPresenter presenter;

    @Mock
    private SelectAddressContract.View view;

    @Override
    public void initSetup() {
        view = mock(SelectAddressContract.View.class);
        presenter = new SelectAddressPresenter();
        presenter.attachView(view);
    }


    @Test
    public void selectAddress(){
        List<Address> addressEntityList = new ArrayList<>();
        Address address = new Address();
        address.setAddress("0x15asd35f5ad5fa34s5df4as53");
        address.setAvatar("avatar-1");
        address.setName("Ella");
        address.setUuid(UUID.randomUUID().toString());
        addressEntityList.add(address);
        presenter.setAddressEntityList(addressEntityList);
      /*  presenter.selectAddress(0);
        Mockito.verify(view).getAction();*/


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

        Flowable.fromIterable(entityList).filter(new Predicate<AddressEntity>() {
            @Override
            public boolean test(AddressEntity addressInfoEntity) throws Exception {
                return addressInfoEntity != null;
            }
        }).map(new Function<AddressEntity, Address>() {
            @Override
            public Address apply(AddressEntity addressInfoEntity) throws Exception {
                return new Address(addressInfoEntity.getUuid(), addressInfoEntity.getName(), addressInfoEntity.getAddress(), addressInfoEntity.getAvatar());
            }
        }).toList()
                .subscribe(new BiConsumer<List<Address>, Throwable>() {
                    @Override
                    public void accept(List<Address> addressEntities, Throwable throwable) throws Exception {
                        assertNotNull(addressEntities);
                        for (Address address : addressEntities) {
                            System.out.println(address.getUuid() + "===============" + address.getName());
                        }

                    }
                });
    }


}
