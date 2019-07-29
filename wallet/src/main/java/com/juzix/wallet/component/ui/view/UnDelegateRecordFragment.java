package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.DelegateRecordContract;
import com.juzix.wallet.component.ui.presenter.DelegateRecordPresenter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

/**
 * 赎回委托fragment
 */
public class UnDelegateRecordFragment extends MVPBaseFragment<DelegateRecordPresenter> implements DelegateRecordContract.View {

    @Override
    protected DelegateRecordPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onFragmentPageStart() {

    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_record, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        int position = FragmentPagerItem.getPosition(getArguments());
//        showLongToast("" +position);
        showLongToast("" +"UnDelegateRecordFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
//        int position = FragmentPagerItem.getPosition(getArguments());
//        showLongToast("" +position);
        showLongToast(""+"onResume" +"UnDelegateRecordFragment");
    }
}
