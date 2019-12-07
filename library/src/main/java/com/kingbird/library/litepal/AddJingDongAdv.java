package com.kingbird.library.litepal;

import java.io.Serializable;

/**
 * 说明：京东广告参数
 *
 * @author Pan Yingdao
 * @time : 2019/7/26/026
 */
public class AddJingDongAdv implements Serializable {

    private String requestId;
    private int adKey;
    private int showTime;
    private int height;
    private int width;
    private String title;
    private String md5;
    private String url;
    private String type;
    private String deviceId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getAdKey() {
        return adKey;
    }

    public void setAdKey(int adKey) {
        this.adKey = adKey;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
