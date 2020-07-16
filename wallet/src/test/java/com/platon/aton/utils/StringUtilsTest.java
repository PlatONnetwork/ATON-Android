package com.platon.aton.utils;

import android.app.Application;
import android.util.Log;

import com.platon.aton.BuildConfig;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class StringUtilsTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    public void setup() {
        Application app = RuntimeEnvironment.application;
        ApiResponse.init(app);
        PreferenceTool.init(app);
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        //输出日志
        ShadowLog.stream = System.out;

    }

    @Test
    public void subString() throws UnsupportedEncodingException {
        String str = "afdsajfskldfjaksdjfkasdjfa";
        byte[] bytes = str.getBytes("Unicode");
        String newStr = null;
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < 6; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1) {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }
        newStr = new String(bytes, 0, i, "Unicode");
        Log.d("======", "得到结果result---------->" + newStr);
    }

    @Test
    public  void  toDBC(){
        String newStr = null;
        String str = "afdsajfskldfjaksdjfkasdjfa";
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }

       newStr  =new String(c);
        Log.d("======", "得到结果result---------->" + newStr);
    }


    @Test
    public  void split(){
        String str = "adfad-fasdfa-11454-afsdfa";
        String splitsign ="-";
        int index;
        if (str == null || splitsign == null)
            return ;

        ArrayList<String> al = new ArrayList<String>();
        while ((index = str.indexOf(splitsign)) != -1) {
            al.add(str.substring(0, index));
            str = str.substring(index + splitsign.length());
        }
        al.add(str);

        Log.d("======", "得到结果result---------->" + al.toString());

    }

}
