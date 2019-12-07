package com.kingbird.library.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/**
 * wifi监听
 *
 * @author Pan yingdao
 * @date 2019/4/29/029.
 */
public class WifiSwitchPresenter {

    private static String ACTION_SERVICE_STATE_CHANGE = "ACTION_SERVICE_STATE_CHANGE";
    private Context mContext;
    private Receiver receiver;
    private WifiSwitchInterface mInterface;


    public WifiSwitchPresenter(Context context, WifiSwitchInterface mInterface) {
        this.mContext = context;
        this.mInterface = mInterface;

        observeWifiSwitch();
    }

    private void observeWifiSwitch() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ACTION_SERVICE_STATE_CHANGE);
        receiver = new Receiver();
        mContext.registerReceiver(receiver, filter);
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        try {
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
            if (mContext != null) {
                mContext = null;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Plog.e("广播", intent.getAction());
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    if (mInterface != null) {
                        mInterface.wifiSwitchState(WifiSwitchInterface.WIFI_STATE_DISABLED);
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    if (mInterface != null) {
                        mInterface.wifiSwitchState(WifiSwitchInterface.WIFI_STATE_DISABLING);
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    if (mInterface != null) {
                        mInterface.wifiSwitchState(WifiSwitchInterface.WIFI_STATE_ENABLED);
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    if (mInterface != null) {
                        mInterface.wifiSwitchState(WifiSwitchInterface.WIFI_STATE_ENABLING);
                    }
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    if (mInterface != null) {
                        mInterface.wifiSwitchState(WifiSwitchInterface.WIFI_STATE_UNKNOWN);
                    }
                    break;
                default:
            }
            if (ACTION_SERVICE_STATE_CHANGE.equals(intent.getAction())){
                if (mInterface != null) {
                    mInterface.wifiSwitchState(WifiSwitchInterface.ACTION_SERVICE_STATE_CHANGE);
                }
            }
        }
    }
}
