package com.platon.wallet.component.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.platon.wallet.BuildConfig;
import com.platon.wallet.R;
import com.platon.wallet.app.LoadingTransformer;
import com.platon.wallet.component.ui.base.BaseActivity;
import com.platon.wallet.entity.Node;
import com.platon.wallet.utils.RxUtils;
import com.platon.wallet.utils.ToastUtil;

import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonBlock;
import org.web3j.protocol.http.HttpService;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class NodeListAdapter extends RecyclerView.Adapter<NodeListAdapter.ViewHolder> {

    private BaseActivity activity;

    private List<Node> mNodeList;
    private OnItemCheckedListener mCheckedListener;


    public void setOnItemCheckedListener(OnItemCheckedListener mListener) {
        this.mCheckedListener = mListener;
    }

    public NodeListAdapter(BaseActivity activity, List<Node> nodeList) {
        this.activity = activity;
        this.mNodeList = nodeList;
    }

    public void notifyDataChanged(List<Node> nodeEntityList) {
        this.mNodeList = nodeEntityList;
        notifyDataSetChanged();
    }

    public void setChecked(int position) {

        if (mNodeList == null || mNodeList.size() <= position) {
            return;
        }

        for (int i = 0; i < mNodeList.size(); i++) {
            Node node = mNodeList.get(i);
            if (position == i) {
                if (!node.isChecked()) {
                    node.setChecked(true);
                    notifyItemChanged(i);
                    if (mCheckedListener != null) {
                        mCheckedListener.onItemChecked(node, node.isChecked());
                    }
                }
            } else {
                if (node.isChecked()) {
                    node.setChecked(false);
                    notifyItemChanged(i);
                    if (mCheckedListener != null) {
                        mCheckedListener.onItemChecked(node, node.isChecked());
                    }
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_node_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        Node nodeEntity = mNodeList.get(position);

        holder.ivSelected.setVisibility(nodeEntity.isChecked() ? View.VISIBLE : View.GONE);
        String nodeName = getNodeName(nodeEntity);
        holder.tvNodeName.setVisibility(TextUtils.isEmpty(nodeName) ? View.GONE : View.VISIBLE);
        holder.tvNodeName.setText(nodeName);
        holder.tvNodeInfo.setText(getNodeInfo(nodeEntity));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nodeEntity.isChecked()) {
                    return;
                }
                checkAddress(position, nodeEntity.getRPCUrl());
            }
        });

    }

    private String getNodeInfo(Node node) {
        return String.format("%s(chainId:%s)", node.getRPCUrl(), node.getChainId());
    }

    private String getNodeName(Node node) {
        if (BuildConfig.URL_MAIN_SERVER.equals(node.getNodeAddress())) {
            return activity.getString(R.string.newbaleyworld);
        } else if (BuildConfig.URL_TEST_MAIN_SERVER.equals(node.getNodeAddress())) {
            return activity.getString(R.string.uat_net);
        } else if (TextUtils.equals(BuildConfig.URL_TEST_SERVER, node.getNodeAddress()) || TextUtils.equals(BuildConfig.URL_TEST_OUTER_SERVER, node.getNodeAddress())) {
            return activity.getString(R.string.test_net);
        } else if (TextUtils.equals(BuildConfig.URL_DEVELOP_SERVER, node.getNodeAddress())) {
            return activity.getString(R.string.develop_net);
        }
        return "";
    }

    private void checkAddress(int position, String address) {

        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                PlatonBlock platonBlock = Web3jFactory.build(new HttpService(address)).platonGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
                return platonBlock != null && platonBlock.getBlock() != null && platonBlock.getBlock().getNumber().longValue() > 0;
            }
        })
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(activity))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isValid) throws Exception {
                        if (isValid) {
                            setChecked(position);
                        }
                        ToastUtil.showShortToast(activity, isValid ? R.string.switch_node_successed : R.string.switch_node_failed);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showShortToast(activity, R.string.switch_node_failed);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mNodeList != null) {
            return mNodeList.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_node_name)
        TextView tvNodeName;
        @BindView(R.id.iv_selected)
        ImageView ivSelected;
        @BindView(R.id.tv_node_info)
        TextView tvNodeInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(Node nodeEntity);
    }

    public interface OnItemCheckedListener {
        void onItemChecked(Node nodeEntity, boolean isChecked);
    }
}
