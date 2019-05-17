package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.VoteDetailContract;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.VoteDetailItemEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.RxUtils;

import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class VoteDetailPresenter extends BasePresenter<VoteDetailContract.View> implements VoteDetailContract.Presenter {

    public VoteDetailPresenter(VoteDetailContract.View view) {
        super(view);
    }
}
