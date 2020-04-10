package com.platon.aton;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.platon.aton.component.ui.presenter.ReceiveTransactionPresenter;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.GZipUtil;
import com.platon.aton.utils.QRCodeEncoder;
import com.platon.aton.utils.RxUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class ReceiveTransactionPresenterTest {
    private ReceiveTransactionPresenter presenter;
    @Mock
    private ReceiveTransationContract.View view;

    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SchedulerTestProvider schedulerTestProvider;

    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();
    private Application application ;

    @Before
    public void setup() {
        application =RuntimeEnvironment.application;
        AppSettings appSettings = AppSettings.getInstance();
        NodeManager nodeManager = NodeManager.getInstance();
        //输出日志
        ShadowLog.stream = System.out;
        schedulerTestProvider = new SchedulerTestProvider();
        view = mock(ReceiveTransationContract.View.class);
        presenter = new ReceiveTransactionPresenter(view);
        presenter.attachView(view);
        appSettings.init(RuntimeEnvironment.application);
    }


    @Test
    public void loadData() {
        Wallet  wallet = new Wallet();
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
