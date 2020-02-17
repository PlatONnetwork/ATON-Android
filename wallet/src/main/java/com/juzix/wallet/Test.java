package com.juzix.wallet;

import org.web3j.crypto.TransactionDecoder;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.util.List;

/**
 * @author ziv
 * date On 2020-02-13
 */
public class Test {

    public static void main(String[] args) {

        String hex = TransactionDecoder.decode("0xf8bd1e853a3529440082c10c94100000000000000000000000000000000000000280b857f855838203ec8180b842b840411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c8a890ccb286d9e5030000081eea07135ac91637a960acaf8410727321fec1d29e7b06f38d9ef80cd7f3a79de96e7a05ee4850f1638de44813433f66e0fa15d4f0321bb501655f540bf91f6afb1cc13").getData();

        decodeNodeId(hex);
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
