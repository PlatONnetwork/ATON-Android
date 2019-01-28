package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedTransactionDetailContract;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class SharedTransactionDetailPresenter extends BasePresenter<SharedTransactionDetailContract.View> implements SharedTransactionDetailContract.Presenter {

    private SharedTransactionEntity        transactionEntity;

    public SharedTransactionDetailPresenter(SharedTransactionDetailContract.View view) {
        super(view);
        transactionEntity = view.getTransactionFromIntent();
    }

    @Override
    public void fetchTransactionDetail() {
        if (isViewAttached() && transactionEntity != null) {
            ArrayList<TransactionResult> resultList = transactionEntity.getTransactionResult();
            ArrayList<TransactionResult> confirmList = new ArrayList<>();
            ArrayList<TransactionResult> revokeList = new ArrayList<>();
            for (TransactionResult transactionResult : resultList){
                switch(transactionResult.getOperation()){
                    case TransactionResult.OPERATION_APPROVAL:
                        confirmList.add(transactionResult);
                        break;
                    case TransactionResult.OPERATION_REVOKE:
                        revokeList.add(transactionResult);
                        break;
                    default:
                        break;
                }
            }
            if (!resultList.isEmpty()){
                resultList.clear();
            }
            if (!confirmList.isEmpty()){
                resultList.addAll(confirmList);
            }
            if (!revokeList.isEmpty()){
                resultList.addAll(revokeList);
            }

            getView().showTransactionResult(resultList);

            getView().setTransactionDetailInfo(transactionEntity);
        }
    }

}
