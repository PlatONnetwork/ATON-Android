package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.AddNewAddressContract;
import com.platon.aton.component.ui.contract.CreateWalletContract;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class CreateWalletPresenterTest extends BaseTestCase {

    private  CreateWalletContract.View view;

    private CreateWalletPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(CreateWalletContract.View.class);
        presenter = new CreateWalletPresenter();
        presenter.attachView(view);

    }


    @Test
    public void testCreateWallet(){
        String name = "Ella";
        String password = "123456";
        String repeatPassword = "1234567";

        presenter.createWallet(name,password,repeatPassword);

        Mockito.verify(view).showPasswordError(null,true);
    }
}