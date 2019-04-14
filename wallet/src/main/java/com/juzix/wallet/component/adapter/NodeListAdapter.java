package com.juzix.wallet.component.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.CustomEditText;
import com.juzix.wallet.component.widget.TextChangedListener;
import com.juzix.wallet.entity.NodeEntity;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class NodeListAdapter extends RecyclerView.Adapter<NodeListAdapter.ViewHolder> {

    private final static String TAG = NodeListAdapter.class.getSimpleName();

    private BaseActivity activity;

    private Map<String, String> map = new HashMap<>();
    private List<NodeEntity> mNodeList;
    private boolean mIsEdit;
    private OnItemRemovedListener mRemovedListener;
    private OnItemCheckedListener mCheckedListener;

    public void setOnItemRemovedListener(OnItemRemovedListener mListener) {
        this.mRemovedListener = mListener;
    }

    public void setOnItemCheckedListener(OnItemCheckedListener mListener) {
        this.mCheckedListener = mListener;
    }

    public NodeListAdapter(BaseActivity activity, List<NodeEntity> nodeList) {
        this.activity = activity;
        this.mNodeList = nodeList;
    }

    public List<NodeEntity> getNodeList() {
        return mNodeList;
    }

    public void notifyDataChanged(List<NodeEntity> nodeEntityList) {
        this.mNodeList = nodeEntityList;
        notifyDataSetChanged();
    }

    public void setEditable(boolean isEdit) {
        this.mIsEdit = isEdit;
        notifyDataSetChanged();
    }

    public String getNodeAddress(String id) {
        return map.get(id);
    }

    public void addNode() {
        if (mNodeList == null) {
            mNodeList = new ArrayList<>();
        }

        mNodeList.add(mNodeList.size(), NodeEntity.createNullNode());

        notifyItemInserted(mNodeList.size() - 1);
    }

    public void removeNodeList(List<NodeEntity> nodeEntityList) {

        if (mNodeList == null || mNodeList.isEmpty()) {
            return;
        }

        for (NodeEntity nodeEntity : nodeEntityList) {
            notifyItemRemoved(mNodeList.indexOf(nodeEntity));
            map.remove(nodeEntity.getUuid());
        }

        mNodeList.removeAll(nodeEntityList);
    }

    public void removeNode(String uuid) {

        if (mNodeList == null || mNodeList.isEmpty()) {
            return;
        }

        NodeEntity tempNodeEntity = new NodeEntity.Builder()
                .uuid(uuid)
                .build();

        if (mNodeList.contains(tempNodeEntity)) {
            int position = mNodeList.indexOf(tempNodeEntity);
            if (mNodeList.remove(position) != null) {
                map.remove(tempNodeEntity.getUuid());
                notifyItemRemoved(position);

                if (mRemovedListener != null) {
                    mRemovedListener.onItemRemoved(tempNodeEntity);
                }
            }
        }
    }

    public void updateNodeList(List<NodeEntity> nodeEntityList) {

        if (mNodeList == null || mNodeList.isEmpty()) {
            return;
        }

        for (NodeEntity entity : nodeEntityList) {
            int position = mNodeList.indexOf(entity);
            if (position != -1) {
                mNodeList.set(position, entity);
            }
        }

        notifyDataSetChanged();
    }

    public void setChecked(int position) {

        if (mNodeList == null || mNodeList.size() <= position) {
            return;
        }

        for (int i = 0; i < mNodeList.size(); i++) {
            NodeEntity node = mNodeList.get(i);
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

        if (holder.etNode.getTag() instanceof TextChangedListener) {
            holder.etNode.removeTextChangedListener((TextWatcher) holder.etNode.getTag());
        }

        NodeEntity nodeEntity = mNodeList.get(position);
        holder.etNode.setEnabled(mIsEdit && !nodeEntity.isDefaultNode());
        if (holder.etNode.isEnabled() && position == mNodeList.size() - 1) {
            showSoftInput(holder.etNode, true);
        } else {
            showSoftInput(holder.etNode, false);
        }

        holder.etNode.setError(nodeEntity.isFormatCorrect() ? holder.nodeFormatError : null);

        holder.ivDel.setVisibility(mIsEdit && !nodeEntity.isDefaultNode() ? View.VISIBLE : View.GONE);

        holder.ivSelected.setVisibility(!mIsEdit && nodeEntity.isChecked() ? View.VISIBLE : View.GONE);

        TextChangedListener textChangedListener = new TextChangedListener() {
            @Override
            protected void onTextChanged(CharSequence s) {
                map.put(nodeEntity.getUuid(), s.toString());
            }
        };

        holder.etNode.addTextChangedListener(textChangedListener);
        holder.etNode.setTag(textChangedListener);
        String nodeAddress = TextUtils.isEmpty(nodeEntity.getNodeAddress()) ? map.get(nodeEntity.getUuid()) : nodeEntity.getNodeAddress();
        boolean isDefaultMainNetwork = nodeEntity.isDefaultNode() && nodeEntity.isMainNetworkNode();
        boolean isDefaultTestNetwork = nodeEntity.isDefaultNode() && !nodeEntity.isMainNetworkNode();
        if (isDefaultMainNetwork) {
            holder.etNode.setText(String.format("%s(%s)", nodeAddress, activity.getString(R.string.default_main_network)));
        } else if (isDefaultTestNetwork) {
            holder.etNode.setText(String.format("%s(%s)", nodeAddress, activity.getString(R.string.default_test_network)));
        } else {
            holder.etNode.setText(nodeAddress);
        }
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNode(nodeEntity.getUuid());
                if (nodeEntity.isChecked()) {
                    setChecked(0);
                    return;
                }
                if (mNodeList.size() == 1) {
                    showSoftInput(holder.etNode, false);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEdit) {
                    return;
                }
                if (nodeEntity.isChecked()) {
                    return;
                }
                checkAddress(position, nodeEntity.getNodeAddress());
            }
        });

    }

    private void checkAddress(int position, String address) {

        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return CommonUtil.validUrl(address);
            }
        })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(activity))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isValid) throws Exception {
                        if (isValid) {
                            setChecked(position);
                        }
                        ToastUtil.showShortToast(activity, isValid ? R.string.switch_node_successed : R.string.switch_node_failed);
                    }
                });
    }

    private void showSoftInput(EditText editText, boolean isShow) {
        if (isShow) {
            activity.showSoftInput(editText);
        } else {
            activity.hideSoftInput(activity, editText);
        }
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setSelection(editText.getText().toString().length());
            }
        }, 150);
    }

    @Override
    public int getItemCount() {
        if (mNodeList != null) {
            return mNodeList.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_del)
        ImageView ivDel;
        @BindView(R.id.et_node)
        CustomEditText etNode;
        @BindView(R.id.iv_selected)
        ImageView ivSelected;
        @BindString(R.string.node_format_error)
        String nodeFormatError;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(NodeEntity nodeEntity);
    }

    public interface OnItemCheckedListener {
        void onItemChecked(NodeEntity nodeEntity, boolean isChecked);
    }
}
