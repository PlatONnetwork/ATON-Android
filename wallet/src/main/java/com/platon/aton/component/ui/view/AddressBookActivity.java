package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.AddressBookListAdapter;
import com.platon.aton.component.ui.contract.AddressBookContract;
import com.platon.aton.component.ui.presenter.AddressBookPresenter;
import com.platon.aton.component.widget.CommonTitleBar;
import com.platon.aton.component.widget.swipeenulistview.SwipeMenu;
import com.platon.aton.component.widget.swipeenulistview.SwipeMenuCreator;
import com.platon.aton.component.widget.swipeenulistview.SwipeMenuItem;
import com.platon.aton.component.widget.swipeenulistview.SwipeMenuListView;
import com.platon.aton.entity.Address;
import com.platon.aton.utils.DensityUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AddressBookActivity extends BaseActivity<AddressBookContract.View, AddressBookPresenter> implements AddressBookContract.View {

    @BindView(R.id.swipeMenuListView)
    SwipeMenuListView swipeMenuListView;
    @BindString(R.string.address_book)
    String addressBookTitle;
    @BindString(R.string.edit)
    String editAddress;
    @BindString(R.string.delete)
    String deleteAddress;
    @BindView(R.id.layout_no_data)
    View emptyView;
    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;

    private Unbinder unbinder;
    private AddressBookListAdapter addressBookListAdapter;

    @Override
    public AddressBookPresenter createPresenter() {
        return new AddressBookPresenter();
    }

    @Override
    public AddressBookContract.View createView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        return R.layout.activity_address_book;
    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(Constants.UMPages.ADDRESS_BOOK);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.ADDRESS_BOOK);
        super.onPause();
    }

    private void initViews() {

        commonTitleBar.setRightDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewAddressActivity.actionStartWithExtraForResult(AddressBookActivity.this, null);
            }
        });

        addressBookListAdapter = new AddressBookListAdapter(R.layout.item_wallet_address_list, null);

        swipeMenuListView.setAdapter(addressBookListAdapter);

        swipeMenuListView.setEmptyView(emptyView);

        swipeMenuListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu(menu);
            }
        });

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    getPresenter().editAddress(position);
                } else {
                    getPresenter().deleteAddress(position);
                }
                swipeMenuListView.smoothCloseMenu();
                return true;
            }
        });

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().selectAddress(position);
            }
        });


    }

    private void createMenu(SwipeMenu menu) {

        SwipeMenuItem editAddressItem = new SwipeMenuItem(getContext());
        editAddressItem.setBackground(R.color.color_f9fbff);
        editAddressItem.setWidth(DensityUtil.dp2px(getContext(), 60));
        editAddressItem.setTitleSize(13);
        editAddressItem.setTitleColor(ContextCompat.getColor(getContext(), R.color.color_000000));
        editAddressItem.setTitle(editAddress);
        menu.addMenuItem(editAddressItem);

        SwipeMenuItem deleteAddressItem = new SwipeMenuItem(getContext());
        deleteAddressItem.setBackground(R.color.color_ff2222);
        deleteAddressItem.setWidth(DensityUtil.dp2px(getContext(), 60));
        deleteAddressItem.setTitleSize(13);
        deleteAddressItem.setTitleColor(ContextCompat.getColor(getContext(), R.color.color_ffffff));
        deleteAddressItem.setTitle(deleteAddress);
        menu.addMenuItem(deleteAddressItem);
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
        Intent intent = new Intent(this, AddressBookActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, addressEntity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getAction() {
        return getIntent().getAction();
    }

    public static void actionStart(Context context, String action) {
        Intent intent = new Intent(context, AddressBookActivity.class);
        intent.setAction(action);
        context.startActivity(intent);
    }
}
