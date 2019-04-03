package com.juzix.wallet.component.ui.presenter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.app.FlowableSchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ReceiveTransationContract;
import com.juzix.wallet.component.ui.dialog.ShareDialogFragment;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.config.JZDirType;
import com.juzix.wallet.entity.ShareAppInfo;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.PhotoUtil;
import com.juzix.wallet.utils.QRCodeEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class ReceiveTransationPresenter extends BasePresenter<ReceiveTransationContract.View> implements ReceiveTransationContract.Presenter {

    private WalletEntity walletEntity;
    private Bitmap mQRCodeBitmap;

    public ReceiveTransationPresenter(ReceiveTransationContract.View view) {
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
                    return QRCodeEncoder.syncEncodeQRCode(walletEntity.getPrefixAddress(), DensityUtil.dp2px(getContext(), 250f));
                }
            }).compose(new FlowableSchedulersTransformer())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            mQRCodeBitmap = bitmap;
                            if (isViewAttached() && bitmap != null) {
                                getView().setWalletAddressQrCode(bitmap);
                            }
                        }
                    });
        }
    }

    private Bitmap screenShot(@NonNull final View decorView) {
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        Bitmap ret = Bitmap.createBitmap(bmp, 0, 0, decorView.getWidth(), decorView.getHeight());
        decorView.destroyDrawingCache();
        return ret;
    }

    @Override
    public void shareView() {
        if (mQRCodeBitmap == null) {
            return;
        }
        View shareView = getView().shareView(walletEntity.getName(), walletEntity.getPrefixAddress(), mQRCodeBitmap);
        final BaseActivity activity = currentActivity();
        JZAppConfigure.getInstance().getDir(activity, JZDirType.plat, new JZAppConfigure.DirCallback() {
            @Override
            public void callback(File dir) {
                if (dir != null) {
                    boolean saved = PhotoUtil.saveImageToAlbum(activity, dir, getImageName(), screenShot(shareView));
                    if (saved) {
                        showLongToast(R.string.save_image_tips);
                        List<ShareAppInfo> shareAppInfoList = AppUtil.getShareAppInfoList(getContext());
                        if (!shareAppInfoList.isEmpty()) {
                            ShareDialogFragment.newInstance((ArrayList<ShareAppInfo>) shareAppInfoList).show(activity.getSupportFragmentManager(), "share");
                        }
                    } else {
                        showShortToast(R.string.save_image_failed_tips);
                    }
                }
            }
        });
    }

    @Override
    public void copy() {

        CommonUtil.copyTextToClipboard(getContext(), walletEntity.getPrefixAddress());
    }

    private String getImageName() {
        return walletEntity.getPrefixAddress() + ".jpg";
    }
}
