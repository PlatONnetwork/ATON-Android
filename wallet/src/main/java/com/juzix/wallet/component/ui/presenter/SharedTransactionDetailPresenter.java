package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedTransactionDetailContract;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.TransactionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matrixelement
 */
public class SharedTransactionDetailPresenter extends BasePresenter<SharedTransactionDetailContract.View> implements SharedTransactionDetailContract.Presenter {

    private SharedTransactionEntity transactionEntity;
    private String queryAddress;

    public SharedTransactionDetailPresenter(SharedTransactionDetailContract.View view) {
        super(view);
        transactionEntity = view.getTransactionFromIntent();
        queryAddress = view.getAddressFromIntent();
    }

    @Override
    public void fetchTransactionDetail() {
        if (isViewAttached() && transactionEntity != null) {
            List<TransactionResult> resultList = transactionEntity.getTransactionResult();
            List<TransactionResult> confirmList = new ArrayList<>();
            List<TransactionResult> revokeList = new ArrayList<>();
            List<TransactionResult> undeterminedList = new ArrayList<>();
            for (TransactionResult transactionResult : resultList) {
                switch (transactionResult.getStatus()) {
                    case OPERATION_APPROVAL:
                        confirmList.add(transactionResult);
                        break;
                    case OPERATION_REVOKE:
                        revokeList.add(transactionResult);
                        break;
                    case OPERATION_UNDETERMINED:
                        undeterminedList.add(transactionResult);
                        break;
                }
            }
            if (!resultList.isEmpty()) {
                resultList.clear();
            }
            if (!confirmList.isEmpty()) {
                resultList.addAll(confirmList);
            }
            if (!revokeList.isEmpty()) {
                resultList.addAll(revokeList);
            }
            if (!undeterminedList.isEmpty()) {
                resultList.addAll(undeterminedList);
            }

            getView().showTransactionResult(resultList);

            getView().setTransactionDetailInfo(transactionEntity, queryAddress);
        }
    }

}
