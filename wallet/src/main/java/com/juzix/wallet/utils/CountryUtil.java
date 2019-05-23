package com.juzix.wallet.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juzix.wallet.entity.Country;

import java.util.List;

public class CountryUtil {

    private CountryUtil() {

    }

    public static List<Country> getCountryList(Context context) {
        String json = FileUtil.getAssets(context, "country.json");
        JSONObject object = (JSONObject) JSON.parse(json);
        return JSONObject.parseArray(((JSONArray) object.get("countrys")).toJSONString(), Country.class);
    }
}
