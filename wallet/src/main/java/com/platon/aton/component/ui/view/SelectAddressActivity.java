package com.platon.aton.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.SelectAddressListAdapter;
import com.platon.aton.component.ui.contract.SelectAddressContract;
import com.platon.aton.component.ui.presenter.SelectAddressPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.entity.Address;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SelectAddressActivity extends BaseActivity<SelectAddressContract.View, SelectAddressPresenter> implements SelectAddressContract.View {

    @BindView(R.id.list_wallet_address)
    ListView listWalletAddress;

    @BindView(R.id.layout_no_data)
    View emptyView;
    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;

    private Unbinder unbinder;
    private SelectAddressListAdapter addressBookListAdapter;

    @Override
    public SelectAddressPresenter createPresenter() {
        return new SelectAddressPresenter();
    }

    @Override
    public SelectAddressContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        initViews();
        getPresenter().fetchAddressList();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_address;
    }

    private void initViews() {

        String senderAddress = getIntent().getStringExtra(Constants.Extra.EXTRA_ADDRESS);

        addressBookListAdapter = new SelectAddressListAdapter(R.layout.item_wallet_address_list, null, senderAddress);

        listWalletAddress.setAdapter(addressBookListAdapter);

        listWalletAddress.setEmptyView(emptyView);

        listWalletAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((Address) parent.getAdapter().getItem(position)).getPrefixAddress().equals(senderAddress)) {
                    showLongToast(R.string.can_not_send_to_itself);
                    return;
                } else {
                    getPresenter().selectAddress(position);
                }
            }
        });
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.RequestCode.REQUEST_CODE_ADD_ADDRESS:
                case Constants.RequestCode.REQUEST_CODE_EDIT_ADDRESS:
                    getPresenter().fetchAddressList();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void notifyAddressListChanged(List<Address> addressEntityList) {
        addressBookListAdapter.notifyDataChanged(addressEntityList);
    }

    @Override
    public void setResult(Address addressEntity) {
        Intent intent = new Intent(this, SelectAddressActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, addressEntity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getAction() {
        return getIntent().getAction();
    }

    public static void actionStartForResult(Context context, String action, int requestCode, String senderAddress) {
        Intent intent = new Intent(context, SelectAddressActivity.class);
        intent.setAction(action);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, senderAddress);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
