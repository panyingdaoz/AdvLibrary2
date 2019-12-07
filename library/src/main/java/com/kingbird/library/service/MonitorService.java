package com.kingbird.library.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.kingbird.library.base.Base;
import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.litepal.PlayList;
import com.kingbird.library.manager.ExecutorServiceManager;
import com.kingbird.library.manager.ProtocolDao;
import com.kingbird.library.manager.ProtocolManager;
import com.kingbird.library.manager.SocketManager;
import com.kingbird.library.manager.ThreadManager;
import com.kingbird.library.manager.UdpManager;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.MyLocationListener;
import com.kingbird.library.utils.SharedPreferencesUtils;
import com.kingbirdle.advertistingjar.base.UdpIoHandlerAdapter;
import com.kuaifa.ad.KuaiFaClient;
import com.socks.library.KLog;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.kingbird.library.base.Base.bindDealer;
import static com.kingbird.library.base.Base.bytes2HexString;
import static com.kingbird.library.base.Base.checkMd5;
import static com.kingbird.library.base.Base.convertHexToString;
import static com.kingbird.library.base.Base.dataModify;
import static com.kingbird.library.base.Base.dataProcessing;
import static com.kingbird.library.base.Base.getAnIntHex;
import static com.kingbird.library.base.Base.getAnString;
import static com.kingbird.library.base.Base.getQuery;
import static com.kingbird.library.base.Base.hexToStringGbk;
import static com.kingbird.library.base.Base.intentActivity;
import static com.kingbird.library.base.Base.readBaiDuShow;
import static com.kingbird.library.base.Base.reconnect;
import static com.kingbird.library.base.Base.removeFile;
import static com.kingbird.library.base.Base.setIntentData;
import static com.kingbird.library.base.Base.setShowData;
import static com.kingbird.library.base.Base.showIdQuery;
import static com.kingbird.library.base.Base.showPlay;
import static com.kingbird.library.base.Base.updateDownloadState;
import static com.kingbird.library.base.Base.webInterface;
import static com.kingbird.library.utils.Config.ADV_JINGDONG;
import static com.kingbird.library.utils.Config.CONSTANT_FIVE;
import static com.kingbird.library.utils.Config.CONSTANT_ONE;
import static com.kingbird.library.utils.Config.CONSTANT_TEN;
import static com.kingbird.library.utils.Config.CONSTANT_THREE;
import static com.kingbird.library.utils.Config.CONSTANT_TWO;
import static com.kingbird.library.utils.Config.DOMAIN_NAME;
import static com.kingbird.library.utils.Config.DOWNLOAD_COMPLETE;
import static com.kingbird.library.utils.Config.FILE_DOWNLOAD;
import static com.kingbird.library.utils.Config.FILE_SAVE_URL;
import static com.kingbird.library.utils.Config.GET_MEDIA_TIME;
import static com.kingbird.library.utils.Config.INITIAL_IP;
import static com.kingbird.library.utils.Config.INITIAL_PORT;
import static com.kingbird.library.utils.Config.JINGDONG_APP_ID;
import static com.kingbird.library.utils.Config.JINGDONG_APP_KEY;
import static com.kingbird.library.utils.Config.JINGDONG_HOST;
import static com.kingbird.library.utils.Config.LOTTERY_MACHINE;
import static com.kingbird.library.utils.Config.LOTTERY_MACHINE_MODEL;
import static com.kingbird.library.utils.Config.MY_LOG_URL;
import static com.kingbird.library.utils.Config.NUMBER_01;
import static com.kingbird.library.utils.Config.NUMBER_0B;
import static com.kingbird.library.utils.Config.NUMBER_B5;
import static com.kingbird.library.utils.Config.NUMBER_B6;
import static com.kingbird.library.utils.Config.NUMBER_BC;
import static com.kingbird.library.utils.Config.SET_LOGO_QR_SIZE;
import static com.kingbird.library.utils.Config.START_FILE_DOWNLOAD;
import static com.kingbird.library.utils.Config.VIDEO_TYPE;
import static com.kingbird.library.utils.Config.WRITE_DATA;
import static com.kingbird.library.utils.Const.DEVICE_MODEL;
import static com.kingbird.library.utils.Const.IS_INTERVAL_TIME;
import static com.kingbird.library.utils.Const.LOGO_SIZE;
import static com.kingbird.library.utils.Const.PICTURE_FILE_NAME;
import static com.kingbird.library.utils.Const.QR_SIZE;
import static com.kingbird.library.utils.Plog.e;

/**
 * 通讯服务 class
 *
 * @author panyingdao
 * @date 2017/12/15.
 */
public class MonitorService extends Service implements UdpIoHandlerAdapter.UdpIoHandlerListener {
    private static final String TAG = "MonitorService";
    private BroadcastReceiver closeRec;
    private static int failedCount = 0;
    private int onFailureCount;
    private static byte[] receive;

    public LocationClient mLocationClient = null;
    private BDAbstractLocationListener myListener = new MyLocationListener();
    private boolean isLoop = true;
    private long startTimeDownload;
    private int heartBeat;
    private int heartBeatCount = 0;
    private int failCount = 0;

    private int orderNumber;
    private String condition;
    private String startTime;
    private String endTime;
    private int duration;
    private int playType;
    private String fileName;
    private int account;
    private String showName;
    private String pictureName;
    private int showId;
    private int member;
    private int vip;
    private int splitScreen;
    private int redPacket;
    private int isBaiDu;
    private int isIntervalTime;
    private String fileMd5;
    private static ArrayList<String> intervalTimeArray = new ArrayList<>();
    byte[] sendCommand = new byte[1];
    byte[] sendNumber = new byte[1];

    @Override
    public IBinder onBind(Intent intent) {
        IBinder result = new ServiceBinder();
        e("onBind", Toast.LENGTH_LONG);
        return result;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        List<Parameter> parameters = LitePal.findAll(Parameter.class);
        for (Parameter parameter : parameters) {
            heartBeat = parameter.getHeartBeat();
            e("通讯心跳周期", heartBeat);
            if (2 == parameter.getProtocolType()) {
                connectUDP(parameter);
            } else {
                doReceiveDataFromServer();
                initHeartBeat();//心跳
            }
            screenHeartBeatDemo(parameter.getDeviceId());
        }

        initLocation();//定位

        closeRec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SocketManager.getInstance().close();
                stopSelf();
            }
        };

        IntentFilter filter = new IntentFilter(Const.EXIT_APP);
        registerReceiver(closeRec, filter);

    }

    /**
     * UDP连接
     */
    private void connectUDP(Parameter parameter) {
        isLoop = false;
        UdpManager.setIp(parameter.getIp());
        UdpManager.setPort(parameter.getPort());
        //初始化UDP mina相关
        if (UdpManager.getInstance().getConnectorUdp() == null) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    e("udp连接情况", UdpManager.getInstance().connectUdp(new
                            UdpIoHandlerAdapter(MonitorService.this)));
                }
            });
        }
    }

    private void doReceiveDataFromServer() {
        if (isLoop) {
            ExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (SocketManager.getInstance().getSocket() != null) {
                        SharedPreferencesUtils.writeString(MonitorService.this, Const.NET_TYPE, "tcp");
                        receive = SocketManager.getInstance().receive();
                        rcvDataProcessing(receive);
                    }
                }
            }, 0, 10, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 接收数据
     */
    public void rcvDataProcessing(byte[] data) {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (final Parameter parameters : parameter) {
            if (data != null) {
                int dataLength = data.length;
                e("接收数据长度", dataLength);
                if (dataLength == 22) {
                    dealWithHeartBeat(data);
                    heartBeatCount = 0;
                    failCount = 0;
                }
                if (dataLength > 22) {
                    int rcvLength = (dataLength - 20);
                    byte[] validData = new byte[rcvLength];
                    System.arraycopy(data, 20, validData, 0, rcvLength);
                    byte[] command = ProtocolManager.getInstance().parseParameter(validData, 1, 1);
                    byte[] number = ProtocolManager.getInstance().parseParameter(validData, 4, 1);
                    String commands = bytes2HexString(command);
                    String numbers = bytes2HexString(number);
                    String id = convertHexToString(getAnString(data, 2, 11));
                    String deviceId = parameters.getDeviceId();
                    String rCode = parameters.getrCode();
                    e("服务器请求数据", bytes2HexString(validData));
                    e("服务器请求ID", id);
                    e("本地ID", deviceId);
                    e("命令", commands);
                    e("编号", numbers);
                    if (id.equals(parameters.getDeviceId())) {
                        SharedPreferencesUtils.writeString(MonitorService.this, Const.CONTROL_TYPE, "INTERNET");
                        List<String> commandArray = Arrays.asList(FILE_DOWNLOAD, START_FILE_DOWNLOAD, SET_LOGO_QR_SIZE, WRITE_DATA, NUMBER_0B, ADV_JINGDONG);
                        if (commandArray.contains(commands)) {
                            controlCommand(parameters, validData, command, number, commands, numbers, deviceId, rCode);
                        } else {
                            dataProcessing(validData, "");
                        }
                    }
                    if ("90".equals(commands) && "01".equals(numbers)) {
                        ProtocolManager.getInstance().readAnswer
                                (deviceId, rCode, command, number, deviceId, "");
                    }
                }
            }
        }
    }

    /**
     * 获取本地log
     */
    private void logDataAnalysis(byte[] data, String deviceId) {
        try {
            KLog.e("要解析的数据：" + bytes2HexString(data));
            String packge, strDay = null;
            int year = getAnIntHex(data, 5, 2, 16);
            int month = getAnIntHex(data, 7, 1, 16);
            int day = getAnIntHex(data, 8, 1, 16);
            if (day < CONSTANT_TEN) {
                strDay = "0" + day;
            }
            if (month < CONSTANT_TEN) {
                packge = year + "-" + "0" + month + "-" + strDay;
            } else {
                packge = year + "-" + month + "-" + strDay;
            }
            KLog.e("设备ID：" + deviceId);
            KLog.e(year + "年" + "-" + month + "月" + "-" + strDay + "日");
            String path = MY_LOG_URL + packge;
            KLog.e("log路径：" + path);
            File file = new File(path);
            File[] subFiles = file.listFiles();
            assert subFiles != null;
            KLog.e("数组大小：" + subFiles.length);
            for (File f : subFiles) {
                KLog.e("获取到的文件名：" + f.getName());
                Base.postLog(this, deviceId, f.getName());
            }
        } catch (Exception e) {
            KLog.e("异常原因：" + e.toString());
        }
    }

    /**
     * 指令控制
     */
    private void controlCommand(Parameter parameters, byte[] validData, byte[] command, byte[] number, String commands, String numbers, String deviceId, String rCode) {
        switch (commands) {
            case FILE_DOWNLOAD:
                if (NUMBER_B5.equals(numbers)) {
                    e("文件下载");
                    httpFileDownload(validData, command, number, parameters, commands, numbers);
                }
                break;
            case START_FILE_DOWNLOAD:
                if (NUMBER_B6.equals(numbers)) {
                    httpFileDownload(validData, command, number, parameters, commands, numbers);
                }
                break;
            case SET_LOGO_QR_SIZE:
                logoQr(validData, deviceId, rCode);
                break;
//            case ADV_VOICE:
//                voiceAnalysis(validData);
//                break;
            case WRITE_DATA:
                if (numbers.equals(NUMBER_0B)) {
                    e("开始节目加数据解析");
                    try {
                        parseShow(validData, parameters);
                        if (isBaiDu != 1 && isIntervalTime != 1) {
                            if (playType != 1 && playType != CONSTANT_TWO && playType != CONSTANT_THREE) {
                                setDeviceShow(parameters);
                            }
                            sendCommand[0] = (byte) 0x91;
                            sendNumber[0] = (byte) 0x0B;
                            ProtocolManager.getInstance().internetShowAnswer(parameters.getDeviceId(),
                                    parameters.getrCode(), sendCommand, sendNumber, orderNumber, showId, 2, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        e("清单数据解析异常", e.toString());
                    }
                } else {
                    dataProcessing(validData, "");
                }
                break;
            case ADV_JINGDONG:
                if (numbers.equals(NUMBER_BC)) {
                    String isStart = getAnString(validData, 6, 1);
                    e("是否启动京东广告", isStart);
                    if (NUMBER_01.equals(isStart)) {
                        SharedPreferencesUtils.writeBoolean(MonitorService.this, Const.IS_START_JINGDONG, true);
                    } else {
                        SharedPreferencesUtils.writeBoolean(MonitorService.this, Const.IS_START_JINGDONG, false);
                    }
                    intentActivity("23");
                }
                break;
            case NUMBER_0B:
                logDataAnalysis(validData, deviceId);
                break;
            default:
        }
    }

    /**
     * logo、二维码显示大小控制
     */
    private void logoQr(byte[] validData, String deviceID, String rCode) {
        String number = "B8", number2 = "B9";
        e("数据", bytes2HexString(validData));
        String logoNumber = getAnString(validData, 4, 1);
        String qrNumber = getAnString(validData, 7, 1);
        int logoSize = getAnIntHex(validData, 6, 1, 16);
        int qrSize = getAnIntHex(validData, 9, 1, 16);
        e("控制logo显示编号：", logoNumber + ";" + qrNumber);
        e("logo、qr显示比例", logoSize + ";" + qrSize);
        if (number.equals(logoNumber) && number2.equals(qrNumber)) {
            SharedPreferencesUtils.writeInt(MonitorService.this, LOGO_SIZE, logoSize);
            SharedPreferencesUtils.writeInt(MonitorService.this, QR_SIZE, qrSize);
            intentActivity("21");
            byte[] commandLogo = new byte[1];
            commandLogo[0] = (byte) 0x96;
            byte[] numberLogo = new byte[1];
            numberLogo[0] = (byte) 0xB8;
            controlAnswer(commandLogo, numberLogo, deviceID, rCode);
        }
    }

    /**
     * 清单数据解析
     */
    private void parseShow(byte[] receive, Parameter parameter) {
        orderNumber = getAnIntHex(receive, 6, 2, 16);
        e("序号", orderNumber);
        condition = getAnString(receive, 8, 1);
        e("限定条件", condition);
        int startYear = getAnIntHex(receive, 9, 2, 16);
        int startMonths = getAnIntHex(receive, 11, 1, 16);
        int startDay = getAnIntHex(receive, 12, 1, 16);
        int startHour = getAnIntHex(receive, 13, 1, 16);
        int startMinute = getAnIntHex(receive, 14, 1, 16);
        int startSecond = getAnIntHex(receive, 15, 1, 16);
        startTime = (startYear + "-" + startMonths + "-" + startDay + " " + startHour + ":" + startMinute + ":" + startSecond);
        e("起始时间", startTime);
        int endYear = getAnIntHex(receive, 16, 2, 16);
        int endMonths = getAnIntHex(receive, 18, 1, 16);
        int endDay = getAnIntHex(receive, 19, 1, 16);
        int endHour = getAnIntHex(receive, 20, 1, 16);
        int endMinute = getAnIntHex(receive, 21, 1, 16);
        int endSecond = getAnIntHex(receive, 22, 1, 16);
        endTime = (endYear + "-" + endMonths + "-" + endDay + " " + endHour + ":" + endMinute + ":" + endSecond);
        e("终止时间", endTime);
        duration = getAnIntHex(receive, 23, 2, 16);
        e("设置播放次数", duration);
        playType = getAnIntHex(receive, 25, 1, 16);
        e("播放类型", playType);
        int fileLength = getAnIntHex(receive, 26, 1, 16);
        e("文件长度", fileLength);
        try {
            fileName = java.net.URLDecoder.decode(hexToStringGbk(getAnString(receive, 27, fileLength)), "UTF-8");
            e("文件名", fileName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        account = getAnIntHex(receive, 27 + fileLength, 4, 16);
        e("商家账号", account);
        int showLength = getAnIntHex(receive, 31 + fileLength, 1, 16);
        showName = hexToStringGbk(getAnString(receive, 32 + fileLength, showLength));
        e("节目名称", showName);
        showId = getAnIntHex(receive, 32 + fileLength + showLength, 4, 16);
        e("节目ID", showId);
        member = getAnIntHex(receive, 36 + fileLength + showLength, 1, 16);
        e("会员类型", member);
        vip = getAnIntHex(receive, 37 + fileLength + showLength, 1, 16);
        e("是否是VIP播放", vip);
        splitScreen = getAnIntHex(receive, 38 + fileLength + showLength, 1, 16);
        e("是否是分屏广告", splitScreen);
        SharedPreferencesUtils.writeInt(MonitorService.this, Const.SPLIT_SCREEN, splitScreen);
        redPacket = getAnIntHex(receive, 39 + fileLength + showLength, 1, 16);
        SharedPreferencesUtils.writeInt(MonitorService.this, Const.REDPACKET, redPacket);
        e("是普通广告：1 还是红包广告：2", redPacket);
        if (redPacket == CONSTANT_TWO) {
            pictureName = "redpacket_" + parameter.getDeviceId() + "_" + showId + ".jpg";
        } else {
            pictureName = "";
        }
        isBaiDu = getAnIntHex(receive, 40 + fileLength + showLength, 1, 16);
        SharedPreferencesUtils.writeInt(MonitorService.this, Const.IS_BAIDU, isBaiDu);
        e("是否是百度广告", isBaiDu);
        if (isBaiDu == CONSTANT_ONE) {
            int count = readBaiDuShow().size();
            if (count > 0) {
                do {
                    count--;
                    int isDelete = LitePal.deleteAll(PlayList.class, "isBaiDu = ?", "1");
                    e("百度广告清单删除结果", isDelete);
                } while (count != 0);
            }
            intentActivity("13");
        }
        isIntervalTime = getAnIntHex(receive, 41 + fileLength + showLength, 1, 16);
        SharedPreferencesUtils.writeInt(MonitorService.this, IS_INTERVAL_TIME, isIntervalTime);
        e("是否是时段广告", isIntervalTime);
        if (isIntervalTime == CONSTANT_ONE) {
            intervalTimeAnalysis(parameter);
        }
        fileMd5 = getAnString(receive, 42 + fileLength + showLength, 16);
        e("md5值", fileMd5);
        int lG = 58 + fileLength + showLength;
        parseShowRecursion(receive, lG);
    }

    /**
     * 粘包后的下载处理
     */
    private void parseShowRecursion(byte[] totalData, int showLength) {
        int length = totalData.length - showLength;
        e("剩余数据长度", length);
        if (length > CONSTANT_FIVE) {
            try {
                byte[] data = new byte[length - 1];
                System.arraycopy(totalData, showLength + 1, data, 0, length - 1);
                e("还剩余参数", bytes2HexString(data));
                rcvDataProcessing(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清单设置
     */
    public void setDeviceShow(Parameter parameter) {
        sendCommand[0] = (byte) 0x91;
        sendNumber[0] = (byte) 0x0B;
        boolean isNew = true;
        int baiDushowId = 0;
        int lastPlayId;
        PlayList playList = new PlayList();
        if (showId > 0) {
            List<PlayList> mpList = LitePal.findAll(PlayList.class);
            int playListSize = mpList.size();
            if (playListSize == 0) {
                lastPlayId = playListSize;
            } else {
                PlayList lastNews = LitePal.findLast(PlayList.class);
                lastPlayId = lastNews.getPlayId();
            }
            e("最终的节目播放ID", lastPlayId);
            e("数据库大小", playListSize);
            if (mpList.isEmpty()) {
                setShowData(lastPlayId + 1, condition, startTime, endTime, duration, playType, fileName, account, showName,
                        showId, member, vip, splitScreen, redPacket, pictureName, isBaiDu, baiDushowId, isIntervalTime, intervalTimeArray, fileMd5, playList);
                playList.save();
            }
            for (PlayList mpLists : mpList) {
                //2018-8-22 改为节目ID
                if (showId == mpLists.getShowId()) {
                    dataModify(lastPlayId, condition, startTime, endTime, duration, playType, fileName, account, showName, showId,
                            member, vip, splitScreen, redPacket, pictureName, isBaiDu, baiDushowId, isIntervalTime, fileMd5);
                    isNew = false;
                }
            }
            if (isNew) {
                setShowData(lastPlayId + 1, condition, startTime, endTime, duration, playType, fileName, account, showName,
                        showId, member, vip, splitScreen, redPacket, pictureName, isBaiDu, baiDushowId, isIntervalTime, intervalTimeArray, fileMd5, playList);
                playList.save();
            }

//            if (isBaiDu != 1 && isIntervalTime != 1 && splitScreen != CONSTANT_TWO && playType == 1) {
//                liveFor(parameter);
//            }
        }
    }

    /**
     * 时段广告时间获取
     */
    public void intervalTimeAnalysis(final Parameter parameter) {
        String url = GET_MEDIA_TIME + showId;
        MyOkHttp myOkHttp = new MyOkHttp();
        myOkHttp.get()
                .url(url)
                .tag(Base.getActivity())
                .enqueue(new RawResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        e("获取的数据:" + response);
                        intervalTimeList(response, parameter);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMsg) {
                        e("时段广告时间获取失败原因:" + errorMsg);
                    }
                });
    }

    /**
     * 时段广告时间解析
     */
    private void intervalTimeList(String response, final Parameter parameter) {
        int intervalTimeCount = 0;
        String success = "success", time;
        try {
            JSONObject json = new JSONObject(response);
            if (json.getBoolean(success)) {
                JSONArray deviceTimes = json.getJSONArray("times");
                e("时间数组内容", deviceTimes);
                for (int i = 0; i < deviceTimes.length(); i++) {
                    IntervalTime deviceTime = new IntervalTime();
                    deviceTime.mediaId = deviceTimes.getJSONObject(i).getString("mediaId");
                    deviceTime.startTime = deviceTimes.getJSONObject(i).getString("startTime");
                    deviceTime.endTime = deviceTimes.getJSONObject(i).getString("endTime");
                    e("开始时间", deviceTime.startTime);
                    e("结束时间", deviceTime.endTime);
                    e("节目ID", deviceTime.mediaId);
                    time = deviceTime.startTime + "/" + deviceTime.endTime;
                    intervalTimeArray.add(time);
                }
                e("时段集合", intervalTimeArray);
                if (intervalTimeArray.size() > 0) {
                    setDeviceShow(parameter);
                    intervalTimeCount++;
                    if (intervalTimeCount == 1) {
                        sendCommand[0] = (byte) 0x91;
                        sendNumber[0] = (byte) 0x0B;
                        ThreadManager.getInstance().doExecute(new Runnable() {
                            @Override
                            public void run() {
                                e("节目设置完成回应");
                                ProtocolManager.getInstance().internetShowAnswer(parameter.getDeviceId(), parameter.getrCode(), sendCommand, sendNumber, orderNumber, showId, 2, "");
                            }
                        });
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            e("解析出错原因", e.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部匿名时段播放时间类
     */
    static class IntervalTime {
        String mediaId;
        String startTime;
        String endTime;
    }

    /**
     * 直播
     */
    private void liveFor(Parameter parameter) {
        List<PlayList> newsList = showIdQuery("fileName", Integer.toString(showId));
        for (PlayList book1 : newsList) {
            if (fileName.equals(book1.getFileName())) {
                if (playType == 1) {
                    String startTime = null, endTime = null;
                    Date startDate, endDate;
                    Date currentDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

                    List<PlayList> qery = getQuery("startTime", "fileName", fileName);
                    for (PlayList start : qery) {
                        startTime = start.getStartTime();
                    }
                    List<PlayList> qeryEnd = getQuery("endTime", "fileName", fileName);
                    for (PlayList end : qeryEnd) {
                        endTime = end.getEndTime();
                    }

                    try {
                        assert startTime != null;
                        startDate = format.parse(startTime);
                        assert endTime != null;
                        endDate = format.parse(endTime);
                        e("起始时间", startTime);
                        e("现在时间", format.format(currentDate));
                        e("终止时间", endTime);
                        assert startDate != null;
                        assert endDate != null;
                        if (startDate.before(currentDate) && endDate.after(currentDate)) {
                            e("可以播放");
                            showPlay(fileName, "4");
                        }
                    } catch (ParseException e) {
                        e("异常", e.toString());
                        e.printStackTrace();
                    }
                }
                SharedPreferencesUtils.writeInt(MonitorService.this, Const.SHOW_ID, showId);
                e("直播清单回应");
                ProtocolManager.getInstance().internetShowAnswer(parameter.getDeviceId(), parameter.getrCode(), sendCommand, sendNumber, orderNumber, showId, 2, "");
                break;
            } else {
                ProtocolManager.getInstance().internetShowAnswer(parameter.getDeviceId(), parameter.getrCode(), sendCommand, sendNumber, orderNumber, showId, 0, "");
            }
        }
    }

    /**
     * 文件下载
     */
    private void httpFileDownload(byte[] validData, final byte[] command, final byte[] number, final Parameter parameter, final String commands, final String numbers) {
        try {
            onFailureCount = 0;
            int rcvLengths = getAnIntHex(validData, 2, 2, 16);
            final String url = java.net.URLDecoder.decode(hexToStringGbk(getAnString(validData, 6, rcvLengths - 2)), "UTF-8");
            e("路径和长度", url + "\n" + rcvLengths);
            final String fileName = url.substring(url.lastIndexOf("/") + 1);
            final String saveDir = FILE_SAVE_URL + fileName;
            List<PlayList> querShow = Base.fileNameQuery("showId", fileName);
            for (PlayList querShows : querShow) {
                byte[] commandArrays = new byte[1];
                commandArrays[0] = (byte) 0x80;
                byte[] numberArray = new byte[1];
                numberArray[0] = (byte) 0xB5;
                String showId = Integer.toString(querShows.getShowId());
                ProtocolManager.getInstance().fileDownloadAnswer(parameter.getDeviceId(), parameter.getrCode(), commandArrays[0], numberArray[0], showId, false);
            }
            if (Base.fileIsExists(saveDir)) {
                if (!checkMd5(fileName)) {
                    SharedPreferencesUtils.writeString(MonitorService.this, Const.FILE_NAME, fileName);
                    removeFile(fileName);
                    okHttpDownload(command, number, parameter, commands, url, saveDir, numbers, fileName);
                } else {
                    e("文件存在无需下载");
                }
            } else {
                SharedPreferencesUtils.writeString(MonitorService.this, Const.FILE_NAME, fileName);
                okHttpDownload(command, number, parameter, commands, url, saveDir, numbers, fileName);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     */
    private void okHttpDownload(final byte[] command, final byte[] number, final Parameter parameter, final String commands,
                                final String url, final String saveDir, final String numbers, final String fileName) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(600000L, TimeUnit.MILLISECONDS)
                .build();
        MyOkHttp mMyOkHttp = new MyOkHttp(okHttpClient);
        mMyOkHttp.download()
                .url(url)
                .filePath(saveDir)
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        startTimeDownload = System.currentTimeMillis();
                        e("doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        downloadFinish(downloadFile, commands, parameter, numbers, command, number);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                    }

                    @Override
                    public void onFailure(String error) {
                        e("doDownload onFailure:" + error);
                        e("保存路径", saveDir);
                        removeFile(fileName);
                        onFailureCount++;
                        if (onFailureCount <= CONSTANT_TEN) {
                            okHttpDownload(command, number, parameter, commands, url, saveDir, numbers, fileName);
                        }
                    }
                });
    }

    /**
     * 文件下载完成处理
     */
    private void downloadFinish(File downloadFile, String commands, final Parameter parameter, String numbers, final byte[] command, final byte[] number) {
        String fileName = downloadFile.toString().substring(downloadFile.toString().lastIndexOf("/") + 1);
        e("doDownload onFinish:", (System.currentTimeMillis() - startTimeDownload) / 1000, fileName);
        e("下载类型", commands);
        if (checkMd5(fileName)) {
            switch (commands) {
                case FILE_DOWNLOAD:
                    updateDownloadState(fileName);

                    List<PlayList> showList = getQuery("showId", "fileName", fileName);
                    for (PlayList showLists : showList) {
                        int showId = showLists.getShowId();
                        String url = DOWNLOAD_COMPLETE + parameter.getDeviceId() + "&mediaId=" + showId;
                        webInterface(url);

                        int red = SharedPreferencesUtils.readInt(MonitorService.this, Const.REDPACKET);
                        e("红包值", red);
                        if (showLists.getRedPacket() == 2 || red == 2) {
                            String pictureFileName = "redpacket_" + parameter.getDeviceId() + "_" + showId + ".jpg";
                            SharedPreferencesUtils.writeString(MonitorService.this, PICTURE_FILE_NAME, pictureFileName);
                            String picture = DOMAIN_NAME + "RedPacket/" + pictureFileName;
                            final String saveDir = FILE_SAVE_URL + pictureFileName;
                            e("红包图片名字", pictureFileName);
                            pictureDownload(picture, saveDir);
                        }
                    }
                    int screen = SharedPreferencesUtils.readInt(MonitorService.this, Const.SPLIT_SCREEN);
                    int interval = SharedPreferencesUtils.readInt(MonitorService.this, IS_INTERVAL_TIME);
                    if (interval == 1) {
                        intentActivity("20");
                    }
                    if (screen == CONSTANT_TWO || screen == CONSTANT_THREE) {
                        showPlay(fileName, "16");
                    } else {
                        int isBaiDu = SharedPreferencesUtils.readInt(MonitorService.this, Const.IS_BAIDU);
                        //只有不是百度广告才发送展示通知，CONSTANT_ONE==1边上百度广告
                        if (isBaiDu != CONSTANT_ONE) {
                            String startUpNumber = "B6";
                            if (startUpNumber.equals(numbers)) {
                                setIntentData("startPlayUrl", fileName);
                                startAPP();
                            } else {
                                String strSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
                                e("下载文件的后缀", strSuffix);
                                if (VIDEO_TYPE.equals(strSuffix)) {
                                    SharedPreferencesUtils.writeBoolean(MonitorService.this, Const.SHOW_PLAY, true);
                                } else {
                                    SharedPreferencesUtils.writeBoolean(MonitorService.this, Const.SHOW_PLAY, false);
                                }
                                showPlay(fileName, "4");
                            }
                        }
                    }
                    break;
                case START_FILE_DOWNLOAD:
                    setIntentData("startPlayUrl", fileName);
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            ProtocolManager.getInstance().fileDownloadAnswer(parameter.getDeviceId(), parameter.getrCode(), command[0], number[0], "0000", true);
                        }
                    });
                    break;
                default:
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            ProtocolManager.getInstance().fileDownloadAnswer(parameter.getDeviceId(), parameter.getrCode(), command[0], number[0], "0000", true);
                        }
                    });
                    break;
            }
        } else {
            e("文件不完整移除文件");
            removeFile(fileName);
        }
    }

    /**
     * 图片下载
     */
    public void pictureDownload(final String url, final String saveDir) {
        MyOkHttp mMyOkHttp = new MyOkHttp();
        mMyOkHttp.download()
                .url(url)
                .filePath(saveDir)
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        startTimeDownload = System.currentTimeMillis();
                        e("doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        String fileName = downloadFile.toString().substring(downloadFile.toString().lastIndexOf("/") + 1);
                        e("doDownload onFinish:", (System.currentTimeMillis() - startTimeDownload) / 1000, fileName);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                    }

                    @Override
                    public void onFailure(String error) {
                        e(TAG, "doDownload onFailure:" + error);
                    }
                });
    }

    /**
     * 软件重启
     */
    private void startAPP() {
        Intent mStartActivity = new Intent();
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (mgr != null) {
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        }
        System.exit(0);
    }

    /**
     * 控制结果回应
     */
    private void controlAnswer(final byte[] command, final byte[] number, final String deviceId, final String rCode) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                ProtocolManager.getInstance().writeAnswer(deviceId, rCode, command, number, true, "");
            }
        });
    }

    /**
     * 百度定位初始化
     */
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(2 * 60 * 1000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 心跳
     */
    private void initHeartBeat() {
        if (isLoop) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    ExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            List<Parameter> parameters = LitePal.findAll(Parameter.class);
                            for (Parameter parameter : parameters) {
                                heartBeat = parameter.getHeartBeat();
                                e("最新通讯心跳周期", heartBeat);
                                if (SocketManager.getInstance().getSocket() != null) {
                                    SharedPreferencesUtils.writeString(MonitorService.this, Const.NET_TYPE, "tcp");
                                    ProtocolManager.getInstance().sendHeartBeat();
                                    heartBeatCount = 0;
                                    failCount++;
                                    if (failCount > CONSTANT_ONE) {
                                        e("心跳超过1次没有收到回复，开始进行重连");
                                        reconnect();
                                    }
                                } else {
                                    e("send heartBeat failed cause by bad net work");
                                    heartBeatCount++;
                                    e("心跳连接失败次数", heartBeatCount);
                                    if (heartBeatCount > CONSTANT_TWO) {
                                        setIntentData("ip", INITIAL_IP);
                                        setIntentData("port", INITIAL_PORT);
                                    }
                                    reconnect();
                                }
                            }
                            String model = SharedPreferencesUtils.readString(MonitorService.this, DEVICE_MODEL);
                            if (LOTTERY_MACHINE_MODEL.equals(model)) {
                                try {
                                    if (!getProcess()) {
                                        e("彩票软件没有运行");
                                        PackageManager packageManager = getPackageManager();
                                        Intent intent = packageManager.getLaunchIntentForPackage(LOTTERY_MACHINE);
                                        if (intent == null) {
                                            Toast.makeText(MonitorService.this, "当前软件还没有安装，请先安装此软件！", Toast.LENGTH_LONG).show();
                                        } else {
                                            startActivity(intent);
                                        }
                                    } else {
                                        e("彩票软件在运行");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            boolean result = SharedPreferencesUtils.readBoolean(MonitorService.this, Const.BIND_DEALER_RESULT);
                            if (!result) {
                                String dealerCode = SharedPreferencesUtils.readString(MonitorService.this, Const.DEALER_CODE);
                                if (!"".equals(dealerCode)) {
                                    bindDealer(MonitorService.this, dealerCode);
                                }
                            }

                        }
                    }, 1, heartBeat, TimeUnit.SECONDS);
                }
            });
        }
    }

    /**
     * 屏幕心跳-demo
     */
    private static void screenHeartBeatDemo(final String deviceId) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                ExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        KuaiFaClient client = new KuaiFaClient(JINGDONG_APP_ID, JINGDONG_APP_KEY, JINGDONG_HOST,
                                true);
                        client.screenHeartBeat(deviceId, 0);
                    }
                }, 1, 10, TimeUnit.MINUTES);
            }
        });
    }

    /**
     * 获取进程
     */
    private boolean getProcess() {
        boolean isClsRunning = false;

        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        for (AndroidAppProcess process : processes) {
            String processName = process.name;
            if (processName.equals(LOTTERY_MACHINE)) {
                isClsRunning = true;
                break;
            }
        }
        return isClsRunning;
    }

    /**
     * UDP心跳
     */
    private void initHeartBeatUdp() {
        ExecutorServiceManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                e("UDP心跳准备");
                SharedPreferencesUtils.writeString(MonitorService.this, Const.NET_TYPE, "udp");
                ProtocolManager.getInstance().sendHeartBeat();
            }
        }, 1, heartBeat, TimeUnit.SECONDS);
    }

    /**
     * 心跳校验
     *
     * @param receive 接收数据
     */
    private void dealWithHeartBeat(byte[] receive) {
        int count = 3;
        if (receive != null && receive.length == ProtocolDao.HEARTBEAT_DATA_LENGTH) {
            boolean b = ProtocolManager.getInstance().checkHeartBeat(receive);
            if (!b) {
                //判断失败次数，如果为3重新初始化socket
                failedCount++;
                e(TAG, "失败次数" + failedCount);
                if (failedCount == count) {
                    failedCount = 0;
                    //初始化socket
                    e(TAG, "准备初始化socket");
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            SocketManager.getInstance().close();
                            SocketManager.getInstance().connect();
                        }
                    });
                } else {
                    e(TAG, "再次发送心跳");
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferencesUtils.writeString(MonitorService.this, Const.NET_TYPE, "tcp");
                            ProtocolManager.getInstance().sendHeartBeat();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) {
        e("exceptionCaught", throwable.getLocalizedMessage());
    }

    @Override
    public void messageReceived(IoSession ioSession, byte[] data) {
        e("messageReceived", bytes2HexString(data));
        SharedPreferencesUtils.writeString(MonitorService.this, Const.NET_TYPE, "udp");
        rcvDataProcessing(data);
    }

    @Override
    public void sessionClosed(IoSession ioSession) {
        e("sessionClosed", "sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession ioSession) {
        e("sessionCreated", "sessionCreated");
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) {
        e("sessionIdle", "sessionIdle");
    }

    @Override
    public void sessionOpened(IoSession ioSession) {
        e("sessionOpened", "sessionOpened");
        initHeartBeatUdp();
    }

    @Override
    public void messageSent(IoSession ioSession, Object object) {
        e("messageSent", "messageSent");
    }

    public class ServiceBinder extends Binder {
        public MonitorService getService() {
            return MonitorService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeRec);
        closeRec = null;
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        ThreadManager.getInstance().shutdown();
        ExecutorServiceManager.getInstance().shutdown();
        UdpManager.getInstance().closeUdp();
        e("销毁广播");
    }
}
