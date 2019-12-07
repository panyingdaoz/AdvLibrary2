package com.kingbird.library.jsonbean;

import com.google.gson.Gson;

/**
 * 百度参数类
 *
 * @author Administrator
 * @date 2018/12/15/015.
 */
public class BaiDuAdvParam {

    /**
     * success : true
     * advInfo : {"id":2,"requestId":"10f447f5324a44e7b64bb1f36ab5c87a","adSlotId":"JUieKRwdK","searchKey":"a4de15d3d92e3fb9","expirationDate":"2018-12-15T10:06:25","adKey":"PWTsnHmkPjmsPjfb","adType":"IMAGE","videoUrl":null,"imageUrl":["http://jpaccess.baidu.com/material/jitouchuanmei_20181130_1543547834.jpg"],"mediaWidth":1080,"mediaHeight":1920,"videoDuration":0,"mediaSize":[0],"mediaMd5":["377f28ab3c831ce6bfc6341c18b19281"],"winNoticeUrl":["http://jpaccess.baidu.com/win_third?app_id=jitouchuanmei&adslot_id=20181130&type=win&search_key=win_a4de15d3d92e3fb9"],"thirdMonitorUrl":[]}
     */

    private boolean success;
    private int errorCode;
    private AdvInfoBean advInfo;

    public static BaiDuAdvParam objectFromData(String str) {

        return new Gson().fromJson(str, BaiDuAdvParam.class);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public AdvInfoBean getAdvInfo() {
        return advInfo;
    }

    public void setAdvInfo(AdvInfoBean advInfo) {
        this.advInfo = advInfo;
    }

    @Override
    public String toString() {
        return "BaiDuAdvParam{" +
                "success=" + success +
                ", errorCode=" + errorCode +
                ", advInfo=" + advInfo +
                '}';
    }
}
