package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ImportKeystoreContract;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ImportKeystorePresenterTest extends BaseTestCase {


    @Mock
    private ImportKeystoreContract.View view;
    private ImportKeystorePresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(ImportKeystoreContract.View.class);
        presenter = new ImportKeystorePresenter();
        presenter.attachView(view);

    }

    @Test
    public void testParseQRCode(){
        String QRCode = "lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z";
        presenter.parseQRCode(QRCode);
        Mockito.verify(view).showQRCode(Mockito.anyString());
    }


  /*  @Test
    public void testCheckPaste(){
        presenter.checkPaste();
        Mockito.verify(view).enablePaste(Mockito.anyBoolean());
    }*/


     @Test
     public void importKeystore(){
         String keystore = "{\"address\":{\"mainnet\":\"lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z\",\"testnet\":\"lax1x8z5nfgjnaryutayxxkzzkrfzenpwl7k4xcjfd\"},\"id\":\"054f3f85-c00f-4f76-b707-05d8d5c81ce6\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"44c541ccc02edfb61f51eeddc1c010f8\"},\"ciphertext\":\"eb5b975d540f19469b191b44405a1bddc58355562a529264140a467fedff77ca\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":16384,\"p\":1,\"r\":8,\"salt\":\"c2b94a06c2ededad04cb4ba68c57eb86eba6b7f52ba14849b1538a84fe8bf9f1\"},\"mac\":\"9c81f37af1921a90f98a41655866e6d7b8139fa6c844b2724c260fbf45cc8f28\"}}\n";
         String name = "Ella";
         String password = "qq123456";
         presenter.importKeystore(keystore,name,password);


     }








}