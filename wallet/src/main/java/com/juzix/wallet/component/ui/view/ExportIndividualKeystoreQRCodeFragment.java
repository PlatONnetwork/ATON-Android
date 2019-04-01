package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.QRCodeEncoder;

public class ExportIndividualKeystoreQRCodeFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View     view    = inflater.inflate(R.layout.fragment_export_individual_keystore_qr_code, container, false);
        Activity activity = getActivity();
        int size = DensityUtil.dp2px(activity, 250f);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                showLoadingDialog();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(activity.getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD), size);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                dismissLoadingDialogImmediately();
                if (bitmap != null) {
                    ImageView                 imageView    = view.findViewById(R.id.iv_qrcode);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
        return view;
    }
}
