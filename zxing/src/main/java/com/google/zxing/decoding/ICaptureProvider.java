package com.google.zxing.decoding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.google.zxing.view.ViewfinderView;

public interface ICaptureProvider {

    ViewfinderView getViewfinderView();

    Handler getHandler();
    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    void handleDecode(Result result, Bitmap barcode);

    void onScanResult(int resultCode, Intent data);
}
