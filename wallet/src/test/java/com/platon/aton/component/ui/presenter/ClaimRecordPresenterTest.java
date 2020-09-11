package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ClaimRecordContract;
import com.platon.aton.entity.ClaimRewardRecord;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ClaimRecordPresenterTest extends BaseTestCase {

   @Mock
    private ClaimRecordContract.View view;

    ClaimRecordPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(ClaimRecordContract.View.class);
        presenter = new ClaimRecordPresenter();
        presenter.attachView(view);

    }

  /*  @Test
    public void getRewardTransactions(){
       presenter.getRewardTransactions(Direction.DIRECTION_NEW);
    }*/


    @Test
    public void getBeginSequence(){
       long sequence = presenter.getBeginSequence(Direction.DIRECTION_NEW);
        LogUtils.i("--sequence id:" + sequence);
    }


    @Test
    public void getNewList(){

        List<ClaimRewardRecord> newClaimRewardRecordList = new ArrayList<>();
        for (int i=0; i > 5; i++){
            ClaimRewardRecord claimRewardRecord = new ClaimRewardRecord();
            claimRewardRecord.setSequence(1234);
            claimRewardRecord.setAddress("0xa577c0230df2cb329415bfebcb936496ab8ae2e4");
            claimRewardRecord.setWalletName("钱包" + i);
            newClaimRewardRecordList.add(claimRewardRecord);
        }

        List<ClaimRewardRecord> claimRewardRecords = presenter.getNewList(null,newClaimRewardRecordList,true);
        LogUtils.i("--claimRewardRecords size:" + claimRewardRecords.size());
    }




}