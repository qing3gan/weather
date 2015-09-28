package com.example.agony.weather.util;

import android.test.InstrumentationTestCase;
import android.util.Log;

/**
 * Created by Agony on 2015/9/28 0028.
 */
public class HttpUtilTest extends InstrumentationTestCase {

    public static final String TAG = "HttpUtilTest";

    public void testSendHttpRequest() throws Exception {
        String address = "http://www.weather.com.cn/data/list3/city.xml";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, response);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }
}
