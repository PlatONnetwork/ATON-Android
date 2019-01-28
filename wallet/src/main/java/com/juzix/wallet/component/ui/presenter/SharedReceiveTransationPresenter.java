package com.juzix.wallet.component.ui.presenter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedReceiveTransationContract;
import com.juzix.wallet.component.ui.dialog.ShareDialogFragment;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.config.JZDirType;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.NodeEntity;
import com.juzix.wallet.entity.ShareAppInfo;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.PhotoUtil;
import com.juzix.wallet.utils.QRCodeEncoder;

import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class SharedReceiveTransationPresenter extends BasePresenter<SharedReceiveTransationContract.View> implements SharedReceiveTransationContract.Presenter {

    private SharedWalletEntity walletEntity;
    private Bitmap mQRCodeBitmap;

    public SharedReceiveTransationPresenter(SharedReceiveTransationContract.View view) {
        super(view);
        walletEntity = view.getWalletFromIntent();
    }

    @Override
    public void loadData() {
        if (isViewAttached() && walletEntity != null) {

            getView().setWalletInfo(walletEntity);

            Flowable.fromCallable(new Callable<Bitmap>() {

                @Override
                public Bitmap call() throws Exception {
                    return QRCodeEncoder.syncEncodeQRCode(walletEntity.getPrefixContractAddress(), DensityUtil.dp2px(getContext(), 250f));
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Subscription>() {
                @Override
                public void accept(Subscription subscription) throws Exception {
                    showLoadingDialog();
                }
            }).doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    dismissLoadingDialogImmediately();
                }
            }).subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    mQRCodeBitmap = bitmap;
                    if (isViewAttached() && bitmap != null) {
                        getView().setWalletAddressQrCode(bitmap);
                    }
                }
            });

            NodeManager.getInstance()
                    .getCheckedNode()
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<NodeEntity>() {
                        @Override
                        public boolean test(NodeEntity nodeEntity) throws Exception {
                            return !(nodeEntity.isDefaultNode() && nodeEntity.isMainNetworkNode());
                        }
                    })
                    .subscribe(new Consumer<NodeEntity>() {
                        @Override
                        public void accept(NodeEntity nodeEntity) throws Exception {
                            if (isViewAttached()) {
                                getView().showWarnDialogFragment();
                            }
                        }
                    });
        }
    }

    private Bitmap screenShot(@NonNull final View decorView) {
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap         bmp = decorView.getDrawingCache();
        Bitmap ret = Bitmap.createBitmap(bmp, 0, 0, decorView.getWidth(), decorView.getHeight());
        decorView.destroyDrawingCache();
        return ret;
    }

    @Override
    public void shareView() {
        if (mQRCodeBitmap == null){
            return;
        }
        View  shareView = getView().shareView(walletEntity.getName(), walletEntity.getPrefixContractAddress(), mQRCodeBitmap);
        final BaseActivity activity  = currentActivity();
        JZAppConfigure.getInstance().getDir(activity, JZDirType.plat, new JZAppConfigure.DirCallback() {
            @Override
            public void callback(File dir) {
                if (dir != null) {
                    boolean saved = PhotoUtil.saveImageToAlbum(activity, dir, getImageName(), screenShot(shareView));
                    if (saved){
                        showLongToast(string(R.string.save_image_tips, dir.getAbsolutePath()));
                        List<ShareAppInfo> shareAppInfoList = AppUtil.getShareAppInfoList(getContext());
                        if (!shareAppInfoList.isEmpty()){
                            ShareDialogFragment.newInstance((ArrayList<ShareAppInfo>) shareAppInfoList).show(activity.getSupportFragmentManager(), "share");
                        }
                    }else {
                        showShortToast(R.string.save_image_failed_tips);
                    }
                }
            }
        });
    }

    private String getImageName(){
        return walletEntity.getPrefixContractAddress() + ".jpg";
    }
}
