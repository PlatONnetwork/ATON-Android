package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.VerificationMnemonicContract;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class VerificationMnemonicPresenterTest extends BaseTestCase {

    private VerificationMnemonicContract.View view;
    private VerificationMnemonicPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(VerificationMnemonicContract.View.class);
        presenter = new VerificationMnemonicPresenter();
        presenter.attachView(view);

    }

    @Test
    public void checkTopListItem(){

        String mMnemonic = "year rib clay employ boring icon vote shrug lab fluid loyal panda";
        String[] worlds = mMnemonic.split(" ");
        VerificationMnemonicContract.DataEntity[]  mTopList = new VerificationMnemonicContract.DataEntity[worlds.length];
        presenter.setmTopList(mTopList);

        presenter.checkTopListItem(0);
        Mockito.verify(view).showTopList(Mockito.any());
    }


    @Test
    public void checkBottomListItem(){
         ArrayList<VerificationMnemonicContract.DataEntity> mAllList = new ArrayList<>();

        String mMnemonic = "year rib clay employ boring icon vote shrug lab fluid loyal panda";
        String[] worlds = mMnemonic.split(" ");
        VerificationMnemonicContract.DataEntity[]  mTopList = new VerificationMnemonicContract.DataEntity[worlds.length];
        presenter.setmTopList(mTopList);
         for (String world : worlds) {
            VerificationMnemonicContract.DataEntity dataEntity = presenter.generateDataEntity(world);
            mAllList.add(dataEntity);
        }
         presenter.setmAllList(mAllList);

         presenter.checkBottomListItem(0);
         Mockito.verify(view).showBottomList(Mockito.any());
    }


    @Test
    public void emptyChecked(){
        ArrayList<VerificationMnemonicContract.DataEntity> mAllList = new ArrayList<>();

        String mMnemonic = "year rib clay employ boring icon vote shrug lab fluid loyal panda";
        String[] worlds = mMnemonic.split(" ");
        VerificationMnemonicContract.DataEntity[]  mTopList = new VerificationMnemonicContract.DataEntity[worlds.length];
        presenter.setmTopList(mTopList);
        for (String world : worlds) {
            VerificationMnemonicContract.DataEntity dataEntity = presenter.generateDataEntity(world);
            mAllList.add(dataEntity);
        }
        presenter.setmAllList(mAllList);

        presenter.emptyChecked();
        Mockito.verify(view).showBottomList(Mockito.any());
    }





}