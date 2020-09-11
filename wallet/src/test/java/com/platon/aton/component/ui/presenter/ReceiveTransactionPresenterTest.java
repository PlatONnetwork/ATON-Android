package com.platon.aton.component.ui.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.platon.aton.BaseTestCase;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.ReceiveTransationContract;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.engine.directory.DirectroyController;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static org.mockito.Mockito.mock;


public class ReceiveTransactionPresenterTest extends BaseTestCase{
    private ReceiveTransactionPresenter presenter;
    @Mock
    private ReceiveTransationContract.View view;



    @Override
    public void initSetup() {
        view = mock(ReceiveTransationContract.View.class);
        presenter = new ReceiveTransactionPresenter();
        presenter.attachView(view);

        List<Wallet> walletList = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setUuid("9eed1da8-58e9-4b10-8bea-2bfdd1cac433");
        wallet.setAvatar("avatar_3");
        wallet.setAddress("0x0d395e21b23f8d920f8556cbf573af9ba2ad1a59");
        wallet.setChainId("101");
        wallet.setName("Ella");
        wallet.setKeystorePath("UTC--2020-06-28T06-36-42.751--b1ca4a13ee33ed68d097223f186f65864ecdb98c9b35c664566f096761649aa7d9c9c2bc317785a5c2580b305803e5f29aafce422e49579792623575215675e7.json");
        Bech32Address bech32Address = new Bech32Address("lat1p5u4ugdj87xeyru92m9l2ua0nw326xjekflsf8","lax1p5u4ugdj87xeyru92m9l2ua0nw326xjeevdl8g");
        wallet.setBech32Address(bech32Address);
        walletList.add(wallet);

        WalletManager.getInstance().setWalletList(walletList);

    }


    @Test
    public void loadData2(){

        presenter.loadData();
        Mockito.verify(view).setWalletInfo(Mockito.any());
    }


    @Test
    public void shareView(){

        //初始化Directroy
        DirectroyController.getInstance().init(application);

        //准备数据源
        Wallet wallet = new Wallet();
        wallet.setUuid("9eed1da8-58e9-4b10-8bea-2bfdd1cac433");
        wallet.setAvatar("avatar_3");
        wallet.setAddress("0x0d395e21b23f8d920f8556cbf573af9ba2ad1a59");
        wallet.setChainId("101");
        wallet.setName("Ella");
        wallet.setKeystorePath("UTC--2020-06-28T06-36-42.751--b1ca4a13ee33ed68d097223f186f65864ecdb98c9b35c664566f096761649aa7d9c9c2bc317785a5c2580b305803e5f29aafce422e49579792623575215675e7.json");
        Bech32Address bech32Address = new Bech32Address("lat1p5u4ugdj87xeyru92m9l2ua0nw326xjekflsf8","lax1p5u4ugdj87xeyru92m9l2ua0nw326xjeevdl8g");
        wallet.setBech32Address(bech32Address);

        Bitmap bmp = BitmapFactory.decodeResource(application.getResources(),R.drawable.avatar_1);

        presenter.setWalletEntity(wallet);
        presenter.setmQRCodeBitmap(bmp);

     /*   presenter.shareView();
        Mockito.verify(view).shareView(Mockito.anyString(),Mockito.anyString(),Mockito.any());*/


    }

    @Test
    public void copy(){

        //准备数据源
        Wallet wallet = new Wallet();
        wallet.setUuid("9eed1da8-58e9-4b10-8bea-2bfdd1cac433");
        wallet.setAvatar("avatar_3");
        wallet.setAddress("0x0d395e21b23f8d920f8556cbf573af9ba2ad1a59");
        wallet.setChainId("101");
        wallet.setName("Ella");
        wallet.setKeystorePath("UTC--2020-06-28T06-36-42.751--b1ca4a13ee33ed68d097223f186f65864ecdb98c9b35c664566f096761649aa7d9c9c2bc317785a5c2580b305803e5f29aafce422e49579792623575215675e7.json");
        Bech32Address bech32Address = new Bech32Address("lat1p5u4ugdj87xeyru92m9l2ua0nw326xjekflsf8","lax1p5u4ugdj87xeyru92m9l2ua0nw326xjeevdl8g");
        wallet.setBech32Address(bech32Address);

        presenter.setWalletEntity(wallet);

       // presenter.copy();

    }




    @Test
    public void loadData() {
        Wallet wallet = new Wallet();
        wallet.setName("高富帅");
        wallet.setChainId("103");
        wallet.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");

        Flowable.fromCallable(new Callable<Bitmap>() {

            @Override
            public Bitmap call() throws Exception {
                String text = wallet.getPrefixAddress();
                if (!TextUtils.isEmpty(text) && !text.toLowerCase().startsWith("0x")){
                    text = "0x" + text;
                }
                return QRCodeEncoder.syncEncodeQRCode(GZipUtil.compress(text), DensityUtil.dp2px(application, 250f));
            }
        }).compose(RxUtils.getFlowableSchedulerTransformer())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        if (bitmap != null) {
                            presenter.getView().setWalletAddressQrCode(bitmap);
                        }
                    }
                });

    }

}
