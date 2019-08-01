package com.juzix.wallet.component.widget;

import android.content.Context;
import android.widget.PopupWindow;

import com.juzix.wallet.entity.Wallet;

import java.util.List;

public class WalletListPop extends PopupWindow {

    private List<Wallet> walletList;

    public WalletListPop(Context context, List<Wallet> walletList) {
        super(context);
        this.walletList = walletList;
    }
}
