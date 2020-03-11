package com.platon.aton.component.widget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author ziv
 * date On 2020-02-24
 */
public class MyWatcher implements TextWatcher {

    private int beforeDot = -1;
    private int afterDot = 8;

    public MyWatcher() {
    }

    /**
     * 构造器
     *
     * @param beforeDot 小数点前位数  不限制输入-1
     * @param afterDot  小数点后位数  不限制输入-1
     */
    public MyWatcher(int beforeDot, int afterDot) {
        this.beforeDot = beforeDot;
        this.afterDot = afterDot;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        judge(s);
    }

    private void judge(Editable editable) {
        String temp = editable.toString();
        int posDot = temp.indexOf(".");
        //直接输入小数点的情况
        if (posDot == 0) {
            editable.insert(0, "0");
            return;
        }
        //连续输入0
        if (temp.equals("00")) {
            editable.delete(1, 2);
            return;
        }
        //输入"08" 等类似情况
        if (temp.startsWith("0") && temp.length() > 1 && (posDot == -1 || posDot > 1)) {
            editable.delete(0, 1);
            return;
        }

        //不包含小数点 不限制小数点前位数
        if (posDot < 0 && beforeDot == -1) {
            //do nothing 仅仅为了理解逻辑而已
            return;
        } else if (posDot < 0 && beforeDot != -1) {
            //不包含小数点 限制小数点前位数
            if (temp.length() <= beforeDot) {
                //do nothing 仅仅为了理解逻辑而已
            } else {
                editable.delete(beforeDot, beforeDot + 1);
            }
            return;
        }

        //如果包含小数点 限制小数点后位数
        if (temp.length() - posDot - 1 > afterDot && afterDot != -1) {
            editable.delete(posDot + afterDot + 1, posDot + afterDot + 2);//删除小数点后多余位数
        }
    }
}
