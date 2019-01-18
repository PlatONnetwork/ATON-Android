package com.juzix.wallet.engine;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.view.AddNewAddressActivity;
import com.juzix.wallet.component.ui.view.ImportIndividualWalletActivity;
import com.juzix.wallet.component.ui.view.SendIndividualTransationActivity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import org.reactivestreams.Subscription;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class QRCodeParser {
    public static void parseMainQRCode(BaseActivity activity, String code) {
        if (JZWalletUtil.isValidAddress(code)){
            if (IndividualWalletManager.getInstance().getWalletList().isEmpty()){
                AddNewAddressActivity.actionStartWithAddress(activity, code);
            }else {
                ArrayList<IndividualWalletEntity> walletEntities = IndividualWalletManager.getInstance().getWalletList();
                IndividualWalletEntity entity = null;
                for (int i = 0; i < walletEntities.size(); i++){
                    if (walletEntities.get(i).getBalance() > 0){
                        entity = walletEntities.get(i);
                        break;
                    }
                }
                if (walletEntities.isEmpty() || entity == null){
                    AddNewAddressActivity.actionStartWithAddress(activity, code);
                }else {
                    SendIndividualTransationActivity.actionStart(activity, code, entity.getPrefixAddress());
                }
//                Flowable.fromIterable(IndividualWalletManager.getInstance().getWalletList()).map(new Function<IndividualWalletEntity, IndividualWalletEntity>() {
//                    @Override
//                    public IndividualWalletEntity apply(IndividualWalletEntity walletEntity) throws Exception {
//                        return IndividualWalletTransactionManager.getInstance().getBalanceByAddress(walletEntity);
//                    }
//                }).map(new Function<IndividualWalletEntity, Double>() {
//
//                    @Override
//                    public Double apply(IndividualWalletEntity walletEntity) throws Exception {
//                        return walletEntity.getBalance();
//                    }
//                }).reduce(new BiFunction<Double, Double, Double>() {
//                    @Override
//                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
//                        return aDouble + aDouble2;
//                    }
//                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).toFlowable().doOnSubscribe(new Consumer<Subscription>() {
//                    @Override
//                    public void accept(Subscription subscription) throws Exception {
//                        activity.showLoadingDialog();
//                    }
//                }).doOnTerminate(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        activity.dismissLoadingDialogImmediately();
//                    }
//                }).subscribe(new Consumer<Double>() {
//                    @Override
//                    public void accept(Double aDouble) throws Exception {
//                        if (aDouble > 0) {
//                            SendIndividualTransationActivity.actionStart(activity, code);
//                        } else {
//                            AddNewAddressActivity.actionStartWithAddress(activity, code);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        activity.showLongToast(R.string.network_error);
//                    }
//                });
            }
            return;
        }
        if (JZWalletUtil.isValidKeystore(code)){
            ImportIndividualWalletActivity.actionStart(activity,0, code);
            return;
        }
        if (JZWalletUtil.isValidPrivateKey(code)){
            ImportIndividualWalletActivity.actionStart(activity,2, code);
            return;
        }
        if (JZWalletUtil.isValidMnemonic(code)){
            ImportIndividualWalletActivity.actionStart(activity,1, code);
            return;
        }
        activity.showLongToast(activity.string(R.string.unrecognized));
    }

    public static void parseImportQRCode(BaseActivity activity, String code) {
        if (JZWalletUtil.isValidKeystore(code)){
            ImportIndividualWalletActivity.actionStart(activity,0, code);
            return;
        }
        if (JZWalletUtil.isValidPrivateKey(code)){
            ImportIndividualWalletActivity.actionStart(activity,2, code);
            return;
        }
        if (JZWalletUtil.isValidMnemonic(code)){
            ImportIndividualWalletActivity.actionStart(activity,1, code);
            return;
        }
        activity.showLongToast(activity.string(R.string.unrecognized));
    }

    private static void getTotalAssets() {

    }
}
