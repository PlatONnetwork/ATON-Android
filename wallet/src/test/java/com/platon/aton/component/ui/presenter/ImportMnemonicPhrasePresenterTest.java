package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ImportMnemonicPhraseContract;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ImportMnemonicPhrasePresenterTest extends BaseTestCase {

    @Mock
    ImportMnemonicPhraseContract.View view;

    ImportMnemonicPhrasePresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(ImportMnemonicPhraseContract.View.class);
        presenter = new ImportMnemonicPhrasePresenter();
        presenter.attachView(view);

    }


    @Test
    public void testParseQRCode(){
        String QRCode = "a b c d e f g i q t h e";
        presenter.parseQRCode(QRCode);
        Mockito.verify(view).showMnemonicWords(Mockito.anyList());
    }

    @Test
    public void importMnemonic(){
        String str = "a b c d e f g i q t h e";
        presenter.importMnemonic(str,"Ella","qq123456","qq123456");
    }



}