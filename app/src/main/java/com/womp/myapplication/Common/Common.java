package com.womp.myapplication.Common;

import com.womp.myapplication.Remote.IMyAPI;
import com.womp.myapplication.Remote.RetrofitClient;

public class Common {
    public static final String BASE_URL = "http://172.16.34.238/frota/controls/";

    public static IMyAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IMyAPI.class);
    }
}
