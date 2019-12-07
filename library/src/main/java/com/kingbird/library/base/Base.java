package com.kingbird.library.base;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.kingbird.library.jsonbean.AppLog;
import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.litepal.PlayList;
import com.kingbird.library.manager.ExecutorServiceManager;
import com.kingbird.library.manager.ProtocolDao;
import com.kingbird.library.manager.ProtocolManager;
import com.kingbird.library.manager.SocketManager;
import com.kingbird.library.manager.ThreadManager;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.FileUtils;
import com.kingbird.library.utils.HttpUtil;
import com.kingbird.library.utils.Plog;
import com.kingbird.library.utils.SharedPreferencesUtils;
import com.kingbird.library.view.UdpView;
import com.socks.library.KLog;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kingbird.library.utils.Config.ACTION_LIST;
import static com.kingbird.library.utils.Config.APK_CHECK;
import static com.kingbird.library.utils.Config.APP_LOG;
import static com.kingbird.library.utils.Config.BIND_DEALER;
import static com.kingbird.library.utils.Config.CONSTANT_FOUR;
import static com.kingbird.library.utils.Config.CONSTANT_TWO;
import static com.kingbird.library.utils.Config.CURRENT_PLAY;
import static com.kingbird.library.utils.Config.DELETE_LOCAL_VIDEO;
import static com.kingbird.library.utils.Config.DELETE_PLAYED_MEDIA;
import static com.kingbird.library.utils.Config.DELETE_SHOW;
import static com.kingbird.library.utils.Config.FILE_SAVE_URL;
import static com.kingbird.library.utils.Config.GET_START_PARAM;
import static com.kingbird.library.utils.Config.INITIAL_HERATBEAT;
import static com.kingbird.library.utils.Config.INITIAL_IP;
import static com.kingbird.library.utils.Config.INITIAL_PORT;
import static com.kingbird.library.utils.Config.LOGO_DOWNLOAD;
import static com.kingbird.library.utils.Config.MY_LOG_URL;
import static com.kingbird.library.utils.Config.PACKAGE_NAME;
import static com.kingbird.library.utils.Config.PALY_NEXT;
import static com.kingbird.library.utils.Config.PALY_PREVIOUS;
import static com.kingbird.library.utils.Config.PART_ID;
import static com.kingbird.library.utils.Config.QRCODE_DOWNLOAD;
import static com.kingbird.library.utils.Config.READ_DATA;
import static com.kingbird.library.utils.Config.READ_LOCAL_VIDEO_FILE;
import static com.kingbird.library.utils.Config.REMOTE_CONTROL;
import static com.kingbird.library.utils.Config.REMOTE_CONTROL_VIDEO_PLAY;
import static com.kingbird.library.utils.Config.ROOT_DIRECTORY_URL;
import static com.kingbird.library.utils.Config.SET_DEVICE_LIST;
import static com.kingbird.library.utils.Config.START_SHOW_PLAY;
import static com.kingbird.library.utils.Config.SUCCESS;
import static com.kingbird.library.utils.Config.UPDATE_TISSUE_COUNT;
import static com.kingbird.library.utils.Config.WRITE_DATA;
import static com.kingbird.library.utils.Plog.e;

/**
 * 基类
 *
 * @author panyingdao
 * @date 2017/12/12.
 */

public class Base {
    private static int tissueCount = 0;
    private static boolean isRestart = false;

    private static int netType;
    private static int decodingWay;
    private static String newIp;
    private static Activity mActivity;

    public static void setActivity(Activity activity) {
        mActivity = activity;
    }

    public static Activity getActivity() {
        return mActivity;
    }

    /**
     * 通过制定值读取制定值
     */
    public static List<PlayList> getQuery(String str1, String str2, String str3) {
        return LitePal.select(str1).where(str2 + " = ?", str3)
                .find(PlayList.class);
    }

    /**
     * 通过文件名读取数据 PlayList
     */
    public static List<PlayList> fileNameQuery(String str1, String fileName) {
        return LitePal.select(str1).where("fileName = ?", fileName)
                .find(PlayList.class);
    }

    /**
     * 通过播放ID读取数据 PlayList
     */
    public static List<PlayList> dataQuery(String str1, String playId) {
        return LitePal.select(str1).where("playId = ?", playId)
                .find(PlayList.class);
    }

    /**
     * 通过节目ID读取数据 PlayList
     */
    public static List<PlayList> showIdQuery(String str1, String showId) {
        return LitePal.select(str1).where("showId = ?", showId)
                .find(PlayList.class);
    }

    /**
     * 设置京东广告
     */
    public static void setJdShowData(int playId, String condition, String startTime, String endTime, int duration,
                                     int playType, String fileName, int account, String showName, int showId, int member, int vip, int screenType,
                                     int redPacket, String pictureName, int isBaiDu, int baiDushowId, int intervalTime,
                                     List<String> intervalTimeList, String fileMd5, String startUrl, String stopUrl, ArrayList<String> jdUrl, PlayList playList) {
        playList.setPlayId(playId);
        playList.setShowId(showId);
        playList.setCondition(condition);
        playList.setStartTime(startTime);
        playList.setEndTime(endTime);
        playList.setDuration(duration);
        playList.setPlayType(playType);
        playList.setFileName(fileName);
        playList.setAccountName(account);
        playList.setShowName(showName);
        playList.setMember(member);
        playList.setDownloadSuccess(1);
        playList.setVip(vip);
        playList.setScreenType(screenType);
        playList.setRedPacket(redPacket);
        playList.setPictureName(pictureName);
        playList.setIsBaiDu(isBaiDu);
        playList.setBaiDushowId(baiDushowId);
        playList.setIntervalTime(intervalTime);
        playList.setIntervalTimeList(intervalTimeList);
        playList.setFileMd5(fileMd5);
        playList.setJdStartUrl(startUrl);
        playList.setJdStopUrl(stopUrl);
        playList.setJdUrlList(jdUrl);
        playList.setDownloadCount(0);
    }

    /**
     * 设置即投云媒广告
     */
    public static void setShowData(int playId, String condition, String startTime, String endTime, int duration,
                                   int playType, String fileName, int account, String showName, int showId, int member, int vip, int screenType,
                                   int redPacket, String pictureName, int isBaiDu, int baiDushowId, int intervalTime,
                                   List<String> intervalTimeList, String fileMd5, PlayList playList) {
        playList.setPlayId(playId);
        playList.setShowId(showId);
//        playList.setOrderNumber(orderNumber);
        playList.setCondition(condition);
        playList.setStartTime(startTime);
        playList.setEndTime(endTime);
        playList.setDuration(duration);
        playList.setPlayType(playType);
        playList.setFileName(fileName);
        playList.setAccountName(account);
        playList.setShowName(showName);
        playList.setMember(member);
        playList.setDownloadSuccess(1);
        playList.setVip(vip);
        playList.setScreenType(screenType);
        playList.setRedPacket(redPacket);
        playList.setPictureName(pictureName);
        playList.setIsBaiDu(isBaiDu);
        playList.setBaiDushowId(baiDushowId);
        playList.setIntervalTime(intervalTime);
        playList.setIntervalTimeList(intervalTimeList);
        playList.setFileMd5(fileMd5);
        playList.setDownloadCount(0);
    }

    /**
     * 修改即投云媒广告
     */
    public static void dataModify(int playId, String condition, String startTime, String endTime, int duration,
                                  int playType, String fileName, int account, String showName, int showId, int member, int vip, int screenType,
                                  int redPacket, String pictureName, int isBaiDu, int baiDushowId, int intervalTime, String fileMd5) {
        ContentValues values = new ContentValues();
        values.put("playId", playId);
        values.put("condition", condition);
        values.put("startTime", startTime);
        values.put("endTime", endTime);
        values.put("duration", duration);
        values.put("playType", playType);
        values.put("fileName", fileName);
        values.put("accountName", account);
        values.put("showName", showName);
        values.put("showId", showId);
        values.put("count", 0);
        values.put("member", member);
        values.put("downloadSuccess", 1);
        values.put("vip", vip);
        values.put("screenType", screenType);
        values.put("redPacket", redPacket);
        values.put("pictureName", pictureName);
        values.put("isBaiDu", isBaiDu);
        values.put("baiDushowId", baiDushowId);
        values.put("intervalTime", intervalTime);
        values.put("fileMd5", fileMd5);
        values.put("downloadCount", 0);
        LitePal.updateAll(PlayList.class, values, "showId = ?", Integer.toString(showId));
    }

    /**
     * 设置当前播放文件
     */
    private static void setIntent(String mediaId) {
        List<PlayList> screenList = showIdQuery("screenType", mediaId);
        for (PlayList screenLists : screenList) {
            List<PlayList> newsList = showIdQuery("fileName", mediaId);
            for (PlayList fileNames : newsList) {
                String fileName = fileNames.getFileName();
                int screenType = screenLists.getScreenType();
                if (screenType == 2 || screenType == 3) {
                    SharedPreferencesUtils.writeInt(mActivity, Const.SPLIT_SCREEN, screenType);
                    SharedPreferencesUtils.writeString(mActivity, Const.START_SCREENOR_FILE, fileName);
                } else if (screenType == 1) {
                    SharedPreferencesUtils.writeInt(mActivity, Const.SPLIT_SCREEN, 1);
                    setIntentData("playUrl", fileName);
                    e("要播放节目ID和文件", mediaId, fileName);
                }
                intentActivity("4");
            }
        }
    }

    /**
     * 给VideoViewActivity 发通知
     */
    public static void intentActivity(String value) {
        Intent intent = new Intent();
        intent.setAction("tcpServerReceiver");
        intent.putExtra("tcpServerReceiver", value);
        //将消息发送给主界面
        //安全性更好，同时拥有更高的运行效率
//        e("context对象", context);
//        if (context != null) {
        mActivity.sendBroadcast(intent);
        e("将消息发送给主界面", value);
//        }
    }

    /**
     * 解析整型数据
     */
    private static int getAnInt(byte[] buff) {
        return Integer.parseInt(bytes2HexString(ProtocolManager.getInstance().parseParameter(buff, 6, 1)));
    }

    /**
     * 解析字符串数据
     */
    public static String getAnString(byte[] buff, int index, int length) {
        return bytes2HexString(ProtocolManager.getInstance().parseParameter(buff, index, length));
    }

    /**
     * 解析16进制整型数据
     */
    public static int getAnIntHex(byte[] buff, int index, int length, int radix) {
        return Integer.parseInt(bytes2HexString(ProtocolManager.getInstance().parseParameter(buff, index, length)), radix);
    }

    /**
     * 读取播放广告清单
     */
    public static ArrayList<Integer> showSum() {
        ArrayList<Integer> arrList = new ArrayList<>();
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        for (PlayList playlist : playLists) {
            int showSum = playlist.getPlayId();
            List<PlayList> query = dataQuery("screenType", Integer.toString(showSum));
            for (PlayList screenType : query) {
                if (screenType.getScreenType() == 1) {
                    arrList.add(showSum);
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取普通广告清单
     */
    private static ArrayList<Integer> showSum2() {
        ArrayList<Integer> arrList = new ArrayList<>();
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        for (PlayList playlist : playLists) {
            int showSum = playlist.getPlayId();
            List<PlayList> query = dataQuery("screenType", Integer.toString(showSum));
            for (PlayList screenType : query) {
                List<PlayList> queryisBaidu = dataQuery("isBaiDu", Integer.toString(showSum));
                for (PlayList queryisBaidus : queryisBaidu) {
                    if (screenType.getScreenType() == 1 && queryisBaidus.getIsBaiDu() == 0) {
                        arrList.add(showSum);
                    }
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取节目Id
     */
    private static ArrayList<Integer> getShowId() {
        ArrayList<Integer> arrList = new ArrayList<>();
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        for (PlayList playlist : playLists) {
            int showId = playlist.getShowId();
            if (showId != 0) {
                arrList.add(showId);
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取时段广告清单
     */
    public static ArrayList<Integer> readIntervalTimeShow() {
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        ArrayList<Integer> arrList = new ArrayList<>();
        for (PlayList playList : playLists) {
            int playId = playList.getPlayId();
            List<PlayList> query = getQuery("intervalTime", "playId", Integer.toString(playId));
            for (PlayList querys : query) {
                if (querys.getIntervalTime() == 1) {
                    arrList.add(playId);
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取副屏滚动广告清单
     */
    public static ArrayList<String> getSplitScreenShow() {
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        ArrayList<String> arrList = new ArrayList<>();
        for (PlayList playList : playLists) {
            int show = playList.getShowId();
            String showId = Integer.toString(show);
            List<PlayList> query = showIdQuery("screenType", showId);
            for (PlayList query1s : query) {
                if (query1s.getScreenType() == 2 || query1s.getScreenType() == 3) {
                    List<PlayList> query2 = showIdQuery("fileName", showId);
                    for (PlayList query2s : query2) {
                        String fileName = query2s.getFileName();
                        String fileUrl = FILE_SAVE_URL + fileName;
                        e("获取到的副屏广告文件", fileName);
                        arrList.add(fileUrl);
                    }
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取百度广告清单
     */
    public static ArrayList<Integer> readBaiDuShow() {
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        ArrayList<Integer> arrList = new ArrayList<>();
        for (PlayList playList : playLists) {
            int playId = playList.getPlayId();
            List<PlayList> query = dataQuery("isBaiDu", Integer.toString(playId));
            for (PlayList querys : query) {
                if (querys.getIsBaiDu() == 1) {
                    arrList.add(playId);
                } else {
                    e("是即投云媒广告");
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取京东广告清单
     */
    public static ArrayList<Integer> readJingDongShow() {
        List<PlayList> playLists = LitePal.findAll(PlayList.class);
        ArrayList<Integer> arrList = new ArrayList<>();
        for (PlayList playList : playLists) {
            int playId = playList.getPlayId();
            List<PlayList> query = dataQuery("isBaiDu", Integer.toString(playId));
            for (PlayList querys : query) {
                if (querys.getIsBaiDu() == 2) {
                    arrList.add(playId);
                } else {
                    e("是即投云媒广告");
                }
            }
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 读取vip广告清单
     */
    public static ArrayList<Integer> vipShowSum() {
        ArrayList<Integer> arrList = new ArrayList<>();
        List<PlayList> vipList = getQuery("playId", "vip", "2");
        for (PlayList vipLists : vipList) {
            int showSum = vipLists.getPlayId();
            arrList.add(showSum);
            Collections.sort(arrList);
        }
        return arrList;
    }

    /**
     * 设置节目播放次数
     */
    public static void setCount(String playId, int count) {
        ContentValues values = new ContentValues();
        values.put("count", count);
        LitePal.updateAll(PlayList.class, values, "playId = ?", playId);
    }

    /**
     * 播放次数上报
     */
    public static void showReport(String deviceId, String rCode, String playShowId, int playCount) {
        //即投平台广告记录上报
        List<PlayList> showId = dataQuery("showId", playShowId);
        for (PlayList showIds : showId) {
            final byte[] command = new byte[1];
            command[0] = (byte) 0x87;
            final byte[] number = new byte[1];
            number[0] = (byte) 0x20;
            e("上报次数ID", showIds.getShowId());

            ProtocolManager.getInstance().sendTimesReported(deviceId, rCode, command, number, showIds.getShowId(), playCount);
        }
    }

    /**
     * 删除文件
     */
    private static void deleteFiles(byte[] buff, String server) {
        int fileLength = getAnIntHex(buff, 5, 1, 16);
        String fileName = hexToStringGbk(getAnString(buff, 6, fileLength));
        e("删除的文件名", fileName);
        deleteFile(fileName, server);
    }

    /**
     * 读取本地文件名
     */
    private static StringBuffer readLocalfile() {
        StringBuffer fb = new StringBuffer();
        File file = new File(ROOT_DIRECTORY_URL + PACKAGE_NAME);
        File[] listFile = file.listFiles();
        for (int i = 0; i < listFile.length; i++) {
            File fs = listFile[i];
            String fsName = fs.getName().toUpperCase();
            if (fsName.endsWith(".MP4") || fsName.endsWith(".MOV") || fsName.endsWith(".JPG") || fsName.endsWith(".JPEG")
                    || fsName.endsWith(".PNG") || fsName.endsWith(".GIF")) {
                byte[] filename = ProtocolDao.getListFile(i, fsName);
                fb.append(hexToStringGbk(bytes2HexString(filename)));
            }
        }
        return fb;
    }

    /**
     * 读取本地文件
     */
    public static void readLocalFile() {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> arrList = new ArrayList<>();
                    ArrayList<String> fileArray = getPlayShowFile();
//                    Plog.e("获取到的清单文件列表", fileArray);
                    File file = new File(ROOT_DIRECTORY_URL + PACKAGE_NAME);
                    File[] listFile = file.listFiles();
                    assert listFile != null;
                    for (File fs : listFile) {
                        arrList.add(fs.getName());
                    }
                    for (int b = fileArray.size() - 1; b >= 0; b--) {
                        arrList.remove(fileArray.get(b));
                    }
                    e("多余的本地文件列表", arrList);
                    if (arrList.size() > 0) {
                        for (int k = 0; k < arrList.size(); k++) {
                            String arrListFile = arrList.get(k);
                            List<Parameter> parameter = LitePal.findAll(Parameter.class);
                            for (Parameter parameter1 : parameter) {
                                String hasRedPacketName = SharedPreferencesUtils.readString(mActivity, Const.HAS_REDPACKET_NAME);
                                String qrName = "qr" + parameter1.getDeviceId() + ".jpg";
                                String logoUrl = SharedPreferencesUtils.readString(mActivity, Const.LOGO_URL);
                                String logo = logoUrl.substring(logoUrl.lastIndexOf("/") + 1);
                                ArrayList<String> pictureFileNameList = new ArrayList<>();
                                List<PlayList> picture = LitePal.findAll(PlayList.class);
                                for (PlayList pictures : picture) {
                                    pictureFileNameList.add(pictures.getPictureName());
                                }
                                pictureFileNameList.add(parameter1.getStartPlayUrl());
                                pictureFileNameList.add("1.mp4");
                                pictureFileNameList.add("0.mp4");
                                pictureFileNameList.add(qrName);
                                pictureFileNameList.add(hasRedPacketName);
                                pictureFileNameList.add(logo);
                                if (!pictureFileNameList.contains(arrListFile)) {
                                    File deleteFile = new File(FILE_SAVE_URL + arrListFile);
                                    if (deleteFile.delete()) {
                                        e("删除成功", deleteFile);
                                    }
                                }
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    e("异常", e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Md5文件校验
     */
    public static boolean checkMd5(String fileName) {
        List<PlayList> queryMd5 = fileNameQuery("fileMd5", fileName);
        for (PlayList queryMd5s : queryMd5) {
            String fileMd5 = queryMd5s.getFileMd5();
            if (fileMd5 != null) {
                e("获取到的文件md5值", fileMd5);
                String saveDir = FILE_SAVE_URL + fileName;
                File file = new File(saveDir);
                if (!fileMd5.equals(FileUtils.getFileMd5(file))) {
                    e("文件存在,但不完整！");
                    return false;
                } else {
                    e("文件存在且完整");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取清单文件名
     */
    private static ArrayList<String> getPlayShowFile() {
        ArrayList<String> arrList = new ArrayList<>();
        List<PlayList> query = LitePal.findAll(PlayList.class);
        for (PlayList querys : query) {
            arrList.add(querys.getFileName());
        }
        return arrList;
    }

    /**
     * 2字节byte[]转16进制字符串
     *
     * @param data 数据源
     * @return 返回
     */
    public static String bytes2HexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String hex;
        for (byte aData : data) {
            hex = Integer.toHexString(aData & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 十六进制到字符串gbk
     */
    public static String hexToStringGbk(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            // UTF-16le:Not
            s = new String(baKeyword, "GBK");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }

//    public static String convertStringToHex(String str) {
//        //将字符串转换为十六进制
//        char[] chars = str.toCharArray();
//        StringBuilder hex = new StringBuilder();
//        for (char aChar : chars) {
//            hex.append(Integer.toHexString((int) aChar));
//        }
//        return hex.toString();
//    }

    /**
     * 十六进制到字符串
     */
    public static String convertHexToString(String hex) {
        //十六进制转换为字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += CONSTANT_TWO) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    /**
     * @param n 需要转换整数
     * @return 返回
     */
    public static byte[] intToButeArray(int n) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 全写时的字符串转成整型byte数组
     *
     * @param str 要转换的字符串
     */
    public static byte[] strToByteArray1(String str) {
        int data;
        int length = str.length() / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < str.length(); i += 2) {
            data = Integer.parseInt(str.substring(i, (i + 2)), 16);
            for (int j = 0; j < length; j++) {
                if ((i - j) == j) {
                    bytes[j] = (byte) data;
                }
            }
        }
        return bytes;
    }

    /**
     * 删除单个文件
     */
    public static void deleteFile(String fileName, String server) {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            String id = parameters.getDeviceId();
            String rCode = parameters.getrCode();
            String filePath = FILE_SAVE_URL + fileName;
            File file = new File(filePath);
            byte[] command = new byte[1];
            command[0] = (byte) 0x83;
            byte[] number = new byte[1];
            number[0] = (byte) 0xA3;
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
                    e("删除单个文件" + fileName + "成功！");
                } else {
                    ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
                    e("删除单个文件" + fileName + "失败！");
                }
            } else {
                e("删除单个文件失败：" + fileName + "不存在！");
                ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
            }
        }
    }

    /**
     * 删除本地文件
     */
    public static void removeFile(String fileName) {
        String filePath = FILE_SAVE_URL + fileName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                e("删除成功", fileName);
            }
        } else {
            e("删除失败，文件不存在", fileName);
        }
    }

    /**
     * 本地文件校验
     */
    public static boolean fileIsExists(String strFile) {
        String fileName = strFile.substring(strFile.lastIndexOf("/") + 1);
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                e(fileName + " ," + "文件不存在！");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        e(fileName + " ," + "文件存在！");
        return true;
    }

    /**
     * 服务器指令解析
     */
    public static void dataProcessing(byte[] receive, String server) {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            String client = "client";
            byte[] command = ProtocolManager.getInstance().parseParameter(receive, 1, 1);
            byte[] number = ProtocolManager.getInstance().parseParameter(receive, 4, 1);
            String commands = bytes2HexString(command);
            String numbers = bytes2HexString(number);
            e("数据:" + bytes2HexString(receive));
            e("命令字:" + commands);
            e("编号:" + numbers);
            switch (commands) {
                case READ_DATA:
                    readDat(receive, command, number, parameters, server);
                    break;
                case WRITE_DATA:
                    writeData(receive, server, parameters, client, command, number);
                    break;
                case REMOTE_CONTROL:
                    if (client.equals(server)) {
                        SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_TYPE, "LAN");
                    } else {
                        SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_TYPE, "INTERNET");
                    }
                    int value = getAnIntHex(receive, 6, 1, 16);
                    if ("B4".equals(numbers)) {
                        SharedPreferencesUtils.writeInt(mActivity, Const.VOLUME, value);
                        intentActivity("8");
                    } else {
                        intentActivity(Integer.toString(value));
                    }
                    break;
                case REMOTE_CONTROL_VIDEO_PLAY:
                    if (client.equals(server)) {
                        SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_TYPE, "LAN");
                    } else {
                        SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_TYPE, "INTERNET");
                    }
                    setsSartPlay(receive);
                    break;
                case DELETE_LOCAL_VIDEO:
                    deleteFiles(receive, server);
                    break;
                case READ_LOCAL_VIDEO_FILE:
                    ProtocolManager.getInstance().internetFileRead(parameters.getDeviceId(), parameters.getrCode(), readLocalfile(), server);
                    break;
                case ACTION_LIST:
                    listControl(receive, server, parameters, command, number, numbers);
                    break;
                case QRCODE_DOWNLOAD:
                    newZxing(parameters.getDeviceId());
                    break;
                case LOGO_DOWNLOAD:
                    intentActivity("10");
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 纸巾数量请求
     */
    public static void tissueCountRequest() {
        List<Parameter> deviceId = LitePal.findAll(Parameter.class);
        for (Parameter deviceIds : deviceId) {
            //2018-12-14修改 R.string.domain_name 为DOMAIN_NAME
            String url = UPDATE_TISSUE_COUNT + deviceIds.getDeviceId();
            e("更新路径", url);
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e("失败原因", e.toString());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    int code = response.code();
                    e("纸巾数量访问结果", code);
                    String data = response.body().string();
                    String urlDecoder = java.net.URLDecoder.decode(data, "UTF-8");
                    e("数据", urlDecoder);
                    try {
                        JSONObject json = new JSONObject(urlDecoder);
                        if (code == 200) {
                            if (json.getBoolean("success")) {
                                tissueCount = 0;
                            } else {
                                tissueCount++;
                                if (tissueCount > 0 && tissueCount < 4) {
                                    tissueCountRequest();
                                }
                            }
                        } else {
                            e("访问失败", urlDecoder);
                            tissueCount++;
                            if (tissueCount > 0 && tissueCount < 4) {
                                tissueCountRequest();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 写数据
     */
    private static void writeData(byte[] receive, String server, final Parameter parameters, String client, byte[] command, byte[] number) {
        if (client.equals(server)) {
            e("局域网参数设置");
            setLANData(receive, parameters, command, number, server);
        } else {
            e("互联网参数全设");
            int len = getAnIntHex(receive, 3, 1, 16);
            byte[] totalData = new byte[len];
            System.arraycopy(receive, 4, totalData, 0, len);
            StringBuffer sb = new StringBuffer();
            setData(totalData, sb);
            List<Parameter> querId = LitePal.findAll(Parameter.class);
            for (Parameter querIds : querId) {
                ProtocolManager.getInstance().internetAllWriteAnswer(querIds.getDeviceId(), parameters.getrCode(), sb);
            }
            if (isRestart) {
                intentActivity("6");
            }
        }
    }

    /**
     * 节目控制
     */
    private static void listControl(byte[] receive, String server, Parameter parameters, byte[] command, byte[] number, String numbers) {
        int mediaId;
        switch (numbers) {
            case DELETE_SHOW:
                mediaId = getAnIntHex(receive, 6, 4, 16);
                deleteShow(parameters, Integer.toString(mediaId), server);
                break;
            case START_SHOW_PLAY:
                SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_NUMBER, "AD");
                SharedPreferencesUtils.writeBoolean(mActivity, Const.SHOW_PLAY, true);
                mediaId = getAnIntHex(receive, 6, 4, 16);
                setIntent(Integer.toString(mediaId));
                break;
            case PALY_PREVIOUS:
                SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_NUMBER, "B0");
                intentActivity("4");
                break;
            case PALY_NEXT:
                SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_NUMBER, "B1");
                intentActivity("4");
                break;
            case CURRENT_PLAY:
                playShowNumber(command, number, server);
                break;
            default:
                break;
        }
    }

    /**
     * 参数读取
     */
    private static void readDat(byte[] receive, byte[] command, byte[] number, Parameter parameter, String server) {
        String id = parameter.getDeviceId();
        String rCode = parameter.getrCode();
        switch (bytes2HexString(number)) {
            case "01":
                if ("client".equals(server)) {
                    ProtocolManager.getInstance().readAnswer(id, rCode, command, number, id, "client");
                }
                break;
            case "02":
                ProtocolManager.getInstance().readAnswer(id, rCode, command, number, rCode, server);
                break;
            case "03":
                ProtocolManager.getInstance().readAnswer(id, rCode, command, number, parameter.getIp(), server);
                break;
            case "04":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getPort(), server);
                break;
            case "05":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getProtocolType(), server);
                break;
            case "06":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getStartPlayType(), server);
                break;
            case "07":
                ProtocolManager.getInstance().readAnswer(id, rCode, command, number, parameter.getStartPlayUrl(), server);
                break;
            case "08":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getPlayType(), server);
                break;
            case "09":
                ProtocolManager.getInstance().readAnswer(id, rCode, command, number, parameter.getPlayUrl(), server);
                break;
            case "0A":
                int count = showSum2().size();
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, count, server);
                break;
            case "0B":
//                readShowData(receive, command, number, server, id, rCode);
                showDataReport(id);
                break;
            case "0C":
                readShowCount(receive, command, number, server, id, rCode);
                break;
            case "0D":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getIsResult(), server);
                break;
            case "0E":
                ProtocolManager.getInstance().readSwitchesTime(id, rCode, command, number, parameter.getStartHour(),
                        parameter.getStartMinute(), parameter.getEndHour(), parameter.getEndMinute(), server);
                break;
            case "0F":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getScreenSize(), server);
                break;
            case "10":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getIsUploading(), server);
                break;
            case "11":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getHeartBeat(), server);
                break;
            case "12":
                ProtocolManager.getInstance().readAnswer(id, rCode, command, number, APK_CHECK, server);
                break;
            case "13":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getDecodingWay(), server);
            case "14":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getNetworkType(), server);
                break;
            case "15":
                ProtocolManager.getInstance().readAnswer2(id, rCode, command, number, parameter.getApplicationType(), server);
                break;
            case "FF":
                ProtocolManager.getInstance().readAllDataAnswer(id, rCode, command);
                break;
            default:
                break;
        }
    }

    /**
     * 节目读取
     */
    private static void readShowCount(byte[] receive, byte[] command, byte[] number, String server, String id, String rCode) {
        int showId = getAnIntHex(receive, 6, 4, 16);
        List<PlayList> queryCount = showIdQuery("count", Integer.toString(showId));
        for (PlayList queryCounts : queryCount) {
            ProtocolManager.getInstance().readAnswer3(id, rCode, command, number, showId, queryCounts.getCount(), server);
        }
    }

    /**
     * 给web上传节目ID
     */
    private static void showDataReport(final String deviceId) {
        try {
            ArrayList<Integer> showId = getShowId();
            JSONObject postJson = new JSONObject();
            JSONArray array = new JSONArray(showId);
            postJson.put("deviceId", deviceId);
            postJson.put("medias", array);
            webPostReport(postJson, SET_DEVICE_LIST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 终端数据上传
     */
    public static void webPostReport(final JSONObject postJson, final String url) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                e("app启动上传数据", postJson.toString());
                MyOkHttp mMyOkHttp = new MyOkHttp();
                mMyOkHttp.post()
                        .url(url)
                        .jsonParams(postJson.toString())
                        .tag(this)
                        .enqueue(new JsonResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, JSONObject response) {
                                e("post JSONObject数据上传成功:" + response);
                            }

                            @Override
                            public void onSuccess(int statusCode, JSONArray response) {
                                e("dpost JSONArray数据上传成功:" + response);
                            }

                            @Override
                            public void onFailure(int statusCode, String errorMsg) {
                                e("doPostJSON onFailure:" + errorMsg);
                            }
                        });
            }
        });
    }

    /**
     * 设置参数
     */
    private static void setData(byte[] totalData, StringBuffer sb) {
        String parameter;
        int length;
        String number = getAnString(totalData, 0, 1);
        e("编号", number);
        length = getAnIntHex(totalData, 1, 1, 16);
        parameter = convertHexToString(getAnString(totalData, 2, length));
        e("参数值", parameter);
        int type = totalData[2];
        e("类型", type);
        ContentValues values = new ContentValues();
        List<Parameter> parameters = LitePal.findAll(Parameter.class);
        for (Parameter parameter1 : parameters) {
            switch (number) {
                case "01":
                    setIntentDeviceId(totalData, sb, parameter, length, number, parameter1);
                    break;
                case "02":
                    setIntentRcode(totalData, sb, parameter, length, number, parameter1);
                    break;
                case "03":
                    setIntentIp(totalData, sb, parameter, length, number, parameter1);
                    break;
                case "04":
                    setIntentPort(totalData, sb, length, number, parameter1);
                    break;
                case "05":
                    setIntentProtocolType(totalData, sb, length, number, type, parameter1);
                    break;
                case "06":
                    setIntentStartPlayType(totalData, sb, length, number, type, parameter1);
                    break;
                case "07":
                    setIntentData("startPlayUrl", hexToStringGbk(getAnString(totalData, 2, length)));

                    if (parameter.equals(parameter1.getStartPlayUrl())) {
                        setSucceed(number, sb);
                    } else {
                        setFailure(number, sb);
                    }
                    recursion(totalData, length, sb);
                    break;
                case "10":
                    setUploading(totalData, sb, length, number, type, parameter1);
                    break;
                case "0D":
                    setIsResult(totalData, sb, length, number, type, parameter1);
                    break;
                case "0E":
                    setIntentOnOfData(totalData, sb, length, number, values);
                    break;
                case "0F":
                    setIntentScreentSize(totalData, sb, length, number, type, parameter1);
                    break;
                case "11":
                    setIntentHearBeat(totalData, sb, length, number, parameter1);
                    break;
                case "13":
                    setIntentDecodingWay(totalData, sb, length, number, type, parameter1);
                    break;
                case "14":
                    setIntentNetWortType(totalData, sb, length, number, type, parameter1);
                    break;
                case "15":
                    setIntentApplicationType(totalData, sb, length, number, type, parameter1);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置设备ID
     */
    private static void setIntentDeviceId(byte[] totalData, StringBuffer sb, String parameter, int length, String number, Parameter parameter1) {
        setIntentData("deviceID", parameter);
        if (!parameter1.getDeviceId().equals(parameter)) {
            newZxing(parameter);
        }
        setSucceed(number, sb);
        recursion(totalData, length, sb);
    }

    /**
     * 设置区号
     */
    private static void setIntentRcode(byte[] totalData, StringBuffer sb, String parameter, int length, String number, Parameter parameter1) {
        setIntentData("rCode", parameter);

        if (parameter.equals(parameter1.getrCode())) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置IP
     */
    private static void setIntentIp(byte[] totalData, StringBuffer sb, String ip, int length, String number, Parameter parameter1) {
        if (isIp(ip)) {
            setIntentData("ip", ip);
            e("旧设备IP号", parameter1.getIp());
            e("自动设置设备IP号", ip);
            setSucceed(number, sb);
            if (!ip.equals(parameter1.getIp())) {
                ExecutorServiceManager.getInstance().schedule(connect, 50, TimeUnit.MILLISECONDS);
                UdpView.getInstance().connectUdp(ip);
            }
        } else {
            e("服务器IP非法");
            setFailure(number, sb);
        }

        recursion(totalData, length, sb);
    }

    /**
     * 设置端口
     */
    private static void setIntentPort(byte[] totalData, StringBuffer sb, int length, String number, Parameter parameter1) {
        int port = getAnIntHex(totalData, 2, length, 16);
        e("端口", port);
        setIntentData("port", port);

        if (port == parameter1.getPort()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置通讯协议类型
     */
    private static void setIntentProtocolType(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        getProtocolType();

        setIntentData("protocolType", type);

        if (type == parameter1.getProtocolType()) {
            if (type != netType) {
                isRestart = true;
            }
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置启动播放类型
     */
    private static void setIntentStartPlayType(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("startPlayType", type);

        if (type == parameter1.getStartPlayType()) {
            sb.append(number);
            sb.append("01");
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置是否上报
     */
    private static void setUploading(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("isUploading", type);

        if (type == parameter1.getIsUploading()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置心跳
     */
    private static void setIntentHearBeat(byte[] totalData, StringBuffer sb, int length, String number, Parameter parameter1) {
        setIntentData("heartBeat", getAnIntHex(totalData, 2, length, 16));

        if (getAnIntHex(totalData, 2, length, 16) == parameter1.getHeartBeat()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置视频解码方式
     */
    private static void setIntentDecodingWay(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        getDecodingWay();
        setIntentData("decodingWay", type);

        if (type == parameter1.getDecodingWay()) {
            if (type != decodingWay) {
                isRestart = true;
            }
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置网络类型
     */
    private static void setIntentNetWortType(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("networkType", type);

        if (type == parameter1.getNetworkType()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置应用类型
     */
    private static void setIntentApplicationType(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("applicationType", type);

        if (type == parameter1.getApplicationType()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置开关机
     */
    private static void setIsResult(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("isResult", type);

        if (type == parameter1.getIsResult()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置屏幕类型
     */
    private static void setIntentScreentSize(byte[] totalData, StringBuffer sb, int length, String number, int type, Parameter parameter1) {
        setIntentData("screenSize", type);

        if (type == parameter1.getScreenSize()) {
            setSucceed(number, sb);
        } else {
            setFailure(number, sb);
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置开关机时间
     */
    private static void setIntentOnOfData(byte[] totalData, StringBuffer sb, int length, String number, ContentValues values) {
        int startHour = getAnIntHex(totalData, 2, 1, 16);
        int startMinute = getAnIntHex(totalData, 3, 1, 16);
        e("开机时间", startHour, startMinute);
        int endHour = getAnIntHex(totalData, 5, 1, 16);
        int endMinute = getAnIntHex(totalData, 6, 1, 16);
        e("关机时间", endHour, endMinute);
        values.put("startHour", startHour);
        values.put("startMinute", startMinute);
        values.put("endHour", endHour);
        values.put("endMinute", endMinute);
        LitePal.updateAll(Parameter.class, values);
        List<Parameter> getOnOffTime = LitePal.findAll(Parameter.class);
        for (Parameter getOnOffTimes : getOnOffTime) {
            if (getOnOffTimes.getEndMinute() == endMinute) {
                setSucceed(number, sb);
                if (getOnOffTimes.getIsResult() == CONSTANT_TWO) {
                    intentActivity("7");
                }
            } else {
                setFailure(number, sb);
            }
        }
        recursion(totalData, length, sb);
    }

    /**
     * 设置失败
     */
    private static void setFailure(String number, StringBuffer sb) {
        sb.append(number);
        sb.append("00");
    }

    /**
     * 设置成功
     */
    private static void setSucceed(String number, StringBuffer sb) {
        sb.append(number);
        sb.append("01");
    }

    /**
     * 递归
     */
    private static void recursion(byte[] totalData, int length, StringBuffer sb) {
        if (totalData.length - (CONSTANT_TWO + length) > 0) {
            byte[] data = new byte[totalData.length - (2 + length)];
            System.arraycopy(totalData, 2 + length, data, 0, totalData.length - (2 + length));
            e("还剩余参数", bytes2HexString(data));
            if (data.length > 0) {
                setData(data, sb);
            }
        }
    }

    private static boolean isIp(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        //============对之前的ip判断的bug在进行判断
        if (ipAddress == true) {
            String ips[] = addr.split("\\.");

            if (ips.length == 4) {
                try {
                    for (String ip : ips) {
                        if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
        return ipAddress;
    }

    /**
     * get请求方式
     * 生成二维码
     */
    private static void newZxing(String deviceId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        String partId = deviceId.substring(0, 2);
        e("二维码ID和前两位ID号", deviceId, partId);
        if (PART_ID.equals(partId)) {
            String url = GET_START_PARAM + deviceId;
            //创建一个网络请求的对象，如果没有写请求方式，默认的是get
            Request request = new Request.Builder()
                    .url(url).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //失败
                    SharedPreferencesUtils.writeBoolean(mActivity, Const.ISSUCCEED, false);
                    e(Thread.currentThread().getName() + "结果  " + e.toString());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //成功
                    //子线程

                    try {
                        SharedPreferencesUtils.writeBoolean(mActivity, Const.ISSUCCEED, true);
                        String data = response.body().string();
                        e(Thread.currentThread().getName() + "结果  " + data);
                        String urlDecoder, logoUrl;

                        urlDecoder = java.net.URLDecoder.decode(data, "UTF-8");
                        e("数据", urlDecoder);
                        JSONObject json;

                        json = new JSONObject(urlDecoder);
                        if (json.getBoolean("success")) {
                            logoUrl = json.getString("logoUrl");
                            e("logo路径", logoUrl);
                            SharedPreferencesUtils.writeString(mActivity, Const.LOGO_SHOW_DATA, logoUrl);

                            intentActivity("9");
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
            //  call.cancel();取消任务
        }
    }

    /**
     * 修改整型数据
     */
    public static void setIntentData(String key, int value) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        LitePal.updateAll(Parameter.class, values);
    }

    /**
     * 修改字符串数据
     */
    public static void setIntentData(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        LitePal.updateAll(Parameter.class, values);
    }

    /**
     * 修改布尔值数据
     */
    public static void setIntentData(String key, boolean value) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        LitePal.updateAll(Parameter.class, values);
    }

    /**
     * 获取协议类型
     */
    private static void getProtocolType() {
        List<Parameter> parameters = LitePal.findAll(Parameter.class);
        for (Parameter netTypes : parameters) {
            netType = netTypes.getProtocolType();
        }
    }

    /**
     * 获取服务器ip
     */
    private static void getIP() {
        List<Parameter> parameters = LitePal.findAll(Parameter.class);
        for (Parameter ips : parameters) {
            newIp = ips.getIp();
        }
    }

    /**
     * 获取解码方式
     */
    private static void getDecodingWay() {
        List<Parameter> parameters = LitePal.findAll(Parameter.class);
        for (Parameter netTypes : parameters) {
            decodingWay = netTypes.getDecodingWay();
        }
    }

    /**
     * web回应接口
     */
    public static void webInterface(final String url) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest(url, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e("失败原因", e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        e("status_code:" + response.body().string());
                    }
                });
            }
        });
    }

    private static final int STATUS_CODE = 200;
    private static String webApiData;

    /**
     * 纸巾控制回应
     */
    public static void webControlAnswer(final String url) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest(url, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e("失败原因", e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        int code = response.code();
                        e("访问结果", code);
                        if (code == STATUS_CODE) {
                            webApiData = response.body().string();
                            String urlDecoder = java.net.URLDecoder.decode(webApiData, "UTF-8");
                            e("数据", urlDecoder);
                            try {
                                JSONObject json = new JSONObject(urlDecoder);
                                if (json.getBoolean(SUCCESS)) {
                                    e("返回结果成功");
                                } else {
                                    e("返回结果失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            e("访问失败");
                        }
                    }
                });
            }
        });
    }

    /**
     * 连接服务器
     */
    public static void reconnect() {
        SocketManager.getInstance().close();
        SocketManager.getInstance().connect();
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtils.writeString(mActivity, Const.NET_TYPE, "tcp");
                ProtocolManager.getInstance().sendHeartBeat();
            }
        });
    }

    private static TimerTask connect = new TimerTask() {
        @Override
        public void run() {
            reconnect();
        }
    };

    /**
     * 互联网设置
     */
    private static void setLANData(byte[] buff, Parameter parameters, byte[] command, byte[] number, String server) {
        String id = parameters.getDeviceId();
        String rCode = parameters.getrCode();
        int length = getAnIntHex(buff, 5, 1, 16);
        e("参数长度", length);
        String rcvParameter = hexToStringGbk(getAnString(buff, 6, length));
        int rcvType = getAnIntHex(buff, 6, 1, 16);
        e("参数值", rcvParameter);
        e("参数值", rcvType);
        int type = buff[6];
        Parameter parameter = new Parameter();
        switch (bytes2HexString(number)) {
            case "01":
                setDeviceId(command, number, server, id, rCode, rcvParameter, parameter);
                break;
            case "02":
                setRCode(command, number, server, id, rCode, rcvParameter, parameter);
                break;
            case "03":
                setIP(command, number, server, id, rCode, rcvParameter, parameter);
                break;
            case "04":
                setPort(command, number, server, id, rCode, rcvParameter, type, parameter);
                break;
            case "05":
                setPtotocolType(command, number, server, id, rCode, type, parameter);
                break;
            case "06":
                setStartPlayTypeUrl(buff, command, number, server, id, rCode, type, parameter);
                break;
            case "0D":
                setOnOffData(buff, command, number, server, id, rCode, rcvType, parameter);
                break;
            case "0F":
                setScreenParameter(command, number, server, id, rCode, rcvType, parameter);
                break;
            case "10":
                setUploadingParameter(command, number, server, id, rCode, rcvType, parameter);
                break;
            case "11":
                setHeartBearParameter(buff, command, number, server, id, rCode, parameter);
                break;
            case "13":
                setDecodingWay(command, number, server, id, rCode, rcvType, parameter);
                break;
            case "14":
                setNetWorkType(command, number, server, id, rCode, rcvType, parameter);
                break;
            case "15":
                setApplicationType(command, number, server, id, rCode, rcvType, parameter);
                break;
            default:
                break;
        }
    }

    /**
     * 设置应用类型
     */
    private static void setApplicationType(byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        parameter.setApplicationType(rcvType);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvType == parameter.getApplicationType()) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置网络类型
     */
    private static void setNetWorkType(byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        parameter.setNetworkType(rcvType);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvType == parameter.getNetworkType()) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置播放器解码方式
     */
    private static void setDecodingWay(byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        getDecodingWay();

        parameter.setDecodingWay(rcvType);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvType == parameter.getDecodingWay()) {
            if (rcvType != decodingWay) {
                intentActivity("6");
            }
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置心跳周期
     */
    private static void setHeartBearParameter(byte[] buff, byte[] command, byte[] number, String server, String id, String rCode, Parameter parameter) {
        int heartBeat = getAnIntHex(buff, 6, 2, 16);
        parameter.setHeartBeat(heartBeat);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (heartBeat == parameter.getHeartBeat()) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置是否上报
     */
    private static void setUploadingParameter(byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        parameter.setIsUploading(rcvType);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvType == parameter.getIsUploading()) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置屏幕尺寸
     */
    private static void setScreenParameter(byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        parameter.setScreenSize(rcvType);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (parameter.getScreenSize() == rcvType) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置开关机时间
     */
    private static void setOnOffData(byte[] buff, byte[] command, byte[] number, String server, String id, String rCode, int rcvType, Parameter parameter) {
        int startHour = getAnIntHex(buff, 9, 1, 16);
        int startMinute = getAnIntHex(buff, 10, 1, 16);
        e("开机时间", startHour, startMinute);
        int endHour = getAnIntHex(buff, 12, 1, 16);
        int endMinute = getAnIntHex(buff, 13, 1, 16);
        e("关机时间", endHour, endMinute);

        parameter.setIsResult(rcvType);
        parameter.setStartHour(startHour);
        parameter.setStartMinute(startMinute);
        parameter.setEndHour(endHour);
        parameter.setEndMinute(endMinute);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvType == parameter.getIsResult() && startHour == parameter.getStartHour() && endHour == parameter.getEndHour()) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
            intentActivity("7");
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置启动播放路径
     */
    private static void setStartPlayTypeUrl(byte[] buff, byte[] command, byte[] number, String server, String id, String rCode, int type, Parameter parameter) {
        int dataLenght = getAnIntHex(buff, 8, 1, 16);
        String name = ".mp4";
        String startPath = hexToStringGbk(getAnString(buff, 9, dataLenght));
        e("开始播放路径：", startPath);
        if (type == 1) {
            if (!startPath.endsWith(name)) {
                startPath = startPath + ".mp4";
                e("当前的文件名", startPath);
            }
        }
        parameter.setStartPlayType(type);
        parameter.setStartPlayUrl(startPath);
        parameter.updateAll("uniqueness = ?", "Pan");

        if (type == parameter.getStartPlayType() && (startPath.equals(parameter.getStartPlayUrl())
                || startPath.equals(parameter.getStartPlayUrl() + ".mp4"))) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置通讯协议
     */
    private static void setPtotocolType(byte[] command, byte[] number, String server, String id, String rCode, int type, Parameter parameter) {
        getProtocolType();

        parameter.setProtocolType(type);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (type == parameter.getProtocolType()) {

            if (type != netType) {
                intentActivity("6");
            }
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置服务器端口
     */
    private static void setPort(byte[] command, byte[] number, String server, String id, String rCode, String rcvParameter, int type, Parameter parameter) {
        parameter.setPort(type);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvParameter.equals(Integer.toString(parameter.getPort()))) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置服务器ip
     */
    private static void setIP(byte[] command, byte[] number, String server, String id, String rCode, String rcvParameter, Parameter parameter) {
        getIP();
        if (isIp(rcvParameter)) {
            parameter.setIp(rcvParameter);
            parameter.updateAll("uniqueness = ?", "Pan");

        }

        if (rcvParameter.equals(parameter.getIp())) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
            if (!newIp.equals(parameter.getIp())) {
                reconnect();
            }
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置区号
     */
    private static void setRCode(byte[] command, byte[] number, String server, String id, String rCode, String rcvParameter, Parameter parameter) {
        parameter.setrCode(rcvParameter);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvParameter.equals(parameter.getrCode())) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置设备ID
     */
    private static void setDeviceId(byte[] command, byte[] number, String server, String id, String rCode, String rcvParameter, Parameter parameter) {
        parameter.setDeviceId(rcvParameter);
        parameter.updateAll("uniqueness = ?", "Pan");
        if (rcvParameter.equals(parameter.getDeviceId())) {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, true, server);
        } else {
            ProtocolManager.getInstance().writeAnswer(id, rCode, command, number, false, server);
        }
    }

    /**
     * 设置开始播放文件、路径
     */
    private static void setsSartPlay(byte[] receive) {
        int playType = getAnInt(receive);
        int playLength = getAnIntHex(receive, 8, 1, 16);
        String playUrl = hexToStringGbk(getAnString(receive, 9, playLength));

        String fileName, name = ".mp4";
        Parameter parameter = new Parameter();
        parameter.setPlayType(playType);
        if (playType == CONSTANT_FOUR) {
            if (!playUrl.endsWith(name)) {
                fileName = playUrl + ".mp4";
                e("当前的文件名", fileName);
            } else {
                fileName = playUrl;
            }
            parameter.setPlayUrl(fileName);
        } else {
            parameter.setPlayUrl(playUrl);
        }
        e("要设置的启动播放的类型：", playType);
        parameter.updateAll("Uniqueness=?", "Pan");

        intentActivity("03");
    }

    /**
     * 节目播放
     */
    public static void showPlay(String fileName, String value) {
        setIntentData("playUrl", fileName);
        SharedPreferencesUtils.writeString(mActivity, Const.CONTROL_NUMBER, "AD");

        intentActivity(value);
    }

    /**
     * 删除清单节目
     */
    private static void deleteShow(Parameter parameter, String mediaId, String server) {
        List<PlayList> showFile = showIdQuery("fileName", mediaId);
        for (PlayList showFiles : showFile) {
            List<PlayList> screenList = showIdQuery("screenType", mediaId);
            for (PlayList screenLists : screenList) {
                String deletefileName = showFiles.getFileName();
                e("实时播放与删除文件", parameter.getPlayUrl(), deletefileName);
                int screenType = screenLists.getScreenType();
                e("删除广告主副屏（1：主 2：副）", screenType);
                if (screenType == 1) {
                    if (!parameter.getPlayUrl().equals(deletefileName) || showSum().size() == 1) {
                        int deleteCount = LitePal.deleteAll(PlayList.class, "showId = ?", mediaId);
                        e("被删除数 " + deleteCount);
                        if (deleteCount > 0) {
                            intentActivity("19");
                            deleteFile(deletefileName, server);
                        }
                        e("被删除节目ID " + mediaId);
                        //2018-12-14 修改
                        String url = DELETE_PLAYED_MEDIA + parameter.getDeviceId() + "&mediaId=" + mediaId;
                        e("删除云端路径", url);
                        webInterface(url);
                    }
                } else if (screenType == 2 || screenType == 3) {
                    SharedPreferencesUtils.writeString(mActivity, Const.START_SCREENOR_FILE, deletefileName);
                    intentActivity("18");
                }
            }
        }
    }

    /**
     * 获取当前播放节目序号
     */
    private static void playShowNumber(byte[] command, byte[] number, String server) {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            List<PlayList> read = fileNameQuery("showId", parameters.getPlayUrl());
            for (PlayList reads : read) {
                e("读取到的节目ID", reads.getShowId());
                if (reads.getShowId() != 0) {
                    ProtocolManager.getInstance().readShowIdAnswer(parameters.getDeviceId(), parameters.getrCode(), command, number, reads.getShowId(), server);
                }
            }
        }
    }

    /**
     * vip时间检验
     */
    public static boolean timeTrigger(String startDeviceTime, String endDeviceTime) {
        int startHour, startMinute, startSecond, endHour, endMinute, endSecond, currentHour, currentMinute, currentSecond;
        long startTime, endTime, currentTime;
        boolean isVip = false;

        Calendar currentDate = Calendar.getInstance();
        currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        currentMinute = currentDate.get(Calendar.MINUTE);
        currentSecond = currentDate.get(Calendar.SECOND);
        startHour = Integer.parseInt(startDeviceTime.substring(0, 2));
        startMinute = Integer.parseInt(startDeviceTime.substring(3, 5));
        startSecond = Integer.parseInt(startDeviceTime.substring(6, 8));
        endHour = Integer.parseInt(endDeviceTime.substring(0, 2));
        endMinute = Integer.parseInt(endDeviceTime.substring(3, 5));
        endSecond = Integer.parseInt(endDeviceTime.substring(6, 8));

        startTime = startHour * 3600 + startMinute * 60 + startSecond;
        endTime = endHour * 3600 + endMinute * 60 + endSecond;
        currentTime = currentHour * 3600 + currentMinute * 60 + currentSecond;

        if (currentTime >= startTime && currentTime <= endTime) {
            isVip = true;
        }

//        Plog.e("设定开始时间", startTime);
//        Plog.e("获取当前时间", currentTime);
//        Plog.e("设定结束时间", endTime);

        return isVip;
    }

    /**
     * 判断是否第一次安装
     */
    public static boolean isFirstStart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SHARE_APP_TAG", 0);
        boolean isFirst = preferences.getBoolean("FIRSTStart", true);
        if (isFirst) {
            // 第一次
            preferences.edit().putBoolean("FIRSTStart", false).apply();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加设备参数
     */
    public static void addParameterData(String deviceId) {
        Parameter add = new Parameter();
        add.setDeviceId(deviceId);
        add.setrCode("1111");
        add.setIp(INITIAL_IP);
        add.setPort(INITIAL_PORT);
        add.setProtocolType(1);
        add.setStartPlayType(2);
        add.setStartPlayUrl("0.mp4");
        add.setPlayType(1);
        add.setPlayUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        add.setIsResult(2);
        add.setStartHour(6);
        add.setStartMinute(0);
        add.setEndHour(23);
        add.setEndMinute(59);
        add.setIsUploading(2);
        add.setHeartBeat(INITIAL_HERATBEAT);
        add.setScreenSize(1);
        add.setDecodingWay(1);
        add.setNetworkType(1);
        add.setApplicationType(1);
        add.setUniqueness("Pan");
        add.save();
    }

    /**
     * 获取包名
     */
    public static String getTopPackageName(String data) {
        return data.substring(0, data.indexOf(".", data.indexOf(".", data.indexOf(".") + 1) + 1));
    }

    private static long startTime;

    /**
     * 通过文件名更新文件下载状态
     */
    public static void updateDownloadState(String fileName) {
        ContentValues values = new ContentValues();
        values.put("downloadSuccess", 2);
        LitePal.updateAll(PlayList.class, values, "fileName = ?", fileName);
    }

    /**
     * 通过播放ID更新文件下载状态
     */
    public static void updateDownloadState2(String playId) {
        ContentValues values = new ContentValues();
        values.put("downloadSuccess", 2);
        LitePal.updateAll(PlayList.class, values, "playId = ?", playId);
    }

    /**
     * 更新文件下载次数
     */
    public static void updateDownloadCount(int count, String fileName) {
        ContentValues values = new ContentValues();
        values.put("downloadCount", count);
        LitePal.updateAll(PlayList.class, values, "fileName = ?", fileName);
    }

    /**
     * 绑定合伙人
     */
    public static void bindDealer(final Context context, final String code) {
        List<Parameter> idList = LitePal.findAll(Parameter.class);
        for (Parameter idLists : idList) {
            String url = BIND_DEALER + idLists.getDeviceId() + "&dealerId=" + code;
            e("路径", url);
            e("上下文", context);
            MyOkHttp myOkHttp = new MyOkHttp();
            myOkHttp.get()
                    .url(url)
                    .tag(context)
                    .enqueue(new RawResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, String response) {
                            e("合伙人绑定成功:" + response);
                            SharedPreferencesUtils.writeBoolean(mActivity, Const.BIND_DEALER_RESULT, true);
                        }

                        @Override
                        public void onFailure(int statusCode, String errorMsg) {
                            e("合伙人绑定失败原因:" + errorMsg);
                            SharedPreferencesUtils.writeBoolean(mActivity, Const.BIND_DEALER_RESULT, false);
                        }
                    });
        }
    }

    /**
     * 图片下载
     *
     * @param url     图片下载路径
     * @param saveDir 图片保存路径
     */
    public static void pictureDownload(Context context, final String url, final String saveDir) {
        MyOkHttp mMyOkHttp = new MyOkHttp();
        mMyOkHttp.download()
                .url(url)
                .filePath(saveDir)
                .tag(context)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        startTime = System.currentTimeMillis();
                        e("doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        String fileName = downloadFile.toString().substring(downloadFile.toString().lastIndexOf("/") + 1);
                        e("doDownload onFinish:", (System.currentTimeMillis() - startTime) / 1000, fileName);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
//                                Plog.e(TAG, "doDownload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onFailure(String error) {
                        e("doDownload onFailure:" + error);
                    }
                });
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context     对象
     * @param packageName 包名
     * @return 返回结果
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 检查无障碍服务是否打开
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.kingbird.advertisting/com.kingbird.advertisting.service.MyAccessibilityService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            e("accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            e("Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessabilityService = mStringColonSplitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        accessibilityFound = true;
                    }
                }
            }
        } else {
            e("ACCESSIBILIY IS DISABLED");
        }

        return accessibilityFound;
    }

    /**
     * 清除超过7天的log文件
     */
    public static void deleteExpiredLogs(int expiredDays) {
//        File dir = getAppContext().getFilesDir();
        File dir = new File(ROOT_DIRECTORY_URL + "xlog/");
        File[] subFiles = dir.listFiles();
        if (subFiles != null) {
//            int logFileCnt = 0;
            int expiredLogFileCnt = 0;
            int deleteSuccess = 0;
            //one day
            final int dayMilliseconds = 24 * 60 * 60 * 1000;
            long expiredTimeMillis = System.currentTimeMillis() - (expiredDays * dayMilliseconds);
            for (File file : subFiles) {
                String fileName = file.getName();
//                ++logFileCnt;
                e("文件最后修改时间", file.lastModified());
                e("比较时间", expiredTimeMillis);
                if (file.lastModified() < expiredTimeMillis) {
                    ++expiredLogFileCnt;
                    boolean deleteResult = file.delete();
                    if (deleteResult) {
                        deleteSuccess++;
                        e("Delete expired log files successfully:" + fileName);
                    } else {
                        e("Delete expired log files failure:" + fileName);
                    }
                } else {
                    e("当前log文件没有过期", fileName);
                }
            }
            e("文件总数=" + (subFiles.length) + ", " + "过期日志文件总数=" + expiredLogFileCnt
                    + ", 成功删除日志文件数= " + deleteSuccess);
        }
    }

    /**
     * 上传本地log文件
     */
    public static void postLog(Context context, final String deviceId, String fileName) {
        String paths = MY_LOG_URL + Plog.getLogFileName2(new Date()) + "/" + fileName;
//        String paths = MY_LOG_URL + Plog.getLogFileName2(new Date()) + "/" + Plog.getLogFileName3(new Date(), 0);
        KLog.e("文件路径：" + paths);
        String content = readFileContent(paths);
        KLog.e("文本文件大小：" + content.length());

        AppLog.DataBean app = new AppLog.DataBean();
        app.setFilename(fileName);
        app.setContent(content);

        AppLog appLog = new AppLog();
        appLog.setKey("kingbird2019");
        appLog.setData(app);

//        KLog.e("log文件: " + JSON.toJSONString(appLog));
        MyOkHttp myOkHttp = new MyOkHttp();
        myOkHttp.post()
                .url(APP_LOG)
                .jsonParams(JSON.toJSONString(appLog))
                .tag(context)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        KLog.e("doPostJSON log上传成功:" + response);
                        byte[] dataLan = ProtocolDao.appLogAnswer(deviceId, true);
                        ProtocolManager.getInstance().netDataAnser(dataLan);
                    }

                    @Override
                    public void onSuccess(int statusCode, JSONArray response) {
                        KLog.e("doPostJSON log上传成功:" + response);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMsg) {
                        KLog.e("doPostJSON log上传失败:" + errorMsg);
                        byte[] dataLan = ProtocolDao.appLogAnswer(deviceId, false);
                        ProtocolManager.getInstance().netDataAnser(dataLan);
                    }
                });
    }

    /**
     * 读取指定log文件数据
     */
    private static String readFileContent(final String fileName) {

        StringBuilder sbf = new StringBuilder();

        File file = new File(fileName);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return sbf.toString();
    }

    /**
     * 启动APP
     */
    static void startApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str 16进制字符串
     * @return 返回
     */
    public static byte[] toBytes(String str) {
        if (str == null || "".equals(str.trim())) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / CONSTANT_TWO; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
}