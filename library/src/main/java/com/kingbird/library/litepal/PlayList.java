package com.kingbird.library.litepal;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放清单表
 *
 * @author panyingdao
 * @date 2017-11-28.
 */

public class PlayList extends LitePalSupport {
    private int playId;
    private int orderNumber;
    private String condition;
    private String startTime;
    private String endTime;
    private int duration;
    private int playType;
    private String fileName;
    private int accountName;
    private String showName;
    private int count;
    private int showId;
    private int member;
    private String uniqueness;
    private int downloadSuccess;
    private int vip;
    private int screenType;
    private int redPacket;
    private String pictureName;
    private int isBaiDu;
    private int baiDushowId;
    private int intervalTime;
    private int downloadCount;
    private String fileMd5;
    private String jdStartUrl;
    private String jdStopUrl;
    private List<String> peakTimeList;
    private List<String> peakStartTimeList;
    private List<String> peakEndTimeList;
    private List<String> intervalTimeList;
    private List<String> intervalStartTimeList;
    private List<String> intervalEndTimeList;
    private ArrayList<String> jdUrlList;

    public int getPlayId() {
        return playId;
    }

    public void setPlayId(int playId) {
        this.playId = playId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getAccountName() {
        return accountName;
    }

    public void setAccountName(int accountName) {
        this.accountName = accountName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }

    public int getDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(int downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public int getRedPacket() {
        return redPacket;
    }

    public void setRedPacket(int redPacket) {
        this.redPacket = redPacket;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public int getIsBaiDu() {
        return isBaiDu;
    }

    public void setIsBaiDu(int isBaiDu) {
        this.isBaiDu = isBaiDu;
    }

    public int getBaiDushowId() {
        return baiDushowId;
    }

    public void setBaiDushowId(int baiDushowId) {
        this.baiDushowId = baiDushowId;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getJdStartUrl() {
        return jdStartUrl;
    }

    public void setJdStartUrl(String jdStartUrl) {
        this.jdStartUrl = jdStartUrl;
    }

    public String getJdStopUrl() {
        return jdStopUrl;
    }

    public void setJdStopUrl(String jdStopUrl) {
        this.jdStopUrl = jdStopUrl;
    }

    public List<String> getPeakTimeList() {
        return peakTimeList;
    }

    public void setPeakTimeList(List<String> peakTimeList) {
        this.peakTimeList = peakTimeList;
    }

    public List<String> getPeakStartTimeList() {
        return peakStartTimeList;
    }

    public void setPeakStartTimeList(List<String> peakStartTimeList) {
        this.peakStartTimeList = peakStartTimeList;
    }

    public List<String> getPeakEndTimeList() {
        return peakEndTimeList;
    }

    public void setPeakEndTimeList(List<String> peakEndTimeList) {
        this.peakEndTimeList = peakEndTimeList;
    }

    public List<String> getIntervalTimeList() {
        return intervalTimeList;
    }

    public void setIntervalTimeList(List<String> intervalTimeList) {
        this.intervalTimeList = intervalTimeList;
    }

    public List<String> getIntervalStartTimeList() {
        return intervalStartTimeList;
    }

    public void setIntervalStartTimeList(List<String> intervalStartTimeList) {
        this.intervalStartTimeList = intervalStartTimeList;
    }

    public List<String> getIntervalEndTimeList() {
        return intervalEndTimeList;
    }

    public void setIntervalEndTimeList(List<String> intervalEndTimeList) {
        this.intervalEndTimeList = intervalEndTimeList;
    }

    public ArrayList<String> getJdUrlList() {
        return jdUrlList;
    }

    public void setJdUrlList(ArrayList<String> jdUrlList) {
        this.jdUrlList = jdUrlList;
    }
}
