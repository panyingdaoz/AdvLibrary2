package com.kingbird.library.utils;

import android.os.Environment;

import com.kingbird.library.BuildConfig;

/**
 * @author Pan yingdao
 */
public class Config {

    public static final String ROOT_DIRECTORY_URL = Environment.getExternalStorageDirectory() + "/";
    public static final String PACKAGE_NAME = "com.kingbird.advertise";
    public static final String FILE_SAVE_URL = ROOT_DIRECTORY_URL + PACKAGE_NAME + "/";
    public static final String MY_LOG_URL = ROOT_DIRECTORY_URL + "Mylog/";
    public static final String PACKAGE_NAME2 = "com.kingbird.advertisting";
    public static final String YZDJ_PACKAGE_NAME = "com.yzdj.tt";
    public static final String DOMAIN_NAME = "https://login.xjymedia.com/";
    public static final String DOMAIN_NAME2 = "https://login.xjymedia.com";
    public static final String ILLEGAL_LOGO_URR = "https://login.xjymedia.comnull";
    public static final String APPID = "jt5a1992e086073442";
    public static final String APPKEY = "72f88ec9b819467d8892909730047d26";
    public static final String USER_NAME = "即投云媒";
    public static final String APP_VERSION = "A";
    public static final String APK_CHECK = BuildConfig.VERSION_NAME;
    public static final String PART_ID = "00";
    public static final String BACK_PASSWORD = "511353";
    public static final String SUCCESS = "success";
    public static final String SUCCESS2 = "Success";
    public static final String SUER = "确认";
    public static final String OPEN_YUNZHONG_ACTION = "com.android.action.SHOW_BUSINESS_FUNCTION";
    public static final String LOTTERY_MACHINE = "com.fulei.lottery.socket.soft";
    public static final String LOTTERY_MACHINE_MODEL = "msm8909";
    public static final String CHARGE_MACHINE_MODEL = "rk312x";
    //    public static final String OPEN_ACTION = "android.kingbird.action.OPEN_ADVERTISTING";
    public static final String INITIAL_IP = "www.jtymedia.com";
    public static final String GET_START_PARAM = DOMAIN_NAME + "api/Device/GetStartParam?deviceId=";
    public static final String UPDATE_TISSUE_COUNT = DOMAIN_NAME + "api/Device/UpdateTissueCount?deviceId=";
    static final String SET_CONTROL_RESULT = DOMAIN_NAME + "api/Device/SetControlResult?deviceId=";
    public static final String GET_MEDIA_TIME = DOMAIN_NAME + "api/MediaInfo/GetMediaTime/";
    public static final String SET_DEVICE_LIST = DOMAIN_NAME + "api/Device/SetDeviceList";
    public static final String GET_BAIDU_AD = DOMAIN_NAME + "api/MediaInfo/GetBaiduAd?deviceId=";
    public static final String BAIDU_IS_PLAYED = DOMAIN_NAME + "api/MediaInfo/BaiduIsPlayed/";
    public static final String SAVE_BAIDU_LOG = DOMAIN_NAME + "api/MediaInfo/SaveBaiduLog";
    public static final String SET_DEVICE_PARAM = DOMAIN_NAME + "api/Device/SetDeviceParam";
    public static final String DOWNLOAD_COMPLETE = DOMAIN_NAME + "api/Device/DownloadComplete?deviceId=";
    public static final String DELETE_PLAYED_MEDIA = DOMAIN_NAME + "api/Device/DeletePlayedMedia?deviceId=";
    public static final String DEVICE_TIME = DOMAIN_NAME + "api/Device/GetDeviceTime?deviceId=";
    public static final String REDPACKET_AMOUNT = DOMAIN_NAME + "api/MediaInfo/GetRedPacketAmount?mediaId=";
    public static final String GET_REDPACKET_AMOUNT = DOMAIN_NAME + "api/Device/GetRedPacketAmount?deviceId=";
    static final String CHANGE_LOCATION = DOMAIN_NAME + "api/Device/ChangeLocation?deviceId=";
    public static final String BIND_DEALER = DOMAIN_NAME + "api/device/BindDealer?deviceId=";
    public static final String ADD_JINGDONG = DOMAIN_NAME + "api/JdAdv/Add";
    public static final String START_JINGDONG = DOMAIN_NAME + "api/JdAdv/startPlay";
    public static final String END_JINGDONG = DOMAIN_NAME + "api/JdAdv/stopPlay";
    public static final String APP_LOG = "http://log.jtymedia.com/api/log";
    /**
     * 京东广告APPID 调试8754
     */
    public static final int JINGDONG_APP_ID = 9116;
    /**
     * 京东广告APPID 调试5f8ec75cd8a6c2886704fdd1af6264c7
     */
    public static final String JINGDONG_APP_KEY = "ddcf3f59e9120ff12c8be1106b3efbfb";
    /**
     * 京东广告APPID
     */
    public static final String JINGDONG_APP_HOST = "http://api.jdmomedia.com/ad/request?version=v1";
    /**
     * 京东广告APPID
     */
    public static final String JINGDONG_HOST = "http://api.jdmomedia.com/";
//    /**
//     * 出纸
//     */
//    public static final String OUT_TISSUE = "01050005FF009C3B";
    /**
     * 出纸失败
     */
    static final String OUT_TISSUE_FAILURE = "0105000700007C0B";
    /**
     * 出纸成功
     */
    static final String OUT_TISSUE_SUCCESS = "010500050000DDCB";
    /**
     * 缺纸
     */
    static final String LACK_TISSUE = "0105000600002DCB";

    /**
     * 读取数据指令
     */
    public static final String READ_DATA = "90";
    /**
     * 写数据指令
     */
    public static final String WRITE_DATA = "91";
    /**
     * 文件下载指令
     */
    public static final String FILE_DOWNLOAD = "80";
    /**
     * 远程控制播放指令
     */
    public static final String REMOTE_CONTROL = "81";
    /**
     * 实时播放指令
     */
    public static final String REMOTE_CONTROL_VIDEO_PLAY = "82";
    /**
     * 删除终端本地视频文件指令
     */
    public static final String DELETE_LOCAL_VIDEO = "83";
    /**
     * 读取终端本地视频文件指令
     */
    public static final String READ_LOCAL_VIDEO_FILE = "84";
    /**
     * 清单操作指令
     */
    public static final String ACTION_LIST = "85";
    /**
     * 设置滚动字幕指令
     */
    public static final String SCROLL_TEXT = "86";
    /**
     * APP远程下载
     */
    public static final String APP_DOWNLOAD = "88";
    /**
     * 出纸巾
     */
    public static final String TISSUE = "89";
    /**
     * logo下载
     */
    public static final String LOGO_DOWNLOAD = "92";
    /**
     * 二维码下载
     */
    public static final String QRCODE_DOWNLOAD = "93";
    /**
     * APP启动播放文件下载
     */
    public static final String START_FILE_DOWNLOAD = "94";
    /**
     * 手机通电控制指令
     */
    public static final String ELECTRIFY = "95";
    /**
     * 控制logo、二维码显示大小
     */
    public static final String SET_LOGO_QR_SIZE = "96";
    /**
     * 广告语音播报
     */
    public static final String ADV_VOICE = "97";
    /**
     * 广告语音播报
     */
    public static final String ADV_JINGDONG = "98";
    /**
     * 广告语音播报
     */
    public static final String LOG_FILE = "98";
    /**
     * APP远程重启
     */
    public static final String APP_FRONT = "AA";
    /**
     * 删除清单
     */
    public static final String DELETE_SHOW = "AE";
    /**
     * 启动清单播放
     */
    public static final String START_SHOW_PLAY = "AD";
    /**
     * 播放上一个清单
     */
    public static final String PALY_PREVIOUS = "B0";
    /**
     * 播放下一个清单
     */
    public static final String PALY_NEXT = "B1";
    /**
     * 当前播放序号
     */
    public static final String CURRENT_PLAY = "B2";
    public static final String NUMBER_B5 = "B5";
    public static final String NUMBER_B6 = "B6";
    public static final String NUMBER_0B = "0B";
    public static final String NUMBER_BC = "BC";
    public static final String NUMBER_01 = "01";
    public static final String YIN_NUO_HENG_MODEL = "v40";
    public static final String YIN_NUO_HENG_MODEL2 = "QUAD-CORE T3 p1";
    public static final String RK_3128 = "rk312x";
    public static final String RK_3288 = "3280";
    public static final String YI_SHENG_MODEL_3288 = "rk3288";
    public static final String TEN_INCH_DEVICE_MODEL = "QUAD-CORE T3 p1";
    public static final String NUMBER_VALUE = "12";
    public static final String VIDEO_TYPE = "MP4";
    public static final int INITIAL_PORT = 9029;
    public static final int INITIAL_HERATBEAT = 120;
    public static final int STATUS_CODE = 200;
    public static final int STATUS_PLIT_SCREEN = 2;
    public static final int CONSTANT_ONE = 1;
    public static final int CONSTANT_TWO = 2;
    public static final int CONSTANT_THREE = 3;
    public static final int CONSTANT_FOUR = 4;
    public static final int CONSTANT_FIVE = 5;
    public static final int CONSTANT_SIX = 6;
    public static final int CONSTANT_EIGHT = 8;
    public static final int CONSTANT_TEN = 10;
    public static final int CONSTANT_ELEVEN = 11;
    public static final int CONSTANT_THIRTEEN = 13;
    public static final int CONSTANT_FIFTEEN = 15;
    public static final int CONSTANT_TWENTY = 20;
    public static final int TWENTY_FIVE = 25;
    public static final int CONSTANT_FORTY = 40;
    static final int CONSTANT_ONE_HUNDRED = 100;
    public static final int CONSTANT_BUTTON = 122;
    public static final int CONSTANT_FIVE_HUNDRED = 500;
    public static final int CONSTANT_ONE_THOUSAND = 1000;
    public static final int CONSTANT_LENGTH = 1024;
    public static final int VOICE_CONTENT_LENGTH = 99;

    /**
     * 京东错误码
     */
    public static final int JINGDONG_CODE1 = 100001;
    /**
     * 京东错误码
     */
    public static final int JINGDONG_CODE2 = 104071;
    /**
     * 京东错误码
     */
    public static final int JINGDONG_CODE3 = 200000;
    /**
     * 京东上报错误码 Report
     */
    public static final int JINGDONG_REPORT = 408;
    /**
     * 京东上状态码 Report
     */
    public static final int JINGDONG_CODE = 400;

}
