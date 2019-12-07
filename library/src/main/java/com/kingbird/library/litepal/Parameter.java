package com.kingbird.library.litepal;

import org.litepal.crud.LitePalSupport;

/**
 * 设备参数表
 *
 * @author panyingdao
 * @date 2017-11-22.
 */
public class Parameter extends LitePalSupport {
    private String uniqueness;
    private String deviceId;
    private String rCode;
    private String ip;
    private int port;
    private int protocolType;
    private int startPlayType;
    private String startPlayUrl;
    /**
     * 实时播放类型
     */
    private int playType;
    private String playUrl;
    private int isUploading;
    /**
     * 创建的文件夹路径
     */
    private String fileUrl;
    private int isResult;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int heartBeat;
    private int screenSize;
    private int decodingWay;
    private int networkType;
    private int applicationType;
    private boolean isPeakTimes;

    public String getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getrCode() {
        return rCode;
    }

    public void setrCode(String rCode) {
        this.rCode = rCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int netType) {
        this.protocolType = netType;
    }

    public int getStartPlayType() {
        return startPlayType;
    }

    public void setStartPlayType(int startPlayType) {
        this.startPlayType = startPlayType;
    }

    public String getStartPlayUrl() {
        return startPlayUrl;
    }

    public void setStartPlayUrl(String startPlayUrl) {
        this.startPlayUrl = startPlayUrl;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getIsUploading() {
        return isUploading;
    }

    public void setIsUploading(int isUploading) {
        this.isUploading = isUploading;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getIsResult() {
        return isResult;
    }

    public void setIsResult(int isResult) {
        this.isResult = isResult;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public int getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(int screenSize) {
        this.screenSize = screenSize;
    }

    public int getDecodingWay() {
        return decodingWay;
    }

    public void setDecodingWay(int decodingWay) {
        this.decodingWay = decodingWay;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }

    public boolean getIsPeakTimes() {
        return isPeakTimes;
    }

    public void setIsPeakTimes(boolean isPeakTime) {
        this.isPeakTimes = isPeakTime;
    }

}

