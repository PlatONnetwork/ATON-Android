package com.juzix.wallet.component.ui.popwindow;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.DelegatePopWindowAdapter;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.entity.DelegateType;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.platon.bean.RestrictingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class DelegatePopWindow extends PopupWindow {
    private Context mContext;//上下文
    private View parentView;//父视图
    private List<DelegateType> dataList;//item 数据源
    private ListView lv;
    private DelegatePopWindowAdapter mAdapter;//适配器
    private OnPopItemClickListener listener;
    private String walletAddress;
    private String balanceType;//余额类型
    private Map<String, Double> map = new HashMap<>();

    public void setListener(OnPopItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnPopItemClickListener {
        void onPopItemClick(View view, int positon, DelegateType bean);

    }


    public DelegatePopWindow(Context context, View view, String walletAddress, String balanceType) {
        this.mContext = context;
        this.walletAddress = walletAddress;
        this.balanceType = balanceType;
        initViews(view);
    }

    private void initViews(View view) {
        parentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_delegate, null);
        setContentView(parentView);
        setAnimationStyle(R.style.Animation_slide_in_bottom);
        lv = parentView.findViewById(R.id.listview_popwindow);
        //设置弹出窗体的宽和高
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置弹出窗体可点击
        this.setFocusable(true);

        //view添加OnTouchListener监听判断获取触屏位置如果在布局外面则销毁弹出框
        parentView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = parentView.findViewById(R.id.listview_popwindow).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        update();
        mAdapter = new DelegatePopWindowAdapter(R.layout.popwindow_delegate_item, null, lv);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DelegateType bean = mAdapter.getItem(position);
                mAdapter.notifyDataSetChanged();
                dismiss();
                if (null != listener) {
                    listener.onPopItemClick(view, position, bean);
                }
            }
        });

        showAsDropDown(view);

        loadWalletAmountType(walletAddress);
    }

    private void loadWalletAmountType(String walletAddress) {
        List<DelegateType> typeList = new ArrayList<>();

        //获取可用余额
        double usefulBalance = WalletManager.getInstance().getWalletAmountByAddress(walletAddress);
        map.put("balance", usefulBalance);
        //获取锁仓余额
        WalletManager.getInstance().getRestrictingInfo(walletAddress)
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<RestrictingItem>() {
                    @Override
                    public void accept(RestrictingItem restrictingItem) throws Exception {
                        if (restrictingItem.getBalance().doubleValue() > 0) {
                            map.put("locked", restrictingItem.getBalance().doubleValue());
                        }
                    }
                });

        //遍历map集合
        Set<Map.Entry<String, Double>> entries = map.entrySet();
        Iterator<Map.Entry<String, Double>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double> entry = it.next();
            typeList.add(new DelegateType(entry.getKey(), entry.getValue()));
        }

        mAdapter.notifyDataChanged(typeList);
        lv.setItemChecked((TextUtils.equals(balanceType, "balance") ? 0 : 1), true);

    }


}
