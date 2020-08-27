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
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletDepth;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.netlistener.NetworkType;
import com.platon.aton.netlistener.NetworkUtil;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.framework.utils.RUtils;

import java.util.ArrayList;
import java.util.List;

public class SidebarWalletListAdapter extends RecyclerView.Adapter<SidebarWalletListAdapter.ViewHolder> {
    private OnSelectClickListener mClickListener;
    private ArrayList<Wallet> mWalletList;
    private Context mContext;
    private List<Wallet> mWalletListHD;

    public SidebarWalletListAdapter(ArrayList<Wallet> walletList, Context context, List<Wallet> walletListHD) {
        mWalletList = walletList;
        this.mContext = context;
        this.mWalletListHD = walletListHD;

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


    private int walletIndex = 0;
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Wallet item = mWalletList.get(position);
        Wallet previousItem = position - 1 < 0 ? Wallet.getNullInstance() : mWalletList.get(position - 1);
        viewHolder.tvwalletName.setText(item.getName());
        viewHolder.tvWalletAddress.setText(AddressFormatUtil.formatAddress(item.getPrefixAddress()));

        if(!item.isHD()){//普通钱包
            viewHolder.rlItem.setBackgroundResource(R.color.color_f9fbff);
        }else if(item.isHD() && previousItem.isHD() &&
                 item.getDepth() == WalletDepth.DEPTH_ONE &&
                 previousItem.getDepth() == WalletDepth.DEPTH_ONE){//上一个钱包与下一个钱包都为子钱包

                 for (int i = 0; i < mWalletListHD.size(); i++) {
                      Wallet walletHD = mWalletListHD.get(i);
                      if(walletHD.getUuid().equals(item.getParentId())){
                          //由于数据源已经进行了排序，所以遍历时，同一组子钱包的walletIndex是相同的
                          walletIndex = i;
                          break;
                      }
                 }

                 if(walletIndex % 2 == 0){
                     viewHolder.rlItem.setBackgroundResource(R.color.color_eff4fd);
                 }else{
                     viewHolder.rlItem.setBackgroundResource(R.color.color_f9fbff);
                 }

        }else if(item.isHD() && !previousItem.isHD() &&
                item.getDepth() == WalletDepth.DEPTH_ONE &&
                previousItem.getDepth() == WalletDepth.DEPTH_ZERO){//上一个为普通钱包与下一个钱包都为子钱包

                   viewHolder.rlItem.setBackgroundResource(R.color.color_eff4fd);
        }

        //是否选中钱包
        if(fromType != FROMTYPE_DELEGATE && item.getSelectedIndex() == WalletSelectedIndex.SELECTED){
            viewHolder.linearViewItem.setBackgroundResource(R.drawable.bg_item_sidebar_wallet);
        }else{
            viewHolder.linearViewItem.setBackgroundResource(R.color.color_ffffff);
        }

        viewHolder.linearViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position);
                }
            }
        });
    }


    public void setHDWalletListDataSource(List<Wallet> walletListHD){
        this.mWalletListHD = walletListHD;
    }


    public interface OnSelectClickListener {
        void onItemClick(int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rlItem;
        LinearLayout linearViewItem;
        TextView tvwalletName;
        TextView tvWalletAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            rlItem = itemView.findViewById(R.id.rl_item);
            linearViewItem = itemView.findViewById(R.id.linear_view_item);
            tvwalletName = (TextView) itemView.findViewById(R.id.tv_wallet_name);
            tvWalletAddress = (TextView) itemView.findViewById(R.id.tv_wallet_address);
        }

    }
}
