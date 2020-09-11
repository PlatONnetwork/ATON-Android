package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ImportObservedContract;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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

        presenter.IsImportObservedWallet("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z",true);
        Mockito.verify(view).enableImportObservedWallet(Mockito.anyBoolean());
    }


  /*  @Test
    public void testImportWalletAddress(){

        presenter.importWalletAddress("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z");
    }
*/






}