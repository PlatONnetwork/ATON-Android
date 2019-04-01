package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.CaptureProviderHandler;
import com.google.zxing.decoding.ICaptureProvider;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.decoding.RGBLuminanceSource;
import com.google.zxing.view.ViewfinderView;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.utils.PhotoUtil;
import com.juzix.wallet.utils.QRCodeDecoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ScanQRCodeActivity extends BaseActivity implements ICaptureProvider, View.OnClickListener, SurfaceHolder.Callback {

    public static final  String                           EXTRA_SCAN_QRCODE_DATA    = "extra_scan_qrcode_data";
    private static final int                              REQUEST_CODE_SCAN_GALLERY = 100;
    private static final float                            BEEP_VOLUME               = 0.10f;
    private static final long                             VIBRATE_DURATION          = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final        MediaPlayer.OnCompletionListener beepListener              = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private CaptureProviderHandler handler;
    private ViewfinderView         viewfinderView;
    private ImageButton            back;
    private ImageButton            btnFlash;
    private Button                 btnAlbum; // 相册
    private boolean                isFlashOn = false;
    private boolean                hasSurface;
    private Vector<BarcodeFormat>  decodeFormats;
    private String                 characterSet;
    private InactivityTimer        inactivityTimer;
    private MediaPlayer            mediaPlayer;
    private boolean                playBeep;
    private boolean                vibrate;
    private ProgressDialog         mProgress;
    //	private Button cancelScanButton;
    private Uri                    photoUri;
    private Bitmap                 scanBitmap;

    public static void actionStart(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, ScanQRCodeActivity.class), requestCode);
    }

    private View.OnClickListener flashListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                boolean isSuccess = CameraManager.get().setFlashLight(!isFlashOn);
                if (!isSuccess) {
                    showLongToast(R.string.scan_qr_code_open_lights_failed);
                    return;
                }
                btnFlash.setImageResource(isFlashOn ? R.drawable.flash_off : R.drawable.flash_on);
                isFlashOn = !isFlashOn;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Called when the activity is first created.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_left).setOnClickListener(this);
//        ((TextView) findViewById(R.id.tv_middle)).setText(R.string.scan_qr_code);
        ((ImageView)findViewById(R.id.iv_left)).setImageResource(R.drawable.icon_back_white);
        TextView tvRight =  findViewById(R.id.tv_right);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(R.string.photo_album);

        findViewById(R.id.ll_left).setOnClickListener(this);
        findViewById(R.id.ll_right).setOnClickListener(this);
        CameraManager.init(getApplication());
        viewfinderView =  findViewById(R.id.viewfinder_content);

        btnFlash =  findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(flashListener);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left:
                ScanQRCodeActivity.this.finish();
                break;
            case R.id.ll_right:
                openAlbum();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    handleAlbumPic(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openAlbum() {
        final BaseActivity activity = currentActivity();
        String[] params = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                //打开手机中的相册
//            Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
//            innerIntent.setType("image/*");
//            startActivityForResult(innerIntent, REQUEST_CODE_SCAN_GALLERY);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SCAN_GALLERY);
            }

            @Override
            public void onHasPermission(int what) {
                //打开手机中的相册
//            Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
//            innerIntent.setType("image/*");
//            startActivityForResult(innerIntent, REQUEST_CODE_SCAN_GALLERY);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SCAN_GALLERY);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, params);
    }
 
    private void handleAlbumPic(Intent data) {
        photoUri = Uri.parse(PhotoUtil.getPath(this, data.getData()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(photoUri.getPath()));
        }
        mProgress = new ProgressDialog(ScanQRCodeActivity.this);
        mProgress.setMessage(string(R.string.gauge_scanning_qr_code));
        mProgress.setCancelable(false);
        mProgress.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.dismiss();
                Result result = scanningImage(photoUri);
                if (result != null) {
                    Intent resultIntent = new Intent();
                    Bundle bundle       = new Bundle();
                    bundle.putString(EXTRA_SCAN_QRCODE_DATA, result.getText());
                    resultIntent.putExtras(bundle);
                    ScanQRCodeActivity.this.setResult(RESULT_OK, resultIntent);
                    ScanQRCodeActivity.this.finish();
                } else {
                    showLongToast(R.string.scan_qr_code_failed_tips);
                }
            }
        });
    }

    public Result scanningImage(Uri uri) {
        if (uri == null) {
            return null;
        }

        return QRCodeDecoder.syncDecodeQRCode(PhotoUtil.getDecodeAbleBitmap(this, uri));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView   surfaceView   = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
//		cancelScanButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CaptureActivity.this.finish();
//			}
//		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureProviderHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //FIXME
        if (TextUtils.isEmpty(resultString)) {
            showLongToast(R.string.scan_qr_code_failed_tips);
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle       = new Bundle();
            bundle.putString(EXTRA_SCAN_QRCODE_DATA, resultString);
            // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
//            bundle.putParcelable("bitmap", barcode);
//            Logger.d("saomiao",resultString);
            resultIntent.putExtras(bundle);
            ScanQRCodeActivity.this.setResult(RESULT_OK, resultIntent);
        }
        ScanQRCodeActivity.this.finish();
    }

    @Override
    public void onScanResult(int resultCode, Intent data) {
        Bundle bundle     = data.getExtras();
        String scanResult = bundle.getString(EXTRA_SCAN_QRCODE_DATA);
        showLongToast(scanResult);
        ScanQRCodeActivity.this.setResult(resultCode, data);
        ScanQRCodeActivity.this.finish();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public static void startActivityForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
