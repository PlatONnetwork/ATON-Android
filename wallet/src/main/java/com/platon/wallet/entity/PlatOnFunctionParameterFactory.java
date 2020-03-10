package com.platon.wallet.entity;

import java.util.HashMap;
import java.util.Map;

public class PlatOnFunctionParameterFactory {

    private PlatOnFunctionParameterFactory() {

    }

    public static Map<String, Object> createDelegateParameter(int type, String nodeId, String nodeName, String sender, String amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("nodeId", nodeId);
        map.put("sender", sender);
        map.put("amount", amount);
        map.put("nodeName", nodeName);
        return map;
    }

    public static Map<String, Object> createUnDelegateParameter(int type, String nodeId, String nodeName, String sender, String amount, String stakingBlockNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("nodeId", nodeId);
        map.put("sender", sender);
        map.put("amount", amount);
        map.put("nodeName", nodeName);
        map.put("stakingBlockNum", stakingBlockNum);
        return map;
    }
}
