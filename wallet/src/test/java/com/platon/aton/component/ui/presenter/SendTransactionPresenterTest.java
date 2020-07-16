package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.SendTransationContract;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SendTransactionPresenterTest extends BaseTestCase {

    @Mock
    private SendTransationContract.View view;

    private SendTransactionPresenter presenter;


    @Override
    public void initSetup() {
        view = Mockito.mock(SendTransationContract.View.class);
        presenter = new SendTransactionPresenter();
        presenter.attachView(view);
    }

    @Test
    public void checkToAddress(){

        presenter.checkToAddress("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a81");
        Mockito.verify(view).showToAddressError(Mockito.any());

    }

    @Test
    public void checkToAddressNotSelf(){
        String toAddress = "lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a81";
        presenter.checkToAddressNotSelf(toAddress,toAddress);

    }

    @Test
    public void checkTransferAmount(){
        String transferAmount = "";
        presenter.checkTransferAmount(transferAmount);
        Mockito.verify(view).showAmountError(Mockito.any());
    }








}