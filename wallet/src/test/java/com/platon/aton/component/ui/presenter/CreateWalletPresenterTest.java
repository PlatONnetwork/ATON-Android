package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.CreateWalletContract;
import com.platon.aton.entity.WalletType;

import org.junit.Test;
import org.mockito.Mockito;

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
    public void testCreateOrdinaryWallet(){
        String name = "Ella";
        String password = "123456";
        String repeatPassword = "1234567";

        presenter.createWallet(name,password,repeatPassword,WalletType.ORDINARY_WALLET);

        Mockito.verify(view).showPasswordError(null,true);
    }

    @Test
    public void testCreateHDWallet(){
        String name = "Ella";
        String password = "123456";
        String repeatPassword = "1234567";

        presenter.createWallet(name,password,repeatPassword,WalletType.HD_WALLET);

        Mockito.verify(view).showPasswordError(null,true);
    }
}