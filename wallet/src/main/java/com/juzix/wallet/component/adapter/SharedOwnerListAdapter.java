package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author matrixelement
 */
public class SharedOwnerListAdapter extends RecyclerView.Adapter<SharedOwnerListAdapter.ViewHolder> {

    private final static String TAG = SharedOwnerListAdapter.class.getSimpleName();
    private OnSelectAddressClickListener mSelectAddressClickListener;
    private OnScanQRCodeClickListener mScanQRCodeClickListener;
    private OnAddressChangedListener mAddressChangedListener;
    private List<OwnerEntity> addressEntityList;
    private int mPosition = -1;
    private Context context;

    public SharedOwnerListAdapter(Context context){
        this.context = context;
    }

    public void setOnSelectAddressClickListener(OnSelectAddressClickListener selectAddressClickListener) {
        this.mSelectAddressClickListener = selectAddressClickListener;
    }

    public void setOnScanQRCodeClickListener(OnScanQRCodeClickListener scanQRCodeClickListener) {
        this.mScanQRCodeClickListener = scanQRCodeClickListener;
    }

    public void setOnAddressChangedListener(OnAddressChangedListener onAddressChangedListener) {
        this.mAddressChangedListener = onAddressChangedListener;
    }

    private void updateWalletName(int position, String walletName) {
        if (addressEntityList == null || addressEntityList.isEmpty()) {
            return;
        }

        OwnerEntity addressEntity = addressEntityList.get(position);
        if (addressEntity != null) {
            addressEntity.setName(walletName);
        }
    }

    private void updateWalletAddress(int mPosition, String walletAddress) {
        if (addressEntityList == null || addressEntityList.isEmpty()) {
            return;
        }

        OwnerEntity addressEntity = addressEntityList.get(mPosition);
        if (addressEntity != null) {
            addressEntity.setAddress(walletAddress);
        }
    }

    public void notifyWalletAddressError(ViewHolder holder, String errorMsg) {
        if (TextUtils.isEmpty(errorMsg)) {
            holder.tvAddressError.setText("");
            holder.tvAddressError.setVisibility(View.GONE);
        } else{
            holder.tvAddressError.setText(errorMsg);
            holder.tvAddressError.setVisibility(View.VISIBLE);
        }
    }

    public void notifyWalletAddressChanged(String walletAddress) {

        if (addressEntityList == null || addressEntityList.isEmpty()) {
            return;
        }

        OwnerEntity addressEntity = addressEntityList.get(mPosition);
        if (addressEntity != null) {
            addressEntity.setAddress(walletAddress);
            notifyItemChanged(mPosition);
        }

    }

    public void setSelectedWalletAddress(String walletAddress, String name) {
        if (addressEntityList == null || addressEntityList.isEmpty()) {
            return;
        }
        OwnerEntity addressEntity = addressEntityList.get(0);
        if (addressEntity != null) {
            addressEntity.setAddress(walletAddress);
            addressEntity.setName(name);
            notifyItemChanged(0);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_shared_owner_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        OwnerEntity addressEntity = addressEntityList.get(position);

        if (holder.etWalletName.getTag() instanceof TextChangedListener) {
            holder.etWalletName.removeTextChangedListener((TextWatcher) holder.etWalletName.getTag());
        }

        if (holder.etWalletAddress.getTag() instanceof TextChangedListener) {
            holder.etWalletAddress.removeTextChangedListener((TextWatcher) holder.etWalletAddress.getTag());
        }

        holder.ivScan.setOnClickListener(v -> {
            if (mScanQRCodeClickListener != null) {
                mPosition = position;
                mScanQRCodeClickListener.onScanQRCodeClick(position);
//                holder.etWalletAddress.setSelection(holder.etWalletAddress.getText().toString().length());
            }
        });

        holder.ivAddressBook.setOnClickListener(v -> {
            if (mSelectAddressClickListener != null) {
                mPosition = position;
                mSelectAddressClickListener.onSelectAddressClick(position);
//                holder.etWalletAddress.setSelection(holder.etWalletAddress.getText().toString().length());
            }
        });

        TextChangedListener walletNameListener = new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                updateWalletName(position, s.toString());
                if (mAddressChangedListener != null) {
                    mAddressChangedListener.onAddressChanged();
                }
            }
        };

        TextChangedListener walletAddressListener = new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                updateWalletAddress(position, s.toString());
                if (mAddressChangedListener != null) {
                    mAddressChangedListener.onAddressChanged();
                }
            }
        };

        View.OnFocusChangeListener onWalletAddressFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String text = holder.etWalletAddress.getText().toString();
                    String errorMsg = "";
                    if (TextUtils.isEmpty(text)) {
                        errorMsg = context.getString(R.string.address_cannot_be_empty);
                    } else if (!JZWalletUtil.isValidAddress(text)) {
                        errorMsg = context.getString(R.string.address_format_error);
                    }
                    notifyWalletAddressError(holder, errorMsg);
                }
            }
        };

//        View.OnFocusChangeListener onWalletNameFocusChangeListener = new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus){
//                    String text = ((EditText) v).getText().toString();
//                    String errorMsg = "";
//                    if (TextUtils.isEmpty(text)) {
//                        errorMsg = context.getString(R.string.address_cannot_be_empty);
//                    } else if (!JZWalletUtil.isValidAddress(text)) {
//                        errorMsg = context.getString(R.string.address_format_error);
//                    }
//                    notifyWalletAddressError(holder, errorMsg);
//                }
//            }
//        };

        if (position == 0){
            holder.etWalletName.setEnabled(false);
            holder.etWalletAddress.setEnabled(false);
            holder.ivAddressBook.setEnabled(false);
            holder.ivScan.setEnabled(false);
        }else {
            holder.etWalletName.setEnabled(true);
            holder.etWalletAddress.setEnabled(true);
            holder.ivAddressBook.setEnabled(true);
            holder.ivScan.setEnabled(true);
        }

        holder.tvWalletAddressInfo.setText(context.getString(R.string.member, String.valueOf(position + 1)));
        holder.etWalletName.addTextChangedListener(walletNameListener);
//        holder.etWalletName.setOnFocusChangeListener(onWalletAddressFocusChangeListener);
        holder.etWalletName.setTag(walletNameListener);
        holder.etWalletName.setText(addressEntity.getName());
        holder.etWalletAddress.addTextChangedListener(walletAddressListener);
        holder.etWalletAddress.setOnFocusChangeListener(onWalletAddressFocusChangeListener);
        holder.etWalletAddress.setTag(walletAddressListener);
//        holder.etWalletAddress.setText(AddressFormatUtil.formatAddress(addressEntity.getPrefixAddress()));
        holder.etWalletAddress.setText(addressEntity.getAddress());
    }

    @Override
    public int getItemCount() {
        if (addressEntityList != null) {
            return addressEntityList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_wallet_address_info)
        TextView tvWalletAddressInfo;
        @BindView(R.id.et_wallet_name)
        EditText etWalletName;
        @BindView(R.id.et_wallet_address)
        EditText etWalletAddress;
        @BindView(R.id.iv_scan)
        ImageView ivScan;
        @BindView(R.id.iv_address_book)
        ImageView ivAddressBook;
        @BindView(R.id.tv_address_error)
        TextView tvAddressError;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void notifyDataChanged(List<OwnerEntity> addressEntityList) {
        this.addressEntityList = addressEntityList;
        notifyDataSetChanged();
    }

    public List<OwnerEntity> getDatas() {
        return addressEntityList;
    }


    public interface OnSelectAddressClickListener {

        void onSelectAddressClick(int position);
    }

    public interface OnScanQRCodeClickListener {

        void onScanQRCodeClick(int position);
    }

    public interface OnAddressChangedListener {

        void onAddressChanged();
    }
}
