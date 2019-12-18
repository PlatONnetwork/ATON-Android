package com.juzix.wallet.component.adapter;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.netlistener.NetworkType;
import com.juzix.wallet.netlistener.NetworkUtil;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;

public class WalletManagerAdapter extends RecyclerView.Adapter<WalletManagerAdapter.ViewHolder> {
    private OnBackupClickListener mClickListener;
    private ArrayList<Wallet> mWalletList;
    private Context mContext;

    public WalletManagerAdapter(ArrayList<Wallet> walletList, Context context) {
        mWalletList = walletList;
        this.mContext = context;
    }

    public void setOnBackupClickListener(OnBackupClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_manager_list, parent, false);
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
        int resId = RUtils.drawable(item.getAvatar());
        if (resId < 0) {
            resId = R.drawable.avatar_15;
        }
        viewHolder.ivWalletAvatar.setImageResource(resId);
        viewHolder.tvwalletName.setText(item.getName());
        viewHolder.tvWalletAddress.setText(AddressFormatUtil.formatAddress(item.getPrefixAddress()));
        viewHolder.tvWalletBackup.setVisibility(item.isBackedUp() ? View.GONE : View.VISIBLE);

        if (TextUtils.isEmpty(item.getKey())) {//观察钱包
            viewHolder.tv_wallet_logo.setVisibility(View.VISIBLE);
            viewHolder.tv_wallet_logo.setText(mContext.getString(R.string.observed_wallet));
        } else {
            if (NetworkUtil.getNetWorkType(mContext) == NetworkType.NETWORK_NO) { //没网，冷钱包
                viewHolder.tv_wallet_logo.setVisibility(View.VISIBLE);
                viewHolder.tv_wallet_logo.setText(mContext.getString(R.string.cold_wallet));
            } else {
                viewHolder.tv_wallet_logo.setVisibility(View.GONE);
            }
        }

        viewHolder.tvWalletBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onBackupClick(position);
                }
            }
        });
        viewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position);
                }
            }
        });
    }

    public abstract static class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private GestureDetectorCompat mGestureDetector;
        private RecyclerView recyclerView;

        public OnRecyclerItemClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    onItemClick(vh);
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                    onItemLongClick(vh);
                }
            }
        }

        public abstract void onItemClick(RecyclerView.ViewHolder vh);

        public abstract void onItemLongClick(RecyclerView.ViewHolder vh);
    }

    public interface OnBackupClickListener {
        void onBackupClick(int position);

        void onItemClick(int position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlItem;
        FrameLayout flAvatar;
        ImageView ivWalletAvatar;
        TextView tvwalletName;
        TextView tvWalletBackup;
        TextView tvWalletAddress;
        TextView tv_wallet_logo;

        public ViewHolder(View itemView) {
            super(itemView);
            rlItem = itemView.findViewById(R.id.rl_item);
            flAvatar = itemView.findViewById(R.id.fl_avatar);
            ivWalletAvatar = itemView.findViewById(R.id.iv_wallet_avatar);
            tvwalletName = (TextView) itemView.findViewById(R.id.tv_wallet_name);
            tvWalletBackup = (TextView) itemView.findViewById(R.id.tv_wallet_backup);
            tvWalletAddress = (TextView) itemView.findViewById(R.id.tv_wallet_address);
            tv_wallet_logo = itemView.findViewById(R.id.tv_wallet_logo);
        }

    }
}
