package com.platon.wallet.component.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.platon.biometric.BiometricPromptCompat;
import com.platon.wallet.R;
import com.platon.wallet.app.Constants;
import com.platon.wallet.component.ui.base.BaseActivity;

public class UnlockFigerprintActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = UnlockFigerprintActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PASSWORD = 101;
    public static final int TYPE_MAIN_ACTIVITY = 1;

    public static void actionStart(Context context){
        context.startActivity(new Intent(context, UnlockFigerprintActivity.class));
    }

    public static void actionStartMainActivity(Context context){
        Intent intent = new Intent(context, UnlockFigerprintActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TYPE, TYPE_MAIN_ACTIVITY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_figerprint);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BiometricPromptCompat.supportBiometricPromptCompat(this)) {
            findViewById(R.id.tv_error).setVisibility(View.GONE);
            startAuth();
        }else {
            findViewById(R.id.tv_error).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PASSWORD){
                actionStart();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_unlock:
                UnlockWithPasswordActivity.actionStart(this, REQUEST_CODE_PASSWORD);
                break;
            case R.id.tv_evoke:
                if (BiometricPromptCompat.supportBiometricPromptCompat(this)) {
                    startAuth();
                }
                break;
            case R.id.iv_fingerprint:
                if (BiometricPromptCompat.supportBiometricPromptCompat(this)) {
                    startAuth();
                }
                break;
        }
    }

    private void initView(){
        findViewById(R.id.tv_evoke).setOnClickListener(this);
        findViewById(R.id.iv_fingerprint).setOnClickListener(this);
        findViewById(R.id.tv_unlock).setOnClickListener(this);
    }

    private void actionStart(){
        if (getIntent().hasExtra(Constants.Extra.EXTRA_TYPE) &&
                getIntent().getIntExtra(Constants.Extra.EXTRA_TYPE, 0) == TYPE_MAIN_ACTIVITY){
            MainActivity.actionStart(UnlockFigerprintActivity.this);
            UnlockFigerprintActivity.this.finish();
        }
        UnlockFigerprintActivity.this.finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startAuth() {
        final BiometricPromptCompat mBiometricPromptCompat = new BiometricPromptCompat.Builder(UnlockFigerprintActivity.this)
                .setNegativeButton(string(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .build();
        final CancellationSignal mCancellationSignal = new CancellationSignal();
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {

            }
        });
        mBiometricPromptCompat.authenticate(mCancellationSignal, mCallback);
    }

    private BiometricPromptCompat.IAuthenticationCallback mCallback = new BiometricPromptCompat.IAuthenticationCallback(){
        @Override
        public void onAuthenticationError(int errorCode, @Nullable CharSequence errString) {
            //多次指纹密码验证错误后，进入此方法；并且，不可再验（短时间）
            if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT){
//                mCancellationSignal.cancel();
            }else {
            }
        }

        @Override
        public void onAuthenticationHelp(int helpCode, @Nullable CharSequence helpString) {
            //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。

        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPromptCompat.IAuthenticationResult result) {
            //指纹密码验证成功
            actionStart();
        }

        @Override
        public void onAuthenticationFailed() {
            //指纹验证失败，指纹识别失败，可再验，错误原因为：该指纹不是系统录入的指纹
        }
    };
}
