package com.platon.aton.entity;

import com.platon.aton.R;

import java.util.HashMap;
import java.util.Map;


public enum NodeType {

    NOMINEES {
        @Override
        public int getStatusDescRes() {
            return R.string.nominees;
        }
    }, CANDIDATES {
        @Override
        public int getStatusDescRes() {
            return R.string.candidates;
        }
    }, VALIDATOR {
        @Override
        public int getStatusDescRes() {
            return R.string.validator;
        }
    };

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    private static Map<String, NodeType> map = new HashMap<>();

    static {
        for (NodeType type : values()) {
            map.put(type.toString(), type);
        }
    }

    public static NodeType getNodeTypeByName(String name) {
        return map.get(name);
    }

    public abstract int getStatusDescRes();
}
