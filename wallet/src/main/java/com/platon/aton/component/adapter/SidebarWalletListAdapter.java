package com.platon.aton.component.adapter;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.netlistener.NetworkType;
import com.platon.aton.netlistener.NetworkUtil;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.framework.utils.RUtils;

import java.util.ArrayList;

public class SidebarWalletListAdapter extends RecyclerView.Adapter<SidebarWalletListAdapter.ViewHolder> {
    private OnSelectClickListener mClickListener;
    private ArrayList<Wallet> mWalletList;
    private Context mContext;

    public SidebarWalletListAdapter(ArrayList<Wallet> walletList, Context context) {
        mWalletList = walletList;
        this.mContext = context;
    }

    public void setOnSelectClickListener(OnSelectClickListener listener) {
        mClickListener = listener;
    }

    private int fromType = 0;
    public static int  FROMTYPE_MAIN = 1;
    public static int  FROMTYPE_DELEGATE = 2;

    public void setFromType(int fromType){
        this.fromType = fromType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sidebar_wallet_list, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mWalletList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Wallet item = mWalletList.get(position);
        viewHolder.tvwalletName.setText(item.getName());
        viewHolder.tvWalletAddress.setText(AddressFormatUtil.formatAddress(item.getPrefixAddress()));
        if(fromType != FROMTYPE_DELEGATE && item.getSelectedIndex() == WalletSelectedIndex.SELECTED){
            viewHolder.rlItem.setBackgroundResource(R.drawable.bg_item_sidebar_wallet);
        }else{
            viewHolder.rlItem.setBackgroundResource(R.color.color_ffffff);
        }

        viewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position);
                }
            }
        });
    }


    public interface OnSelectClickListener {
        void onItemClick(int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rlItem;
        TextView tvwalletName;
        TextView tvWalletAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            rlItem = itemView.findViewById(R.id.rl_item);
            tvwalletName = (TextView) itemView.findViewById(R.id.tv_wallet_name);
            tvWalletAddress = (TextView) itemView.findViewById(R.id.tv_wallet_address);
        }

    }
}
