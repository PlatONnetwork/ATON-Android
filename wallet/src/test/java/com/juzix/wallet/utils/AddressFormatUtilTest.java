package com.juzix.wallet.utils;

import com.juzhen.framework.app.log.Log;
import com.juzix.wallet.BaseTestCase;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddressFormatUtilTest extends BaseTestCase {

    @Test
    public void formatAddress() {
        String address ="0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text =null;
        if (address != null) {
            String regex = "(\\w{10})(\\w*)(\\w{10})";
            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.debug("格式化后的地址","=============" + text);
    }

    @Test
    public void formatTransactionAddress() {
        String address ="0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text =null;
        if (address != null) {

            String regex = "(\\w{4})(\\w*)(\\w{4})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.debug("格式化后的地址","=============" + text);

    }
}