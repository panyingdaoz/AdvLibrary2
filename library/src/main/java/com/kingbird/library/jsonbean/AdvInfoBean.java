package com.kingbird.library.jsonbean;

import java.util.List;

/**
 * 类具体作用
 *
 * @author Administrator
 * @date 2018/12/15/015.
 */
public class AdvInfoBean {
    /**
     * id : 2
     * requestId : 10f447f5324a44e7b64bb1f36ab5c87a
     * adSlotId : JUieKRwdK
     * searchKey : a4de15d3d92e3fb9
     * expirationDate : 2018-12-15T10:06:25
     * adKey : PWTsnHmkPjmsPjfb
     * adType : IMAGE
     * videoUrl : null
     * imageUrl : ["http://jpaccess.baidu.com/material/jitouchuanmei_20181130_1543547834.jpg"]
     * mediaWidth : 1080
     * mediaHeight : 1920
     * videoDuration : 0
     * mediaSize : [0]
     * mediaMd5 : ["377f28ab3c831ce6bfc6341c18b19281"]
     * winNoticeUrl : ["http://jpaccess.baidu.com/win_third?app_id=jitouchuanmei&adslot_id=20181130&type=win&search_key=win_a4de15d3d92e3fb9"]
     * thirdMonitorUrl : []
     */

    private int id;
    private String requestId;
    private String adSlotId;
    private String searchKey;
    private String expirationDate;
    private String adKey;
    private String adType;
    private Object videoUrl;
    private int mediaWidth;
    private int mediaHeight;
    private int videoDuration;
    private List<String> imageUrl;
    private List<Integer> mediaSize;
    private List<String> mediaMd5;
    private List<String> winNoticeUrl;
    private List<String> thirdMonitorUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAdSlotId() {
        return adSlotId;
    }

    public void setAdSlotId(String adSlotId) {
        this.adSlotId = adSlotId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAdKey() {
        return adKey;
    }

    public void setAdKey(String adKey) {
        this.adKey = adKey;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public Object getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(Object videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getMediaWidth() {
        return mediaWidth;
    }

    public void setMediaWidth(int mediaWidth) {
        this.mediaWidth = mediaWidth;
    }

    public int getMediaHeight() {
        return mediaHeight;
    }

    public void setMediaHeight(int mediaHeight) {
        this.mediaHeight = mediaHeight;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Integer> getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(List<Integer> mediaSize) {
        this.mediaSize = mediaSize;
    }

    public List<String> getMediaMd5() {
        return mediaMd5;
    }

    public void setMediaMd5(List<String> mediaMd5) {
        this.mediaMd5 = mediaMd5;
    }

    public List<String> getWinNoticeUrl() {
        return winNoticeUrl;
    }

    public void setWinNoticeUrl(List<String> winNoticeUrl) {
        this.winNoticeUrl = winNoticeUrl;
    }

    public List<String> getThirdMonitorUrl() {
        return thirdMonitorUrl;
    }

    public void setThirdMonitorUrl(List<String> thirdMonitorUrl) {
        this.thirdMonitorUrl = thirdMonitorUrl;
    }

    @Override
    public String toString() {
        return "AdvInfoBean{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", adSlotId='" + adSlotId + '\'' +
                ", searchKey='" + searchKey + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", adKey='" + adKey + '\'' +
                ", adType='" + adType + '\'' +
                ", videoUrl=" + videoUrl +
                ", mediaWidth=" + mediaWidth +
                ", mediaHeight=" + mediaHeight +
                ", videoDuration=" + videoDuration +
                ", imageUrl=" + imageUrl +
                ", mediaSize=" + mediaSize +
                ", mediaMd5=" + mediaMd5 +
                ", winNoticeUrl=" + winNoticeUrl +
                ", thirdMonitorUrl=" + thirdMonitorUrl +
                '}';
    }
}
