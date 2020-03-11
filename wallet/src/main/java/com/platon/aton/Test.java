package com.platon.aton;

import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziv
 * date On 2020-02-13
 */
public class Test {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();

        list.add("1");
        list.add("2");
        list.add("1");

        for (String s : list) {

            if ("2".equals(s)) {
                list.remove(s);
            }

            System.out.println(s);

        }
    }


    private static String decodeNodeId(String hex) {

        RlpList rlp = RlpDecoder.decode(Numeric.hexStringToByteArray(hex));

        List<RlpType> typeList = rlp.getValues();

        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        List<RlpType> rlpList = ((RlpList) (typeList.get(0))).getValues();

        if (rlpList == null || rlpList.size() < 3) {
            return null;
        }

        List<RlpType> rlpTypeList = RlpDecoder.decode(((RlpString) rlpList.get(2)).getBytes()).getValues();

        List<RlpType> rlpTypeList2 = RlpDecoder.decode(((RlpString) rlpList.get(3)).getBytes()).getValues();

        System.out.println(((RlpString) rlpTypeList.get(0)).asString());
        String contractAmount = Numeric.decodeQuantity(((RlpString) rlpTypeList2.get(0)).asString()).toString(10);
        System.out.println(contractAmount);

        if (rlpTypeList == null || rlpTypeList.isEmpty()) {
            return null;
        }

        return ((RlpString) rlpTypeList.get(0)).asString();
    }
}
