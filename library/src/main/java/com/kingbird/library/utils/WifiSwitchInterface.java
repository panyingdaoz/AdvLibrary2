package com.kingbird.library.utils;

/**
 * WiFi监听接口
 *
 * @author Pan yingdao
 * @date 2019/4/29/029.
 */
public interface WifiSwitchInterface {

    int WIFI_STATE_ENABLING = 0;
    int WIFI_STATE_ENABLED = 1;
    int WIFI_STATE_DISABLING = 2;
    int WIFI_STATE_DISABLED = 3;
    int WIFI_STATE_UNKNOWN = 4;
    int ACTION_SERVICE_STATE_CHANGE = 5;

    /**
     *  WIFI状态监听
     * @param state WiFi状态
     */
    void wifiSwitchState(int state);
}
