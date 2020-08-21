package com.platon.aton.component.ui.presenter;


import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.AddNewAddressContract;
import com.platon.framework.utils.LogUtils;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AddNewAddressPresenterTest extends BaseTestCase {


    AddNewAddressPresenter presenter;

    @Mock
    AddNewAddressContract.View view;


    @Override
    public void initSetup() {
        view = Mockito.mock(AddNewAddressContract.View.class);
        presenter = new AddNewAddressPresenter();
        presenter.attachView(view);
    }


    @Test
    public void checkAddress(){

        String mainnet = "lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z";
        String testnet = "lax1x8z5nfgjnaryutayxxkzzkrfzenpwl7k4xcjfd";
        String invalid = "xxxxx";
        boolean checkAddressBoolean = presenter.checkAddress(invalid);
        LogUtils.i("------checkAddress result:" + checkAddressBoolean);
        if(checkAddressBoolean){//地址校验通过
            Mockito.verify(view).setAddressVisibility(Mockito.any());
        }else{
            Mockito.verify(view).showAddressError(Mockito.any());
        }
    }


    @Test
    public void checkAddressName(){
        String validName = "Ella";
        String invalidName = "";
        boolean checkAddressNameBool = presenter.checkAddressName(validName);
        LogUtils.i( "------checkAddressNameBool result:" + checkAddressNameBool);
        //验证输入地址名称符合要求
        Mockito.verify(view).setNameVisibility(Mockito.anyInt());
        //输入无效的地址名称
       // Mockito.verify(view).showAddressError(Mockito.anyString());

    }


    @Test
    public void testLoadAddressInfo(){

        presenter.loadAddressInfo();
        Mockito.verify(view).setBottonBtnText(Mockito.any());
    }



    @Test
    public void testValidQRCode(){

        //有效地址
        String testnet = "lax1x8z5nfgjnaryutayxxkzzkrfzenpwl7k4xcjfd";
        presenter.validQRCode(testnet);
        Mockito.verify(view).showAddress(Mockito.anyString());
    }

    @Test
    public void testInValidQRCode(){
        //无效的地址
        String testnet = "lax1x8z5nfgjnaryutayxxkzzkrfzenpwl7k4xcjf1";
        presenter.validQRCode(testnet);
        Mockito.verify(view).showLongToast(Mockito.any());
    }







}