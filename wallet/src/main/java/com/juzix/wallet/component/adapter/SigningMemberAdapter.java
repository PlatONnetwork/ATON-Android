package com.juzix.wallet.component.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.entity.TransactionResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author matrixelement
 */
public class SigningMemberAdapter extends RecyclerView.Adapter<SigningMemberAdapter.ViewHolder> {

    private List<TransactionResult> transactionResultList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_member, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TransactionResult result = transactionResultList.get(position);
        TransactionResult.Status status = result.getStatus();

        holder.tvName.setText(result.getName());
        holder.progressBar.setVisibility(status == TransactionResult.Status.OPERATION_SIGNING ? View.VISIBLE : View.GONE);
        holder.ivHook.setVisibility(status == TransactionResult.Status.OPERATION_APPROVAL || status == TransactionResult.Status.OPERATION_REVOKE ? View.VISIBLE : View.GONE);
        holder.ivHook.setImageResource(status == TransactionResult.Status.OPERATION_APPROVAL ? R.drawable.icon_hook_m : R.drawable.icon_fork_m);
    }

    @Override
    public int getItemCount() {
        if (transactionResultList != null) {
            return transactionResultList.size();
        }
        return 0;
    }

    public void notifyItemChanged(String address, TransactionResult.Status status) {

        if (transactionResultList != null && !transactionResultList.isEmpty()) {
            TransactionResult tempResult = null;
            for (TransactionResult result : transactionResultList) {

                String tempAddress = address.startsWith("0x") ? address : "0x" + address;

                if (result.getPrefixAddress().equals(tempAddress)) {
                    tempResult = result;
                    break;
                }
            }

            int index = transactionResultList.indexOf(tempResult);
            TransactionResult result = transactionResultList.get(index);
            result.setStatus(status);
            transactionResultList.set(index, result);

            notifyItemChanged(index);
        }

    }

    public void notifyDataSetChanged(List<TransactionResult> transactionResults) {
        this.transactionResultList = transactionResults;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.iv_hook)
        ImageView ivHook;
        @BindView(R.id.tv_name)
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
