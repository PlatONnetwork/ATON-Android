package com.platon.wallet.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziv
 * date On 2019/4/6
 */
public class CommonTextUtils {

    private CommonTextUtils() {
        throw new RuntimeException("can't be instanced");
    }

    /**
     * TextView中文字通过SpannableString来设置文本（下横线,点击等）属性
     *
     * @param tv     textView控件
     * @param str    原文本
     * @param regExp 正则表达式
     * @returnType void
     */
    public static void richText(TextView tv, String str, String regExp, CharacterStyle... spans) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        if (!TextUtils.isEmpty(regExp)) {
            Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(str);
            int index = 0;
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                //Android4.0以上默认是淡绿色，低版本的是黄色。解决方法就是通过重新设置文字背景为透明色
                tv.setHighlightColor(tv.getResources().getColor(android.R.color.transparent));
                if (index < spans.length) {
                    style.setSpan(spans[index], start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                index++;
            }
            tv.setText(style);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tv.setText(str);
        }
    }

    /**
     * TextView中文字通过SpannableString来设置文本（下横线,点击等）属性
     *
     * @param tv         textView控件
     * @param str        原文本
     * @param regExpList 正则表达式
     * @returnType void
     */
    public static void richText(TextView tv, String str, List<String> regExpList, CharacterStyle... spans) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < regExpList.size(); i++) {
            String regExp = regExpList.get(i);
            if (!TextUtils.isEmpty(regExp)) {
                Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(str);
                int index = 0;
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    //Android4.0以上默认是淡绿色，低版本的是黄色。解决方法就是通过重新设置文字背景为透明色
                    tv.setHighlightColor(tv.getResources().getColor(android.R.color.transparent));
                    if (index < spans.length) {
                        style.setSpan(spans[index], start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    index++;
                }
            }
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(style);
    }

    public static void setHintTextSize(TextView editText, int size) {
        // 新建一个可以添加文本的对象
        SpannableString ss = new SpannableString(editText.getHint());
        // 设置文本字体大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, false);
        // 将字体大小附加到文本的属性
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint属性
        editText.setHint(new SpannedString(ss));//转码
    }

    /**
     * 处理价钱为粗体
     *
     * @param text
     * @param smallTextSize
     * @param bigTextSize
     * @return
     */
    public static SpannableString getPriceText(String text, float smallTextSize, float bigTextSize) {
        SpannableString spannableString = new SpannableString(text);
        if (TextUtils.isEmpty(text)) {
            return spannableString;
        }

        int length = text.length();
        int index = text.indexOf("¥") == -1 ? length : text.indexOf("¥");
        spannableString.setSpan(new AbsoluteSizeSpan((int) smallTextSize, false), index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan((int) bigTextSize, false), index + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 处理价钱为粗体
     *
     * @param text
     * @param smallTextSize
     * @param bigTextSize
     * @return
     */
    public static SpannableString getPriceTextWithBold(String text, int smallTextColor, int bigTextColor, float smallTextSize, float bigTextSize) {
        SpannableString spannableString = new SpannableString(text);
        if (TextUtils.isEmpty(text)) {
            return spannableString;
        }

        int length = text.length();
        int index = text.contains(".") ? text.indexOf(".") : length;
        spannableString.setSpan(new AbsoluteSizeSpan((int) bigTextSize, false), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan((int) smallTextSize, false), index + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(bigTextColor), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(smallTextColor), index + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), index + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public static boolean isBankCard(String bankCard) {
        if (!TextUtils.isEmpty(bankCard)) {
            String nonCheckCodeCardId = bankCard.substring(0, bankCard.length() - 1);
            if (nonCheckCodeCardId.matches("\\d+")) {
                char[] chs = nonCheckCodeCardId.toCharArray();
                int luhmSum = 0;
                for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
                    int k = chs[i] - '0';
                    if (j % 2 == 0) {
                        k *= 2;
                        k = k / 10 + k % 10;
                    }
                    luhmSum += k;
                }
                char b = (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
                return bankCard.charAt(bankCard.length() - 1) == b;
            }
        }
        return false;
    }


    /**
     * @param url 接口地址(无参数)
     * @param map 拼接参数集合
     * @Description get请求URL拼接参数
     */
    public static String getAppendUrl(String url, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (TextUtils.isEmpty(buffer.toString())) {
                    buffer.append("?");
                } else {
                    buffer.append("&");
                }
                buffer.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url += buffer.toString();
        }
        return url;
    }


}
