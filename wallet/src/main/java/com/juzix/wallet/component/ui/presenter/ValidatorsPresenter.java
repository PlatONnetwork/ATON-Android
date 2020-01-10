package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.db.entity.VerifyNodeEntity;
import com.juzix.wallet.db.sqlite.VerifyNodeDao;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.ValidatorsService;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.RxUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ValidatorsPresenter extends BasePresenter<ValidatorsContract.View> implements ValidatorsContract.Presenter {

    private List<VerifyNode> mVerifyNodeList;

    public ValidatorsPresenter(ValidatorsContract.View view) {
        super(view);
    }

    @Override
    public void loadValidatorsData(int tab, SortType sortType, String keywords) {
        ServerUtils.getCommonApi().getVerifyNodeList()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> nodeList) {
                        if (isViewAttached()) {
                            getView().showValidatorsDataOnAll(nodeList);
                        }

                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        if (isViewAttached()) {
                            getView().showValidatorsFailed();
                        }
                    }
                });
    }

}



