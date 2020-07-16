package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ImportPrivateKeyContract;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ImportPrivateKeyPresenterTest extends BaseTestCase {

    @Mock
   private ImportPrivateKeyContract.View view;

   private ImportPrivateKeyPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(ImportPrivateKeyContract.View.class);
        presenter = new ImportPrivateKeyPresenter();
        presenter.attachView(view);
    }


    @Test
    public void importPrivateKey(){
        String privateKey = "541a6474dfccc0d36d88ef9269fbede85c35b3ec08d9a327ce488349fcd52888";
        String name = "Ella";
        String password = "qq123456";
        String repeatPassword = "qq123456";
        presenter.importPrivateKey(privateKey,name,password,repeatPassword);

    }


    @Test
    public void parseQRCode(){
        String QRCode = "541a6474dfccc0d36d88ef9269fbede85c35b3ec08d9a327ce488349fcd52888";
        presenter.parseQRCode(QRCode);
        Mockito.verify(view).showQRCode(Mockito.anyString());
    }


}