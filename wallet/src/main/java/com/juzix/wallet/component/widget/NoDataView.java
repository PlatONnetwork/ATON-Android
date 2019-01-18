package com.juzix.wallet.component.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author matrixelement
 */
public class NoDataView extends FrameLayout {

    //    private ImageView ivNoData;
//    private ImageView ivNoNetWork;
//    private ImageView ivError;
//    private ImageView ivNoWallet;
//    private TextView tvWarning;
//
    public NoDataView(@NonNull Context context) {
        super(context, null, 0);
    }

    public NoDataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoDataView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context);
    }
//
//    private void init(Context context) {
//
//        LayoutInflater.from(context).inflate(R.layout.include_no_address, this);
//
//        ivNoData = findViewById(R.id.iv_no_data);
//        ivNoNetWork = findViewById(R.id.iv_no_network);
//        ivError = findViewById(R.id.iv_error);
//        ivNoWallet = findViewById(R.id.iv_no_wallet);
//        tvWarning = findViewById(R.id.tv_warning);
//    }
//
//    public void showPage(EmptyStatus emptyStatus, String warning) {
//        if (emptyStatus == EmptyStatus.NO_WALLET) {
//            ivNoWallet.setVisibility(VISIBLE);
//            ivNoData.setVisibility(GONE);
//            ivNoNetWork.setVisibility(GONE);
//            ivError.setVisibility(GONE);
//        } else if (emptyStatus == EmptyStatus.NO_DATA) {
//            ivNoWallet.setVisibility(GONE);
//            ivNoData.setVisibility(VISIBLE);
//            ivNoNetWork.setVisibility(GONE);
//            ivError.setVisibility(GONE);
//        } else if (emptyStatus == EmptyStatus.NO_NETWORK) {
//            ivNoWallet.setVisibility(GONE);
//            ivNoData.setVisibility(GONE);
//            ivNoNetWork.setVisibility(VISIBLE);
//            ivError.setVisibility(GONE);
//        } else {
//            ivNoWallet.setVisibility(GONE);
//            ivNoData.setVisibility(GONE);
//            ivNoNetWork.setVisibility(GONE);
//            ivError.setVisibility(VISIBLE);
//        }
//    }
//
//    public enum EmptyStatus {
//        NO_WALLET, NO_DATA, NO_NETWORK, ERROR;
//    }
}
