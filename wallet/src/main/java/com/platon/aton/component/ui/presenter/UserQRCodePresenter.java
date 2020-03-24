package com.platon.aton.component.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.zxing.encoding.EncodingHandler;
import com.platon.framework.utils.AndroidUtil;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.UserQRCodeContract;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.PhotoUtil;

import java.io.File;

public class UserQRCodePresenter extends BasePresenter<UserQRCodeContract.View> implements UserQRCodeContract.Presenter{

    private Bitmap mQRCodeBitmap;
    private String mCode;

    public UserQRCodePresenter(UserQRCodeContract.View view) {
        super(view);
    }

    @Override
    public void update() {
        UserQRCodeContract.View view = getView();
        Wallet walletEntity = view.getWalletFromIntent();
        view.showWalletName(walletEntity.getName());
        view.showWalletAddress(walletEntity.getPrefixAddress());
        Context context = getContext();
        int     size    = (int) (AndroidUtil.getWindowWith(context) * (200.0f / 376.0f));
        mQRCodeBitmap = EncodingHandler.createQRCode(walletEntity.getPrefixAddress(), size, size, null);
        if (mQRCodeBitmap != null) {
            view.showQRCode(mQRCodeBitmap, size);
        }
    }

    public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bitmap ret;
        if (isDeleteStatusBar) {
            Resources resources       = activity.getResources();
            int       resourceId      = resources.getIdentifier("status_bar_height", "dimen", "android");
            int       statusBarHeight = resources.getDimensionPixelSize(resourceId);
            ret = Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
            );
        } else {
            ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        }
        decorView.destroyDrawingCache();
        return ret;
    }

    @Override
    public void saveQRCode(final View view) {
        if (mQRCodeBitmap == null){
            return;
        }
        JZAppConfigure.getInstance().getDir(currentActivity(), JZDirType.plat, new JZAppConfigure.DirCallback() {
            @Override
            public void callback(File dir) {
                if (dir != null) {
                    boolean saved = PhotoUtil.saveImageToAlbum(currentActivity(), dir, getImageName(), screenShot(currentActivity(), false));
                    if (saved){
                        showLongToast(string(R.string.save_image_tips, dir.getAbsolutePath()));
                    }else {
                        showShortToast(R.string.save_image_failed_tips);
                    }
                }
            }
        });
    }

    private String getImageName(){
        return mCode + ".jpg";
    }
}
