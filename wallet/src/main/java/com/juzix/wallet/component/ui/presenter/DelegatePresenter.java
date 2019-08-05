package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.dialog.SelectWalletDialogFragment;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.Wallet;

public class DelegatePresenter extends BasePresenter<DelegateContract.View> implements DelegateContract.Presenter {
    private Wallet mWallet;

    public DelegatePresenter(DelegateContract.View view) {
        super(view);

    }

    @Override
    public void showSelectWalletDialogFragment() {

        SelectWalletDialogFragment.newInstance(mWallet != null ? mWallet.getUuid() : "", true)
                .setOnItemClickListener(new SelectWalletDialogFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(Wallet walletEntity) {
                        if (isViewAttached()) {
                            mWallet = walletEntity;
                            getView().showSelectedWalletInfo(walletEntity);
                        }
                    }
                })
                .show(currentActivity().getSupportFragmentManager(), "showSelectWalletDialog");

    }

    @Override
    public void showWalletInfo() {
        showSelectedWalletInfo();
    }

    @Override
    public boolean checkDelegateAmount(String delegateAmount) {

        //检查委托的数量
        String errMsg = null;
        //todo 判断语句没写完整
        if (TextUtils.isEmpty(delegateAmount)) {
            errMsg = string(R.string.transfer_amount_cannot_be_empty);
        } else {
            //            if (!isBalanceEnough(transferAmount)) { //是否超出可赎回数量，超出也无法提交
//                errMsg = string(R.string.insufficient_balance);
//            }
        }

        getView().showAmountError(errMsg);

        return TextUtils.isEmpty(errMsg);

    }

    @Override
    public void updateDelegateButtonState() {
        if (isViewAttached()) {
            String withdrawAmount = getView().getDelegateAmount();
//              boolean isAmountValid =!TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) > 0 && isBalanceEnough(transferAmount);
            boolean isAmountValid = !TextUtils.isEmpty(withdrawAmount) && NumberParserUtils.parseDouble(withdrawAmount) > 0;
            getView().setDelegateButtonState(isAmountValid);
        }


    }

    private void showSelectedWalletInfo() {
        mWallet = WalletManager.getInstance().getFirstValidIndividualWalletBalance();
        if (isViewAttached() && mWallet != null) {
            getView().showSelectedWalletInfo(mWallet);
        }

    }
}
