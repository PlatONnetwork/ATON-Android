package com.juzix.wallet.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.SelectAddressContract;
import com.juzix.wallet.component.ui.presenter.SelectAddressPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SelectAddressActivity extends MVPBaseActivity<SelectAddressPresenter> implements SelectAddressContract.View {

    @BindView(R.id.list_wallet_address)
    ListView listWalletAddress;

    private Unbinder unbinder;

    @Override
    protected SelectAddressPresenter createPresenter() {
        return new SelectAddressPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartForResult(Context context) {
        Intent intent = new Intent(context, SelectAddressActivity.class);
        ((Activity) context).startActivityForResult(intent, 0);
    }
}
