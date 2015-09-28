package com.example.agony.weather.util;

/**
 * Callback Listener
 * Created by Agony on 2015/9/27 0027.
 */
public interface HttpCallbackListener {

    /**
     * Http Success
     *
     * @param response
     */
    void onSuccess(String response);

    /**
     * Http Error
     *
     * @param e
     */
    void onError(Exception e);
}
