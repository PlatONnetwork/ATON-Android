package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.app.AppFramework;
import com.platon.aton.component.ui.contract.ImportObservedContract;
import com.platon.aton.engine.WalletManager;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ImportObservedPresenterTest extends BaseTestCase {

    @Mock
   private ImportObservedContract.View view;

   private ImportObservedPresenter presenter;



    @Override
    public void initSetup() {
        view = Mockito.mock(ImportObservedContract.View.class);
        presenter = new ImportObservedPresenter();
        presenter.attachView(view);


    }

    @Test
    public void testIsImportObservedWallet(){

        presenter.IsImportObservedWallet("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z");
        Mockito.verify(view).enableImportObservedWallet(Mockito.anyBoolean());
    }


  /*  @Test
    public void testImportWalletAddress(){

        presenter.importWalletAddress("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z");
    }
*/






}